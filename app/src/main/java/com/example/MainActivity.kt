package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.browser.PrivacyEngine
import com.example.browser.ui.SakuraBrowserApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SakuraBrowserApp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Zero trace on destruction: wipe all ephemeral cache and session cookies
        PrivacyEngine.executeNuclearPurge(applicationContext, null)
    }
}
