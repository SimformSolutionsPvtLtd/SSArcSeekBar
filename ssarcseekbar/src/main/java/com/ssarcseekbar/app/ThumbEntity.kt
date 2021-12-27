package com.ssarcseekbar.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import android.util.Log


class ThumbEntity(private val centerPosition: PointF,
                  private var progress: Float,
                  private val startAngle: Float,
                  private val thumbRadius: Float,
                  private var thumbDrawable: Drawable,
                  private var context: Context) {

    companion object {
        private const val DEGREE_TO_RADIAN_RATIO = 0.0174533
    }

    init {
        updatePosition(progress, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun draw(canvas: Canvas, progress: Float, thumbOutside: Boolean) {
        this.progress = progress

        updatePosition(progress, thumbOutside)

        val angle1 = (startAngle + (360 - 2 * startAngle) * progress)

        val bounds = thumbDrawable.getBounds()
        val saveCount = canvas.save()
        canvas.rotate(angle1, bounds.exactCenterX(), bounds.exactCenterY())
        thumbDrawable.setBounds(bounds)
        thumbDrawable.draw(canvas)
        canvas.restoreToCount(saveCount)
    }

    private fun updatePosition(progress: Float, thumbOutside: Boolean) {
        // Here I changed code
        var seekbarRadius = Math.min(centerPosition.x, centerPosition.y) - thumbRadius
        Log.e("thumbRadius", thumbRadius.toString())
        if (thumbOutside)
            seekbarRadius = Math.min(centerPosition.x, centerPosition.y) - 40

        val angle = (startAngle + (360 - 2 * startAngle) * progress) * DEGREE_TO_RADIAN_RATIO
        val angle1 = (startAngle + (360 - 2 * startAngle) * progress)

        Log.e("Angle::", angle.toString())
        Log.e("Angle1::", angle1.toString())

        val indicatorX = centerPosition.x - Math.sin(angle) * seekbarRadius
        val indicatorY = Math.cos(angle) * seekbarRadius + centerPosition.y

        thumbDrawable.setBounds(
                (indicatorX - thumbRadius).toInt(),
                (indicatorY - thumbRadius).toInt(),
                (indicatorX + thumbRadius).toInt(),
                (indicatorY + thumbRadius).toInt())
    }

    private fun getRotateDrawable(d: Drawable, angle: Float): Drawable? {
        val arD = arrayOf(d)
        return object : LayerDrawable(arD) {
            override fun draw(canvas: Canvas) {
                canvas.save()
                canvas.rotate(angle, d.bounds.width() / 2.toFloat(), d.bounds.height() / 2.toFloat())
                super.draw(canvas)
                canvas.restore()
            }
        }
    }
}