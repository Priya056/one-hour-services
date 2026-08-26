package com.marketplace.onehour.customer.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.marketplace.onehour.common.components.InitialsAvatar
import com.marketplace.onehour.common.placeholders.FirebaseChatPlaceholder
import com.marketplace.onehour.common.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    bookingId: String,
    helperId: String,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(bookingId, helperId) {
        viewModel.loadChatDetails(bookingId, helperId)
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (state.helper != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box {
                                if (state.helper!!.photoUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(state.helper!!.photoUrl)
                                            .crossfade(250)
                                            .build(),
                                        contentDescription = state.helper!!.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    InitialsAvatar(name = state.helper!!.name, size = 40.dp, shape = CircleShape)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = state.helper!!.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "1-Hour ${state.helper!!.mainCategory} • Online",
                                    fontSize = 11.sp,
                                    color = SuccessGreen
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Call helper */ }) {
                        Icon(Icons.Default.Call, contentDescription = "Call Helper")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Quick Action Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        listOf("Are you nearby?", "Call me please", "Where are you?", "Gate pin is 1234").forEach { quickMsg ->
                            SuggestionChip(
                                onClick = { viewModel.sendMessage(quickMsg) },
                                label = { Text(quickMsg, fontSize = 11.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Message Input Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { /* Attachment picker */ }) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = "Attach", tint = MaterialTheme.colorScheme.primary)
                        }

                        OutlinedTextField(
                            value = state.messageText,
                            onValueChange = { viewModel.onMessageTextChanged(it) },
                            placeholder = { Text("Type a message to helper...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        val haptics = LocalHapticFeedback.current
                        FloatingActionButton(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.sendMessage()
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send Message", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Message Bubble List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.messages) { message ->
                    val isUserMessage = message.senderId == "c1"
                    ChatBubble(message = message, isUserMessage = isUserMessage)
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: FirebaseChatPlaceholder.ChatMessage,
    isUserMessage: Boolean
) {
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormatter.format(Date(message.timestamp)) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUserMessage) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isUserMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUserMessage) 16.dp else 2.dp,
                bottomEnd = if (isUserMessage) 2.dp else 16.dp
            )
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = message.text,
                    color = if (isUserMessage) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedTime,
                        fontSize = 10.sp,
                        color = if (isUserMessage) Color.White.copy(alpha = 0.7f) else Color.Gray
                    )
                    if (isUserMessage) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
