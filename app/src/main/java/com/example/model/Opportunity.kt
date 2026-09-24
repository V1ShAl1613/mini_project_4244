package com.example.model

enum class OpportunityCategory(val displayName: String) {
    ALL("All"),
    INTERNSHIP("Internships"),
    JOB("Jobs"),
    HACKATHON("Hackathons"),
    SCHOLARSHIP("Scholarships"),
    COMPETITION("Competitions"),
    WORKSHOP("Workshops"),
    RESEARCH("Research"),
    EVENT("Events")
}

data class Opportunity(
    val id: String,
    val title: String,
    val organization: String,
    val category: OpportunityCategory,
    val location: String,
    val isRemote: Boolean = false,
    val deadline: String, // e.g. "2026-09-24", "Tomorrow", "14 Oct 2026"
    val daysRemaining: Int, // e.g. 1 (tomorrow), 3, 7, 20
    val stipendOrSalary: String, // e.g. "₹45,000/month", "₹1,00,000 Cash Prize", "Fully Funded", "Free"
    val requiredSkills: List<String>,
    val eligibility: String,
    val description: String,
    val applicationLink: String = "https://campusconnect.edu/apply",
    val postedDate: String = "20 Sep 2026",
    val isPaid: Boolean = true,
    val isTrending: Boolean = false,
    val matchScore: Int = 85,
    val matchReason: String = "Matches your department and skill profile."
)

data class AiMatchResult(
    val matchScore: Int,
    val reason: String,
    val priority: String // "high", "medium", "low"
)
