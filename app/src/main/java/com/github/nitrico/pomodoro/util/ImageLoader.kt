package com.github.nitrico.pomodoro.util

import android.graphics.Bitmap
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget


interface ImageLoaderInterface {

    fun load(imageView: ImageView,
             url: String,
             circular: Boolean = false)

}


object ImageLoader : ImageLoaderInterface {

    override fun load(imageView: ImageView,
                      url: String,
                      circular: Boolean) {
        if (circular) Glide.with(imageView.context)
                .load(url)
                .asBitmap()
                .centerCrop()
                .into(object : BitmapImageViewTarget(imageView) {
                    override fun setResource(bitmap: Bitmap) {
                        val bm = RoundedBitmapDrawableFactory.create(imageView.context.resources, bitmap)
                        bm.isCircular = true
                        imageView.setImageDrawable(bm)
                    }
                })
        else Glide.with(imageView.context)
                .load(url)
                .crossFade()
                .into(imageView)
    }

}
