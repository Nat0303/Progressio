package com.example.notifications.ui.theme.models

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val date: String,
    val isRead: Boolean,
    val taskId: String
)