package com.gabr.gabc.qook.presentation.shared

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import java.io.File
import java.io.FileOutputStream

class ResizeImageUtil {
    companion object {
        fun resizeImageToFile(
            uri: Uri,
            contentProvider: ContentResolver,
            requiredSize: Int = 250,
            name: String = "photo"
        ): File {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(
                contentProvider.openInputStream(uri),
                null,
                options
            )
            var widthTmp = options.outWidth
            var heightTmp = options.outHeight
            var scale = 1
            while (true) {
                if (widthTmp / 2 < requiredSize || heightTmp / 2 < requiredSize) break
                widthTmp /= 2
                heightTmp /= 2
                scale *= 2
            }
            val options2 = BitmapFactory.Options()
            options2.inSampleSize = scale
            val bitmap =
                BitmapFactory.decodeStream(
                    contentProvider.openInputStream(uri),
                    null,
                    options2
                )

            val dir =
                File("${Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).absolutePath}/qook")
            if (!dir.exists()) dir.mkdir()
            val file = File(dir, "$name.jpg")
            file.createNewFile()
            val fOut = FileOutputStream(file)

            bitmap?.compress(Bitmap.CompressFormat.PNG, 90, fOut)
            fOut.flush()
            fOut.close()

            return file

        }
    }
}