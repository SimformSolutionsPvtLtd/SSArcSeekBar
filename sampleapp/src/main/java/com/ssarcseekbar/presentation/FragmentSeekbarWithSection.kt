package com.ssarcseekbar.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssarcseekbar.app.segmented.SegmentedArc
import kotlinx.android.synthetic.main.fragment_seekbar_with_section.progressTextView
import kotlinx.android.synthetic.main.fragment_seekbar_with_section.view.segmentedArc

class FragmentSeekbarWithSection : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_seekbar_with_section, container, false)
        view.segmentedArc.setOnProgressChangedListener(object :
            SegmentedArc.onProgressChangedListener {
            override fun onProgressChanged(progress: Int) {
                progressTextView.text = progress.toString()
            }
        })
        return view
    }
}