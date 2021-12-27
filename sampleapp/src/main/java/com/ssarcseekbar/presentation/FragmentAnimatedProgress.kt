package com.ssarcseekbar.presentation

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_animated_progress.progress
import kotlinx.android.synthetic.main.fragment_animated_progress.seekbar
import kotlinx.android.synthetic.main.fragment_animated_progress.view.updateButton

class FragmentAnimatedProgress : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_animated_progress, container, false)
        view.updateButton.setOnClickListener {
            val newProgress = seekbar.progress / seekbar.max.toFloat()
            val oldProgress = progress.getProgress()
            val valueAnimator = ValueAnimator.ofFloat(oldProgress, newProgress)
            valueAnimator.duration = 1000
            valueAnimator.addUpdateListener {
                if ((it.animatedValue as Float) >= 30f) {
                    progress.setProgressGradientArray(IntArray(R.array.newProgressColor))
                }
                progress.setProgress(it.animatedValue as Float)
            }
            valueAnimator.start()
        }
        return view
    }
}