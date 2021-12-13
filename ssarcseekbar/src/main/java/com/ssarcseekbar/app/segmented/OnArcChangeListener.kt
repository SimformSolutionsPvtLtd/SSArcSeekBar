package com.ssarcseekbar.app.segmented

interface OnArcChangeListener {
    fun onProgressChanged(segmentedArc: SegmentedArc?, progress: Int)
    fun onStartTrackingTouch(segmentedArc: SegmentedArc?)
    fun onStopTrackingTouch(segmentedArc: SegmentedArc?)
}