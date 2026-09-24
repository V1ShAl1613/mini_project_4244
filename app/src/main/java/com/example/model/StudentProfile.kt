package com.example.model

data class StudentProfile(
    val name: String = "Aarav Sharma",
    val email: String = "aarav.sharma@campus.edu.in",
    val college: String = "Indian Institute of Information Technology (IIIT)",
    val degree: String = "B.Tech",
    val department: String = "Computer Science & Engineering",
    val academicYear: String = "3rd Year",
    val semester: String = "6th Semester",
    val cgpa: String = "8.9",
    val skills: List<String> = listOf("Python", "AI/ML", "React", "SQL", "Git", "Docker"),
    val interests: List<String> = listOf("AI & Machine Learning", "Software Development", "Cloud Computing", "Research"),
    val careerGoals: List<String> = listOf("Internship", "Research", "Job"),
    val isOnboarded: Boolean = true
) {
    fun calculateCompletionPercentage(): Int {
        var score = 0
        if (name.isNotBlank()) score += 15
        if (email.isNotBlank()) score += 10
        if (college.isNotBlank()) score += 15
        if (degree.isNotBlank() && department.isNotBlank()) score += 15
        if (academicYear.isNotBlank() && semester.isNotBlank()) score += 15
        if (skills.isNotEmpty()) score += 15
        if (interests.isNotEmpty()) score += 15
        return score.coerceAtMost(100)
    }
}
