package ch.rethinc.gastrocheckin

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import com.journeyapps.barcodescanner.BarcodeView

internal class ScannerView : BarcodeView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(
        context,
        attrs,
        attributeSetId
    )

    var scannerPadding = 0.0f

    override fun calculateFramingRect(container: Rect, surface: Rect): Rect {
        val intersection = Rect(container)
        intersection.intersect(surface)
        val frameSize = (intersection.height().toFloat() - 2 * scannerPadding).toInt()
        intersection.inset(
            (intersection.width() - frameSize) / 2,
            (intersection.height() - frameSize) / 2
        )
        return intersection
    }

}