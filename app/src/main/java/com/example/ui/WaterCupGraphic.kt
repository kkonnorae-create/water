package com.example.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterCardBorder
import com.example.ui.theme.WaterFillDeep
import com.example.ui.theme.WaterFillLight

@Composable
fun WaterCupGraphic(
  waterCups: Int,
  goalCups: Int,
  modifier: Modifier = Modifier,
) {
  val targetFraction = (waterCups.toFloat() / goalCups.coerceAtLeast(1)).coerceIn(0f, 1f)
  val animatedFraction by
    animateFloatAsState(
      targetValue = targetFraction,
      animationSpec =
        spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
      label = "waterFillFraction",
    )

  Canvas(
    modifier =
      modifier.size(width = 88.dp, height = 104.dp).semantics {
        contentDescription = "물컵 아이콘, 현재 ${waterCups}잔 마심"
      }
  ) {
    val w = size.width
    val h = size.height
    val strokeWidth = 4.dp.toPx()

    // Tapered glass polygon coordinates
    val topLeftX = w * 0.14f
    val topRightX = w * 0.86f
    val bottomLeftX = w * 0.24f
    val bottomRightX = w * 0.76f
    val topY = h * 0.12f
    val bottomY = h * 0.90f

    val glassInteriorPath =
      Path().apply {
        moveTo(topLeftX, topY)
        lineTo(topRightX, topY)
        lineTo(bottomRightX, bottomY)
        lineTo(bottomLeftX, bottomY)
        close()
      }

    // Background of empty glass
    drawPath(path = glassInteriorPath, color = Color.White.copy(alpha = 0.85f))

    // Animated water fill clipped inside the glass silhouette
    if (animatedFraction > 0.01f) {
      clipPath(path = glassInteriorPath) {
        val innerHeight = bottomY - topY
        // Leave slight headroom at top even when full so rim remains crisp
        val fillHeight = innerHeight * (0.06f + animatedFraction * 0.86f)
        val waterTopY = bottomY - fillHeight

        val waterPath =
          Path().apply {
            moveTo(0f, waterTopY)
            quadraticTo(w * 0.25f, waterTopY - 4.dp.toPx(), w * 0.5f, waterTopY)
            quadraticTo(w * 0.75f, waterTopY + 4.dp.toPx(), w, waterTopY)
            lineTo(w, bottomY + 4.dp.toPx())
            lineTo(0f, bottomY + 4.dp.toPx())
            close()
          }

        drawPath(
          path = waterPath,
          brush =
            Brush.verticalGradient(
              colors = listOf(WaterFillLight, WaterFillDeep),
              startY = waterTopY,
              endY = bottomY,
            ),
        )

        // Gentle highlight meniscus line
        drawLine(
          color = Color.White.copy(alpha = 0.55f),
          start = Offset(w * 0.28f, waterTopY + 5.dp.toPx()),
          end = Offset(w * 0.62f, waterTopY + 5.dp.toPx()),
          strokeWidth = 3.dp.toPx(),
          cap = StrokeCap.Round,
        )

        // Small cheerful bubbles when water is present
        if (animatedFraction > 0.2f) {
          drawCircle(
            color = Color.White.copy(alpha = 0.45f),
            radius = 3.5.dp.toPx(),
            center = Offset(w * 0.38f, bottomY - fillHeight * 0.35f),
          )
          drawCircle(
            color = Color.White.copy(alpha = 0.35f),
            radius = 2.5.dp.toPx(),
            center = Offset(w * 0.60f, bottomY - fillHeight * 0.55f),
          )
        }
      }
    }

    // Glass outer border
    drawPath(
      path = glassInteriorPath,
      color = if (waterCups > 0) WaterBlue else WaterCardBorder,
      style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )

    // Top rim accent
    drawRoundRect(
      color = if (waterCups > 0) WaterBlue else WaterCardBorder,
      topLeft = Offset(topLeftX - 4.dp.toPx(), topY - 3.dp.toPx()),
      size = Size((topRightX - topLeftX) + 8.dp.toPx(), 6.dp.toPx()),
      cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
    )
  }
}
