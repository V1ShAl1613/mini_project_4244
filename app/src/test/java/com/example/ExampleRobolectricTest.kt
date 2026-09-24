package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CampusDataRepository
import com.example.model.ApplicationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies CampusConnect branding`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CampusConnect", appName)
    }

    @Test
    fun `repository has initial opportunities and personalized scores`() {
        val opportunities = CampusDataRepository.opportunities.value
        assertTrue(opportunities.isNotEmpty())
        assertTrue(opportunities.size >= 50)
        // Check that scores are computed
        assertTrue(opportunities.all { it.matchScore in 70..99 })
    }

    @Test
    fun `save and apply opportunity workflow`() {
        val opp = CampusDataRepository.opportunities.value.first()
        val initiallySaved = CampusDataRepository.isSaved(opp.id)

        // Toggle save
        CampusDataRepository.toggleSave(opp)
        assertEquals(!initiallySaved, CampusDataRepository.isSaved(opp.id))

        // Apply
        CampusDataRepository.applyToOpportunity(opp, "Test note")
        val app = CampusDataRepository.applications.value.firstOrNull { it.opportunityId == opp.id }
        assertTrue(app != null)
        assertEquals(ApplicationStatus.APPLIED, app?.status)
    }
}

