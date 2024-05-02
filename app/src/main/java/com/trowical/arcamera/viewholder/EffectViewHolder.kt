package com.trowical.arcamera.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.trowical.arcamera.databinding.CardEffectBinding
import com.trowical.arcamera.utils.setIconEffect

class EffectViewHolder(private val binding: CardEffectBinding) :
    RecyclerView.ViewHolder(binding.root) {

    /**
     * set image
     */
    fun setIcon(icon:Int) {
        binding.imgEffect.setIconEffect(icon)
    }

}