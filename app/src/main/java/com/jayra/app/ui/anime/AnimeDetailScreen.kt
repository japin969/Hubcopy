package com.jayra.app.ui.anime

import android.os.Message
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@Composable fun AnimeDetailScreen(animeUrl: String, viewModel: AnimeViewModel = viewModel(), onPlayEpisode: (String) -> Unit) {
    val state by viewModel.uiState.collectAsState(); LaunchedEffect(animeUrl) { viewModel.loadDetail(animeUrl) }
    if (state.isLoading) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    else if (state.error != null) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Error: ${state.error}") }
    else LazyColumn(Modifier.fillMaxSize()) {
        item { Row(Modifier.padding(16.dp)) { AsyncImage(state.detail?.image, null, Modifier.width(120.dp).height(180.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop); Spacer(Modifier.width(16.dp)); Column { Text(state.detail?.title ?: "", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge); Spacer(Modifier.height(8.dp)); Text("Rating: ${state.detail?.rating ?: "-"}", style = MaterialTheme.typography.bodySmall); Text("Total: ${state.detail?.totalEpisodes ?: 0} Episode", style = MaterialTheme.typography.bodySmall); Spacer(Modifier.height(8.dp)); Text(state.detail?.synopsis ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 6) } } }
        item { Text("Daftar Episode", Modifier.padding(16.dp), fontWeight = FontWeight.Bold) }
        items(state.episodes) { ep -> ListItem(headlineContent = { Text(ep.title ?: "Episode ${ep.episode}") }, leadingContent = { Icon(Icons.Rounded.PlayArrow, null) }, modifier = Modifier.clickable { ep.episodeUrl?.let(onPlayEpisode) }); HorizontalDivider() }
    }
}

@Composable fun AnimePlayerScreen(videoUrl: String) {
    AndroidView(factory = { ctx -> WebView(ctx).apply {
        settings.javaScriptEnabled = true; settings.domStorageEnabled = true; settings.loadWithOverviewMode = true; settings.useWideViewPort = true
        webChromeClient = object : WebChromeClient() { override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?) = false }
        webViewClient = object : WebViewClient() { override fun onPageFinished(view: WebView?, url: String?) { super.onPageFinished(view, url); view?.evaluateJavascript("document.querySelectorAll('[class*=ads], [id*=ads], [class*=banner], [class*=popup], .overlay, .ad-container, iframe[src*=ads]').forEach(function(el){el.style.display='none'});", null) } }
        loadUrl(videoUrl)
    } }, modifier = Modifier.fillMaxSize())
}
