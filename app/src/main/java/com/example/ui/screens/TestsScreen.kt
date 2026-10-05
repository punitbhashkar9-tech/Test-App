package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PurchaseEntity
import com.example.data.model.TestEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestsScreen(
    mainViewModel: MainViewModel,
    mainUiState: MainUiState,
    currentUser: UserEntity?,
    onStartTest: (TestEntity) -> Unit,
    onBuyTest: (TestEntity) -> Unit
) {
    val categories by mainViewModel.categories.collectAsState()
    val allTests by mainViewModel.publishedTests.collectAsState()

    var filterType by remember { mutableStateOf("ALL") } // "ALL", "FREE", "PAID"
    var selectedTestForDetails by remember { mutableStateOf<TestEntity?>(null) }

    val userPurchases by if (currentUser != null) {
        mainViewModel.getUserPurchases(currentUser.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<PurchaseEntity>()) }
    }

    val isUserPro = currentUser?.subscriptionPlan in listOf("Pro", "Pro Max")

    val filteredTests = remember(allTests, mainUiState.selectedCategoryId, filterType) {
        allTests.filter { test ->
            val matchCat = mainUiState.selectedCategoryId == "all" || test.categoryId == mainUiState.selectedCategoryId
            val matchType = when (filterType) {
                "FREE" -> test.isFree
                "PAID" -> !test.isFree
                else -> true
            }
            matchCat && matchType
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ऑनलाइन टेस्ट सीरीज (Tests)", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            // Category horizontal scroll chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = mainUiState.selectedCategoryId == "all",
                        onClick = { mainViewModel.selectCategory("all") },
                        label = { Text("All Exams") },
                        modifier = Modifier.testTag("tests_cat_chip_all")
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = mainUiState.selectedCategoryId == cat.id,
                        onClick = { mainViewModel.selectCategory(cat.id) },
                        label = { Text(cat.name) },
                        modifier = Modifier.testTag("tests_cat_chip_${cat.id}")
                    )
                }
            }

            // Free vs Paid Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filterOptions = listOf(
                    "ALL" to "सभी टेस्ट (${allTests.size})",
                    "FREE" to "Free Tests",
                    "PAID" to "Paid / Premium"
                )
                filterOptions.forEach { (type, label) ->
                    AssistChip(
                        onClick = { filterType = type },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (filterType == type) FontWeight.Bold else FontWeight.Normal,
                                color = if (filterType == type) PrimaryBlue else Color(0xFF475569)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (filterType == type) PrimaryBlueContainer else Color.White
                        )
                    )
                }
            }

            // Tests List
            if (filteredTests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "इस श्रेणी में कोई टेस्ट नहीं मिला",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTests) { test ->
                        val isPurchased = userPurchases.any { it.productId == test.id }
                        val canAttempt = test.isFree || isPurchased || isUserPro

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_item_${test.id}")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            color = PrimaryBlueContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "${test.examName} • ${test.subject}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryBlue,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = test.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Badge
                                    if (test.isFree) {
                                        Surface(
                                            color = Color(0xFFDCFCE7),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "FREE",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF15803D),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            color = Color(0xFFFEF3C7),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = if (isPurchased || isUserPro) "UNLOCKED" else "₹${test.price.toInt()}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (isPurchased || isUserPro) Color(0xFF15803D) else Color(0xFFB45309),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                if (test.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = test.description,
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Specifications row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Questions", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("${test.totalQuestions}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Total Marks", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("${test.totalMarks.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Time", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("${test.timeLimitMinutes} min", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Negative", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text(if (test.negativeMarking > 0) "-${test.negativeMarking}" else "None", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { selectedTestForDetails = test },
                                        modifier = Modifier.weight(1f).height(42.dp)
                                    ) {
                                        Text("दिशानिर्देश (Info)", fontSize = 12.sp)
                                    }

                                    if (canAttempt) {
                                        Button(
                                            onClick = { onStartTest(test) },
                                            modifier = Modifier.weight(1.5f).height(42.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Start Test", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    } else {
                                        Button(
                                            onClick = { onBuyTest(test) },
                                            modifier = Modifier.weight(1.5f).height(42.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
                                        ) {
                                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Buy @ ₹${test.price.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Info & Syllabus Guidelines Dialog
    selectedTestForDetails?.let { test ->
        AlertDialog(
            onDismissRequest = { selectedTestForDetails = null },
            title = {
                Text(test.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    Text("• परीक्षा का नाम: ${test.examName}", fontSize = 13.sp)
                    Text("• विषय: ${test.subject}", fontSize = 13.sp)
                    Text("• कुल प्रश्न: ${test.totalQuestions}", fontSize = 13.sp)
                    Text("• कुल अंक: ${test.totalMarks}", fontSize = 13.sp)
                    Text("• समय सीमा: ${test.timeLimitMinutes} मिनट", fontSize = 13.sp)
                    Text("• नकारात्मक अंकन: ${if (test.negativeMarking > 0) "-${test.negativeMarking} अंक प्रति गलत उत्तर" else "कोई नकारात्मक अंकन नहीं"}", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "निर्देश: समय पूरा होते ही टेस्ट अपने आप सबमिट हो जाएगा। आप कभी भी Question Palette से किसी भी प्रश्न पर जा सकते हैं।",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val testToStart = test
                        selectedTestForDetails = null
                        val isPurchased = userPurchases.any { it.productId == testToStart.id }
                        if (testToStart.isFree || isPurchased || isUserPro) {
                            onStartTest(testToStart)
                        } else {
                            onBuyTest(testToStart)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("आगे बढ़ें")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTestForDetails = null }) {
                    Text("बंद करें")
                }
            }
        )
    }
}
