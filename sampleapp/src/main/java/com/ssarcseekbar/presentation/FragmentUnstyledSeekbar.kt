package com.ssarcseekbar.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_unstyled_seekbar.progressText
import kotlinx.android.synthetic.main.fragment_unstyled_seekbar.view.progress

class FragmentUnstyledSeekbar : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_unstyled_seekbar, container, false)
        view.progress.progressChangedCallback = {
            progressText.text = String.format("%.2f", it)
        }
        return view
    }
}