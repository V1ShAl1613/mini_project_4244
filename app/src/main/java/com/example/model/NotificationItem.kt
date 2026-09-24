package com.example.model

enum class NotificationCategory(val title: String) {
    ALL("All"),
    IMPORTANT("Important"),
    RECOMMENDED("Recommended"),
    COLLEGE("College")
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val category: NotificationCategory,
    val isRead: Boolean = false,
    val relatedOpportunityId: String? = null
)
