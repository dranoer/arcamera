package com.trowical.arcamera.ui.viewer

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.trowical.arcamera.databinding.FragmentMediaViewerBinding
import com.trowical.arcamera.model.MediaItemObj
import com.trowical.arcamera.utils.MEDIA_KEY
import com.trowical.arcamera.utils.setThumbnail
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class MediaViewerFragment : Fragment() {

    private val exoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }
    private lateinit var binding: FragmentMediaViewerBinding
    private var mediaItemObj: MediaItemObj? = null

    companion object {
        @JvmStatic
        fun newInstance(mediaItemObj: MediaItemObj) =
            MediaViewerFragment().apply {
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
        binding = FragmentMediaViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewsVisibility()
        playVideoOrSetThumbnail()
    }

    /**
     * show-hide views
     */
    private fun setViewsVisibility() {
        mediaItemObj?.let {
            binding.playerView.isVisible = it.isVideo()
            binding.thumbnail.isVisible = !it.isVideo()
        }
    }

    /**
     * play video
     */
    private fun playVideoOrSetThumbnail() {
        mediaItemObj?.let {
            it.uri?.let { uri ->
                if (it.isVideo()) {
                    binding.playerView.player = exoPlayer
                    exoPlayer.setMediaItem(MediaItem.fromUri(uri))
                    exoPlayer.playWhenReady = true
                    exoPlayer.seekTo(0, 0)
                    exoPlayer.prepare()
                } else {
                    binding.thumbnail.setThumbnail(uri)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.pause()
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

}