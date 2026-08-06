package com.veroflow.verohealth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.veroflow.verohealth.data.repository.Session
import com.veroflow.verohealth.ui.navigation.VeroHealthNavGraph
import com.veroflow.verohealth.ui.theme.VeroHealthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VeroHealthAppRoot()
        }
    }
}

@Composable
fun VeroHealthAppRoot() {
    val themeMode by Session.themeMode
    VeroHealthTheme(themeMode = themeMode) {
        Surface(modifier = Modifier.fillMaxSize()) {
            VeroHealthNavGraph()
        }
    }
}
