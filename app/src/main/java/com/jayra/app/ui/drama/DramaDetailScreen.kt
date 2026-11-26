package com.jayra.app.ui.drama

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage

@Composable fun DramaDetailScreen(dramaUrl: String, viewModel: DramaViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState(); var playingUrl by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(dramaUrl) { viewModel.loadDetail(dramaUrl) }
    if (playingUrl != null) { FullScreenVideoPlayer(playingUrl!!, onBack = { playingUrl = null }); return }
    if (state.isLoading) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    else if (state.error != null) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Error: ${state.error}", color = Color.Red) }
    else LazyColumn(Modifier.fillMaxSize()) {
        item { Row(Modifier.padding(16.dp)) {
            AsyncImage(state.detail?.cover, null, Modifier.width(120.dp).height(180.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(16.dp)); Column {
                Text(state.detail?.title ?: "Judul Drama", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp)); Text("${state.detail?.chapterCount ?: 0} Episode", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp)); Text(state.detail?.intro ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 5)
            }
        } }
        item { Text("Daftar Episode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
        items(state.episodes) { ep ->
            val unlocked = ep.unlock == true && ep.videoUrl != null
            ListItem(headlineContent = { Text(ep.name ?: "Episode ${ep.index}") }, supportingContent = { Text(if (unlocked) "Tersedia" else "Terkunci") }, leadingContent = { Icon(Icons.Rounded.PlayArrow, null, tint = if (unlocked) MaterialTheme.colorScheme.primary else Color.Gray) }, modifier = Modifier.clickable(enabled = unlocked) { playingUrl = ep.mp4 ?: ep.videoUrl })
            HorizontalDivider()
        }
    }
}

@Composable fun FullScreenVideoPlayer(videoUrl: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val player = remember(videoUrl) { ExoPlayer.Builder(context).build().apply { setMediaItem(MediaItem.fromUri(videoUrl)); prepare(); playWhenReady = true } }
    DisposableEffect(player) { onDispose { player.release() } }
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(factory = { ctx -> PlayerView(ctx).apply { this.player = player; useController = true } }, modifier = Modifier.fillMaxWidth().align(Alignment.Center).aspectRatio(16f / 9f))
        TextButton(onClick = onBack, modifier = Modifier.padding(16.dp)) { Text("Tutup", color = Color.White) }
    }
}
