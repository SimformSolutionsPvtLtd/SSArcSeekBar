package com.ssarcseekbar.app

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.SweepGradient

class TrackDrawable(position: PointF,
                    private val radiusPx: Float,
                    private val margin: Float,
                    private val gradientArray: IntArray,
                    private val startAngle: Float,
                    private val trackWidthPx: Float,
                    gradientPositionsArray: FloatArray? = null) : DrawableEntity(position) {

    private val gradientPositionsArray: FloatArray = gradientPositionsArray
            ?: FloatArray(gradientArray.size) { it.toFloat() / gradientArray.size }
    private var isThumbOutside: Boolean = false

    init {
        if (gradientArray.size != this.gradientPositionsArray.size)
            throw IllegalStateException("gradientArray and gradientPositionsArray sizes must be equal.")
    }

    private val progressPaint = Paint().apply {
        strokeWidth = trackWidthPx
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE

        if (gradientArray.size > 1) {
            val shader = createSweepGradient()
            setShader(shader)
        } else {
            color = gradientArray[0]
        }
    }

    private fun createSweepGradient(): SweepGradient {
        val shader = SweepGradient(centerPosition.x, centerPosition.y, gradientArray, getGradientPositions())
        val gradientRotationMatrix = Matrix()
        gradientRotationMatrix.preRotate(90f + startAngle - 5, centerPosition.x, centerPosition.y)
        shader.setLocalMatrix(gradientRotationMatrix)
        return shader
    }

    private fun getGradientPositions(): FloatArray {
        val normalizedStartAngle = startAngle / 360f
        val normalizedAvailableSpace = 1f - 2 * normalizedStartAngle

        return FloatArray(gradientArray.size) {
            normalizedStartAngle + normalizedAvailableSpace * gradientPositionsArray[it]
        }
    }

    fun draw(canvas: Canvas, thumbOutside: Boolean) {
        isThumbOutside = thumbOutside
        draw(canvas)
    }

    override fun draw(canvas: Canvas) {
        val angle = (360 - (startAngle * 2))
        var rect = RectF(centerPosition.x - radiusPx + margin,
                centerPosition.y - radiusPx + margin,
                centerPosition.x + radiusPx - margin,
                centerPosition.y + radiusPx - margin)

        if (isThumbOutside) {
            rect = RectF(centerPosition.x - radiusPx + margin + 60,
                    centerPosition.y - radiusPx + margin + 60,
                    centerPosition.x + radiusPx - margin - 60,
                    centerPosition.y + radiusPx - margin - 60)
        }

        canvas.drawArc(
            rect,
            90f + startAngle,
            angle,
            false,
            progressPaint
        )
    }

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {}
}