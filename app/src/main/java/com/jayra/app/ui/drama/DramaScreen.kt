package com.jayra.app.ui.drama

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.jayra.app.data.DramaItem

@Composable
fun DramaScreen(
    viewModel: DramaViewModel = viewModel(),
    onDramaClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            state.featured.isEmpty() && state.error != null -> EmptyDrama("Gagal memuat drama", state.error ?: "Koneksi bermasalah", viewModel::retry)
            state.featured.isEmpty() -> EmptyDrama("Belum ada drama", "Daftar drama sedang kosong. Coba muat ulang.", viewModel::retry)
            else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
                item { BannerItem(drama = state.featured.first()) { onDramaClick(it) } }
                if (state.featured.size > 1) item {
                    Text("Trending Minggu Ini", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.featured.drop(1)) { drama -> DramaPosterCard(drama) { onDramaClick(it) } }
                    }
                }
            }
        }
    }
}

@Composable private fun EmptyDrama(title: String, message: String, retry: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(14.dp)); Button(onClick = retry) { Text("Coba Lagi") }
    }
}

@Composable fun BannerItem(drama: DramaItem, onClick: (String) -> Unit) {
    Box(Modifier.fillMaxWidth().height(450.dp).clickable { onClick(drama.url ?: "") }) {
        AsyncImage(model = drama.cover, contentDescription = drama.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black), startY = 300f)))
        Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text(drama.title ?: "Unknown", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Spacer(Modifier.height(4.dp)); Text(drama.intro ?: "", style = MaterialTheme.typography.bodySmall, color = Color.LightGray, maxLines = 2)
            Spacer(Modifier.height(12.dp)); Button(onClick = { onClick(drama.url ?: "") }) { Text("Tonton Sekarang") }
        }
    }
}

@Composable fun DramaPosterCard(drama: DramaItem, onClick: (String) -> Unit) {
    Column(Modifier.width(120.dp).clickable { onClick(drama.url ?: "") }) {
        AsyncImage(model = drama.cover, contentDescription = drama.title, modifier = Modifier.width(120.dp).height(180.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
        Spacer(Modifier.height(8.dp)); Text(drama.title ?: "Unknown", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
        Text("${drama.chapterCount ?: 0} Episode", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
