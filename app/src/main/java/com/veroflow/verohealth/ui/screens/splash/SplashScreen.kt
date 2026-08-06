package com.veroflow.verohealth.ui.screens.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.BuildConfig
import kotlinx.coroutines.delay

/**
 * Screen 1 — Splash.
 * Displays ~2s while "initializing local resources", then routes to Onboarding
 * on first launch or straight to Welcome on subsequent launches, per spec.
 */
@Composable
fun SplashScreen(
    isFirstLaunch: Boolean,
    onFinished: (goToOnboarding: Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onFinished(isFirstLaunch)
    }

    val transition = rememberInfiniteTransition(label = "splash_loading")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "splash_progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "Splash screen, loading VeroHealth" },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(PaddingValues(24.dp))
        ) {
            Icon(
                imageVector = Icons.Filled.LocalHospital,
                contentDescription = "VeroHealth logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "VeroHealth",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Your health, in one place",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )
            CircularProgressIndicator(progress = { progress })
        }

        Text(
            text = "v${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
