package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    mainUiState: MainUiState,
    currentUser: UserEntity?,
    onNavigateToTests: (String?) -> Unit,
    onNavigateToPdfs: (String?) -> Unit,
    onNavigateToSubscriptions: () -> Unit,
    onStartTest: (TestEntity) -> Unit,
    onBuyTest: (TestEntity) -> Unit,
    onOpenPdf: (PdfEntity) -> Unit,
    onBuyPdf: (PdfEntity) -> Unit
) {
    val categories by mainViewModel.categories.collectAsState()
    val allTests by mainViewModel.publishedTests.collectAsState()
    val allPdfs by mainViewModel.allPdfs.collectAsState()
    val banners by mainViewModel.homeBanners.collectAsState()
    val settings by mainViewModel.appSettings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val userPurchases by if (currentUser != null) {
        mainViewModel.getUserPurchases(currentUser.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<PurchaseEntity>()) }
    }

    val isUserPro = currentUser?.subscriptionPlan in listOf("Pro", "Pro Max")

    // Filter tests and pdfs based on search and category
    val filteredTests = remember(allTests, searchQuery, mainUiState.selectedCategoryId) {
        allTests.filter { test ->
            val matchCat = mainUiState.selectedCategoryId == "all" || test.categoryId == mainUiState.selectedCategoryId
            val matchSearch = searchQuery.isBlank() || test.title.contains(searchQuery, ignoreCase = true) ||
                    test.examName.contains(searchQuery, ignoreCase = true) ||
                    test.subject.contains(searchQuery, ignoreCase = true)
            matchCat && matchSearch
        }
    }

    val filteredPdfs = remember(allPdfs, searchQuery, mainUiState.selectedCategoryId) {
        allPdfs.filter { pdf ->
            val matchCat = mainUiState.selectedCategoryId == "all" || pdf.categoryId == mainUiState.selectedCategoryId
            val matchSearch = searchQuery.isBlank() || pdf.title.contains(searchQuery, ignoreCase = true) ||
                    pdf.examName.contains(searchQuery, ignoreCase = true) ||
                    pdf.subject.contains(searchQuery, ignoreCase = true)
            matchCat && matchSearch
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header row with App branding and user greeting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = settings?.appName ?: "Test App",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (currentUser != null) "नमस्ते, ${currentUser.name}" else "ऑनलाइन परीक्षा पोर्टल",
                            fontSize = 13.sp,
                            color = Color(0xFFDBEAFE)
                        )
                    }

                    // Subscription Badge or Plan Chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isUserPro) AccentAmber else Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.clickable { onNavigateToSubscriptions() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isUserPro) Icons.Default.WorkspacePremium else Icons.Default.Stars,
                                contentDescription = null,
                                tint = if (isUserPro) Color.Black else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isUserPro) currentUser?.subscriptionPlan ?: "PRO" else "Go Pro",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUserPro) Color.Black else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Tests, PDFs, Exams (CTET, SSC...)", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryBlue) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("home_search_input")
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            // Notice banner ticker
            if (settings?.noticeText?.isNotBlank() == true) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFEF3C7))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = settings?.noticeText ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF92400E),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Promotional Banners Carousel
            if (banners.isNotEmpty()) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(banners) { banner ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .width(290.dp)
                                    .clickable {
                                        if (banner.actionType == "CATEGORY") {
                                            mainViewModel.selectCategory(banner.actionTarget)
                                            onNavigateToTests(banner.actionTarget)
                                        } else if (banner.actionType == "SUBSCRIPTION") {
                                            onNavigateToSubscriptions()
                                        }
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(PrimaryBlue, Color(0xFF1E40AF))
                                            )
                                        )
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Surface(
                                            color = AccentAmber,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = banner.tag,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = banner.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = banner.subtitle,
                                            fontSize = 12.sp,
                                            color = Color(0xFFDBEAFE),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Categories Selector
            item {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "परीक्षा श्रेणियां (Exam Categories)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = mainUiState.selectedCategoryId == "all",
                                onClick = { mainViewModel.selectCategory("all") },
                                label = { Text("All Exams") },
                                leadingIcon = { Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.testTag("cat_chip_all")
                            )
                        }
                        items(categories) { cat ->
                            FilterChip(
                                selected = mainUiState.selectedCategoryId == cat.id,
                                onClick = { mainViewModel.selectCategory(cat.id) },
                                label = { Text(cat.name) },
                                modifier = Modifier.testTag("cat_chip_${cat.id}")
                            )
                        }
                    }
                }
            }

            // Quick Filter Tabs (Free Tests, Paid Tests, Test Series, PDFs, Previous Year)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf(
                        "ALL" to "सभी (All)",
                        "FREE" to "Free Tests",
                        "PAID" to "Paid Tests",
                        "PDF" to "PDFs / Notes",
                        "SERIES" to "Test Series"
                    )
                    tabs.forEach { (key, label) ->
                        AssistChip(
                            onClick = { mainViewModel.setFilterType(key) },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (mainUiState.filterType == key) FontWeight.Bold else FontWeight.Normal,
                                    color = if (mainUiState.filterType == key) PrimaryBlue else Color(0xFF475569)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (mainUiState.filterType == key) PrimaryBlueContainer else Color.White
                            )
                        )
                    }
                }
            }

            // Featured Tests Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "लोकप्रिय टेस्ट (Featured Tests)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { onNavigateToTests(null) }) {
                        Text("सभी देखें (View All)", fontSize = 13.sp, color = PrimaryBlue)
                    }
                }
            }

            if (filteredTests.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "इस श्रेणी में कोई टेस्ट नहीं मिला (No tests found)",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            } else {
                items(filteredTests.take(4)) { test ->
                    val isPurchased = userPurchases.any { it.productId == test.id }
                    val canAttempt = test.isFree || isPurchased || isUserPro

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("test_card_${test.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                            text = test.examName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = test.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Price tag or FREE badge
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

                            Spacer(modifier = Modifier.height(10.dp))

                            // Test metadata icons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${test.totalQuestions} प्रश्न", fontSize = 12.sp, color = Color(0xFF64748B))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${test.timeLimitMinutes} मिनट", fontSize = 12.sp, color = Color(0xFF64748B))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Grade, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${test.totalMarks.toInt()} अंक", fontSize = 12.sp, color = Color(0xFF64748B))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action Button
                            if (canAttempt) {
                                Button(
                                    onClick = { onStartTest(test) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp)
                                        .testTag("start_test_btn_${test.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("टेस्ट शुरू करें (Start Test)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            } else {
                                Button(
                                    onClick = { onBuyTest(test) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp)
                                        .testTag("buy_test_btn_${test.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("अनलॉक करें (Buy @ ₹${test.price.toInt()})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // PDF Notes Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "स्टडी मैटेरियल व पीडीएफ (PDF Notes)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { onNavigateToPdfs(null) }) {
                        Text("PDF Store देखें", fontSize = 13.sp, color = PrimaryBlue)
                    }
                }
            }

            items(filteredPdfs.take(3)) { pdf ->
                val isPurchased = userPurchases.any { it.productId == pdf.id }
                val canRead = pdf.isFree || isPurchased || isUserPro

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("pdf_card_${pdf.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // PDF icon / thumbnail
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFEF2F2))
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pdf.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${pdf.subject} • ${pdf.pageCount} Pages",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (pdf.isFree) "FREE" else if (canRead) "UNLOCKED" else "₹${pdf.price.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (canRead) Color(0xFF15803D) else Color(0xFFB45309)
                            )
                        }

                        Button(
                            onClick = {
                                if (canRead) onOpenPdf(pdf) else onBuyPdf(pdf)
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canRead) PrimaryBlue else Color(0xFF0D9488)
                            ),
                            modifier = Modifier.testTag("pdf_action_btn_${pdf.id}")
                        ) {
                            Text(if (canRead) "पढ़ें" else "खरीदें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Pro Membership Card Banner
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { onNavigateToSubscriptions() }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(46.dp)
                                .background(AccentAmber, RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.Black)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pro Max Subscription",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Text(
                                text = "सभी टेस्ट सीरीज एवं स्टडी पीडीएफ अनलॉक करें",
                                fontSize = 12.sp,
                                color = Color(0xFFC7D2FE)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
