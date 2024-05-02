package com.trowical.arcamera.ui.navigation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.trowical.arcamera.R
import com.trowical.arcamera.adapter.LibraryAdapter
import com.trowical.arcamera.databinding.FragmentLibraryBinding
import com.trowical.arcamera.ui.MainActivity
import com.trowical.arcamera.ui.viewer.MediaViewerActivity
import com.trowical.arcamera.utils.MEDIA_KEY
import com.trowical.arcamera.viewmodel.LibraryViewModel

class LibraryFragment : Fragment() {

    private lateinit var binding: FragmentLibraryBinding
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val adapter by lazy {
        LibraryAdapter { mediaClicked ->
            Intent(requireContext(), MediaViewerActivity::class.java)
                .apply {
                    putExtra(MEDIA_KEY, mediaClicked)
                    startActivity(this)
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
        //set icon
        binding.layoutNoData.icon.setImageResource(R.drawable.ic_library)
        //set text
        binding.layoutNoData.txtNoData.text = getString(R.string.no_library)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setListeners()
        setObservers()
    }

    /**
     * set listeners
     */
    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().also {
                if (it is MainActivity) {
                    it.showLibrary(false)
                }
            }
        }
    }

    /**
     * setup recyclerview
     */
    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    /**
     * view models observers
     */
    private fun setObservers() {
        libraryViewModel.mediaItems.observe(viewLifecycleOwner) { mediaItems ->
            binding.layoutNoData.root.visibility = if (mediaItems.isEmpty())
                View.VISIBLE else View.GONE
            adapter.submitList(mediaItems)
        }
    }

}