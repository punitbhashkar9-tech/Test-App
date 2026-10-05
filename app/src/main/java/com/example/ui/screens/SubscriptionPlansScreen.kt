package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubscriptionPlanEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionPlansScreen(
    mainViewModel: MainViewModel,
    currentUser: UserEntity?,
    onBuyPlan: (SubscriptionPlanEntity) -> Unit,
    onBack: () -> Unit
) {
    val plans by mainViewModel.subscriptionPlans.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("सब्सक्रिप्शन प्लान्स (Plans)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("subscription_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = AccentAmber,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "अपनी तैयारी को दीजिए प्रो रफ्तार",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "असीमित मॉक टेस्ट, हस्तलिखित नोट्स और ऑल इंडिया रैंक",
                            fontSize = 12.sp,
                            color = Color(0xFFC7D2FE)
                        )
                    }
                }
            }

            items(plans) { plan ->
                val isCurrentPlan = currentUser?.subscriptionPlan.equals(plan.name.replace(" Plan", ""), ignoreCase = true)
                val isProMax = plan.id == "plan_pro_max"

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isProMax) Color(0xFFFAF5FF) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isProMax) 3.dp else 1.5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isProMax) Modifier.border(2.dp, Color(0xFF9333EA), RoundedCornerShape(16.dp)) else Modifier
                        )
                        .testTag("plan_card_${plan.id}")
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = plan.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (isProMax) Color(0xFF7E22CE) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "वैधता: ${plan.durationDays} दिन (${plan.durationDays / 30} महीने)",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            if (plan.price == 0.0) {
                                Text(
                                    text = "FREE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF16A34A)
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹${plan.price.toInt()}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 24.sp,
                                        color = if (isProMax) Color(0xFF7E22CE) else PrimaryBlue
                                    )
                                    Text("एकमुश्त (One-time)", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Features list
                        val featureList = plan.features.split(",").map { it.trim() }
                        featureList.forEach { feat ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (isProMax) Color(0xFF9333EA) else Color(0xFF16A34A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = feat,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        if (isCurrentPlan) {
                            OutlinedButton(
                                onClick = { },
                                enabled = false,
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Text("वर्तमान सक्रिय प्लान (Active Plan)", fontWeight = FontWeight.Bold)
                            }
                        } else if (plan.price > 0.0) {
                            Button(
                                onClick = { onBuyPlan(plan) },
                                modifier = Modifier.fillMaxWidth().height(46.dp).testTag("select_plan_${plan.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isProMax) Color(0xFF9333EA) else PrimaryBlue
                                )
                            ) {
                                Icon(Icons.Default.Stars, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("अपग्रेड करें (₹${plan.price.toInt()})", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
