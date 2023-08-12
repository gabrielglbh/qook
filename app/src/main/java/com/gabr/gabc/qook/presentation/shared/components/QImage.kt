package com.gabr.gabc.qook.presentation.shared.components

import android.os.Build.VERSION.SDK_INT
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
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
import com.gabr.gabc.qook.presentation.theme.seed

enum class QImageType {
    NETWORK, GIF
}

@Composable
fun QImage(
    modifier: Modifier = Modifier,
    uri: String? = null,
    @DrawableRes resource: Int? = null,
    type: QImageType = QImageType.NETWORK,
) {
    if (type == QImageType.GIF && resource == null) {
        throw Exception("Should provide a resource such as R.drawable.xxx with type GIF")
    }
    if (type == QImageType.NETWORK && uri == null) {
        throw Exception("Should provide a resource such as https://domain.es/image.png with type NETWORK")
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
            return Image(
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
            return SubcomposeAsyncImage(
                model = uri!!,
                loading = {
                    CircularProgressIndicator()
                },
                error = {
                    Image(
                        painter = painterResource(R.drawable.no_image),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(seed)
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
fun QImageGIFPreview() {
    return QImage(resource = R.drawable.loading, type = QImageType.GIF)
}

@Preview
@Composable
fun QImageNetworkPreview() {
    return QImage(uri = "https://avatars.githubusercontent.com/u/26835924?v=4")
}