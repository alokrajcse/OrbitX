package com.example.orbitx

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.orbitx.ChatRepository.setUserOffline
import com.example.orbitx.ChatRepository.setUserOnline
import com.example.orbitx.Navigation.AppNavigation
import com.example.orbitx.ui.theme.OrbitXTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    override fun onStart() {
        super.onStart()
        userId?.let { setUserOnline(it) }
    }

    override fun onStop() {
        super.onStop()
        userId?.let { setUserOffline(it) }
    }
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.BLACK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission()
            } else { }
        } else { }
        setContent {
            OrbitXTheme {
                AppNavigation(activity = this, intent = intent)
            }
        }
    }

    private fun requestNotificationPermission() {

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {

                    showToast("Notifications enabled")
                } else {
                    showToast("Notifications permission denied")
                }
            }
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
