package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.UserEntity
import com.example.ui.components.UpiQrView
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PaymentCheckoutState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    checkoutState: PaymentCheckoutState,
    settings: AppSettingsEntity?,
    currentUser: UserEntity?,
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
    onViewPurchases: () -> Unit
) {
    val context = LocalContext.current
    var couponInput by remember { mutableStateOf("") }
    var mockScreenshotUploaded by remember { mutableStateOf(false) }

    val upiId = settings?.upiId ?: "testapp@okaxis"
    val payeeName = settings?.upiReceiverName ?: "Test App Online Prep"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("सुरक्षित QR पेमेंट (Payment)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("payment_back_btn")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (checkoutState.submittedPaymentId != null) {
                // Payment Submitted Successfully Screen
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFFDCFCE7), RoundedCornerShape(32.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "पेमेंट विवरण सबमिट हो गया!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF16A34A)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Payment ID: ${checkoutState.submittedPaymentId}",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "आपकी पेमेंट सत्यापन प्रक्रिया में है (Status: PENDING).\nएडमिन द्वारा UTR व स्क्रीनशॉट सत्यापित होते ही सामग्री 'My Purchases' में स्वतः अनलॉक हो जाएगी।",
                            fontSize = 13.sp,
                            color = Color(0xFF334155),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onViewPurchases,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("मेरी खरीद देखें (Go to My Purchases)", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("मुख्य पृष्ठ पर जाएं (Back to Home)")
                        }
                    }
                }
            } else {
                // Item Price Summary Card
                Card(
                    shape = RoundedCornerShape(14.dp),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = checkoutState.productName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Type: ${checkoutState.productType}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                if (checkoutState.isCouponApplied) {
                                    Text(
                                        text = "₹${checkoutState.originalPrice.toInt()}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8),
                                        style = androidx.compose.ui.text.TextStyle(
                                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                        )
                                    )
                                }
                                Text(
                                    text = "₹${"%.2f".format(checkoutState.finalPrice)}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Coupon discount box
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = { couponInput = it.uppercase() },
                                placeholder = { Text("कूपन कोड (WELCOME50)", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).height(50.dp).testTag("coupon_input")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { mainViewModel.applyCoupon(couponInput) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(50.dp).testTag("apply_coupon_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Text("लागू करें")
                            }
                        }

                        if (checkoutState.couponMessage != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = checkoutState.couponMessage,
                                fontSize = 12.sp,
                                color = if (checkoutState.isCouponApplied) Color(0xFF16A34A) else Color(0xFFDC2626),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Authentic Dynamic UPI QR Code View
                UpiQrView(
                    upiId = upiId,
                    payeeName = payeeName,
                    amount = checkoutState.finalPrice,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Copy UPI ID & Pay Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", upiId))
                            Toast.makeText(context, "UPI ID कॉपी हो गया: $upiId", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy UPI ID", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "UPI ID $upiId पर पेमेंट करने के लिए अपना GPay, PhonePe या Paytm खोलें", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pay via UPI App", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Instructions Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "भुगतान निर्देश (Payment Steps)",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = settings?.paymentInstructions ?: "QR कोड स्कैन करके पेमेंट करें और 12-अंकों का UTR नंबर भरें।",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verification Form: UTR & Screenshot
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "पेमेंट सत्यापन फॉर्म (Verification Details)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = checkoutState.utrNumber,
                            onValueChange = { mainViewModel.setUtrNumber(it) },
                            label = { Text("12-अंकों का UTR / Transaction No.") },
                            placeholder = { Text("उदा. 429381749201") },
                            leadingIcon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("utr_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Screenshot Upload Simulation Box
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                .border(1.dp, if (mockScreenshotUploaded) Color(0xFF16A34A) else Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                .clickable {
                                    mockScreenshotUploaded = true
                                    mainViewModel.setScreenshot("mock_receipt_${System.currentTimeMillis()}.png")
                                    Toast.makeText(context, "पेमेंट स्क्रीनशॉट चयनित (Screenshot Attached)", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("upload_screenshot_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (mockScreenshotUploaded) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = if (mockScreenshotUploaded) Color(0xFF16A34A) else PrimaryBlue
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (mockScreenshotUploaded) "स्क्रीनशॉट संलग्न है (Receipt Attached)" else "पेमेंट स्क्रीनशॉट अपलोड करें (Attach Screenshot)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (mockScreenshotUploaded) Color(0xFF16A34A) else PrimaryBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (currentUser != null) {
                                    mainViewModel.submitPayment(currentUser)
                                } else {
                                    Toast.makeText(context, "कृपया पहले लॉगिन करें", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !checkoutState.isSubmitting && checkoutState.utrNumber.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_payment_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                        ) {
                            if (checkoutState.isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("सत्यापन हेतु सबमिट करें (Submit)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
