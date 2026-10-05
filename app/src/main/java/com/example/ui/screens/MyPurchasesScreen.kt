package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentEntity
import com.example.data.model.PdfEntity
import com.example.data.model.PurchaseEntity
import com.example.data.model.TestEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPurchasesScreen(
    currentUser: UserEntity?,
    mainViewModel: MainViewModel,
    onStartTest: (TestEntity) -> Unit,
    onOpenPdf: (PdfEntity) -> Unit,
    onExploreTests: () -> Unit,
    onExplorePdfs: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Tests, 1: PDFs, 2: Payment Requests
    val tabs = listOf("Purchased Tests", "Purchased PDFs", "Payment History")

    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    val allTests by mainViewModel.publishedTests.collectAsState()
    val allPdfs by mainViewModel.allPdfs.collectAsState()

    val purchases by if (currentUser != null) {
        mainViewModel.getUserPurchases(currentUser.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<PurchaseEntity>()) }
    }

    val payments by if (currentUser != null) {
        mainViewModel.getUserPayments(currentUser.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<PaymentEntity>()) }
    }

    val testPurchases = remember(purchases) { purchases.filter { it.productType == "TEST" } }
    val pdfPurchases = remember(purchases) { purchases.filter { it.productType == "PDF" } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("मेरी खरीद (My Purchases)", fontWeight = FontWeight.Bold) },
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
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PrimaryBlue
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("purchases_tab_$index")
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Purchased Tests Tab
                    if (testPurchases.isEmpty()) {
                        EmptyPurchaseState(
                            message = "आपने अभी तक कोई टेस्ट नहीं खरीदा है",
                            buttonText = "मॉक टेस्ट देखें (Explore Tests)",
                            onAction = onExploreTests
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(testPurchases) { pur ->
                                val targetTest = allTests.find { it.id == pur.productId }

                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = pur.productName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Surface(
                                                color = Color(0xFFDCFCE7),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "ACTIVE",
                                                    color = Color(0xFF15803D),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "खरीद तिथि: ${dateFormat.format(Date(pur.purchaseDate))}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Button(
                                            onClick = {
                                                if (targetTest != null) onStartTest(targetTest)
                                            },
                                            modifier = Modifier.fillMaxWidth().height(42.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("टेस्ट शुरू करें (Start Test)", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Purchased PDFs Tab
                    if (pdfPurchases.isEmpty()) {
                        EmptyPurchaseState(
                            message = "आपने अभी तक कोई PDF नोट्स नहीं खरीदे हैं",
                            buttonText = "PDF Store देखें",
                            onAction = onExplorePdfs
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(pdfPurchases) { pur ->
                                val targetPdf = allPdfs.find { it.id == pur.productId }

                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = pur.productName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Surface(
                                                color = Color(0xFFDCFCE7),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "UNLOCKED",
                                                    color = Color(0xFF15803D),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "खरीद तिथि: ${dateFormat.format(Date(pur.purchaseDate))}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Button(
                                            onClick = {
                                                if (targetPdf != null) onOpenPdf(targetPdf)
                                            },
                                            modifier = Modifier.fillMaxWidth().height(42.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                        ) {
                                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("ऑनलाइन पढ़ें (Read Online)", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Payment History Tab
                    if (payments.isEmpty()) {
                        EmptyPurchaseState(
                            message = "कोई पेमेंट रिकॉर्ड उपलब्ध नहीं है",
                            buttonText = "मॉक टेस्ट देखें",
                            onAction = onExploreTests
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(payments) { pay ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = pay.productName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = "UTR: ${pay.utrNumber}",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }

                                            // Status Badge
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = when (pay.status) {
                                                    "APPROVED" -> Color(0xFFDCFCE7)
                                                    "REJECTED" -> Color(0xFFFEE2E2)
                                                    else -> Color(0xFFFEF3C7)
                                                }
                                            ) {
                                                Text(
                                                    text = when (pay.status) {
                                                        "APPROVED" -> "स्वीकृत (Approved)"
                                                        "REJECTED" -> "अस्वीकृत (Rejected)"
                                                        else -> "प्रतीक्षारत (Pending)"
                                                    },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (pay.status) {
                                                        "APPROVED" -> Color(0xFF15803D)
                                                        "REJECTED" -> Color(0xFFB91C1C)
                                                        else -> Color(0xFFB45309)
                                                    },
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = dateFormat.format(Date(pay.createdAt)),
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                            Text(
                                                text = "₹${pay.amount.toInt()}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = PrimaryBlue
                                            )
                                        }

                                        if (pay.status == "REJECTED" && pay.rejectionReason != null) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "कारण: ${pay.rejectionReason}",
                                                fontSize = 11.sp,
                                                color = Color(0xFFDC2626)
                                            )
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

@Composable
private fun EmptyPurchaseState(
    message: String,
    buttonText: String,
    onAction: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(buttonText)
            }
        }
    }
}
