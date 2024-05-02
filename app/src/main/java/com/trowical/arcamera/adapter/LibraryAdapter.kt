package com.trowical.arcamera.adapter

import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.trowical.arcamera.databinding.CardPhotoBinding
import com.trowical.arcamera.databinding.CardVideoBinding
import com.trowical.arcamera.model.MediaItemObj
import com.trowical.arcamera.utils.DiffUtils
import com.trowical.arcamera.viewholder.PhotoViewHolder
import com.trowical.arcamera.viewholder.VideoViewHolder
import java.lang.IllegalArgumentException

class LibraryAdapter(
    val notifyMediaClicked: (media: MediaItemObj) -> Unit
) : ListAdapter<MediaItemObj, RecyclerView.ViewHolder>(DiffUtils.MEDIA_ITEM_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> {
                return VideoViewHolder(
                    CardVideoBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                return PhotoViewHolder(
                    CardPhotoBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                throw IllegalArgumentException("Invalid view type section: ${this.javaClass.name}")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //get media
        val mediaItem = getItem(position)

        //check if is photo or video
        when (holder) {
            is PhotoViewHolder -> {
                //set thumbnail
                holder.setThumbnail(mediaItem.uri)
            }
            is VideoViewHolder -> {
                //set thumbnail
                holder.setThumbnail(mediaItem.uri)
                //set duration
                holder.setDuration(mediaItem.duration)
            }
        }

        holder.itemView.setOnClickListener { notifyMediaClicked(mediaItem) }

    }

    override fun getItemViewType(position: Int): Int = getItem(position).mediaType

}