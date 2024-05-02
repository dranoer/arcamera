package com.trowical.arcamera.model

import com.trowical.arcamera.utils.NO_EFFECT_KEY

data class DeepAREffect(
    val slot: String,
    val name: String,
    val img: Int,
    val action: Int? = null
) {

    fun getPath(): String = if (this.name == NO_EFFECT_KEY)
        NO_EFFECT_KEY else "file:///android_asset/${this.name}"

}