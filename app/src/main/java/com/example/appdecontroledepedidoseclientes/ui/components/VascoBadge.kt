package com.example.appdecontroledepedidoseclientes.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.appdecontroledepedidoseclientes.R

@Composable
fun VascoBadge(badgeSize: Dp = 120.dp, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(badgeSize), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.escudo_vasco),
            contentDescription = "Escudo do Vasco",
            modifier = Modifier.size(badgeSize)
        )
    }
}
