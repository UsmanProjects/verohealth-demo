package com.veroflow.verohealth.ui.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.ChatConversation
import com.veroflow.verohealth.data.model.MessageSender
import com.veroflow.verohealth.data.repository.HealthDataRepository

/** Screen 25a — Doctor Chat conversation list. */
@Composable
fun DoctorChatListScreen(
    onBack: () -> Unit,
    onOpenConversation: (ChatConversation) -> Unit
) {
    val conversations = HealthDataRepository.conversations

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
            items(conversations, key = { it.id }) { convo ->
                Card(onClick = { onOpenConversation(convo) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(convo.doctor.name, style = MaterialTheme.typography.titleMedium)
                            Text(convo.doctor.specialty.label, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                convo.lastMessage?.content ?: "No messages yet",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(convo.lastMessage?.timestamp ?: "", style = MaterialTheme.typography.bodyMedium)
                            if (convo.unreadCount > 0) {
                                Spacer(Modifier.height(4.dp))
                                Badge { Text("${convo.unreadCount}") }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Screen 25b — Doctor Chat window. */
@Composable
fun DoctorChatScreen(
    conversationId: String,
    onBack: () -> Unit
) {
    LaunchedEffect(conversationId) {
        HealthDataRepository.markConversationRead(conversationId)
    }
    val conversation = HealthDataRepository.conversations.firstOrNull { it.id == conversationId }
    var draft by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(conversation?.doctor?.name ?: "Chat") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                reverseLayout = false
            ) {
                items(conversation?.messages ?: emptyList()) { message ->
                    val isPatient = message.sender == MessageSender.PATIENT
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isPatient) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (isPatient) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(message.content, style = MaterialTheme.typography.bodyMedium)
                                Text(message.timestamp, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* emoji picker - mock */ }) {
                    Icon(Icons.Filled.EmojiEmotions, contentDescription = "Emoji")
                }
                IconButton(onClick = { /* camera - mock */ }) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = "Camera")
                }
                IconButton(onClick = { /* attachment - mock */ }) {
                    Icon(Icons.Filled.AttachFile, contentDescription = "Attachment")
                }
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = { Text("Type a message") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { /* voice note - mock */ }) {
                    Icon(Icons.Filled.Mic, contentDescription = "Voice Note")
                }
                IconButton(onClick = {
                    if (draft.isNotBlank()) {
                        HealthDataRepository.sendMessage(conversationId, draft)
                        draft = ""
                    }
                }) {
                    Icon(Icons.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
