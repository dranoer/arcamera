package com.trowical.arcamera.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.OnBackPressedCallback
import com.trowical.arcamera.adapter.ViewPagerAdapter
import com.trowical.arcamera.utils.checkPermissions
import com.trowical.arcamera.databinding.ActivityMainBinding
import com.trowical.arcamera.ui.navigation.CameraFragment
import com.trowical.arcamera.utils.Data
import com.trowical.arcamera.utils.message

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var adapter: ViewPagerAdapter? = null
    private var zoom = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkPermissions { granted ->
            if (granted) {
                setupViewPager()
                setListeners()
            } else {
                message("Permissions necessary...")
                finish()
            }
        }
    }

    /**
     * setup viewpager
     */
    private fun setupViewPager() {
        adapter = ViewPagerAdapter(
            supportFragmentManager, lifecycle, Data.getMainFragments()
        )
        binding.viewPager.apply {
            offscreenPageLimit = 2
            this.adapter = this@MainActivity.adapter
        }
    }

    private fun setListeners() {
        /**
         * navigate to camera fragment or close app
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
     * enable-disable swipe view pager
     */
    fun enableSwipeVP(enable: Boolean = false) {
        binding.viewPager.isUserInputEnabled = enable
    }

    /**
     * volume up-down listener
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            return when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    changeZoom(true)
                    true
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    changeZoom(false)
                    true
                }
                else -> super.dispatchKeyEvent(event)
            }
        }
        return super.dispatchKeyEvent(event)
    }

    /**
     * sum-rest 0.1f from zoom value
     *
     */
    private fun changeZoom(zoomIn: Boolean) {
        if (zoomIn) {
            if (zoom < 1f) {
                zoom += 0.1f
            }
        } else if (zoom > 0f) {
            zoom -= 0.1f
        }
        notifyCameraZoom()
    }

    /**
     * notify new zoom to fragment
     */
    private fun notifyCameraZoom() {
        adapter?.let {
            val cameraFragment = it.fragments[0]
            if (cameraFragment is CameraFragment) {
                cameraFragment.setZoom(zoom)
            }
        }
    }

    /**
     * navigate to library or camera
     */
    fun showLibrary(show: Boolean = true) {
        binding.viewPager.setCurrentItem(if (show) 1 else 0, true)
    }

}