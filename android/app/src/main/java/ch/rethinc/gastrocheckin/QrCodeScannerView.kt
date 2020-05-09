package ch.rethinc.gastrocheckin

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CameraPreview
import kotlinx.android.synthetic.main.view_qr_code_scanner.view.*

class QrCodeScannerView : ConstraintLayout, BarcodeCallback, CameraPreview.StateListener {

    companion object {
        const val PREVIEW_DURATION = 1000L
    }

    var onScanQrCode: ((String) -> Unit)? = null

    private var autoResume: Boolean = false

    var scanning = false
        private set

    init {
        inflate(context, R.layout.view_qr_code_scanner, this)
        scannerView.decodeContinuous(this)
        scannerView.addStateListener(this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        val typedArray = context.obtainStyledAttributes(attrs,
            R.styleable.QrCodeScannerView, 0, 0)
        scannerView.scannerPadding = typedArray.getDimension(R.styleable.QrCodeScannerView_scannerPadding, 0.0f)
        typedArray.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId)

    fun startScanning(autoResume: Boolean = false) {
        this.autoResume = autoResume
        scanning = true
        resume()
    }

    fun stopScanning() {
        scanning = false
        pause()
    }

    private fun pause() {
        scannerView.pause()
    }

    private fun resume() {
        if (scanning) {
            hidePreview()
            scannerView.resume()
        }
    }

    private fun showPreview(previewImage: Bitmap) {
        previewView.setImageBitmap(previewImage)
        previewView.visibility = View.VISIBLE
    }

    private fun hidePreview() {
        previewView.visibility = View.GONE
    }

    override fun barcodeResult(result: BarcodeResult) {
        onScanQrCode?.invoke(result.text)
        showPreview(result.getBitmapWithResultPoints(detectionView.resultPointColor))
        pause()
        if (autoResume) {
            Handler().postDelayed({
                resume()
            }, PREVIEW_DURATION)
        }
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>) {
        detectionView.detectedPoints(resultPoints)
    }

    override fun previewSized() {
        detectionView.previewRectChanged(scannerView.framingRect)
    }

    override fun cameraClosed() {}

    override fun cameraError(error: Exception?) {}

    override fun previewStopped() {}

    override fun previewStarted() {}
}