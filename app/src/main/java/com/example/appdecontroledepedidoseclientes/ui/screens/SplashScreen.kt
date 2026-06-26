package com.example.appdecontroledepedidoseclientes.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp
import com.example.appdecontroledepedidoseclientes.ui.components.VascoBadge
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appdecontroledepedidoseclientes.R
import kotlinx.coroutines.delay

/**
 * EXPLICACAO PARA APRESENTACAO:
 * Esta e a Tela de Abertura (Splash Screen). 
 * Ela aparece assim que o app abre, mostra a logomarca com uma animacao e depois vai para o Login.
 */
@Composable
fun SplashScreen(navController: NavController) {
    // Variavel que controla o tamanho (escala) para fazer o efeito de "pulso" na abertura
    val scale = remember { Animatable(0f) }

    // LaunchedEffect roda uma vez quando a tela abre
    LaunchedEffect(Unit) {
        // Faz a animacao de crescer o logo (de 0 para 1) com um efeito de mola (spring)
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        // Espera 2 segundos para o usuario ver a marca
        delay(2000L)
        // Navega para a tela de login e remove a Splash do historico
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // Fundo com degrade usando a cor primaria do tema
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value) // Aplica o valor da animacao aqui
        ) {
            // Composable reutilizável do escudo/bandeira do Vasco
            VascoBadge(badgeSize = 120.dp)
            Spacer(modifier = Modifier.height(24.dp))
            // Nome do aplicativo
            Text(
                text = stringResource(R.string.splash_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
            // Slogan ou subtitulo
            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }
    }
}
