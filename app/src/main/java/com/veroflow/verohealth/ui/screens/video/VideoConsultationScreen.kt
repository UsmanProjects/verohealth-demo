package com.veroflow.verohealth.ui.screens.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.Doctor
import kotlinx.coroutines.delay

/** Screen 26 — Video Consultation (Simulation). No real video streaming. */
@Composable
fun VideoConsultationScreen(
    doctor: Doctor,
    onEndCall: () -> Unit,
    onOpenChat: () -> Unit
) {
    var micEnabled by remember { mutableStateOf(true) }
    var cameraEnabled by remember { mutableStateOf(true) }
    var speakerEnabled by remember { mutableStateOf(true) }
    var seconds by remember { mutableStateOf(0) }
    var showEndDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds++
        }
    }

    val minutesPart = seconds / 60
    val secondsPart = seconds % 60

    Surface(modifier = Modifier.fillMaxSize(), color = androidx.compose.ui.graphics.Color(0xFF0E1615)) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Doctor's video window (mock — just an icon)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = "${doctor.name} video", tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(120.dp))
                Text(doctor.name, color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.titleMedium)
                Text(
                    "%02d:%02d".format(minutesPart, secondsPart),
                    color = androidx.compose.ui.graphics.Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Patient's small video window (mock)
            Surface(
                color = androidx.compose.ui.graphics.Color(0xFF283533),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(100.dp, 140.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        if (cameraEnabled) Icons.Filled.Person else Icons.Filled.VideocamOff,
                        contentDescription = "Your video",
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
            }

            // Controls
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ControlButton(if (micEnabled) Icons.Filled.Mic else Icons.Filled.MicOff, "Microphone") { micEnabled = !micEnabled }
                ControlButton(if (cameraEnabled) Icons.Filled.Videocam else Icons.Filled.VideocamOff, "Camera") { cameraEnabled = !cameraEnabled }
                ControlButton(Icons.Filled.VolumeUp, "Speaker", tinted = speakerEnabled) { speakerEnabled = !speakerEnabled }
                ControlButton(Icons.Filled.Chat, "Chat") { onOpenChat() }
                ControlButton(Icons.Filled.Description, "Share Report") { /* mock */ }
                ControlButton(Icons.Filled.CallEnd, "End Call", isDanger = true) { showEndDialog = true }
            }
        }
    }

    if (showEndDialog) {
        AlertDialog(
            onDismissRequest = { showEndDialog = false },
            title = { Text("End Consultation?") },
            text = { Text("Are you sure you want to end this video consultation?") },
            confirmButton = { TextButton(onClick = onEndCall) { Text("End Call") } },
            dismissButton = { TextButton(onClick = { showEndDialog = false }) { Text("Continue") } }
        )
    }
}

@Composable
private fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isDanger: Boolean = false,
    tinted: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            icon,
            contentDescription = label,
            tint = when {
                isDanger -> MaterialTheme.colorScheme.error
                tinted -> androidx.compose.ui.graphics.Color.White
                else -> androidx.compose.ui.graphics.Color.Gray
            }
        )
    }
}
