package com.example.model

data class CollegeAnnouncement(
    val id: String,
    val title: String,
    val department: String,
    val date: String,
    val category: String, // "Academic", "Placement", "Exam", "Hackathon", "Event"
    val content: String,
    val isUrgent: Boolean = false,
    val actionUrl: String = ""
)
