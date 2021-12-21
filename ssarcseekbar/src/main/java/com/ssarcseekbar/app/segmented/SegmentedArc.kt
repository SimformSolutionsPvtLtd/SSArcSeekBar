package com.ssarcseekbar.app.segmented

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.ssarcseekbar.app.R
import com.ssarcseekbar.app.utils.Utils
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin


class SegmentedArc : View {
    private var midx = 0f
    private var midy = 0f
    private var progressSecondaryPaint = Paint()
    private var progressPrimaryPaint = Paint()
    private var currdeg = 0f
    private var deg = 3f
    private var downdeg = 0f
    private var progressPrimaryColor = resources.getColor(R.color.default_progress_primary_color)
    private var progressSecondaryColor = resources.getColor(R.color.default_progress_secondary_color)
    private var progressPrimaryCircleSize = -1f
    private var progressSecondaryCircleSize = -1f
    private var progressPrimaryStrokeWidth = 25f
    private var progressSecondaryStrokeWidth = 10f
    private var progressRadius = -1f
    private var max = 25
    private var min = 10
    private var startOffset = 30
    private var startOffset2 = 0
    private var sweepAngle = -1
    private var startEventSent = false
    private var oval = RectF()
    private var mProgressChangeListener: onProgressChangedListener? = null
    private var mArcChangeListener: OnArcChangeListener? = null

    interface onProgressChangedListener {
        fun onProgressChanged(progress: Int)
    }

    fun setOnProgressChangedListener(mProgressChangeListener: onProgressChangedListener?) {
        this.mProgressChangeListener = mProgressChangeListener
    }

    fun setOnArcChangeListener(mArcChangeListener: OnArcChangeListener?) {
        this.mArcChangeListener = mArcChangeListener
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initXMLAttrs(context, attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initXMLAttrs(context, attrs)
        init()
    }

    private fun init() {
        progressSecondaryPaint = Paint()
        progressSecondaryPaint.isAntiAlias = true
        progressSecondaryPaint.strokeWidth = progressSecondaryStrokeWidth
        progressSecondaryPaint.style = Paint.Style.FILL
        progressSecondaryPaint.color = progressSecondaryColor

        progressPrimaryPaint = Paint()
        progressPrimaryPaint.isAntiAlias = true
        progressPrimaryPaint.strokeWidth = progressPrimaryStrokeWidth
        progressPrimaryPaint.style = Paint.Style.FILL
        progressPrimaryPaint.color = progressPrimaryColor

        oval = RectF()
    }

    private fun initXMLAttrs(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SegmentedArc)
        progress = a.getInt(R.styleable.SegmentedArc_start_progress, 1)
        setProgressPrimaryColor(
            a.getColor(
                R.styleable.SegmentedArc_progress_primary_color,
                progressPrimaryColor
            )
        )
        setProgressSecondaryColor(
            a.getColor(
                R.styleable.SegmentedArc_progress_secondary_color,
                progressSecondaryColor
            )
        )
        setProgressPrimaryCircleSize(
            a.getFloat(
                R.styleable.SegmentedArc_progress_primary_circle_size,
                -1f
            )
        )
        setProgressSecondaryCircleSize(
            a.getFloat(
                R.styleable.SegmentedArc_progress_secondary_circle_size,
                -1f
            )
        )
        setProgressPrimaryStrokeWidth(
            a.getFloat(
                R.styleable.SegmentedArc_progress_primary_stroke_width,
                25f
            )
        )
        setProgressSecondaryStrokeWidth(
            a.getFloat(
                R.styleable.SegmentedArc_progress_secondary_stroke_width,
                10f
            )
        )
        setSweepAngle(a.getInt(R.styleable.SegmentedArc_sweep_angle, -1))
        setStartOffset(a.getInt(R.styleable.SegmentedArc_start_offset, 30))
        setMax(a.getInt(R.styleable.SegmentedArc_max, 25))
        setMin(a.getInt(R.styleable.SegmentedArc_min, 1))
        deg = (min + 2).toFloat()
        setProgressRadius(a.getFloat(R.styleable.SegmentedArc_progress_radius, -1f))
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minWidth = Utils.convertDpToPixel(160f, context).toInt()
        val minHeight = Utils.convertDpToPixel(160f, context).toInt()
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var width: Int
        var height: Int
        width = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                min(minWidth, widthSize)
            }
            else -> {
                // only in case of ScrollViews, otherwise MeasureSpec.UNSPECIFIED is never triggered
                // If width is wrap_content i.e. MeasureSpec.UNSPECIFIED, then make width equal to height
                heightSize
            }
        }
        height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                min(minHeight, heightSize)
            }
            else -> {
                // only in case of ScrollViews, otherwise MeasureSpec.UNSPECIFIED is never triggered
                // If height is wrap_content i.e. MeasureSpec.UNSPECIFIED, then make height equal to width
                widthSize
            }
        }
        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            width = minWidth
            height = minHeight
        }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        midx = (width / 2).toFloat()
        midy = (height / 2).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mProgressChangeListener != null) mProgressChangeListener!!.onProgressChanged((deg - 2).toInt())
        mArcChangeListener?.onProgressChanged(
            this,
            (deg - 2).toInt()
        )
        progressPrimaryPaint.color = progressPrimaryColor
        progressSecondaryPaint.color = progressSecondaryColor
        startOffset2 = startOffset - 15
        val radius = (min(midx, midy) * (14.5.toFloat() / 16)).toInt()
        if (sweepAngle == -1) {
            sweepAngle = 360 - 2 * startOffset2
        }
        if (progressRadius == -1f) {
            progressRadius = radius.toFloat()
        }
        var x: Float
        var y: Float
        val deg2 = max(3f, deg)
        val deg3 = min(deg, (max + 2).toFloat())
        for (i in deg2.toInt() until max + 3) {
            val tmp =
                startOffset2.toFloat() / 360 + sweepAngle.toFloat() / 360 * i.toFloat() / (max + 5)
            x = midx + (progressRadius * sin(2 * Math.PI * (1.0 - tmp))).toFloat()
            y = midy + (progressRadius * cos(2 * Math.PI * (1.0 - tmp))).toFloat()
            if (progressSecondaryCircleSize == -1f) canvas.drawCircle(
                x, y,
                radius.toFloat() / 30 * (20.toFloat() / max) * (sweepAngle.toFloat() / 270),
                progressSecondaryPaint
            ) else canvas.drawCircle(x, y, progressSecondaryCircleSize, progressSecondaryPaint)
        }
        var i = 3
        while (i <= deg3) {
            val tmp =
                startOffset2.toFloat() / 360 + sweepAngle.toFloat() / 360 * i.toFloat() / (max + 5)
            x = midx + (progressRadius * sin(2 * Math.PI * (1.0 - tmp))).toFloat()
            y = midy + (progressRadius * cos(2 * Math.PI * (1.0 - tmp))).toFloat()
            if (progressPrimaryCircleSize == -1f) canvas.drawCircle(
                x, y,
                progressRadius / 15 * (20.toFloat() / max) * (sweepAngle.toFloat() / 270),
                progressPrimaryPaint
            ) else canvas.drawCircle(x, y, progressPrimaryCircleSize, progressPrimaryPaint)
            i++
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            val dx = e.x - midx
            val dy = e.y - midy
            downdeg = (atan2(dy.toDouble(), dx.toDouble()) * 180 / Math.PI).toFloat()
            downdeg -= 90f
            if (downdeg < 0) {
                downdeg += 360f
            }
            downdeg = floor((downdeg / 360 * (max + 5)).toDouble()).toFloat()
            if (mArcChangeListener != null) {
                mArcChangeListener!!.onStartTrackingTouch(this)
                startEventSent = true
            }
            return true
        }
        if (e.action == MotionEvent.ACTION_MOVE) {
            val dx = e.x - midx
            val dy = e.y - midy
            currdeg = (atan2(dy.toDouble(), dx.toDouble()) * 180 / Math.PI).toFloat()
            currdeg -= 90f
            if (currdeg < 0) {
                currdeg += 360f
            }
            currdeg = floor((currdeg / 360 * (max + 5)).toDouble()).toFloat()
            if (currdeg / (max + 4) > 0.75f && (downdeg - 0) / (max + 4) < 0.25f) {
                deg--
                if (deg < min + 2) {
                    deg = (min + 2).toFloat()
                }
            } else if (downdeg / (max + 4) > 0.75f && (currdeg - 0) / (max + 4) < 0.25f) {
                deg++
                if (deg > max + 2) {
                    deg = (max + 2).toFloat()
                }
            } else {
                deg += currdeg - downdeg
                if (deg > max + 2) {
                    deg = (max + 2).toFloat()
                }
                if (deg < min + 2) {
                    deg = (min + 2).toFloat()
                }
            }
            downdeg = currdeg
            invalidate()
            return true
        }
        if (e.action == MotionEvent.ACTION_UP) {
            if (mArcChangeListener != null) {
                mArcChangeListener!!.onStopTrackingTouch(this)
                startEventSent = false
            }
            return true
        }
        return super.onTouchEvent(e)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (parent != null && event.action == MotionEvent.ACTION_DOWN) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return super.dispatchTouchEvent(event)
    }

    var progress: Int
        get() = (deg - 2).toInt()
        set(x) {
            deg = (x + 2).toFloat()
            invalidate()
        }

    fun getProgressPrimaryColor(): Int {
        return progressPrimaryColor
    }

    fun setProgressPrimaryColor(progressPrimaryColor: Int) {
        this.progressPrimaryColor = progressPrimaryColor
        invalidate()
    }

    fun getProgressSecondaryColor(): Int {
        return progressSecondaryColor
    }

    fun setProgressSecondaryColor(progressSecondaryColor: Int) {
        this.progressSecondaryColor = progressSecondaryColor
        invalidate()
    }

    fun getProgressPrimaryCircleSize(): Float {
        return progressPrimaryCircleSize
    }

    fun setProgressPrimaryCircleSize(progressPrimaryCircleSize: Float) {
        this.progressPrimaryCircleSize = progressPrimaryCircleSize
        invalidate()
    }

    fun getProgressSecondaryCircleSize(): Float {
        return progressSecondaryCircleSize
    }

    fun setProgressSecondaryCircleSize(progressSecondaryCircleSize: Float) {
        this.progressSecondaryCircleSize = progressSecondaryCircleSize
        invalidate()
    }

    fun getProgressPrimaryStrokeWidth(): Float {
        return progressPrimaryStrokeWidth
    }

    fun setProgressPrimaryStrokeWidth(progressPrimaryStrokeWidth: Float) {
        this.progressPrimaryStrokeWidth = progressPrimaryStrokeWidth
        invalidate()
    }

    fun getProgressSecondaryStrokeWidth(): Float {
        return progressSecondaryStrokeWidth
    }

    fun setProgressSecondaryStrokeWidth(progressSecondaryStrokeWidth: Float) {
        this.progressSecondaryStrokeWidth = progressSecondaryStrokeWidth
        invalidate()
    }

    fun getSweepAngle(): Int {
        return sweepAngle
    }

    fun setSweepAngle(sweepAngle: Int) {
        this.sweepAngle = sweepAngle
        invalidate()
    }

    fun getStartOffset(): Int {
        return startOffset
    }

    fun setStartOffset(startOffset: Int) {
        this.startOffset = startOffset
        invalidate()
    }

    fun getMax(): Int {
        return max
    }

    fun setMax(max: Int) {
        if (max < min) {
            this.max = min
        } else {
            this.max = max
        }
        invalidate()
    }

    fun getMin(): Int {
        return min
    }

    fun setMin(min: Int) {
        when {
            min < 0 -> {
                this.min = 0
            }
            min > max -> {
                this.min = max
            }
            else -> {
                this.min = min
            }
        }
        invalidate()
    }

    fun getProgressRadius(): Float {
        return progressRadius
    }

    fun setProgressRadius(progressRadius: Float) {
        this.progressRadius = progressRadius
        invalidate()
    }
}