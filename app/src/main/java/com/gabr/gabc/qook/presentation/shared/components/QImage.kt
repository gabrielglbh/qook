package com.gabr.gabc.qook.presentation.shared.components

import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

enum class QImageType {
    NETWORK, GIF
}

@Composable
fun QImage(
    modifier: Modifier = Modifier,
    uri: Uri = Uri.EMPTY,
    @DrawableRes resource: Int? = null,
    type: QImageType = QImageType.NETWORK,
) {
    if (type == QImageType.GIF && resource == null) {
        throw Exception("Should provide a resource such as R.drawable.xxx with type GIF")
    }

    when (type) {
        QImageType.GIF -> {
            val imageLoader = ImageLoader.Builder(LocalContext.current)
                .components {
                    if (SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(resource)
                        .build(), imageLoader = imageLoader
                ),
                contentDescription = null,
                modifier = modifier
            )
        }

        else -> {
            SubcomposeAsyncImage(
                model = uri.toString(),
                error = {
                    Icon(
                        Icons.Outlined.ImageNotSupported,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                contentDescription = "",
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        }
    }
}