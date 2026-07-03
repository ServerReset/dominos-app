package com.dominos.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@Composable
fun DominosProductImage(
    productCode: String,
    modifier: Modifier = Modifier.size(80.dp),
    contentScale: ContentScale = ContentScale.Fit
) {
    val imageUrl = "https://cache.dominos.com/olo/6_47_2/assets/build/market/US/_en/images/img/products/larges/${productCode}.jpg"
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
        error = { Icon(Icons.Default.Fastfood, contentDescription = null, modifier = Modifier.size(32.dp)) },
        loading = { Icon(Icons.Default.Fastfood, contentDescription = null, modifier = Modifier.size(32.dp)) }
    )
}
