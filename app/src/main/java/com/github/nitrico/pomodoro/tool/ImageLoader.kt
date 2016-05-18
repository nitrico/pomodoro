package com.github.nitrico.pomodoro.tool

import android.graphics.Bitmap
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget

/**
 * Interface to be implemented by an image loader
 */
interface ImageLoaderInterface {
    fun load(imageView: ImageView, url: String, circular: Boolean = false)
}

/**
 * ImageLoaderInterface implementation using Glide
 */
object ImageLoader : ImageLoaderInterface {

    override fun load(imageView: ImageView,
                      url: String,
                      circular: Boolean) {
        if (circular) Glide.with(imageView.context)
                .load(url)
                .asBitmap()
                .centerCrop()
                .into(object : BitmapImageViewTarget(imageView) {
                    override fun setResource(bm: Bitmap) {
                        val rbd = RoundedBitmapDrawableFactory.create(imageView.context.resources, bm)
                        rbd.isCircular = true
                        imageView.setImageDrawable(rbd)
                    }
                })
        else Glide.with(imageView.context).load(url).into(imageView)
    }

}
