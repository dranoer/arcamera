package com.trowical.arcamera.ui.viewer

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trowical.arcamera.databinding.FragmentMediaInfoBinding
import com.trowical.arcamera.model.MediaItemObj
import com.trowical.arcamera.utils.MEDIA_KEY
import com.trowical.arcamera.utils.getDuration
import com.trowical.arcamera.utils.getSize

class MediaInfoFragment : Fragment() {

    private lateinit var binding: FragmentMediaInfoBinding
    private var mediaItemObj: MediaItemObj? = null

    companion object {
        @JvmStatic
        fun newInstance(mediaItemObj: MediaItemObj) =
            MediaInfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(MEDIA_KEY, mediaItemObj)
                }
            }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mediaItemObj = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(MEDIA_KEY, MediaItemObj::class.java)
            } else {
                it.getParcelable(MEDIA_KEY)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInfo()
    }

    /**
     * set media info
     */
    @SuppressLint("SetTextI18n")
    private fun setInfo() {
        mediaItemObj?.let { media ->
            //set name
            binding.txtName.text = media.name
            //set folder name
            media.folderName?.let { folderName ->
                binding.txtFolder.text = ".../${folderName}"
            }
            //set mime type
            binding.txtMimeType.text = media.mimeType
            //set media size
            binding.txtSize.text = media.size.getSize(requireContext())
            //set media date
            binding.txtDate.text = media.getDayMonthYear()
            setDuration()
            setResolution()
        }
    }

    /**
     * set media resolution
     */
    @SuppressLint("InlinedApi", "SetTextI18n")
    private fun setResolution() {
        mediaItemObj?.let { media ->
            media.uri?.let { uri ->
                if (media.isVideo()) {
                    val metadata = MediaMetadataRetriever()
                    metadata.setDataSource(requireContext(), uri)

                    val width = metadata.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
                    )
                    val height = metadata.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
                    )
                    binding.txtResolution.text = "${height}x${width}"
                    metadata.release()
                } else {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.txtResolution.text = "${bitmap.width}x${bitmap.height}"
                    inputStream?.close()
                }
            }
        }
    }

    /**
     * set video duration or hide text duration
     */
    private fun setDuration() {
        mediaItemObj?.let { media ->
            if (media.isVideo()) {
                binding.txtDuration.text = media.duration.getDuration()
            } else {
                binding.txtDurationTitle.visibility = View.GONE
                binding.txtDuration.visibility = View.GONE
                binding.separatorDuration.root.visibility = View.GONE
            }
        }
    }

}