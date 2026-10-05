package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryBlue

@Composable
fun UpiQrView(
    upiId: String,
    payeeName: String,
    amount: Double,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .testTag("upi_qr_container")
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.5.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Top QR header with UPI branding badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(PrimaryBlue, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "BHIM UPI",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Pay using any UPI App",
                fontSize = 12.sp,
                color = Color(0xFF475569),
                fontWeight = FontWeight.Medium
            )
        }

        // Custom high-fidelity QR Code representation
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(190.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val darkColor = Color(0xFF0F172A)
                val primaryColor = Color(0xFF1E3A8A)

                // 3 Finder Patterns at corners (Top-Left, Top-Right, Bottom-Left)
                val finderSize = canvasWidth * 0.24f
                val cornerRadius = CornerRadius(6f, 6f)

                // Finder 1: Top-Left
                drawRoundRect(
                    color = darkColor,
                    topLeft = Offset(0f, 0f),
                    size = Size(finderSize, finderSize),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = finderSize * 0.22f)
                )
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(finderSize * 0.3f, finderSize * 0.3f),
                    size = Size(finderSize * 0.4f, finderSize * 0.4f),
                    cornerRadius = CornerRadius(3f, 3f)
                )

                // Finder 2: Top-Right
                drawRoundRect(
                    color = darkColor,
                    topLeft = Offset(canvasWidth - finderSize, 0f),
                    size = Size(finderSize, finderSize),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = finderSize * 0.22f)
                )
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(canvasWidth - finderSize * 0.7f, finderSize * 0.3f),
                    size = Size(finderSize * 0.4f, finderSize * 0.4f),
                    cornerRadius = CornerRadius(3f, 3f)
                )

                // Finder 3: Bottom-Left
                drawRoundRect(
                    color = darkColor,
                    topLeft = Offset(0f, canvasHeight - finderSize),
                    size = Size(finderSize, finderSize),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = finderSize * 0.22f)
                )
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(finderSize * 0.3f, canvasHeight - finderSize * 0.7f),
                    size = Size(finderSize * 0.4f, finderSize * 0.4f),
                    cornerRadius = CornerRadius(3f, 3f)
                )

                // Synthetic authentic QR module pattern matrix (deterministic visual hash)
                val gridSize = 19
                val cellSize = canvasWidth / gridSize
                val seed = (upiId.hashCode() + amount.toInt()).toLong()

                for (r in 0 until gridSize) {
                    for (c in 0 until gridSize) {
                        // Skip finder patterns corners
                        if ((r < 6 && c < 6) || (r < 6 && c >= gridSize - 6) || (r >= gridSize - 6 && c < 6)) {
                            continue
                        }
                        // Skip center logo zone
                        if (r in 7..11 && c in 7..11) {
                            continue
                        }

                        val isFilled = ((r * 31 + c * 17 + seed) % 5L) in listOf(0L, 2L, 3L)
                        if (isFilled) {
                            val dotOffset = Offset(c * cellSize + cellSize * 0.1f, r * cellSize + cellSize * 0.1f)
                            drawRoundRect(
                                color = if ((r + c) % 4 == 0) primaryColor else darkColor,
                                topLeft = dotOffset,
                                size = Size(cellSize * 0.85f, cellSize * 0.85f),
                                cornerRadius = CornerRadius(2f, 2f)
                            )
                        }
                    }
                }
            }

            // Central Indian Rupee Badge inside QR
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(2.dp, PrimaryBlue, RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "₹",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Payee info
        Text(
            text = payeeName,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = upiId,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "₹${"%.2f".format(amount)}",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = Color(0xFF16A34A)
        )
    }
}
