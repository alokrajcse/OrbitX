package com.example.orbitx.views

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.border(underline: Boolean = false, color: Color = Color.Black): Modifier {
    return if (underline) {
        this.drawBehind {
            val strokeWidth = 1.dp.toPx()
            drawLine(
                color = color,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = strokeWidth
            )
        }
    } else {
        this
    }
}

@Composable
fun Modifier.backgroundTransparent(): Modifier {
    return this.background(SolidColor(Color.Transparent))
}
