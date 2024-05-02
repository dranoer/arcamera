package com.trowical.arcamera.ui.viewer

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.trowical.arcamera.R
import com.trowical.arcamera.adapter.ViewPagerAdapter
import com.trowical.arcamera.databinding.ActivityMediaViewerBinding
import com.trowical.arcamera.model.MediaItemObj
import com.trowical.arcamera.utils.Data
import com.trowical.arcamera.utils.MEDIA_KEY
import com.trowical.arcamera.utils.shareMedia
import com.trowical.arcamera.utils.showDeleteDialog
import com.trowical.arcamera.viewmodel.DeleteMediaViewModel

class MediaViewerActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMediaViewerBinding.inflate(layoutInflater) }
    private val deleteMediaViewModel: DeleteMediaViewModel by viewModels()
    private var adapter: ViewPagerAdapter? = null
    private var mediaItemObj: MediaItemObj? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getExtras()
        setObservers()
        setListeners()
        setupViewPager()
    }

    /**
     * setup viewpager
     */
    private fun setupViewPager() {
        mediaItemObj?.let { media ->
            adapter = ViewPagerAdapter(
                supportFragmentManager, lifecycle, Data.getViewerFragments(media)
            )
            binding.viewPager.apply {
                offscreenPageLimit = 2
                this.adapter = this@MediaViewerActivity.adapter
            }
        }
    }

    /**
     * set observers
     */
    private fun setObservers() {
        deleteMediaViewModel.mediaDeleted.observe(this) { deleted ->
            if (deleted) {
                finish()
            }
        }

        deleteMediaViewModel.permissionForDeleteMedia.observe(this) { intentSender ->
            intentSender?.let {
                //launch request permission to delete video in newest versions of Android
                deleteVideoForResult.launch(IntentSenderRequest.Builder(it).build())
            }
        }
    }

    /**
     * activity result to delete media
     */
    private val deleteVideoForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                deleteMediaViewModel.deletePendingMedia()
            }
        }

    /**
     * set listeners
     */
    private fun setListeners() {
        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val zeroPos = position == 0
                binding.toolbar.apply {
                    //show-hide title
                    title = if (zeroPos) "" else "Details"
                    //show-hide buttons from toolbar
                    menu.getItem(0).isVisible = zeroPos
                    menu.getItem(1).isVisible = zeroPos
                    menu.getItem(2).isVisible = zeroPos
                }
            }
        })

        //button navigation back is clicked, show viewer or finish activity
        binding.toolbar.setNavigationOnClickListener {
            if (binding.viewPager.currentItem == 0) {
                finish()
            } else {
                binding.viewPager.setCurrentItem(0, true)
            }
        }

        binding.toolbar.inflateMenu(R.menu.menu_viewer)
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_info -> binding.viewPager.setCurrentItem(1, true)
                R.id.menu_share -> mediaItemObj?.let { shareMedia(it) }
                R.id.menu_delete -> mediaItemObj?.let { media ->
                    showDeleteDialog(media) {
                        deleteMediaViewModel.deleteMedia(media)
                    }
                }
            }
            false
        }

        /**
         * show viewer or finish activity
         */
        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.viewPager.currentItem == 0) {
                        finish()
                    } else {
                        binding.viewPager.currentItem = 0
                    }
                }
            }
        )
    }

    /**
     * get extras
     */
    @Suppress("DEPRECATION")
    private fun getExtras() {
        mediaItemObj = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(MEDIA_KEY, MediaItemObj::class.java)
        } else {
            intent.getParcelableExtra(MEDIA_KEY)
        }
    }

}