package com.gabr.gabc.qook.presentation.shared

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.gabr.gabc.qook.R

enum class QImageType {
    ASSET, NETWORK, GIF
}

@Composable
fun QImage(
    modifier: Modifier = Modifier,
    uri: String? = null,
    resource: Int? = null,
    type: QImageType = QImageType.NETWORK,
) {
    if ((type == QImageType.ASSET || type == QImageType.GIF) && resource == null) {
        throw Exception("Should provide a resource such as R.drawable.xxx with type ASSET")
    }
    if (type == QImageType.NETWORK && uri == null) {
        throw Exception("Should provide a resource such as https://domain.es/image.png with type NETWORK")
    }

    when (type) {
        QImageType.ASSET -> {
            return Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.wrapContentSize()
            ) {
                Image(
                    painter = painterResource(resource!!),
                    contentDescription = null,
                    modifier = modifier,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            }
        }
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
            return Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(resource)
                        .build(), imageLoader = imageLoader
                ),
                contentDescription = null,
                modifier = modifier,
            )
        }
        else -> {
            return SubcomposeAsyncImage(
                model = uri!!,
                loading = {
                    CircularProgressIndicator()
                },
                error = {
                    Image(
                        painter = painterResource(R.drawable.no_image),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary)
                    )
                },
                contentDescription = "",
                modifier = modifier
            )
        }
    }
}

@Preview
@Composable
fun QImageAssetPreview() {
    return QImage(resource = R.drawable.no_image, type = QImageType.ASSET)
}

@Preview
@Composable
fun QImageGIFPreview() {
    return QImage(resource = R.drawable.loading, type = QImageType.GIF)
}

@Preview
@Composable
fun QImageNetworkPreview() {
    return QImage(uri = "https://avatars.githubusercontent.com/u/26835924?v=4")
}