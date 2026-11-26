package com.jayra.app.ui/anime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.jayra.app.data.AnimeSearchItem

@Composable fun AnimeHomeScreen(viewModel: AnimeViewModel = viewModel(), onAnimeClick: (String) -> Unit) {
    val state by viewModel.uiState.collectAsState()
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            state.error != null && state.popular.isEmpty() && state.ongoing.isEmpty() && state.completed.isEmpty() -> EmptyAnime("Gagal memuat anime", state.error ?: "Koneksi bermasalah", viewModel::retry)
            state.popular.isEmpty() && state.ongoing.isEmpty() && state.completed.isEmpty() -> EmptyAnime("Belum ada anime", "Daftar anime sedang kosong. Coba muat ulang.", viewModel::retry)
            else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
                if (state.ongoing.isNotEmpty()) item { AnimeSection("Sedang Tayang", state.ongoing, onAnimeClick) }
                if (state.popular.isNotEmpty()) item { AnimeSection("Populer", state.popular, onAnimeClick) }
                if (state.completed.isNotEmpty()) item { AnimeSection("Selesai", state.completed, onAnimeClick) }
            }
        }
    }
}
@Composable private fun EmptyAnime(title: String, message: String, retry: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(6.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center); Spacer(Modifier.height(14.dp)); Button(onClick = retry) { Text("Coba Lagi") }
    }
}
@Composable private fun AnimeSection(title: String, anime: List<AnimeSearchItem>, onClick: (String) -> Unit) {
    Text(title, Modifier.padding(16.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(anime) { AnimePosterCard(it, onClick) } }
}
@Composable fun AnimePosterCard(anime: AnimeSearchItem, onClick: (String) -> Unit) {
    Column(Modifier.width(120.dp).clickable { onClick(anime.link ?: "") }) {
        AsyncImage(anime.image, anime.title, Modifier.width(120.dp).height(180.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
        Spacer(Modifier.height(4.dp)); Text(anime.title ?: "Unknown", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, maxLines = 2)
        Text(anime.latestEpisode ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
