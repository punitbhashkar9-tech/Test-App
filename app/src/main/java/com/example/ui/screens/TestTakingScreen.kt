package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveTestSession
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestTakingScreen(
    session: ActiveTestSession,
    mainViewModel: MainViewModel,
    userId: String,
    onFinishAndShowResult: () -> Unit,
    onExit: () -> Unit
) {
    var showPaletteSheet by remember { mutableStateOf(false) }
    var showSubmitConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(session.isFinished) {
        if (session.isFinished) {
            onFinishAndShowResult()
        }
    }

    // Intercept back button to confirm exit
    var showExitWarning by remember { mutableStateOf(false) }
    BackHandler {
        showExitWarning = true
    }

    val currentQ = session.questions.getOrNull(session.currentQuestionIndex)
    val selectedOption = currentQ?.let { session.selectedAnswers[it.id] }
    val isMarkedForReview = currentQ?.let { session.markedForReview.contains(it.id) } == true

    // Format remaining time MM:SS
    val minutes = session.remainingSeconds / 60
    val seconds = session.remainingSeconds % 60
    val timeFormatted = "%02d:%02d".format(minutes, seconds)
    val isTimerLow = session.remainingSeconds < 120

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = session.test.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = "Q ${session.currentQuestionIndex + 1} of ${session.questions.size}",
                            fontSize = 11.sp,
                            color = Color(0xFFDBEAFE)
                        )
                    }
                },
                actions = {
                    // Timer Chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isTimerLow) Color(0xFFDC2626) else Color(0xFF1E40AF),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timeFormatted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Question Palette Toggle Button
                    IconButton(
                        onClick = { showPaletteSheet = true },
                        modifier = Modifier.testTag("open_palette_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Question Palette",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Top actions: Clear Response & Mark for Review
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { mainViewModel.clearOption() },
                            enabled = selectedOption != null,
                            modifier = Modifier.testTag("clear_response_button")
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear Answer", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { mainViewModel.toggleMarkForReview() },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isMarkedForReview) ExamReviewPurple else Color(0xFF64748B)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("mark_review_button")
                        ) {
                            Icon(
                                imageVector = if (isMarkedForReview) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isMarkedForReview) "Marked" else "Mark for Review", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bottom actions: Previous, Next / Submit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { mainViewModel.prevQuestion() },
                            enabled = session.currentQuestionIndex > 0,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("prev_question_button")
                        ) {
                            Text("Previous", fontWeight = FontWeight.Bold)
                        }

                        if (session.currentQuestionIndex < session.questions.lastIndex) {
                            Button(
                                onClick = { mainViewModel.nextQuestion() },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(48.dp)
                                    .testTag("next_question_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Text("Save & Next", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        } else {
                            Button(
                                onClick = { showSubmitConfirmation = true },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(48.dp)
                                    .testTag("submit_test_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Submit Test", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (currentQ != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(BackgroundLight)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Question Header Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = PrimaryBlueContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Question ${session.currentQuestionIndex + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = PrimaryBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "+${currentQ.marks}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                if (currentQ.negativeMarks > 0) {
                                    Surface(
                                        color = Color(0xFFFEE2E2),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "-${currentQ.negativeMarks}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB91C1C),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Question text
                        Text(
                            text = currentQ.questionText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options List
                val options = listOf(
                    "A" to currentQ.optionA,
                    "B" to currentQ.optionB,
                    "C" to currentQ.optionC,
                    "D" to currentQ.optionD
                )

                options.forEach { (optionKey, optionText) ->
                    val isSelected = selectedOption == optionKey

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PrimaryBlueContainer else Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) PrimaryBlue else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable { mainViewModel.selectOption(optionKey) }
                            .testTag("option_${optionKey}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) PrimaryBlue else Color(0xFFF1F5F9))
                            ) {
                                Text(
                                    text = optionKey,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color.White else Color(0xFF475569)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = optionText,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Question Palette Bottom Sheet
    if (showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Question Palette (प्रश्नावली)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    LegendItem(color = Color(0xFF16A34A), label = "Answered")
                    LegendItem(color = ExamReviewPurple, label = "Review")
                    LegendItem(color = Color(0xFFEA580C), label = "Unanswered")
                    LegendItem(color = Color(0xFF94A3B8), label = "Not Visited")
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 280.dp)
                ) {
                    itemsIndexed(session.questions) { index, q ->
                        val isCurrent = session.currentQuestionIndex == index
                        val hasAnswer = session.selectedAnswers.containsKey(q.id)
                        val isReview = session.markedForReview.contains(q.id)
                        val isVisited = session.visitedQuestions.contains(q.id)

                        val bgColor = when {
                            hasAnswer && isReview -> ExamReviewPurple
                            hasAnswer -> Color(0xFF16A34A)
                            isReview -> ExamReviewPurple
                            isVisited -> Color(0xFFEA580C)
                            else -> Color(0xFF94A3B8)
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgColor)
                                .border(
                                    width = if (isCurrent) 2.5.dp else 0.dp,
                                    color = if (isCurrent) Color.Yellow else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    mainViewModel.jumpToQuestion(index)
                                    showPaletteSheet = false
                                }
                                .testTag("palette_q_${index + 1}")
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        showPaletteSheet = false
                        showSubmitConfirmation = true
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text("Submit Test Now", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Submit confirmation dialog
    if (showSubmitConfirmation) {
        val totalQ = session.questions.size
        val answeredCount = session.selectedAnswers.size
        val reviewCount = session.markedForReview.size
        val unattemptedCount = totalQ - answeredCount

        AlertDialog(
            onDismissRequest = { showSubmitConfirmation = false },
            title = { Text("टेस्ट सबमिट करें? (Submit Test)", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("• कुल प्रश्न (Total): $totalQ")
                    Text("• उत्तर दिए गए (Answered): $answeredCount", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                    Text("• अनुत्तरित (Unanswered): $unattemptedCount", color = Color(0xFFEA580C))
                    Text("• समीक्षा हेतु चिह्नित (Marked for Review): $reviewCount", color = ExamReviewPurple)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("क्या आप टेस्ट समाप्त करके अपना रिजल्ट देखना चाहते हैं?")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmation = false
                        mainViewModel.submitActiveTest(userId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    modifier = Modifier.testTag("confirm_submit_test_btn")
                ) {
                    Text("हाँ, सबमिट करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmation = false }) {
                    Text("जारी रखें (Continue Test)")
                }
            }
        )
    }

    // Exit warning dialog
    if (showExitWarning) {
        AlertDialog(
            onDismissRequest = { showExitWarning = false },
            title = { Text("टेस्ट छोड़ें? (Exit Test)", fontWeight = FontWeight.Bold) },
            text = {
                Text("यदि आप अभी बाहर जाते हैं तो आपका टेस्ट प्रगति खो जाएगी। क्या आप वाकई बाहर जाना चाहते हैं?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitWarning = false
                        onExit()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("बाहर जाएं (Exit)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitWarning = false }) {
                    Text("टेस्ट जारी रखें")
                }
            }
        )
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
    }
}
