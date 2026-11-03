package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage

@Composable
fun PriceChartCanvas(
    priceHistory: List<Int>,
    cropName: String,
    unit: String,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    if (priceHistory.isEmpty()) return

    val daysLabels = if (language == AppLanguage.TAMIL) {
        listOf("7 நாளுக்கு முன்", "6 நா", "5 நா", "4 நா", "3 நா", "நேற்று", "இன்று")
    } else {
        listOf("Day 1", "Day 2", "Day 3", "Day 4", "Day 5", "Yesterday", "Today")
    }

    var selectedIndex by remember(priceHistory) { mutableIntStateOf(priceHistory.lastIndex) }
    val currentPrice = priceHistory[selectedIndex]

    val minPrice = priceHistory.minOrNull() ?: 0
    val maxPrice = priceHistory.maxOrNull() ?: (minPrice + 10)
    val priceRange = if (maxPrice == minPrice) 1f else (maxPrice - minPrice).toFloat()

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = cropName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${if (language == AppLanguage.TAMIL) "தேர்ந்தெடுக்கப்பட்ட நாள்: " else "Selected Date: "}${daysLabels.getOrElse(selectedIndex) { "Today" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹$currentPrice",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Canvas Chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .pointerInput(priceHistory) {
                    detectTapGestures { offset ->
                        val stepX = size.width / (priceHistory.size - 1).coerceAtLeast(1)
                        val index = (offset.x / stepX).toInt().coerceIn(0, priceHistory.lastIndex)
                        selectedIndex = index
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val paddingBottom = 24.dp.toPx()
            val chartHeight = height - paddingBottom

            // Draw horizontal reference lines
            val lineCount = 3
            for (i in 0..lineCount) {
                val y = chartHeight * (i.toFloat() / lineCount)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Compute points
            val stepX = width / (priceHistory.size - 1).coerceAtLeast(1)
            val points = priceHistory.mapIndexed { index, price ->
                val x = index * stepX
                val normalizedY = (price - minPrice) / priceRange
                val y = chartHeight - (normalizedY * (chartHeight * 0.8f)) - (chartHeight * 0.1f)
                Offset(x, y)
            }

            // Gradient Fill Path
            val fillPath = Path().apply {
                moveTo(points.first().x, chartHeight)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, chartHeight)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.35f),
                        primaryColor.copy(alpha = 0.05f)
                    ),
                    startY = 0f,
                    endY = chartHeight
                )
            )

            // Line Stroke Path
            val strokePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val p0 = points[i - 1]
                    val p1 = points[i]
                    val midX = (p0.x + p1.x) / 2
                    cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
                }
            }

            drawPath(
                path = strokePath,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw data points
            points.forEachIndexed { idx, point ->
                val isSelected = idx == selectedIndex
                drawCircle(
                    color = if (isSelected) secondaryColor else primaryColor,
                    radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                    center = point
                )
                if (isSelected) {
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = point
                    )
                }
            }
        }

        // X-Axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysLabels.forEachIndexed { index, label ->
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = if (index == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
