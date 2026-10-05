package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PdfEntity
import com.example.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfReaderScreen(
    pdf: PdfEntity,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var isNightMode by remember { mutableStateOf(false) }
    var fontScale by remember { mutableStateOf(15) } // fontSize in sp

    val bgColor = if (isNightMode) Color(0xFF1E293B) else Color(0xFFFFFDF9) // Warm paper-like white
    val textColor = if (isNightMode) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val accentColor = if (isNightMode) Color(0xFF93C5FD) else PrimaryBlue

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = pdf.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            color = Color.White
                        )
                        Text(
                            text = "${pdf.subject} • ${pdf.pageCount} Pages",
                            fontSize = 11.sp,
                            color = Color(0xFFDBEAFE)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("pdf_reader_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Font decrease
                    IconButton(onClick = { if (fontScale > 12) fontScale -= 1 }) {
                        Text("A-", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    // Font increase
                    IconButton(onClick = { if (fontScale < 24) fontScale += 1 }) {
                        Text("A+", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    // Night mode toggle
                    IconButton(onClick = { isNightMode = !isNightMode }) {
                        Icon(
                            imageVector = if (isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme",
                            tint = Color.White
                        )
                    }
                    // Download action
                    IconButton(
                        onClick = {
                            if (pdf.downloadAllowed) {
                                Toast.makeText(context, "नोट्स ऑफलाइन डाउनलोड हो गया! (Saved offline)", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "इस PDF को केवल ऐप में ऑनलाइन पढ़ने की अनुमति है।", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(bgColor)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // PDF Document Info Box
            Surface(
                color = if (isNightMode) Color(0xFF334155) else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = pdf.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = textColor
                        )
                        Text(
                            text = "परीक्षा: ${pdf.examName} | विषय: ${pdf.subject}",
                            fontSize = 11.sp,
                            color = if (isNightMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }
            }

            // Document Content Renderer
            val contentLines = pdf.content.lines()
            contentLines.forEach { line ->
                val trimmed = line.trim()
                when {
                    trimmed.startsWith("# ") -> {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = trimmed.removePrefix("# ").trim(),
                            fontSize = (fontScale + 6).sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        HorizontalDivider(color = accentColor.copy(alpha = 0.3f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    trimmed.startsWith("## ") -> {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = trimmed.removePrefix("## ").trim(),
                            fontSize = (fontScale + 3).sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNightMode) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    trimmed.startsWith("### ") -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = trimmed.removePrefix("### ").trim(),
                            fontSize = (fontScale + 1).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isNightMode) Color(0xFFCBD5E1) else Color(0xFF334155),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                    trimmed.startsWith("---") -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = if (isNightMode) Color(0xFF475569) else Color(0xFFE2E8F0), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    else -> {
                        if (trimmed.isNotBlank()) {
                            Text(
                                text = trimmed,
                                fontSize = fontScale.sp,
                                lineHeight = (fontScale * 1.5).sp,
                                color = textColor,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
