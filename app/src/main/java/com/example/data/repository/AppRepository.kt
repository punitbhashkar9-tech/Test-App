package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.db.PrepopulateData
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

class AppRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val categoryDao = db.categoryDao()
    private val testDao = db.testDao()
    private val questionDao = db.questionDao()
    private val testAttemptDao = db.testAttemptDao()
    private val pdfDao = db.pdfDao()
    private val paymentDao = db.paymentDao()
    private val purchaseDao = db.purchaseDao()
    private val subscriptionPlanDao = db.subscriptionPlanDao()
    private val couponDao = db.couponDao()
    private val homeBannerDao = db.homeBannerDao()
    private val appSettingsDao = db.appSettingsDao()

    suspend fun checkAndPrepopulate() = withContext(Dispatchers.IO) {
        val existingCats = categoryDao.getAllCategories().firstOrNull()
        if (existingCats.isNullOrEmpty()) {
            for (cat in PrepopulateData.defaultCategories) {
                categoryDao.insert(cat)
            }
            for (test in PrepopulateData.defaultTests) {
                testDao.insert(test)
            }
            questionDao.insertAll(PrepopulateData.defaultQuestions)
            for (pdf in PrepopulateData.defaultPdfs) {
                pdfDao.insert(pdf)
            }
            for (plan in PrepopulateData.defaultSubscriptionPlans) {
                subscriptionPlanDao.insert(plan)
            }
            for (coupon in PrepopulateData.defaultCoupons) {
                couponDao.insert(coupon)
            }
            for (banner in PrepopulateData.defaultBanners) {
                homeBannerDao.insert(banner)
            }
            for (user in PrepopulateData.defaultUsers) {
                userDao.insert(user)
            }
            appSettingsDao.insertOrUpdate(PrepopulateData.defaultSettings)
        }
    }

    // User Operations
    fun getUser(id: String): Flow<UserEntity?> = userDao.getUserById(id)
    suspend fun getUserByMobile(mobile: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserByMobile(mobile)
    }
    suspend fun saveUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.insert(user)
    }
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    suspend fun setUserBlocked(userId: String, blocked: Boolean) = withContext(Dispatchers.IO) {
        userDao.setUserBlocked(userId, blocked)
    }
    suspend fun updateUserSubscription(userId: String, plan: String, expiryDays: Int) = withContext(Dispatchers.IO) {
        val expiryTime = System.currentTimeMillis() + (expiryDays.toLong() * 24 * 3600 * 1000)
        userDao.updateSubscription(userId, plan, expiryTime)
    }

    // Categories
    fun getActiveCategories(): Flow<List<CategoryEntity>> = categoryDao.getActiveCategories()
    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    suspend fun saveCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.insert(category)
    }
    suspend fun deleteCategory(id: String) = withContext(Dispatchers.IO) {
        categoryDao.delete(id)
    }

    // Tests
    fun getPublishedTests(): Flow<List<TestEntity>> = testDao.getPublishedTests()
    fun getAllTests(): Flow<List<TestEntity>> = testDao.getAllTests()
    fun getTestsByCategory(categoryId: String): Flow<List<TestEntity>> = testDao.getTestsByCategory(categoryId)
    fun getTestById(id: String): Flow<TestEntity?> = testDao.getTestById(id)
    suspend fun getTestByIdSync(id: String): TestEntity? = withContext(Dispatchers.IO) {
        testDao.getTestByIdSync(id)
    }
    suspend fun saveTest(test: TestEntity) = withContext(Dispatchers.IO) {
        testDao.insert(test)
    }
    suspend fun deleteTest(id: String) = withContext(Dispatchers.IO) {
        testDao.delete(id)
        questionDao.deleteByTestId(id)
    }
    suspend fun toggleTestPublish(id: String, published: Boolean) = withContext(Dispatchers.IO) {
        testDao.setPublished(id, published)
    }

    // Questions
    fun getQuestionsForTest(testId: String): Flow<List<QuestionEntity>> = questionDao.getQuestionsForTest(testId)
    suspend fun getQuestionsForTestSync(testId: String): List<QuestionEntity> = withContext(Dispatchers.IO) {
        questionDao.getQuestionsForTestSync(testId)
    }
    suspend fun saveQuestion(question: QuestionEntity) = withContext(Dispatchers.IO) {
        questionDao.insert(question)
    }
    suspend fun deleteQuestion(id: String) = withContext(Dispatchers.IO) {
        questionDao.deleteQuestion(id)
    }

    // Test Attempts
    fun getAttemptsForUser(userId: String): Flow<List<TestAttemptEntity>> = testAttemptDao.getAttemptsForUser(userId)
    fun getAttemptById(id: String): Flow<TestAttemptEntity?> = testAttemptDao.getAttemptById(id)
    suspend fun saveTestAttempt(attempt: TestAttemptEntity) = withContext(Dispatchers.IO) {
        testAttemptDao.insert(attempt)
    }

    // PDFs
    fun getAllPdfs(): Flow<List<PdfEntity>> = pdfDao.getAllPdfs()
    fun getPdfsByCategory(categoryId: String): Flow<List<PdfEntity>> = pdfDao.getPdfsByCategory(categoryId)
    fun getPdfById(id: String): Flow<PdfEntity?> = pdfDao.getPdfById(id)
    suspend fun savePdf(pdf: PdfEntity) = withContext(Dispatchers.IO) {
        pdfDao.insert(pdf)
    }
    suspend fun deletePdf(id: String) = withContext(Dispatchers.IO) {
        pdfDao.delete(id)
    }

    // Payments & Verification
    fun getAllPayments(): Flow<List<PaymentEntity>> = paymentDao.getAllPayments()
    fun getPaymentsForUser(userId: String): Flow<List<PaymentEntity>> = paymentDao.getPaymentsForUser(userId)
    fun getPaymentsByStatus(status: String): Flow<List<PaymentEntity>> = paymentDao.getPaymentsByStatus(status)
    fun getPaymentById(id: String): Flow<PaymentEntity?> = paymentDao.getPaymentById(id)

    suspend fun submitPayment(
        userId: String,
        userName: String,
        userMobile: String,
        productId: String,
        productType: String,
        productName: String,
        amount: Double,
        utrNumber: String,
        screenshotUri: String? = null
    ): String = withContext(Dispatchers.IO) {
        val paymentId = "pay_" + UUID.randomUUID().toString().take(8)
        val payment = PaymentEntity(
            id = paymentId,
            userId = userId,
            userName = userName,
            userMobile = userMobile,
            productId = productId,
            productType = productType,
            productName = productName,
            amount = amount,
            utrNumber = utrNumber,
            screenshotUri = screenshotUri,
            status = "PENDING",
            createdAt = System.currentTimeMillis()
        )
        paymentDao.insert(payment)
        paymentId
    }

    suspend fun approvePayment(paymentId: String) = withContext(Dispatchers.IO) {
        val payment = paymentDao.getPaymentByIdSync(paymentId) ?: return@withContext
        paymentDao.updateStatus(paymentId, "APPROVED", null, System.currentTimeMillis())

        if (payment.productType == "SUBSCRIPTION") {
            val days = if (payment.productId == "plan_pro_max") 365 else 90
            val planName = if (payment.productId == "plan_pro_max") "Pro Max" else "Pro"
            updateUserSubscription(payment.userId, planName, days)
        } else {
            // Unlock content by inserting into Purchases
            val purchase = PurchaseEntity(
                id = "pur_" + UUID.randomUUID().toString().take(8),
                userId = payment.userId,
                productId = payment.productId,
                productType = payment.productType,
                productName = payment.productName,
                purchaseDate = System.currentTimeMillis(),
                expiryDate = System.currentTimeMillis() + (365L * 24 * 3600 * 1000), // 1 year access
                paymentId = paymentId
            )
            purchaseDao.insert(purchase)
        }
    }

    suspend fun rejectPayment(paymentId: String, reason: String) = withContext(Dispatchers.IO) {
        paymentDao.updateStatus(paymentId, "REJECTED", reason, System.currentTimeMillis())
    }

    // Purchases
    fun getPurchasesForUser(userId: String): Flow<List<PurchaseEntity>> = purchaseDao.getPurchasesForUser(userId)
    fun getAllPurchases(): Flow<List<PurchaseEntity>> = purchaseDao.getAllPurchases()
    fun isItemPurchased(userId: String, productId: String): Flow<Boolean> = purchaseDao.isItemPurchased(userId, productId)
    suspend fun isItemPurchasedSync(userId: String, productId: String): Boolean = withContext(Dispatchers.IO) {
        purchaseDao.isItemPurchasedSync(userId, productId)
    }

    // Subscriptions
    fun getActivePlans(): Flow<List<SubscriptionPlanEntity>> = subscriptionPlanDao.getActivePlans()
    fun getAllPlans(): Flow<List<SubscriptionPlanEntity>> = subscriptionPlanDao.getAllPlans()
    suspend fun savePlan(plan: SubscriptionPlanEntity) = withContext(Dispatchers.IO) {
        subscriptionPlanDao.insert(plan)
    }

    // Coupons
    suspend fun applyCoupon(code: String, amount: Double): Pair<Double, String?> = withContext(Dispatchers.IO) {
        val coupon = couponDao.getCouponByCode(code.trim().uppercase())
            ?: return@withContext Pair(0.0, "अमान्य कूपन कोड (Invalid Coupon Code)")

        if (coupon.expiryDate < System.currentTimeMillis()) {
            return@withContext Pair(0.0, "कूपन कोड समाप्त हो चुका है (Expired Coupon)")
        }
        if (amount < coupon.minPurchase) {
            return@withContext Pair(0.0, "न्यूनतम ₹${coupon.minPurchase} की खरीद पर लागू (Min purchase ₹${coupon.minPurchase})")
        }

        val discount = ((amount * coupon.discountPercent) / 100.0).coerceAtMost(coupon.maxDiscount)
        Pair(discount, null)
    }

    fun getAllCoupons(): Flow<List<CouponEntity>> = couponDao.getAllCoupons()
    suspend fun saveCoupon(coupon: CouponEntity) = withContext(Dispatchers.IO) {
        couponDao.insert(coupon)
    }
    suspend fun deleteCoupon(id: String) = withContext(Dispatchers.IO) {
        couponDao.delete(id)
    }

    // Home Banners
    fun getActiveBanners(): Flow<List<HomeBannerEntity>> = homeBannerDao.getActiveBanners()
    fun getAllBanners(): Flow<List<HomeBannerEntity>> = homeBannerDao.getAllBanners()
    suspend fun saveBanner(banner: HomeBannerEntity) = withContext(Dispatchers.IO) {
        homeBannerDao.insert(banner)
    }
    suspend fun deleteBanner(id: String) = withContext(Dispatchers.IO) {
        homeBannerDao.delete(id)
    }

    // App Settings
    fun getAppSettings(): Flow<AppSettingsEntity?> = appSettingsDao.getSettings()
    suspend fun getAppSettingsSync(): AppSettingsEntity? = withContext(Dispatchers.IO) {
        appSettingsDao.getSettingsSync()
    }
    suspend fun updateAppSettings(settings: AppSettingsEntity) = withContext(Dispatchers.IO) {
        appSettingsDao.insertOrUpdate(settings)
    }
}
