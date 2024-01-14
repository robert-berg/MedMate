package com.example.medmate

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.medmate.adherence_repository.AdherenceRemoteDataSource
import com.example.medmate.databinding.ActivityMainBinding
import com.example.medmate.ui.FullScreenDialogAddFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var adherenceRemoteDataSource: AdherenceRemoteDataSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_add, R.id.navigation_medicine_list, R.id.navigation_history
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.setupWithNavController(navController)
        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_add -> {
                    showAddModal()
                    false // Return false to not perform any navigation
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(item, navController)
                    true // Allow normal navigation for other items
                }
            }
        }
        val currentDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
        supportActionBar?.title = currentDate
        initializeAdherenceListener()
    }
    private fun initializeAdherenceListener()
    {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val ipAddress = sharedPreferences.getString("IPAddress", "192.168.0.105")
        adherenceRemoteDataSource = AdherenceRemoteDataSource.getInstance(ipAddress)
    }
    private fun showAddModal() {
        Toast.makeText(this, "Add button clicked", Toast.LENGTH_SHORT).show()

        val dialogFragment = FullScreenDialogAddFragment()
        dialogFragment.show(supportFragmentManager, "FullScreenDialogFragmentTag")

    }
}
