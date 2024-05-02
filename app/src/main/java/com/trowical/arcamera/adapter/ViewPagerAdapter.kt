package com.trowical.arcamera.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    var fragments: ArrayList<Fragment> = arrayListOf()
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int) = fragments[position]

    override fun getItemCount() = fragments.size

}