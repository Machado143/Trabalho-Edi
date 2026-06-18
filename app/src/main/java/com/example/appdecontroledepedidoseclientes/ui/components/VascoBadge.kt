package com.example.appdecontroledepedidoseclientes.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VascoBadge(badgeSize: Dp = 120.dp, vTextSize: TextUnit = 52.sp, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(badgeSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawVascoFlag(size)
        }
        Text(
            text = "V",
            color = Color(0xFF1A1A1A),
            fontSize = vTextSize,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Serif
        )
    }
}

private fun DrawScope.drawVascoFlag(canvasSize: Size) {
    val w = canvasSize.width
    val h = canvasSize.height

    // Fundo preto
    drawRect(color = Color(0xFF1A1A1A))

    // Diagonal branca (faixa diagonal característica do uniforme do Vasco)
    val path = Path().apply {
        moveTo(0f, h * 0.35f)
        lineTo(w * 0.65f, 0f)
        lineTo(w, 0f)
        lineTo(w, h * 0.65f)
        lineTo(w * 0.35f, h)
        lineTo(0f, h)
        close()
    }
    drawPath(path = path, color = Color.White)
}

