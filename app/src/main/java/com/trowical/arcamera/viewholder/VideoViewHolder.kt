package com.trowical.arcamera.viewholder

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.trowical.arcamera.databinding.CardVideoBinding
import com.trowical.arcamera.utils.getDuration
import com.trowical.arcamera.utils.setThumbnail

class VideoViewHolder(private val binding: CardVideoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    /**
     * set image
     */
    fun setThumbnail(uri: Uri?) {
        uri?.let {
            binding.thumbnail.setThumbnail(it)
        }
    }

    /**
     * set video duration
     */
    fun setDuration(videoDuration: Long) {
        binding.txtDuration.text = videoDuration.getDuration()
    }

}