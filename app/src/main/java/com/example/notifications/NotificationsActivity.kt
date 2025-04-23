package com.example.notifications


// change here test git
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notifications.databinding.ActivityNotificationsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import android.widget.Button
import android.widget.CheckBox
import android.content.SharedPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var viewModel: NotificationViewModel
    private lateinit var adapter: NotificationsAdapter
    private lateinit var sharedPreferences: SharedPreferences // Declare sharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE) // Initialize sharedPreferences here

        // Create ViewModel using ViewModelFactory
        val factory = NotificationViewModelFactory(sharedPreferences)
        viewModel = ViewModelProvider(this, factory)[NotificationViewModel::class.java]

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.notifications_title)
        }

        // Handle back press using dispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish() // Or custom behavior
            }
        })

        setupRecyclerView()
        setupTabs()

        // 👇 Add this line to initialize the first tab as selected
        binding.tabLayout.getTabAt(0)?.select()

        observeViewModel()
        binding.bottomNavigation.selectedItemId = R.id.navigation_tasks
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notifications_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val markAllItem = menu?.findItem(R.id.action_mark_all_read)
        val currentTab = binding.tabLayout.selectedTabPosition

        val shouldShow = currentTab == 1 &&
                !viewModel.getDontAskAgain() &&
                viewModel.hasUnreadNotifications()

        markAllItem?.isVisible = shouldShow
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_mark_all_read -> {
                viewModel.showMarkAllDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with both item click and tick click handlers
        adapter = NotificationsAdapter(emptyList(), { notification ->
            // Handle notification click
            // For example, navigate to a details screen or show more info
        }, { notification ->
            // Handle tick click, mark notification as read/unread
            viewModel.toggleNotificationReadState(notification)
        })

        binding.notificationsRecyclerView.adapter = adapter
    }


    private fun setupTabs() {
        // Set Tab Mode explicitly to avoid any layout issues
        binding.tabLayout.tabMode = TabLayout.MODE_FIXED

        // Ensure tabs are added if not already in the XML
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("All"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Unread"))

        // Set up the tab selection listener
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        viewModel.showAllNotifications()
                        invalidateOptionsMenu()
                    }

                    1 -> {
                        viewModel.showUnreadNotifications()
                        invalidateOptionsMenu()
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }



    private fun observeViewModel() {
        viewModel.notifications.observe(this) {
            adapter.updateNotifications(it)
        }

        viewModel.showMarkAllDialog.observe(this) { showDialog ->
            if (showDialog == true) {
                showMarkAllReadConfirmation()
                viewModel.onMarkAllDialogShown()
            }
        }
    }

    private fun showMarkAllReadConfirmation() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_mark_all_read, null)
        val dontAskAgainCheckbox = dialogView.findViewById<CheckBox>(R.id.dontAskAgainCheckbox)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        val markAllButton = dialogView.findViewById<Button>(R.id.markAllButton)

        val dialog = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        markAllButton.setOnClickListener {
            val dontAskAgain = dontAskAgainCheckbox.isChecked
            viewModel.setDontAskAgain(dontAskAgain)
            viewModel.markAllAsRead()
            dialog.dismiss()
        }

        dialog.show()
    }

}
