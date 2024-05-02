package com.trowical.arcamera.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trowical.arcamera.databinding.CardEffectBinding
import com.trowical.arcamera.model.DeepAREffect
import com.trowical.arcamera.utils.DiffUtils
import com.trowical.arcamera.viewholder.EffectViewHolder

class EffectAdapter(
    effects: ArrayList<DeepAREffect>,
    val notifyPositionClicked: (position: Int) -> Unit
) : ListAdapter<DeepAREffect, EffectViewHolder>(DiffUtils.EFFECT_DIFF_CALLBACK) {

    init {
        //add list
        submitList(effects)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EffectViewHolder(
        CardEffectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: EffectViewHolder, position: Int) {
        //get effect
        val effect = getItem(position)
        //set icon
        holder.setIcon(effect.img)
        holder.itemView.setOnClickListener { notifyPositionClicked(position) }
    }

}