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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PdfEntity
import com.example.data.model.PurchaseEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfStoreScreen(
    mainViewModel: MainViewModel,
    mainUiState: MainUiState,
    currentUser: UserEntity?,
    onOpenPdfReader: (PdfEntity) -> Unit,
    onBuyPdf: (PdfEntity) -> Unit
) {
    val categories by mainViewModel.categories.collectAsState()
    val allPdfs by mainViewModel.allPdfs.collectAsState()

    var filterType by remember { mutableStateOf("ALL") } // "ALL", "FREE", "PAID"

    val userPurchases by if (currentUser != null) {
        mainViewModel.getUserPurchases(currentUser.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<PurchaseEntity>()) }
    }

    val isUserPro = currentUser?.subscriptionPlan in listOf("Pro", "Pro Max")

    val filteredPdfs = remember(allPdfs, mainUiState.selectedCategoryId, filterType) {
        allPdfs.filter { pdf ->
            val matchCat = mainUiState.selectedCategoryId == "all" || pdf.categoryId == mainUiState.selectedCategoryId
            val matchType = when (filterType) {
                "FREE" -> pdf.isFree
                "PAID" -> !pdf.isFree
                else -> true
            }
            matchCat && matchType
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PDF Store (ई-बुक्स व नोट्स)", fontWeight = FontWeight.Bold) },
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
            // Category selector chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = mainUiState.selectedCategoryId == "all",
                        onClick = { mainViewModel.selectCategory("all") },
                        label = { Text("All Categories") },
                        modifier = Modifier.testTag("pdf_cat_all")
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = mainUiState.selectedCategoryId == cat.id,
                        onClick = { mainViewModel.selectCategory(cat.id) },
                        label = { Text(cat.name) },
                        modifier = Modifier.testTag("pdf_cat_${cat.id}")
                    )
                }
            }

            // Free vs Paid filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL" to "सभी PDFs", "FREE" to "Free Notes", "PAID" to "Premium PDFs").forEach { (type, label) ->
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

            if (filteredPdfs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "इस श्रेणी में कोई PDF उपलब्ध नहीं है",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredPdfs) { pdf ->
                        val isPurchased = userPurchases.any { it.productId == pdf.id }
                        val canRead = pdf.isFree || isPurchased || isUserPro

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pdf_item_${pdf.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFEF2F2))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PictureAsPdf,
                                            contentDescription = null,
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            color = PrimaryBlueContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "${pdf.examName} • ${pdf.subject}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryBlue,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = pdf.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Text(
                                                text = "${pdf.pageCount} पृष्ठ (Pages)",
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                            if (pdf.downloadAllowed) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Default.DownloadDone,
                                                        contentDescription = null,
                                                        tint = Color(0xFF16A34A),
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text("डाउनलोड समर्थित", fontSize = 11.sp, color = Color(0xFF16A34A))
                                                }
                                            }
                                        }
                                    }
                                }

                                if (pdf.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = pdf.description,
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (pdf.isFree) {
                                        Text(
                                            text = "FREE",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF16A34A)
                                        )
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "₹${pdf.price.toInt()}",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 17.sp,
                                                color = PrimaryBlue
                                            )
                                            if (canRead) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = Color(0xFFDCFCE7),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "UNLOCKED",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF15803D),
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (canRead) {
                                        Button(
                                            onClick = { onOpenPdfReader(pdf) },
                                            modifier = Modifier.height(40.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                        ) {
                                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("ऑनलाइन पढ़ें", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    } else {
                                        Button(
                                            onClick = { onBuyPdf(pdf) },
                                            modifier = Modifier.height(40.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
                                        ) {
                                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("अभी खरीदें (Buy @ ₹${pdf.price.toInt()})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
}
