package com.example.model

enum class ApplicationStatus(val title: String, val stepIndex: Int) {
    SAVED("Saved", 0),
    APPLIED("Applied", 1),
    SHORTLISTED("Shortlisted", 2),
    INTERVIEW("Interview", 3),
    SELECTED("Selected", 4),
    REJECTED("Rejected", 4)
}

data class ApplicationItem(
    val id: String,
    val opportunityId: String,
    val opportunityTitle: String,
    val organization: String,
    val category: OpportunityCategory,
    val deadline: String,
    val status: ApplicationStatus = ApplicationStatus.SAVED,
    val applicationDate: String = "21 Sep 2026",
    val notes: String = "",
    val matchScore: Int = 90
)
