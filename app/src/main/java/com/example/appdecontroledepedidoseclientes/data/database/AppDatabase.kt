package com.example.appdecontroledepedidoseclientes.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appdecontroledepedidoseclientes.data.dao.*
import com.example.appdecontroledepedidoseclientes.data.entity.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appdecontroledepedidoseclientes.util.SecurityUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Cliente::class, Produto::class, Pedido::class, Usuario::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun produtoDao(): ProdutoDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun usuarioDao(): UsuarioDao
    // TODO: ItemPedidoDao será adicionado na v2 para suportar múltiplos produtos por pedido

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Usuários padrão para teste
                        CoroutineScope(Dispatchers.IO).launch {
                            // Primeiro usuário: machado / 2009
                            INSTANCE?.usuarioDao()?.insert(Usuario("machado", SecurityUtils.hashPassword("2009")))
                            // Segundo usuário: demo / 1234
                            INSTANCE?.usuarioDao()?.insert(Usuario("demo", SecurityUtils.hashPassword("1234")))
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
