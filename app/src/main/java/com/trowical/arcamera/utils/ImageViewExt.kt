package com.trowical.arcamera.utils

import android.net.Uri
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.isVisible
import com.trowical.arcamera.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

/**
 * show thumbnail with transition
 */
fun ImageView.setThumbnail(uri: Uri, withTransition: Boolean = true) {
    if (withTransition) {
        Glide.with(context)
            .load(uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    } else {
        Glide.with(context)
            .load(uri)
            .into(this)
    }
}

/**
 * set icon effect
 */
fun ImageView.setIconEffect(icon: Int) {
    Glide.with(context)
        .load(icon)
        .apply(RequestOptions().error(R.drawable.shape_no_effect))
        .into(this)
}

/**
 * start recording animation
 */
fun ImageView.startRecAnimation() {
    isVisible = true
    val animation = AnimationUtils.loadAnimation(context, R.anim.anim_recording)
    startAnimation(animation)
}

/**
 * stop recording animation
 */
fun ImageView.stopRecAnimation() {
    isVisible = false
    clearAnimation()
}

