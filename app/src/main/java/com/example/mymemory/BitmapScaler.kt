package com.example.mymemory

import android.graphics.Bitmap

object BitmapScaler {

    //維持比例並調整到適合寬度
    //
    fun scaleToFitWidth(b: Bitmap, width: Int): Bitmap {
        val factor = width / b.width.toFloat()
        return Bitmap.createScaledBitmap(b, width, (b.height * factor).toInt(), true)
    }

    //維持比例並調整到適合高度
    fun scaleToFitHeight(b: Bitmap, height: Int): Bitmap {
        val factor = height / b.height.toFloat()
        return Bitmap.createScaledBitmap(b,  (b.width * factor).toInt(), height,true)
    }
}
