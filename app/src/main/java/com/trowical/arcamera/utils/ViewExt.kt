package com.trowical.arcamera.utils

import android.widget.ImageButton
import androidx.core.content.ContextCompat

/**
 * animate alpha 0 - 1
 * animate scale 0.5 - 1
 */
fun ImageButton.scaleAnim(icon: Int) {
    this.animate()
        .scaleY(0.5f)
        .scaleX(0.5f)
        .alpha(0f)
        .setDuration(100).withEndAction {
            this.setImageDrawable(ContextCompat.getDrawable(context, icon))
            this.animate()
                .scaleY(1.0f)
                .scaleX(1.0f)
                .alpha(1f)
                .duration = 100
        }
}