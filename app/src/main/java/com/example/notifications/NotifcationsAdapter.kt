package com.example.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notifications.databinding.ItemNotificationBinding
import com.example.notifications.ui.theme.models.Notification

class NotificationsAdapter(
    private var notifications: List<Notification>,
    private val onItemClick: (Notification) -> Unit,
    private val onTickClick: (Notification) -> Unit // Add a callback for the tick click event
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            with(binding) {
                notificationTitle.text = notification.title
                notificationDate.text = notification.date

                // Handle the notification's read state
                if (notification.isRead) {
                    notificationTitle.alpha = 0.6f
                    notificationDate.alpha = 0.6f
                    notificationTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else {
                    val dotDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_dot)
                    notificationTitle.setCompoundDrawablesWithIntrinsicBounds(dotDrawable, null, null, null)
                    notificationTitle.compoundDrawablePadding = 8
                    notificationTitle.alpha = 1f
                    notificationDate.alpha = 1f
                }

                // Set up the tick icon for marking notifications as read
                val tickIcon = binding.tickIcon
                if (notification.isRead) {
                    tickIcon.setImageResource(R.drawable.ic_check) // Set the checked tick icon
                } else {
                    tickIcon.setImageResource(R.drawable.ic_check) // Set the unchecked tick icon
                }

                // Handle tick icon click
                tickIcon.setOnClickListener {
                    onTickClick(notification) // Call the callback when tick is clicked
                }

                // Handle notification click
                root.setOnClickListener { onItemClick(notification) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount() = notifications.size

    fun updateNotifications(newNotifications: List<Notification>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }
}
