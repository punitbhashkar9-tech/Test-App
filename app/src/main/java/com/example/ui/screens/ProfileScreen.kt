package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    settings: AppSettingsEntity?,
    authViewModel: AuthViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToPurchases: () -> Unit,
    onNavigateToSubscriptions: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToAdminLogin: () -> Unit,
    onNavigateToAdminPanel: () -> Unit
) {
    val context = LocalContext.current
    var showFaqDialog by remember { mutableStateOf(false) }
    var showPolicyDialog by remember { mutableStateOf<Pair<String, String>?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("मेरी प्रोफाइल (Profile)", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // User Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlueContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser?.name ?: "Guest User",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (currentUser != null) "+91 ${currentUser.mobile}" else "लॉगिन नहीं किया है",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                            if (currentUser?.email?.isNotBlank() == true) {
                                Text(
                                    text = currentUser.email,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        // Subscription Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (currentUser?.subscriptionPlan in listOf("Pro", "Pro Max")) AccentAmber else Color(0xFFE2E8F0)
                        ) {
                            Text(
                                text = currentUser?.subscriptionPlan ?: "Free",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (currentUser == null) {
                        Button(
                            onClick = onNavigateToLogin,
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("लॉगिन / रजिस्टर करें")
                        }
                    } else {
                        OutlinedButton(
                            onClick = onNavigateToSubscriptions,
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                        ) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("सब्सक्रिप्शन प्लान अपग्रेड करें", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Links Section
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileMenuRow(
                        icon = Icons.Default.History,
                        title = "पुराने टेस्ट परिणाम (Test History)",
                        subtitle = "अपने विगत टेस्ट के स्कोर व समीक्षा देखें",
                        onClick = onNavigateToHistory
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileMenuRow(
                        icon = Icons.Default.ShoppingBag,
                        title = "मेरी खरीद (My Purchases)",
                        subtitle = "खरीदे गए टेस्ट, नोट्स व पेमेंट स्थिति",
                        onClick = onNavigateToPurchases
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileMenuRow(
                        icon = Icons.Default.WorkspacePremium,
                        title = "सब्सक्रिप्शन प्लान्स (Plans)",
                        subtitle = "Free, Pro व Pro Max प्लान विवरण",
                        onClick = onNavigateToSubscriptions
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Help & Support Section
            Text(
                text = "सहायता एवं समर्थन (Help & Support)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF475569),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileMenuRow(
                        icon = Icons.Default.Chat,
                        title = "WhatsApp सहायता",
                        subtitle = settings?.supportWhatsapp ?: "+91 98765 43210",
                        onClick = {
                            Toast.makeText(context, "WhatsApp सहायता नंबर: ${settings?.supportWhatsapp}", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileMenuRow(
                        icon = Icons.Default.Phone,
                        title = "कॉल करें (Call Support)",
                        subtitle = settings?.supportPhone ?: "+91 98765 43210",
                        onClick = {
                            Toast.makeText(context, "हेल्पलाइन नंबर: ${settings?.supportPhone}", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileMenuRow(
                        icon = Icons.Default.Email,
                        title = "ईमेल समर्थन (Email)",
                        subtitle = settings?.supportEmail ?: "support@testapp.in",
                        onClick = {
                            Toast.makeText(context, "ईमेल: ${settings?.supportEmail}", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileMenuRow(
                        icon = Icons.Default.QuestionAnswer,
                        title = "प्रायः पूछे जाने वाले सवाल (FAQ)",
                        subtitle = "टेस्ट, पेमेंट और नोट्स संबंधित सामान्य प्रश्न",
                        onClick = { showFaqDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Admin Portal Access Button
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileMenuRow(
                    icon = Icons.Default.AdminPanelSettings,
                    title = if (currentUser?.role == "admin") "एडमिन पैनल खोलें (Admin Panel)" else "एडमिन लॉगिन (Admin Portal)",
                    subtitle = "टेस्ट, नोट्स, यूजर व पेमेंट वेरिफिकेशन प्रबंधन",
                    iconTint = PrimaryBlue,
                    onClick = {
                        if (currentUser?.role == "admin") {
                            onNavigateToAdminPanel()
                        } else {
                            onNavigateToAdminLogin()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Policies Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(
                    onClick = {
                        showPolicyDialog = Pair("गोपनीयता नीति (Privacy Policy)", settings?.privacyPolicy ?: "सभी डेटा सुरक्षित है।")
                    }
                ) {
                    Text("Privacy Policy", fontSize = 11.sp, color = Color(0xFF64748B))
                }
                TextButton(
                    onClick = {
                        showPolicyDialog = Pair("नियम एवं शर्तें (Terms & Conditions)", settings?.termsAndConditions ?: "अध्ययन सामग्री व्यक्तिगत उपयोग हेतु है।")
                    }
                ) {
                    Text("Terms & Conditions", fontSize = 11.sp, color = Color(0xFF64748B))
                }
                TextButton(
                    onClick = {
                        showPolicyDialog = Pair("रिफंड नीति (Refund Policy)", settings?.refundPolicy ?: "48 घंटे में समाधान।")
                    }
                ) {
                    Text("Refund Policy", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }

            if (currentUser != null) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { authViewModel.logout() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("logout_button")
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("लॉगआउट करें (Logout)")
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // FAQ Dialog
    if (showFaqDialog) {
        AlertDialog(
            onDismissRequest = { showFaqDialog = false },
            title = { Text("अक्सर पूछे जाने वाले सवाल (FAQs)", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Q1: पेमेंट करने के बाद टेस्ट कब अनलॉक होगा?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Ans: पेमेंट करने के बाद UTR नंबर सबमिट करें। एडमिन टीम 15-30 मिनट में सत्यापन करके टेस्ट अनलॉक कर देती है।", fontSize = 12.sp, color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Q2: क्या टेस्ट बीच में बंद होने पर दोबारा दे सकते हैं?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Ans: हाँ, आप अपनी तैयारी के लिए टेस्ट कई बार अभ्यास कर सकते हैं।", fontSize = 12.sp, color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Q3: PDF नोट्स को फोन में कैसे पढ़ें?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Ans: PDF Store या My Purchases में जाकर 'ऑनलाइन पढ़ें' पर क्लिक करें। आप नाईट मोड व फॉन्ट साइज बदल सकते हैं।", fontSize = 12.sp, color = Color(0xFF475569))
                }
            },
            confirmButton = {
                Button(onClick = { showFaqDialog = false }) {
                    Text("ठीक है")
                }
            }
        )
    }

    // Policy Dialog
    showPolicyDialog?.let { (title, content) ->
        AlertDialog(
            onDismissRequest = { showPolicyDialog = null },
            title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Text(content, fontSize = 13.sp, lineHeight = 20.sp)
            },
            confirmButton = {
                Button(onClick = { showPolicyDialog = null }) {
                    Text("समझ गया")
                }
            }
        )
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = PrimaryBlue,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.1f))
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}
