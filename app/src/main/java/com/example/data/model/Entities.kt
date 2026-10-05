package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val mobile: String,
    val email: String = "",
    val profilePicUrl: String = "",
    val role: String = "user", // "user", "admin"
    val isBlocked: Boolean = false,
    val subscriptionPlan: String = "Free", // "Free", "Pro", "Pro Max"
    val subscriptionExpiry: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String = "school",
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "tests")
data class TestEntity(
    @PrimaryKey val id: String,
    val title: String,
    val categoryId: String,
    val examName: String,
    val subject: String,
    val totalQuestions: Int,
    val totalMarks: Double,
    val timeLimitMinutes: Int,
    val negativeMarking: Double = 0.25,
    val isFree: Boolean = true,
    val price: Double = 0.0,
    val isPublished: Boolean = true,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val testId: String,
    val questionNumber: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String, // "A", "B", "C", "D"
    val explanation: String = "",
    val marks: Double = 1.0,
    val negativeMarks: Double = 0.25
)

@Entity(tableName = "test_attempts")
data class TestAttemptEntity(
    @PrimaryKey val id: String,
    val testId: String,
    val testTitle: String,
    val userId: String,
    val score: Double,
    val totalMarks: Double,
    val totalQuestions: Int,
    val attempted: Int,
    val correct: Int,
    val wrong: Int,
    val unattempted: Int,
    val percentage: Double,
    val timeTakenSeconds: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val userAnswersJson: String = "" // key: questionId, value: selectedOption
)

@Entity(tableName = "pdfs")
data class PdfEntity(
    @PrimaryKey val id: String,
    val title: String,
    val categoryId: String,
    val examName: String,
    val subject: String,
    val description: String,
    val price: Double = 0.0,
    val isFree: Boolean = true,
    val downloadAllowed: Boolean = true,
    val pageCount: Int = 10,
    val content: String = "", // Formatted text/notes chapters
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userMobile: String,
    val productId: String,
    val productType: String, // "TEST", "PDF", "SUBSCRIPTION"
    val productName: String,
    val amount: Double,
    val utrNumber: String,
    val screenshotUri: String? = null,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val productId: String,
    val productType: String, // "TEST", "PDF", "SUBSCRIPTION"
    val productName: String,
    val purchaseDate: Long = System.currentTimeMillis(),
    val expiryDate: Long? = null,
    val paymentId: String = ""
)

@Entity(tableName = "subscription_plans")
data class SubscriptionPlanEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val durationDays: Int,
    val features: String, // comma-separated or json
    val isActive: Boolean = true
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey val id: String,
    val code: String,
    val discountPercent: Double,
    val maxDiscount: Double,
    val minPurchase: Double,
    val isActive: Boolean = true,
    val expiryDate: Long = System.currentTimeMillis() + 30L * 24 * 3600 * 1000
)

@Entity(tableName = "home_banners")
data class HomeBannerEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val tag: String = "NEW",
    val actionType: String = "CATEGORY", // "CATEGORY", "TEST", "PDF", "SUBSCRIPTION"
    val actionTarget: String = "ALL",
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: String = "default",
    val appName: String = "Test App",
    val supportPhone: String = "+91 98765 43210",
    val supportEmail: String = "support@testapp.in",
    val supportWhatsapp: String = "+91 98765 43210",
    val upiId: String = "testapp@okaxis",
    val upiReceiverName: String = "Test App Online Prep",
    val paymentInstructions: String = "1. Scan the QR Code using any UPI App (GPay, PhonePe, Paytm).\n2. Complete the payment.\n3. Enter the 12-digit UTR / Reference No.\n4. Submit for verification. Access will be unlocked within minutes.",
    val noticeText: String = "🔥 New UP Police & CTET 2025 Test Series Launched! Use code WELCOME50 for 50% discount.",
    val privacyPolicy: String = "We respect your privacy. All your test attempts and payment details are securely processed.",
    val termsAndConditions: String = "Study material and tests are for personal use only. Unauthorized distribution is prohibited.",
    val refundPolicy: String = "In case of duplicate payment or payment failure with amount deduction, full refund is initiated within 48 hours."
)
