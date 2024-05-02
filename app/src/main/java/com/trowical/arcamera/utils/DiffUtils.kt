package com.trowical.arcamera.utils

import androidx.recyclerview.widget.DiffUtil
import com.trowical.arcamera.model.DeepAREffect
import com.trowical.arcamera.model.MediaItemObj

object DiffUtils {

    val MEDIA_ITEM_DIFF_CALLBACK = object : DiffUtil.ItemCallback<MediaItemObj>() {
        override fun areItemsTheSame(
            oldItemObj: MediaItemObj, newItemObj: MediaItemObj
        ): Boolean = oldItemObj.id == newItemObj.id

        override fun areContentsTheSame(
            oldItemObj: MediaItemObj, newItemObj: MediaItemObj
        ): Boolean = oldItemObj == newItemObj
    }

    val EFFECT_DIFF_CALLBACK = object : DiffUtil.ItemCallback<DeepAREffect>() {
        override fun areItemsTheSame(
            oldItem: DeepAREffect, newItem: DeepAREffect
        ): Boolean = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: DeepAREffect, newItem: DeepAREffect
        ): Boolean = oldItem == newItem
    }

}