package com.trowical.arcamera.ui.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Size
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.trowical.arcamera.ui.custom.ActionButton
import com.trowical.arcamera.ui.camera.CameraHelper
import com.trowical.arcamera.ui.custom.SliderLayoutManager
import com.trowical.arcamera.adapter.EffectAdapter
import com.trowical.arcamera.adapter.ViewPagerAdapter
import com.trowical.arcamera.databinding.FragmentCameraBinding
import com.trowical.arcamera.model.DeepAREffect
import com.trowical.arcamera.model.DeepARResult
import com.trowical.arcamera.ui.MainActivity
import com.trowical.arcamera.utils.*
import com.trowical.arcamera.viewmodel.LibraryViewModel
import com.trowical.arcamera.viewmodel.SaveImageViewModel
import com.trowical.arcamera.viewmodel.SaveVideoViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator

class CameraFragment : Fragment(), SurfaceHolder.Callback {

    private lateinit var binding: FragmentCameraBinding
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val saveImageViewModel: SaveImageViewModel by viewModels()
    private val saveVideoViewModel: SaveVideoViewModel by viewModels()

    private val adapterMasks by lazy {
        EffectAdapter(Data.getMasks()) { position ->
            scrollToPosition(position, binding.rViewMasks.layoutManager)
            showCancelEffectButton()
        }
    }

    private val adapterEffects by lazy {
        EffectAdapter(Data.getEffects()) { position ->
            scrollToPosition(position, binding.rViewEffects.layoutManager)
            showCancelEffectButton()
        }
    }

    private val adapterFilters by lazy {
        EffectAdapter(Data.getFilters()) { position ->
            scrollToPosition(position, binding.rViewFilters.layoutManager)
            showCancelEffectButton()
        }
    }

    private val cameraHelper: CameraHelper by lazy {
        CameraHelper(requireContext()) { result ->
            when (result) {
                is DeepARResult.VideoRecStarted -> {
                    setViewsVisibility()
                    enableSwipeVP()
                    binding.layoutTop.c.startChronometer()
                    binding.layoutTop.imgRec.startRecAnimation()
                }
                is DeepARResult.VideoRecFinished -> {
                    setViewsVisibility(true)
                    enableSwipeVP(true)
                    binding.layoutTop.c.stopChronometer()
                    binding.layoutTop.imgRec.stopRecAnimation()
                    cameraHelper.videoFile?.let { saveVideoViewModel.saveVideo(it.absolutePath) }
                }
                is DeepARResult.VideoRecFailed -> {
                    setViewsVisibility(true)
                    enableSwipeVP(true)
                    binding.layoutTop.c.stopChronometer()
                    binding.layoutTop.imgRec.stopRecAnimation()
                }
                is DeepARResult.Photo -> {
                    saveImageViewModel.saveBitmap(result.bitmap)
                }
                is DeepARResult.Error -> {
                    requireContext().message(result.message)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupViewPagerSwitchTypeEffect()
        setObservers()
        setListeners()
        loadMediaItems()
        loadBannerAd()
    }

    private fun loadBannerAd() {
        val adView = binding.adView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onPause() {
        binding.adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
        loadBannerAd()
    }

    override fun onDestroyView() {
        binding.adView.destroy()
        super.onDestroyView()
    }

    /**
     * setup viewpager type effect
     */
    private fun setupViewPagerSwitchTypeEffect() {
        val adapter = ViewPagerAdapter(
            childFragmentManager, lifecycle, Data.getEmptyFragments()
        )
        binding.vpSwitchTypeEffect.adapter = adapter
        TabLayoutMediator(
            binding.layoutBottom.tabLayout, binding.vpSwitchTypeEffect
        ) { tab, position ->
            tab.text = Data.getTitles()[position]
        }.attach()
    }

    /**
     * view models observers
     */
    private fun setObservers() {
        libraryViewModel.firstMediaItem.observe(viewLifecycleOwner) { mediaItem ->
            mediaItem.uri?.let {
                binding.layoutBottom.btnGallery.setThumbnail(it)
            }
        }
    }

    /**
     * load photos and videos
     */
    private fun loadMediaItems() {
        libraryViewModel.loadMediaItems()
    }

    /**
     * scroll to position
     */
    private fun scrollToPosition(position: Int, layoutManager: RecyclerView.LayoutManager?) {
        if (layoutManager is SliderLayoutManager) {
            layoutManager.scrollToEffect(position)
        }
    }

    override fun onStop() {
        cameraHelper.stopCamera()
        super.onStop()
    }

    override fun onStart() {
        cameraHelper.startCamera()
        super.onStart()
    }

    /**
     * set padding start to r.view
     * set custom layout manager
     */
    private fun setupRecyclerViews() {
        //39 = card effect (62dp / 2) + margin (8)
        val padding = requireContext().getScreenWidth() / 2 - 39.px
        //MASKS
        binding.rViewMasks.apply {
            this.setPadding(padding, 0, padding, 0)
            this.layoutManager = SliderLayoutManager(requireContext()) { position ->
                applyEffect(position)
            }
            this.adapter = adapterMasks
        }
        //EFFECTS
        binding.rViewEffects.apply {
            this.setPadding(padding, 0, padding, 0)
            this.layoutManager = SliderLayoutManager(requireContext()) { position ->
                applyEffect(position)
            }
            this.adapter = adapterEffects
        }
        //FILTERS
        binding.rViewFilters.apply {
            this.setPadding(padding, 0, padding, 0)
            this.layoutManager = SliderLayoutManager(requireContext()) { position ->
                applyEffect(position)
            }
            this.adapter = adapterFilters
        }
    }

    /**
     * apply new effect
     */
    private fun applyEffect(position: Int) {
        val effect: DeepAREffect = when (binding.vpSwitchTypeEffect.currentItem) {
            0 -> {
                adapterMasks.currentList[position].also {
                    cameraHelper.deepAR.currentMask = it.getPath()
                    cameraHelper.deepAR.applyEffect(it.slot, it.getPath())
                }
            }
            1 -> {
                adapterEffects.currentList[position].also {
                    cameraHelper.deepAR.currentEffect = it.getPath()
                    cameraHelper.deepAR.applyEffect(it.slot, it.getPath())
                }
            }
            2 -> {
                adapterFilters.currentList[position].also {
                    cameraHelper.deepAR.currentFilter = it.getPath()
                    cameraHelper.deepAR.applyEffect(it.slot, it.getPath())
                }
            }
            else -> {
                adapterFilters.currentList[-1]
            }
        }
        binding.txtActionEffect.showActionEffect(effect.action)
        showCancelEffectButton()
        requireContext().vibrate()
    }

    /**
     * enable-disable swipe viewpager
     */
    private fun enableSwipeVP(enable: Boolean = false) {
        requireActivity().also {
            if (it is MainActivity) {
                it.enableSwipeVP(enable)
            }
        }
    }

    /**
     * show-hide views
     */
    private fun setViewsVisibility(isVisible: Boolean = false) {
        binding.layoutBottom.root.isInvisible = !isVisible
        binding.btnFlash.isVisible = isVisible
        binding.btnGrid.isVisible = isVisible
        val pos = binding.vpSwitchTypeEffect.currentItem
        showRViews(if (isVisible) pos else -1)
        showCancelEffectButton(if (isVisible) pos else -1)
    }

    /**
     * set listeners
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.btnTakeAction.listener = object : ActionButton.Listener {
            override fun onTakePhoto() {
                //take photo
                cameraHelper.deepAR.takePhoto()
            }

            override fun onStartRecording() {
                //create new file
                cameraHelper.videoFile = FileUtil.getFilesDir(requireContext()).also {
                    //start recording
                    cameraHelper.deepAR.startRecording(it.path)
                }
            }

            override fun onRecordingFinished() {
                //stop recording
                cameraHelper.deepAR.stopRecording()
            }

        }

        binding.surfaceView.holder.addCallback(this)

        binding.btnCancelEffect.setOnClickListener {
            when (binding.vpSwitchTypeEffect.currentItem) {
                0 -> binding.rViewMasks.smoothScrollToPosition(0)
                1 -> binding.rViewEffects.smoothScrollToPosition(0)
                2 -> binding.rViewFilters.smoothScrollToPosition(0)
            }
        }

        binding.btnFlash.setOnClickListener {
            cameraHelper.toggleFlash { icon ->
                binding.btnFlash.scaleAnim(icon)
            }
        }

        binding.btnGrid.setOnClickListener {
            binding.gridLines.toggleGridLines { icon -> binding.btnGrid.scaleAnim(icon) }
        }

        binding.layoutBottom.btnGallery.setOnClickListener {
            requireActivity().also {
                if (it is MainActivity) {
                    it.showLibrary()
                }
            }
        }

        binding.layoutBottom.btnSwitchCamera.setOnClickListener { cameraHelper.switchCamera() }

        binding.layoutBottom.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.vpSwitchTypeEffect.currentItem = tab.position
                showRViews()
                showCancelEffectButton()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    /**
     * show-hide list effects
     */
    private fun showRViews(position: Int = binding.vpSwitchTypeEffect.currentItem) {
        binding.rViewMasks.isVisible = position == 0
        binding.rViewEffects.isVisible = position == 1
        binding.rViewFilters.isVisible = position == 2
    }

    /**
     * show-hide cancel effect
     */
    private fun showCancelEffectButton(pos: Int = binding.vpSwitchTypeEffect.currentItem) {
        val isVisible = (pos == 0 && cameraHelper.deepAR.currentMask != NO_EFFECT_KEY) ||
                (pos == 1 && cameraHelper.deepAR.currentEffect != NO_EFFECT_KEY) ||
                (pos == 2 && cameraHelper.deepAR.currentFilter != NO_EFFECT_KEY)
        binding.btnCancelEffect.isVisible = isVisible
    }

    /**
     * set zoom
     */
    fun setZoom(zoom: Float) {
        cameraHelper.setZoom(zoom)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        cameraHelper.deepAR.setRenderSurface(holder.surface, Size(width, height))
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        cameraHelper.deepAR.setRenderSurface(null, Size(0, 0))
    }

}