package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        TestEntity::class,
        QuestionEntity::class,
        TestAttemptEntity::class,
        PdfEntity::class,
        PaymentEntity::class,
        PurchaseEntity::class,
        SubscriptionPlanEntity::class,
        CouponEntity::class,
        HomeBannerEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun testDao(): TestDao
    abstract fun questionDao(): QuestionDao
    abstract fun testAttemptDao(): TestAttemptDao
    abstract fun pdfDao(): PdfDao
    abstract fun paymentDao(): PaymentDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun subscriptionPlanDao(): SubscriptionPlanDao
    abstract fun couponDao(): CouponDao
    abstract fun homeBannerDao(): HomeBannerDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "test_app_database.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
