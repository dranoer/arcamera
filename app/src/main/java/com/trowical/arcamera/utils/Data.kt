package com.trowical.arcamera.utils

import androidx.fragment.app.Fragment
import com.trowical.arcamera.R
import com.trowical.arcamera.model.DeepAREffect
import com.trowical.arcamera.model.MediaItemObj
import com.trowical.arcamera.ui.navigation.CameraFragment
import com.trowical.arcamera.ui.navigation.LibraryFragment
import com.trowical.arcamera.ui.viewer.MediaInfoFragment
import com.trowical.arcamera.ui.viewer.MediaViewerFragment

object Data {

    fun getTitles() = ArrayList<String>().apply {
        add("Masks")
        add("Effects")
        add("Filters")
    }

    fun getEmptyFragments() = ArrayList<Fragment>().apply {
        add(Fragment())
        add(Fragment())
        add(Fragment())
    }

    fun getMainFragments() = ArrayList<Fragment>().apply {
        add(CameraFragment())
        add(LibraryFragment())
    }

    fun getViewerFragments(mediaItemObj: MediaItemObj) = ArrayList<Fragment>().apply {
        add(MediaViewerFragment.newInstance(mediaItemObj))
        add(MediaInfoFragment.newInstance(mediaItemObj))
    }

    fun getMasks() = ArrayList<DeepAREffect>().apply {
        add(DeepAREffect(MASK_KEY, NO_EFFECT_KEY, R.drawable.shape_no_effect))
        add(DeepAREffect(MASK_KEY, "background_segmentation", R.drawable.mask_bg_segmentation))
        add(DeepAREffect(MASK_KEY, "tiny_sunglasses", R.drawable.maks_tiny_sunglasses))
        add(DeepAREffect(MASK_KEY, "aviators", R.drawable.mask_aviators))
        add(DeepAREffect(MASK_KEY, "sleepingmask", R.drawable.mask_sleep))
        add(DeepAREffect(MASK_KEY, "beauty", R.drawable.mask_beauty))
        add(DeepAREffect(MASK_KEY, "flowers", R.drawable.mask_flowers))
        add(DeepAREffect(MASK_KEY, "mudmask", R.drawable.mask_mud))
        add(DeepAREffect(MASK_KEY, "eye_color_mask", R.drawable.mask_eye_color))
        add(DeepAREffect(MASK_KEY, "tape_face", R.drawable.mask_tape_face))
        add(DeepAREffect(MASK_KEY, "metal_face", R.drawable.mask_face_metal))
        add(DeepAREffect(MASK_KEY, "dalmatian", R.drawable.mask_dalmatian))
        add(DeepAREffect(MASK_KEY, "pug", R.drawable.mask_pug))
        add(DeepAREffect(MASK_KEY, "koala", R.drawable.mask_koala))
        add(DeepAREffect(MASK_KEY, "lion", R.drawable.mask_lion))
        add(DeepAREffect(MASK_KEY, "teddycigar", R.drawable.mask_teddybear))
        add(DeepAREffect(MASK_KEY, "pumpkin", R.drawable.mask_pumpkin))
        add(DeepAREffect(MASK_KEY, "frankenstein", R.drawable.mask_frankenstein))
        add(DeepAREffect(MASK_KEY, "fairy_lights", R.drawable.mask_lights))
        add(DeepAREffect(MASK_KEY, "alien", R.drawable.mask_alien))
        add(DeepAREffect(MASK_KEY, "bigmouth", R.drawable.mask_big_mouth))
        add(DeepAREffect(MASK_KEY, "smallface", R.drawable.mask_small_face))
        add(DeepAREffect(MASK_KEY, "tripleface", R.drawable.mask_triple_face))
        add(DeepAREffect(MASK_KEY, "fatify", R.drawable.mask_fatify))
        add(DeepAREffect(MASK_KEY, "twistedface", R.drawable.mask_twisted_face))
        add(DeepAREffect(MASK_KEY, "grumpycat", R.drawable.mask_grumpy))
        add(DeepAREffect(MASK_KEY, "manly_face", R.drawable.mask_manlyface))
        add(DeepAREffect(MASK_KEY, "ball_face", R.drawable.mask_ball_face))
        add(DeepAREffect(MASK_KEY, "beard", R.drawable.mask_beard))
        add(DeepAREffect(MASK_KEY, "scuba", R.drawable.mask_scuba))
        add(DeepAREffect(MASK_KEY, "slash", R.drawable.mask_slash))
        add(DeepAREffect(MASK_KEY, "obama", R.drawable.mask_obama))
        add(DeepAREffect(MASK_KEY, "topology", R.drawable.mask_topology))

    }

    fun getEffects() = ArrayList<DeepAREffect>().apply {
        add(DeepAREffect(EFFECT_KEY, NO_EFFECT_KEY, R.drawable.shape_no_effect))
        add(DeepAREffect(EFFECT_KEY, "disco", R.drawable.effect_disgo))
        add(DeepAREffect(EFFECT_KEY, "rain", R.drawable.effect_rain))
        add(DeepAREffect(EFFECT_KEY, "blizzard", R.drawable.effect_blizzard))
        add(DeepAREffect(EFFECT_KEY, "fire", R.drawable.effect_fire, R.string.oym))
        add(DeepAREffect(EFFECT_KEY, "heart", R.drawable.effect_heart, R.string.oym))
        add(DeepAREffect(EFFECT_KEY, "plastic_ocean", R.drawable.effect_plastic_ocean))
    }

    fun getFilters() = ArrayList<DeepAREffect>().apply {
        add(DeepAREffect(FILTER_KEY, NO_EFFECT_KEY, R.drawable.shape_no_effect))
        add(DeepAREffect(FILTER_KEY, "photo_boot", R.drawable.effect_photo_boot))
        add(DeepAREffect(FILTER_KEY, "drawingmanga", R.drawable.effect_manga))
        add(DeepAREffect(FILTER_KEY, "realvhs", R.drawable.effect_vhs))
        add(DeepAREffect(FILTER_KEY, "fxdrunk", R.drawable.effect_drunk))
        add(DeepAREffect(FILTER_KEY, "tv80", R.drawable.effect_tv))
        add(DeepAREffect(FILTER_KEY, "sepia", R.drawable.effect_sepia))
        add(DeepAREffect(FILTER_KEY, "filmcolorperfection", R.drawable.filter_film_color))
        add(DeepAREffect(FILTER_KEY, "bleachbypass", R.drawable.effect_blachbypass))
        add(DeepAREffect(FILTER_KEY, "filter_lut", R.drawable.effect_lut))
        add(DeepAREffect(FILTER_KEY, "lut_red_green", R.drawable.effect_lut))
        add(DeepAREffect(FILTER_KEY, "lut_gradient", R.drawable.effect_lut))
        add(DeepAREffect(FILTER_KEY, "lut_rainbow", R.drawable.effect_lut))
        add(DeepAREffect(FILTER_KEY, "lut_foggynight", R.drawable.effect_lut))
        add(DeepAREffect(FILTER_KEY, "lut_edgyamber", R.drawable.effect_lut))
        add(DeepAREffect(FILTER_KEY, "lut_kodak_5205", R.drawable.effect_lut))
        add(DeepAREffect(FILTER_KEY, "lut_kodak_5218", R.drawable.effect_lut))
        add(DeepAREffect(FILTER_KEY, "lut_bw", R.drawable.effect_lut))
        add(DeepAREffect(FILTER_KEY, "lut_invert", R.drawable.effect_lut))
        add(DeepAREffect(FILTER_KEY, "lut_late_sunset", R.drawable.effect_lut))
    }

}