package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.repository.AppRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Test App", appName)
  }

  @Test
  fun `test database prepopulation and repository methods`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getInstance(context)
    val repo = AppRepository(db)

    repo.checkAndPrepopulate()

    val user = repo.getUserByMobile("9876543210")
    assertNotNull(user)
    assertEquals("राहुल शर्मा", user?.name)

    val (discount, error) = repo.applyCoupon("WELCOME50", 100.0)
    assertTrue(discount > 0.0)
    assertEquals(null, error)
  }
}
