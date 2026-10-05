package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.*
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.viewmodel.AdminUiState
import com.example.ui.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminViewModel: AdminViewModel,
    adminUiState: AdminUiState,
    onExitAdmin: () -> Unit
) {
    val context = LocalContext.current
    val stats = adminUiState.stats
    val allPayments by adminViewModel.allPayments.collectAsState()
    val allTests by adminViewModel.allTests.collectAsState()
    val allPdfs by adminViewModel.allPdfs.collectAsState()
    val allUsers by adminViewModel.allUsers.collectAsState()
    val allCategories by adminViewModel.allCategories.collectAsState()
    val appSettings by adminViewModel.appSettings.collectAsState()

    var selectedAdminTab by remember { mutableStateOf(0) }
    val adminTabs = listOf("Dashboard", "Payments (${stats.pendingPayments})", "Tests", "PDFs", "Users", "Categories", "Settings")

    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    // Handle back button to exit admin panel cleanly
    BackHandler {
        onExitAdmin()
    }

    LaunchedEffect(adminUiState.toastMessage) {
        adminUiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            adminViewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Admin Control Panel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Test App Management System", fontSize = 11.sp, color = Color(0xFFDBEAFE))
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onExitAdmin,
                        modifier = Modifier.testTag("admin_exit_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Admin", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = onExitAdmin) {
                        Text("Switch to User App", color = Color.White, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B), titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF1F5F9))
        ) {
            // Horizontal Admin Sub-tabs
            ScrollableTabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = Color.White,
                contentColor = PrimaryBlue,
                edgePadding = 8.dp
            ) {
                adminTabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedAdminTab == idx,
                        onClick = { selectedAdminTab = idx },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedAdminTab == idx) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedAdminTab == idx) PrimaryBlue else Color(0xFF475569)
                            )
                        }
                    )
                }
            }

            when (selectedAdminTab) {
                0 -> {
                    // --- 1. OVERVIEW DASHBOARD ---
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            // Revenue Metric Card
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(text = "कुल राजस्व (Total Revenue)", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                    Text(
                                        text = "₹${"%.2f".format(stats.totalRevenue)}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF4ADE80)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("आज का राजस्व: ₹${"%.2f".format(stats.todayRevenue)}", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                                        Text("कुल खरीद: ${stats.totalPurchases}", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                                    }
                                }
                            }
                        }

                        item {
                            // 4 Key Stats Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatCard(
                                    title = "Pending Payments",
                                    value = "${stats.pendingPayments}",
                                    badge = "Action Req.",
                                    color = Color(0xFFD97706),
                                    modifier = Modifier.weight(1f).clickable { selectedAdminTab = 1 }
                                )
                                StatCard(
                                    title = "Approved Payments",
                                    value = "${stats.approvedPayments}",
                                    badge = "Success",
                                    color = Color(0xFF16A34A),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatCard(
                                    title = "Total Users",
                                    value = "${stats.totalUsers}",
                                    badge = "Active: ${stats.activeUsers}",
                                    color = PrimaryBlue,
                                    modifier = Modifier.weight(1f).clickable { selectedAdminTab = 4 }
                                )
                                StatCard(
                                    title = "Tests & PDFs",
                                    value = "${stats.totalTests}T / ${stats.totalPdfs}P",
                                    badge = "Published",
                                    color = Color(0xFF7C3AED),
                                    modifier = Modifier.weight(1f).clickable { selectedAdminTab = 2 }
                                )
                            }
                        }

                        item {
                            // Quick Action Buttons
                            Text(text = "त्वरित क्रियाएं (Quick Actions)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { adminViewModel.openAddTestDialog(true) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Test", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { adminViewModel.openAddPdfDialog(true) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
                                ) {
                                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add PDF", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // --- 2. PAYMENT VERIFICATION REQUESTS ---
                    val paymentsList = remember(allPayments, adminUiState.paymentFilter) {
                        when (adminUiState.paymentFilter) {
                            "PENDING" -> allPayments.filter { it.status == "PENDING" }
                            "APPROVED" -> allPayments.filter { it.status == "APPROVED" }
                            "REJECTED" -> allPayments.filter { it.status == "REJECTED" }
                            else -> allPayments
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        // Payment Filter Chips
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("ALL" to "सभी", "PENDING" to "प्रतीक्षारत (${stats.pendingPayments})", "APPROVED" to "स्वीकृत", "REJECTED" to "अस्वीकृत").forEach { (filterKey, label) ->
                                FilterChip(
                                    selected = adminUiState.paymentFilter == filterKey,
                                    onClick = { adminViewModel.setPaymentFilter(filterKey) },
                                    label = { Text(label, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (paymentsList.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("कोई पेमेंट अनुरोध नहीं मिला (No payment requests)", color = Color(0xFF64748B))
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(paymentsList) { payment ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("admin_pay_card_${payment.id}")
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = payment.productName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text(text = "User: ${payment.userName} (${payment.userMobile})", fontSize = 12.sp, color = Color(0xFF64748B))
                                                }
                                                Text(
                                                    text = "₹${payment.amount.toInt()}",
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 17.sp,
                                                    color = PrimaryBlue
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = "UTR: ${payment.utrNumber}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                                Text(text = dateFormat.format(Date(payment.createdAt)), fontSize = 11.sp, color = Color(0xFF64748B))
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            if (payment.status == "PENDING") {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    OutlinedButton(
                                                        onClick = { adminViewModel.promptReject(payment) },
                                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                                        modifier = Modifier.weight(1f).height(40.dp).testTag("reject_btn_${payment.id}")
                                                    ) {
                                                        Text("Reject", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    }

                                                    Button(
                                                        onClick = { adminViewModel.approvePayment(payment.id) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                                        modifier = Modifier.weight(1.5f).height(40.dp).testTag("approve_btn_${payment.id}")
                                                    ) {
                                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Approve & Unlock", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    }
                                                }
                                            } else {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = if (payment.status == "APPROVED") Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text = "Status: ${payment.status} ${if (payment.rejectionReason != null) "(${payment.rejectionReason})" else ""}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        color = if (payment.status == "APPROVED") Color(0xFF15803D) else Color(0xFFB91C1C),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
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

                2 -> {
                    // --- 3. TEST MANAGEMENT ---
                    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("कुल टेस्ट: ${allTests.size}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Button(
                                onClick = { adminViewModel.openAddTestDialog(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                modifier = Modifier.testTag("admin_add_test_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("नया टेस्ट बनाएं", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(allTests) { test ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = test.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text(text = "${test.examName} • ${test.subject} • ${if (test.isFree) "FREE" else "₹${test.price.toInt()}"}", fontSize = 12.sp, color = Color(0xFF64748B))
                                            }

                                            IconButton(onClick = { adminViewModel.deleteTest(test.id) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (test.isPublished) "Published (सक्रिय)" else "Unpublished (छिपा हुआ)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (test.isPublished) Color(0xFF16A34A) else Color(0xFFDC2626)
                                            )
                                            Switch(
                                                checked = test.isPublished,
                                                onCheckedChange = { adminViewModel.toggleTestPublish(test) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // --- 4. PDF MANAGEMENT ---
                    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("कुल PDFs: ${allPdfs.size}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Button(
                                onClick = { adminViewModel.openAddPdfDialog(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                modifier = Modifier.testTag("admin_add_pdf_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("नया PDF जोड़ें", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(allPdfs) { pdf ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(32.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = pdf.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = "${pdf.subject} • ${if (pdf.isFree) "FREE" else "₹${pdf.price.toInt()}"} • ${pdf.pageCount} Pages", fontSize = 11.sp, color = Color(0xFF64748B))
                                        }
                                        IconButton(onClick = { adminViewModel.deletePdf(pdf.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // --- 5. USER MANAGEMENT ---
                    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        Text("पंजीकृत यूजर्स (Registered Users: ${allUsers.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(allUsers) { user ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (user.role == "admin") Icons.Default.AdminPanelSettings else Icons.Default.AccountCircle,
                                            contentDescription = null,
                                            tint = if (user.role == "admin") PrimaryBlue else Color(0xFF64748B),
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(text = "+91 ${user.mobile} | Plan: ${user.subscriptionPlan}", fontSize = 11.sp, color = Color(0xFF64748B))
                                            Text(
                                                text = if (user.isBlocked) "Blocked (ब्लॉक)" else "Active (सक्रिय)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (user.isBlocked) Color(0xFFDC2626) else Color(0xFF16A34A)
                                            )
                                        }

                                        if (user.role != "admin") {
                                            TextButton(
                                                onClick = { adminViewModel.toggleUserBlock(user) },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = if (user.isBlocked) Color(0xFF16A34A) else Color(0xFFDC2626)
                                                )
                                            ) {
                                                Text(if (user.isBlocked) "Unblock" else "Block", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                5 -> {
                    // --- 6. CATEGORIES MANAGEMENT ---
                    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("परीक्षा श्रेणियां (${allCategories.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Button(
                                onClick = { adminViewModel.openAddCategoryDialog(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("नई श्रेणी", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(allCategories) { cat ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = cat.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        IconButton(onClick = { adminViewModel.deleteCategory(cat.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                6 -> {
                    // --- 7. APP & UPI SETTINGS ---
                    val currentSettings = appSettings ?: AppSettingsEntity()
                    var appName by remember(currentSettings) { mutableStateOf(currentSettings.appName) }
                    var upiId by remember(currentSettings) { mutableStateOf(currentSettings.upiId) }
                    var upiName by remember(currentSettings) { mutableStateOf(currentSettings.upiReceiverName) }
                    var paymentInstructions by remember(currentSettings) { mutableStateOf(currentSettings.paymentInstructions) }
                    var supportPhone by remember(currentSettings) { mutableStateOf(currentSettings.supportPhone) }
                    var supportEmail by remember(currentSettings) { mutableStateOf(currentSettings.supportEmail) }
                    var noticeText by remember(currentSettings) { mutableStateOf(currentSettings.noticeText) }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("UPI व भुगतान सेटिंग्स (Payment Settings)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        item {
                            OutlinedTextField(
                                value = upiId,
                                onValueChange = { upiId = it },
                                label = { Text("Receiver UPI ID") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = upiName,
                                onValueChange = { upiName = it },
                                label = { Text("Receiver Display Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = paymentInstructions,
                                onValueChange = { paymentInstructions = it },
                                label = { Text("भुगतान निर्देश (Instructions)") },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            Text("ऐप विवरण व हेल्पलाइन सेटिंग्स", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        item {
                            OutlinedTextField(
                                value = appName,
                                onValueChange = { appName = it },
                                label = { Text("App Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = noticeText,
                                onValueChange = { noticeText = it },
                                label = { Text("होम पेज नोटिस पट्टी (Notice Bar)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = supportPhone,
                                onValueChange = { supportPhone = it },
                                label = { Text("Support Helpline Phone") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = supportEmail,
                                onValueChange = { supportEmail = it },
                                label = { Text("Support Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            Button(
                                onClick = {
                                    adminViewModel.updateSettings(
                                        currentSettings.copy(
                                            appName = appName,
                                            upiId = upiId,
                                            upiReceiverName = upiName,
                                            paymentInstructions = paymentInstructions,
                                            supportPhone = supportPhone,
                                            supportEmail = supportEmail,
                                            noticeText = noticeText
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Text("सेटिंग्स सेव करें (Save Settings)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Reject Payment Reason Dialog
    if (adminUiState.isRejectDialogOpen) {
        AlertDialog(
            onDismissRequest = { adminViewModel.closePaymentReview() },
            title = { Text("पेमेंट अस्वीकार करें (Reject Payment)", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("अस्वीकृति का कारण दर्ज करें (Reason):", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adminUiState.rejectReasonInput,
                        onValueChange = { adminViewModel.setRejectReason(it) },
                        placeholder = { Text("उदा. बैंक खाते में राशि प्राप्त नहीं हुई") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { adminViewModel.confirmReject() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("अस्वीकृत करें (Reject)")
                }
            },
            dismissButton = {
                TextButton(onClick = { adminViewModel.closePaymentReview() }) {
                    Text("रद्द करें")
                }
            }
        )
    }

    // Modal: Add Quick Test Dialog
    if (adminUiState.isAddTestDialogOpen) {
        var testTitle by remember { mutableStateOf("") }
        var examName by remember { mutableStateOf("UP Police") }
        var subject by remember { mutableStateOf("General Studies") }
        var isFree by remember { mutableStateOf(false) }
        var priceStr by remember { mutableStateOf("49") }
        var durationStr by remember { mutableStateOf("15") }

        AlertDialog(
            onDismissRequest = { adminViewModel.openAddTestDialog(false) },
            title = { Text("नया ऑनलाइन टेस्ट बनाएं", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = testTitle,
                        onValueChange = { testTitle = it },
                        label = { Text("टेस्ट का नाम (Test Title)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = examName,
                        onValueChange = { examName = it },
                        label = { Text("परीक्षा (Exam Name)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("विषय (Subject)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isFree, onCheckedChange = { isFree = it })
                        Text("निःशुल्क टेस्ट (Free Test)")
                    }
                    if (!isFree) {
                        OutlinedTextField(
                            value = priceStr,
                            onValueChange = { priceStr = it },
                            label = { Text("मूल्य (Price ₹)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = durationStr,
                        onValueChange = { durationStr = it },
                        label = { Text("अवधि मिनट (Duration in mins)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (testTitle.isNotBlank()) {
                            adminViewModel.createQuickTest(
                                title = testTitle,
                                categoryId = "up_police",
                                examName = examName,
                                subject = subject,
                                isFree = isFree,
                                price = priceStr.toDoubleOrNull() ?: 49.0,
                                durationMinutes = durationStr.toIntOrNull() ?: 15
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("बनाएं (Create)")
                }
            },
            dismissButton = {
                TextButton(onClick = { adminViewModel.openAddTestDialog(false) }) {
                    Text("रद्द करें")
                }
            }
        )
    }

    // Modal: Add Quick PDF Dialog
    if (adminUiState.isAddPdfDialogOpen) {
        var pdfTitle by remember { mutableStateOf("") }
        var examName by remember { mutableStateOf("CTET") }
        var subject by remember { mutableStateOf("Child Development") }
        var description by remember { mutableStateOf("हस्तलिखित नोट्स एवं सूत्र संग्रह") }
        var isFree by remember { mutableStateOf(false) }
        var priceStr by remember { mutableStateOf("39") }
        var pagesStr by remember { mutableStateOf("45") }
        var contentText by remember { mutableStateOf("# मुख्य अध्ययन नोट्स\n\n- बिंदु 1: महत्वपूर्ण नियम\n- बिंदु 2: परीक्षा उपयोगी सूत्र") }

        AlertDialog(
            onDismissRequest = { adminViewModel.openAddPdfDialog(false) },
            title = { Text("नया PDF नोट्स जोड़ें", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = pdfTitle,
                        onValueChange = { pdfTitle = it },
                        label = { Text("PDF शीर्षक (Title)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = examName,
                        onValueChange = { examName = it },
                        label = { Text("परीक्षा (Exam)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("विषय (Subject)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isFree, onCheckedChange = { isFree = it })
                        Text("निःशुल्क PDF (Free)")
                    }
                    if (!isFree) {
                        OutlinedTextField(
                            value = priceStr,
                            onValueChange = { priceStr = it },
                            label = { Text("मूल्य (Price ₹)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pagesStr,
                        onValueChange = { pagesStr = it },
                        label = { Text("कुल पृष्ठ (Pages)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = contentText,
                        onValueChange = { contentText = it },
                        label = { Text("नोट्स सामग्री (Content Text)") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pdfTitle.isNotBlank()) {
                            adminViewModel.createPdf(
                                title = pdfTitle,
                                categoryId = "ctet",
                                examName = examName,
                                subject = subject,
                                description = description,
                                price = priceStr.toDoubleOrNull() ?: 39.0,
                                isFree = isFree,
                                pages = pagesStr.toIntOrNull() ?: 45,
                                content = contentText
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
                ) {
                    Text("जोड़ें (Add PDF)")
                }
            },
            dismissButton = {
                TextButton(onClick = { adminViewModel.openAddPdfDialog(false) }) {
                    Text("रद्द करें")
                }
            }
        )
    }

    // Modal: Add Category Dialog
    if (adminUiState.isAddCategoryDialogOpen) {
        var catName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { adminViewModel.openAddCategoryDialog(false) },
            title = { Text("नई परीक्षा श्रेणी जोड़ें", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = catName,
                    onValueChange = { catName = it },
                    label = { Text("श्रेणी का नाम (Category Name)") },
                    placeholder = { Text("उदा. BPSC / Banking") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (catName.isNotBlank()) {
                            adminViewModel.createCategory(catName, "school")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("जोड़ें")
                }
            },
            dismissButton = {
                TextButton(onClick = { adminViewModel.openAddCategoryDialog(false) }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    badge: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = badge, fontSize = 10.sp, color = Color(0xFF94A3B8))
        }
    }
}
