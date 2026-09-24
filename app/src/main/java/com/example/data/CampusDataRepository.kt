package com.example.data

import com.example.model.ApplicationItem
import com.example.model.ApplicationStatus
import com.example.model.CollegeAnnouncement
import com.example.model.NotificationItem
import com.example.model.Opportunity
import com.example.model.OpportunityCategory
import com.example.model.StudentProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object CampusDataRepository {

    // Student Profile
    private val _studentProfile = MutableStateFlow(StudentProfile())
    val studentProfile: StateFlow<StudentProfile> = _studentProfile.asStateFlow()

    // Opportunities
    private val _opportunities = MutableStateFlow(DemoDataSource.getAllOpportunities())
    val opportunities: StateFlow<List<Opportunity>> = _opportunities.asStateFlow()

    // Applications Pipeline (Saved, Applied, Shortlisted, Interview, Selected, Rejected)
    private val _applications = MutableStateFlow<List<ApplicationItem>>(
        listOf(
            ApplicationItem(
                id = "app_init_1",
                opportunityId = "intern_1",
                opportunityTitle = "AI/ML Research Intern",
                organization = "Google India Research",
                category = OpportunityCategory.INTERNSHIP,
                deadline = "Tomorrow",
                status = ApplicationStatus.APPLIED,
                applicationDate = "21 Sep 2026",
                notes = "Submitted resume with NLP project portfolio and GitHub links.",
                matchScore = 96
            ),
            ApplicationItem(
                id = "app_init_2",
                opportunityId = "hack_1",
                opportunityTitle = "Smart India Hackathon 2026",
                organization = "Ministry of Education",
                category = OpportunityCategory.HACKATHON,
                deadline = "Tomorrow",
                status = ApplicationStatus.SAVED,
                applicationDate = "20 Sep 2026",
                notes = "Team idea ready: AI Agri-yield advisor in regional languages.",
                matchScore = 98
            ),
            ApplicationItem(
                id = "app_init_3",
                opportunityId = "intern_3",
                opportunityTitle = "Full Stack Developer Intern",
                organization = "Razorpay",
                category = OpportunityCategory.INTERNSHIP,
                deadline = "28 Sep 2026",
                status = ApplicationStatus.SHORTLISTED,
                applicationDate = "15 Sep 2026",
                notes = "Coding assessment completed: 100/100 test cases passed.",
                matchScore = 93
            ),
            ApplicationItem(
                id = "app_init_4",
                opportunityId = "intern_5",
                opportunityTitle = "Backend SDE Intern",
                organization = "Swiggy",
                category = OpportunityCategory.INTERNSHIP,
                deadline = "03 Oct 2026",
                status = ApplicationStatus.INTERVIEW,
                applicationDate = "12 Sep 2026",
                notes = "Technical round scheduled for Thursday 4:00 PM on Google Meet.",
                matchScore = 87
            )
        )
    )
    val applications: StateFlow<List<ApplicationItem>> = _applications.asStateFlow()

    // College Announcements
    private val _announcements = MutableStateFlow(DemoDataSource.announcements)
    val announcements: StateFlow<List<CollegeAnnouncement>> = _announcements.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow(DemoDataSource.notifications)
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Saved Opportunity IDs
    private val _savedIds = MutableStateFlow<Set<String>>(setOf("intern_1", "hack_1", "work_1"))
    val savedIds: StateFlow<Set<String>> = _savedIds.asStateFlow()

    // Admin Mode state
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    init {
        recalculatePersonalizedScores(_studentProfile.value)
    }

    fun updateProfile(newProfile: StudentProfile) {
        _studentProfile.value = newProfile
        recalculatePersonalizedScores(newProfile)
    }

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
    }

    fun setAdminMode(enabled: Boolean) {
        _isAdminMode.value = enabled
    }

    fun isSaved(opportunityId: String): Boolean {
        return _savedIds.value.contains(opportunityId)
    }

    fun toggleSave(opportunity: Opportunity) {
        val current = _savedIds.value.toMutableSet()
        val appList = _applications.value.toMutableList()

        if (current.contains(opportunity.id)) {
            current.remove(opportunity.id)
            appList.removeAll { it.opportunityId == opportunity.id && it.status == ApplicationStatus.SAVED }
        } else {
            current.add(opportunity.id)
            if (appList.none { it.opportunityId == opportunity.id }) {
                appList.add(
                    ApplicationItem(
                        id = UUID.randomUUID().toString(),
                        opportunityId = opportunity.id,
                        opportunityTitle = opportunity.title,
                        organization = opportunity.organization,
                        category = opportunity.category,
                        deadline = opportunity.deadline,
                        status = ApplicationStatus.SAVED,
                        applicationDate = "Just now",
                        matchScore = opportunity.matchScore
                    )
                )
            }
        }
        _savedIds.value = current
        _applications.value = appList
    }

    fun applyToOpportunity(opportunity: Opportunity, customNote: String = "") {
        val currentSaved = _savedIds.value.toMutableSet()
        currentSaved.add(opportunity.id)
        _savedIds.value = currentSaved

        val appList = _applications.value.toMutableList()
        val existingIndex = appList.indexOfFirst { it.opportunityId == opportunity.id }
        if (existingIndex >= 0) {
            appList[existingIndex] = appList[existingIndex].copy(
                status = ApplicationStatus.APPLIED,
                applicationDate = "Today",
                notes = if (customNote.isNotBlank()) customNote else appList[existingIndex].notes
            )
        } else {
            appList.add(
                0,
                ApplicationItem(
                    id = UUID.randomUUID().toString(),
                    opportunityId = opportunity.id,
                    opportunityTitle = opportunity.title,
                    organization = opportunity.organization,
                    category = opportunity.category,
                    deadline = opportunity.deadline,
                    status = ApplicationStatus.APPLIED,
                    applicationDate = "Today",
                    notes = customNote,
                    matchScore = opportunity.matchScore
                )
            )
        }
        _applications.value = appList
    }

    fun updateApplicationStatus(appId: String, newStatus: ApplicationStatus, notes: String? = null) {
        _applications.value = _applications.value.map { item ->
            if (item.id == appId) {
                item.copy(
                    status = newStatus,
                    notes = notes ?: item.notes
                )
            } else item
        }
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    // Admin operations
    fun addOpportunity(opportunity: Opportunity) {
        _opportunities.value = listOf(opportunity) + _opportunities.value
    }

    fun deleteOpportunity(opportunityId: String) {
        _opportunities.value = _opportunities.value.filterNot { it.id == opportunityId }
        _savedIds.value = _savedIds.value - opportunityId
        _applications.value = _applications.value.filterNot { it.opportunityId == opportunityId }
    }

    fun addAnnouncement(announcement: CollegeAnnouncement) {
        _announcements.value = listOf(announcement) + _announcements.value
    }

    fun deleteAnnouncement(id: String) {
        _announcements.value = _announcements.value.filterNot { it.id == id }
    }

    private fun recalculatePersonalizedScores(profile: StudentProfile) {
        val studentSkills = profile.skills.map { it.lowercase() }
        val studentInterests = profile.interests.map { it.lowercase() }
        val goals = profile.careerGoals.map { it.lowercase() }

        _opportunities.value = _opportunities.value.map { opp ->
            var score = 65 // base score

            // Skill matches
            val matchingSkills = opp.requiredSkills.filter { req ->
                studentSkills.any { s -> req.lowercase().contains(s) || s.contains(req.lowercase()) }
            }
            score += (matchingSkills.size * 9).coerceAtMost(25)

            // Category and Career Goal match
            val catName = opp.category.displayName.lowercase()
            if (goals.any { catName.contains(it) }) {
                score += 8
            }

            // Interest matches
            if (studentInterests.any { opp.title.lowercase().contains(it) || opp.description.lowercase().contains(it) }) {
                score += 5
            }

            val finalScore = score.coerceIn(70, 99)
            val reason = when {
                matchingSkills.isNotEmpty() -> "Matches your verified ${matchingSkills.joinToString(", ")} skills and ${profile.department} curriculum."
                catName.contains("internship") -> "Strong industry experience for ${profile.academicYear} ${profile.degree} students."
                catName.contains("hackathon") -> "High prestige hackathon to showcase hands-on engineering."
                else -> "Curated for students at ${profile.college}."
            }

            opp.copy(matchScore = finalScore, matchReason = reason)
        }
    }
}
