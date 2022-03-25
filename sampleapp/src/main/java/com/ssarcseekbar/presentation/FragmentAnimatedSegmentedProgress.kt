package com.ssarcseekbar.presentation

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.ssarcseekbar.app.segmented.SegmentedArc
import kotlinx.android.synthetic.main.fragment_animated_segmented_progress.animatedProgressText
import kotlinx.android.synthetic.main.fragment_animated_segmented_progress.view.animateSegmentedArc
import kotlinx.android.synthetic.main.fragment_animated_segmented_progress.view.btnAnimate
import kotlinx.android.synthetic.main.fragment_animated_segmented_progress.view.edtProgressValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentAnimatedSegmentedProgress : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_animated_segmented_progress, container, false)
        var oldProgress = 1

        // Animate progress when it is first time visible on screen
        animateProgress(oldProgress, view.animateSegmentedArc.getMax())
        lifecycleScope.launch(Dispatchers.Main) {
            delay(1000)
            animateProgress(view.animateSegmentedArc.getMax(), oldProgress)
        }

        view.btnAnimate.setOnClickListener {
            oldProgress = view.animateSegmentedArc.getSegmentedProgress()
            val newProgress: Int
            if (view.edtProgressValue.text?.isNotEmpty() == true) {
                newProgress = view.edtProgressValue.text.toString().toInt()
                if (newProgress > 0 && newProgress <= view.animateSegmentedArc.getMax()) {
                    animateProgress(oldProgress, newProgress)
                } else {
                    Toast.makeText(requireContext(), "Please enter value in range 1 to ${view.animateSegmentedArc.getMax()}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter value", Toast.LENGTH_SHORT).show()
            }
        }

        view.animateSegmentedArc.setOnProgressChangedListener(object : SegmentedArc.onProgressChangedListener {
            override fun onProgressChanged(progress: Int) {
                if (progress > 0 && progress <= view.animateSegmentedArc.getMax()) {
                    animatedProgressText.text = progress.toString()
                }
            }
        })
        return view
    }

    private fun animateProgress(oldProgress: Int, newProgress: Int) {
        val valueAnimator = ValueAnimator.ofInt(oldProgress, newProgress)
        valueAnimator.duration = 1000
        valueAnimator.addUpdateListener {
            view?.animateSegmentedArc?.setSegmentedProgress(it.animatedValue as Int)
        }
        valueAnimator.start()
    }
}