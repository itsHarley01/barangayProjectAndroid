package com.app.barangayguadalupe

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class InputStreamImageLoader : NewsAdapter.ImageLoader {
    override fun loadImage(imageUrl: String, targetImageView: ImageView) {
        Thread {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                val bitmap: Bitmap = BitmapFactory.decodeStream(input)

                targetImageView.post {
                    targetImageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}
