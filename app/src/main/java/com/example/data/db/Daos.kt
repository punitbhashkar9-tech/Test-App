package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE mobile = :mobile LIMIT 1")
    suspend fun getUserByMobile(mobile: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Query("UPDATE users SET isBlocked = :blocked WHERE id = :id")
    suspend fun setUserBlocked(id: String, blocked: Boolean)

    @Query("UPDATE users SET subscriptionPlan = :plan, subscriptionExpiry = :expiry WHERE id = :userId")
    suspend fun updateSubscription(userId: String, plan: String, expiry: Long)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun getActiveCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Update
    suspend fun update(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface TestDao {
    @Query("SELECT * FROM tests WHERE isPublished = 1 ORDER BY createdAt DESC")
    fun getPublishedTests(): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests ORDER BY createdAt DESC")
    fun getAllTests(): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE categoryId = :categoryId AND isPublished = 1 ORDER BY createdAt DESC")
    fun getTestsByCategory(categoryId: String): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE id = :id LIMIT 1")
    fun getTestById(id: String): Flow<TestEntity?>

    @Query("SELECT * FROM tests WHERE id = :id LIMIT 1")
    suspend fun getTestByIdSync(id: String): TestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(test: TestEntity)

    @Update
    suspend fun update(test: TestEntity)

    @Query("DELETE FROM tests WHERE id = :id")
    suspend fun delete(id: String)

    @Query("UPDATE tests SET isPublished = :published WHERE id = :id")
    suspend fun setPublished(id: String, published: Boolean)
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE testId = :testId ORDER BY questionNumber ASC")
    fun getQuestionsForTest(testId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE testId = :testId ORDER BY questionNumber ASC")
    suspend fun getQuestionsForTestSync(testId: String): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("DELETE FROM questions WHERE testId = :testId")
    suspend fun deleteByTestId(testId: String)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestion(id: String)
}

@Dao
interface TestAttemptDao {
    @Query("SELECT * FROM test_attempts WHERE userId = :userId ORDER BY completedAt DESC")
    fun getAttemptsForUser(userId: String): Flow<List<TestAttemptEntity>>

    @Query("SELECT * FROM test_attempts WHERE id = :id LIMIT 1")
    fun getAttemptById(id: String): Flow<TestAttemptEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: TestAttemptEntity)
}

@Dao
interface PdfDao {
    @Query("SELECT * FROM pdfs ORDER BY createdAt DESC")
    fun getAllPdfs(): Flow<List<PdfEntity>>

    @Query("SELECT * FROM pdfs WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getPdfsByCategory(categoryId: String): Flow<List<PdfEntity>>

    @Query("SELECT * FROM pdfs WHERE id = :id LIMIT 1")
    fun getPdfById(id: String): Flow<PdfEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pdf: PdfEntity)

    @Update
    suspend fun update(pdf: PdfEntity)

    @Query("DELETE FROM pdfs WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY createdAt DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPaymentsForUser(userId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE status = :status ORDER BY createdAt DESC")
    fun getPaymentsByStatus(status: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE id = :id LIMIT 1")
    fun getPaymentById(id: String): Flow<PaymentEntity?>

    @Query("SELECT * FROM payments WHERE id = :id LIMIT 1")
    suspend fun getPaymentByIdSync(id: String): PaymentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: PaymentEntity)

    @Query("UPDATE payments SET status = :status, rejectionReason = :reason, reviewedAt = :reviewedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, reason: String?, reviewedAt: Long)
}

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases WHERE userId = :userId ORDER BY purchaseDate DESC")
    fun getPurchasesForUser(userId: String): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchases ORDER BY purchaseDate DESC")
    fun getAllPurchases(): Flow<List<PurchaseEntity>>

    @Query("SELECT COUNT(*) > 0 FROM purchases WHERE userId = :userId AND productId = :productId")
    fun isItemPurchased(userId: String, productId: String): Flow<Boolean>

    @Query("SELECT COUNT(*) > 0 FROM purchases WHERE userId = :userId AND productId = :productId")
    suspend fun isItemPurchasedSync(userId: String, productId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: PurchaseEntity)
}

@Dao
interface SubscriptionPlanDao {
    @Query("SELECT * FROM subscription_plans WHERE isActive = 1")
    fun getActivePlans(): Flow<List<SubscriptionPlanEntity>>

    @Query("SELECT * FROM subscription_plans")
    fun getAllPlans(): Flow<List<SubscriptionPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: SubscriptionPlanEntity)

    @Update
    suspend fun update(plan: SubscriptionPlanEntity)
}

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupons WHERE code = :code AND isActive = 1 LIMIT 1")
    suspend fun getCouponByCode(code: String): CouponEntity?

    @Query("SELECT * FROM coupons ORDER BY expiryDate DESC")
    fun getAllCoupons(): Flow<List<CouponEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coupon: CouponEntity)

    @Query("DELETE FROM coupons WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface HomeBannerDao {
    @Query("SELECT * FROM home_banners WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun getActiveBanners(): Flow<List<HomeBannerEntity>>

    @Query("SELECT * FROM home_banners ORDER BY displayOrder ASC")
    fun getAllBanners(): Flow<List<HomeBannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(banner: HomeBannerEntity)

    @Query("DELETE FROM home_banners WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 'default' LIMIT 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 'default' LIMIT 1")
    suspend fun getSettingsSync(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: AppSettingsEntity)
}
