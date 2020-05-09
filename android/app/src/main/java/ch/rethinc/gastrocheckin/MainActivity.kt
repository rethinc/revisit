package ch.rethinc.gastrocheckin

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {

    companion object {
        private const val cameraPermissionRequest = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        rescan.setOnClickListener {
            askForPermissionAndStartScanner()
        }
        qrCodeScanner.onScanQrCode = this::qrCodeScanned
        askForPermissionAndStartScanner()
    }

    override fun onPause() {
        super.onPause()
        qrCodeScanner.onScanQrCode = null
        qrCodeScanner.stopScanning()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != cameraPermissionRequest) {
            return
        }

        if (grantResults.isPermissionGranted()) {
            startScanner()
        }
    }

    private fun askForPermissionAndStartScanner() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED) {
            startScanner()
        } else {
            requestPermissions(arrayOf(CAMERA), cameraPermissionRequest)
        }
    }

    private fun qrCodeScanned(qrCodeValue: String) {
        showSuccessMessage("OK")
        rescan.visibility = VISIBLE
        content.text = qrCodeValue
    }

    private fun IntArray.isPermissionGranted(): Boolean =
        this.isNotEmpty() && this[0] == PERMISSION_GRANTED

    private fun startScanner() {
        qrCodeScanner.startScanning()
        hideMessage()
    }

    private fun showSuccessMessage(text: String) {
        scanMessage.text = text
        scanMessage.visibility = VISIBLE
        scanMessage.setBackgroundColor(getColor(R.color.colorOk))
    }

    private fun showErrorMessage(text: String) {
        scanMessage.text = text
        scanMessage.visibility = VISIBLE
        scanMessage.setBackgroundColor(getColor(R.color.colorError))

    }

    private fun hideMessage() {
        scanMessage.visibility = GONE
    }
}
