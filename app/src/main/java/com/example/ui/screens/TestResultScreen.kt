package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuestionEntity
import com.example.data.model.TestAttemptEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestResultScreen(
    attempt: TestAttemptEntity,
    questions: List<QuestionEntity>,
    onBackToHome: () -> Unit,
    onViewHistory: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "CORRECT", "WRONG", "SKIPPED"

    // Parse user answers from json string
    val userAnswers = remember(attempt.userAnswersJson) {
        val map = mutableMapOf<String, String>()
        val cleaned = attempt.userAnswersJson.trim().removeSurrounding("{", "}")
        if (cleaned.isNotBlank()) {
            cleaned.split(",").forEach { pair ->
                val parts = pair.split(":")
                if (parts.size == 2) {
                    val k = parts[0].trim().replace("\"", "")
                    val v = parts[1].trim().replace("\"", "")
                    map[k] = v
                }
            }
        }
        map
    }

    val filteredQuestions = remember(questions, selectedFilter, userAnswers) {
        when (selectedFilter) {
            "CORRECT" -> questions.filter { q -> userAnswers[q.id]?.equals(q.correctOption, ignoreCase = true) == true }
            "WRONG" -> questions.filter { q ->
                val ans = userAnswers[q.id]
                ans != null && !ans.equals(q.correctOption, ignoreCase = true)
            }
            "SKIPPED" -> questions.filter { q -> !userAnswers.containsKey(q.id) }
            else -> questions
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("टेस्ट परिणाम (Test Result)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackToHome,
                        modifier = Modifier.testTag("result_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onViewHistory) {
                        Text("पुराने परिणाम", color = Color.White, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            // Summary Performance Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = attempt.testTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Score & Percentage Display
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${"%.1f".format(attempt.score)} / ${attempt.totalMarks.toInt()}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (attempt.score >= attempt.totalMarks / 2) Color(0xFF16A34A) else Color(0xFFDC2626)
                                )
                                Text("कुल प्राप्तांक (Score)", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                            Spacer(modifier = Modifier.width(32.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${"%.1f".format(attempt.percentage)}%",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryBlue
                                )
                                Text("प्रतिशत (Percentage)", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4 Metrics Grid: Attempted, Correct, Wrong, Time
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            MetricColumn(title = "प्रयास (Attempted)", value = "${attempt.attempted}", color = PrimaryBlue)
                            MetricColumn(title = "सही (Correct)", value = "${attempt.correct}", color = Color(0xFF16A34A))
                            MetricColumn(title = "गलत (Wrong)", value = "${attempt.wrong}", color = Color(0xFFDC2626))
                            val minTaken = attempt.timeTakenSeconds / 60
                            val secTaken = attempt.timeTakenSeconds % 60
                            MetricColumn(title = "समय (Time)", value = "${minTaken}m ${secTaken}s", color = Color(0xFF0D9488))
                        }
                    }
                }
            }

            // Solutions & Review Section Header
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(
                        text = "प्रश्नोत्तर व विस्तृत व्याख्या (Detailed Solutions)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Review filter tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AssistChip(
                            onClick = { selectedFilter = "ALL" },
                            label = { Text("सभी (${questions.size})", fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedFilter == "ALL") PrimaryBlueContainer else Color.White
                            )
                        )
                        AssistChip(
                            onClick = { selectedFilter = "CORRECT" },
                            label = { Text("सही (${attempt.correct})", fontSize = 11.sp, color = Color(0xFF16A34A)) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedFilter == "CORRECT") Color(0xFFDCFCE7) else Color.White
                            )
                        )
                        AssistChip(
                            onClick = { selectedFilter = "WRONG" },
                            label = { Text("गलत (${attempt.wrong})", fontSize = 11.sp, color = Color(0xFFDC2626)) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedFilter == "WRONG") Color(0xFFFEE2E2) else Color.White
                            )
                        )
                        AssistChip(
                            onClick = { selectedFilter = "SKIPPED" },
                            label = { Text("छोड़े गए (${attempt.unattempted})", fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedFilter == "SKIPPED") Color(0xFFF1F5F9) else Color.White
                            )
                        )
                    }
                }
            }

            // Question Solutions List
            itemsIndexed(filteredQuestions) { idx, q ->
                val userChoice = userAnswers[q.id]
                val isCorrect = userChoice?.equals(q.correctOption, ignoreCase = true) == true
                val isWrong = userChoice != null && !isCorrect
                val isSkipped = userChoice == null

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Q ${q.questionNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PrimaryBlue
                            )

                            // Status Chip
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when {
                                    isCorrect -> Color(0xFFDCFCE7)
                                    isWrong -> Color(0xFFFEE2E2)
                                    else -> Color(0xFFF1F5F9)
                                }
                            ) {
                                Text(
                                    text = when {
                                        isCorrect -> "✓ सही (Correct)"
                                        isWrong -> "✗ गलत (Incorrect)"
                                        else -> "अनुत्तरित (Skipped)"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isCorrect -> Color(0xFF15803D)
                                        isWrong -> Color(0xFFB91C1C)
                                        else -> Color(0xFF64748B)
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = q.questionText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Options comparison
                        val opts = listOf("A" to q.optionA, "B" to q.optionB, "C" to q.optionC, "D" to q.optionD)
                        opts.forEach { (key, text) ->
                            val isCorrectKey = key.equals(q.correctOption, ignoreCase = true)
                            val isUserSelectedKey = key.equals(userChoice, ignoreCase = true)

                            val optBg = when {
                                isCorrectKey -> Color(0xFFDCFCE7)
                                isUserSelectedKey && !isCorrectKey -> Color(0xFFFEE2E2)
                                else -> Color(0xFFF8FAFC)
                            }
                            val optBorder = when {
                                isCorrectKey -> Color(0xFF16A34A)
                                isUserSelectedKey && !isCorrectKey -> Color(0xFFDC2626)
                                else -> Color.Transparent
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .background(optBg, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$key.",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isCorrectKey) Color(0xFF15803D) else Color(0xFF334155)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = text,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isCorrectKey) {
                                    Text("✓ सही उत्तर", color = Color(0xFF15803D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                } else if (isUserSelectedKey) {
                                    Text("आपका चयन", color = Color(0xFFDC2626), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Detailed explanation
                        if (q.explanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "व्याख्या (Explanation):",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF166534)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = q.explanation,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        color = Color(0xFF14532D)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun MetricColumn(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = title, fontSize = 11.sp, color = Color(0xFF64748B))
    }
}
