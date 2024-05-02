package com.trowical.arcamera.viewholder

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.trowical.arcamera.databinding.CardPhotoBinding
import com.trowical.arcamera.utils.setThumbnail

class PhotoViewHolder(private val binding: CardPhotoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    /**
     * set image
     */
    fun setThumbnail(uri: Uri?) {
        uri?.let {
            binding.thumbnail.setThumbnail(it)
        }
    }

}