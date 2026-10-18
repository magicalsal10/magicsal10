package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.FastingProtocol
import com.example.model.MetabolicStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("FastPulse", appName)
    }

    @Test
    fun `verify metabolic stages mapping`() {
        val hourMs = 3600 * 1000L

        // 2 hours -> Anabolic
        assertEquals(MetabolicStage.ANABOLIC, MetabolicStage.fromElapsedMillis(2 * hourMs))

        // 8 hours -> Catabolic
        assertEquals(MetabolicStage.CATABOLIC, MetabolicStage.fromElapsedMillis(8 * hourMs))

        // 14 hours -> Fat Burning
        assertEquals(MetabolicStage.FAT_BURNING, MetabolicStage.fromElapsedMillis(14 * hourMs))

        // 18 hours -> Autophagy
        assertEquals(MetabolicStage.AUTOPHAGY, MetabolicStage.fromElapsedMillis(18 * hourMs))

        // 30 hours -> Deep Renewal
        assertEquals(MetabolicStage.DEEP_RENEWAL, MetabolicStage.fromElapsedMillis(30 * hourMs))
    }

    @Test
    fun `verify protocol calculations`() {
        val omad = FastingProtocol.OMAD_23_1
        assertEquals(23f, omad.fastingHours)
        assertEquals(1f, omad.eatingHours)

        val leangains = FastingProtocol.LEANGAINS_16_8
        assertEquals(16f, leangains.fastingHours)
        assertEquals(8f, leangains.eatingHours)
    }
}
