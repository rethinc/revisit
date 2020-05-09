package ch.rethinc.gastrocheckin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.google.zxing.ResultPoint

internal class QrCodeDetectionView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val drawableMask = ContextCompat.getDrawable(context, R.drawable.icon_scan_mask)!!
    private val maskPaddingDPI = 20.0f
    private val density = context.resources.displayMetrics.density
    private val paint = Paint(ANTI_ALIAS_FLAG)
    private val maskColor = Color.BLACK
    private val maskOpacity = 0x60
    private val currentPointOpacity = 0xA0
    val resultPointColor = ContextCompat.getColor(context, R.color.colorDefaultDetection)
    private val resultPointSize = 4.0f

    private var detectedPoints = listOf<ResultPoint>()
    private var previewRect: Rect? = null
    private var maskBoundsRect: Rect = Rect(
        (maskPaddingDPI * density).toInt(),
        (maskPaddingDPI * density).toInt(),
        (maskPaddingDPI * density).toInt(),
        (maskPaddingDPI * density).toInt()
    )

    fun previewRectChanged(rect: Rect) {
        previewRect = rect
        maskBoundsRect = Rect(
            (rect.left + maskPaddingDPI * density).toInt(),
            (rect.top + maskPaddingDPI * density).toInt(),
            (rect.right - maskPaddingDPI * density).toInt(),
            (rect.bottom - maskPaddingDPI * density).toInt()
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val rect = previewRect

        if (rect != null) {
            val width = canvas.width.toFloat()
            val height = canvas.height.toFloat()
            paint.color = maskColor
            paint.alpha = maskOpacity
            canvas.drawRect(0.0f, 0.0f, width, rect.top.toFloat(), paint)
            canvas.drawRect(0.0f, rect.top.toFloat(), rect.left.toFloat(), rect.bottom.toFloat() + 1.0f, paint)
            canvas.drawRect(rect.right.toFloat() + 1.0f, rect.top.toFloat(), width, rect.bottom.toFloat() + 1, paint)
            canvas.drawRect(0.0f, rect.bottom.toFloat() + 1.0f, width, height, paint)

            if (!detectedPoints.isEmpty()) {
                val previewFrame = previewRect
                if (previewFrame != null) {
                    val scaleX = rect.width() / previewFrame.width().toFloat()
                    val scaleY = rect.height() / previewFrame.height().toFloat()

                    val frameLeft = rect.left.toFloat()
                    val frameTop = rect.top.toFloat()
                    paint.alpha = currentPointOpacity
                    paint.color = resultPointColor
                    for (point in detectedPoints) {
                        canvas.drawCircle(
                            frameLeft + (point.x * scaleX).toInt(),
                            frameTop + (point.y * scaleY).toInt(),
                            resultPointSize, paint
                        )
                    }
                }
            }
        }
        drawableMask.bounds = maskBoundsRect
        drawableMask.draw(canvas)
    }

    fun detectedPoints(points: List<ResultPoint>) {
        val pointsChanged = points.size != detectedPoints.size
        if (pointsChanged || !points.all { detectedPoints.contains(it) }) {
            detectedPoints = points
            invalidate()
        }
    }
}