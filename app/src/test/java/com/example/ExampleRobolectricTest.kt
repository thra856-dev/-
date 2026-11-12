package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.model.ShiftType
import com.example.data.repository.ShiftRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
        assertEquals("حساب الراتب والشفتات", appName)
    }

    @Test
    fun `verify night shift quarter hour bonus calculation`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        val repository = ShiftRepository(db.shiftDao(), db.settingsDao(), db.monthlyAdjustmentDao())

        // 8 hours at night with 15 mins bonus per hour (+0.25):
        // 8 * 1.25 = 10.0 paid hours!
        // At 5,000 IQD per hour = 50,000 IQD
        val (paidHours, earned) = repository.calculateShiftValues(
            shiftType = ShiftType.NIGHT,
            actualHours = 8.0,
            hourlyRate = 5000.0,
            nightBonusMinutesPerHour = 15
        )

        assertEquals(10.0, paidHours, 0.001)
        assertEquals(50000.0, earned, 0.001)
    }

    @Test
    fun `verify morning shift regular calculation`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        val repository = ShiftRepository(db.shiftDao(), db.settingsDao(), db.monthlyAdjustmentDao())

        // 8 hours morning: paid hours = 8.0, earned = 40,000 IQD
        val (paidHours, earned) = repository.calculateShiftValues(
            shiftType = ShiftType.MORNING,
            actualHours = 8.0,
            hourlyRate = 5000.0,
            nightBonusMinutesPerHour = 15
        )

        assertEquals(8.0, paidHours, 0.001)
        assertEquals(40000.0, earned, 0.001)
    }
}
