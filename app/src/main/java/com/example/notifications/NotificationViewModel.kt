package com.example.notifications

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notifications.ui.theme.models.Notification

class NotificationViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    private val allNotifications = listOf(
        Notification("1", "Task 1", "New task assigned", "2 Feb", false, "task1"),
        Notification("2", "Task 1", "Task submitted", "3 Feb", false, "task1"),
        Notification("3", "Task 1", "Task approved", "3 Feb", false, "task1"),
        Notification("4", "Task 1", "Task completed", "3 Feb", true, "task1"),
        Notification("5", "Task 1", "Reminder", "3 Feb", true, "task1"),
        Notification("6", "Task 1", "Due date approaching", "3 Feb", true, "task1")
    )

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    private val _showMarkAllDialog = MutableLiveData<Boolean>()
    val showMarkAllDialog: LiveData<Boolean> get() = _showMarkAllDialog

    init {
        _notifications.value = allNotifications
    }

    fun showAllNotifications() {
        _notifications.value = allNotifications
    }

    fun showUnreadNotifications() {
        _notifications.value = allNotifications.filter { !it.isRead }
    }

    fun markAllAsRead() {
        val updated = allNotifications.map { it.copy(isRead = true) }
        _notifications.value = updated
    }

    fun showMarkAllDialog() {
        val dontAskAgain = sharedPreferences.getBoolean("dont_ask_again", false)
        if (dontAskAgain) {
            markAllAsRead()
        } else {
            _showMarkAllDialog.value = true
        }
    }

    fun setDontAskAgain(value: Boolean) {
        sharedPreferences.edit().putBoolean("dont_ask_again", value).apply()
    }

    fun onMarkAllDialogShown() {
        _showMarkAllDialog.value = false
    }

    fun hasUnreadNotifications(): Boolean {
        return _notifications.value?.any { !it.isRead } ?: false
    }
    fun getDontAskAgain(): Boolean {
        return sharedPreferences.getBoolean("dont_ask_again", false)
    }

    fun toggleNotificationReadState(notification: Notification) {
        // Create a copy of the notification with the toggled read state
        val updatedNotification = notification.copy(isRead = !notification.isRead)

        // Safely update the list
        val updatedList = _notifications.value?.toMutableList() ?: mutableListOf()
        updatedList.apply {
            val index = indexOfFirst { it.id == notification.id } // Find the notification by its id
            if (index != -1) {
                this[index] = updatedNotification // Replace the old notification with the updated one
            }
        }

        // Update the MutableLiveData with the new list
        _notifications.value = updatedList
    }
}