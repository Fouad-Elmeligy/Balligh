package com.example.ballighandroidapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ballighandroidapp.approot.AppRoot
import com.example.ballighandroidapp.helpers.LocaleHelper
import com.example.ballighandroidapp.ui.theme.BallighTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Wrap the context with the saved locale before the activity is created
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BallighTheme {
                AppRoot()
            }
        }
    }
}
