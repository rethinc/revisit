package ch.rethinc.gastrocheckin

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val cameraPermissionRequest = 1

        fun launch(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            SignInActivity.launch(this)
            finish()
        } else {
            firebaseUser = user
        }
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
        val person = Person.fromJson(qrCodeValue)
        if (person != null) {
            showSuccessMessage(getString(R.string.ok))
            name.text = person.name
            phone.text = person.phone
            contentContainer.visibility = VISIBLE
        } else {
            showErrorMessage(getString(R.string.error))
        }

        rescan.visibility = VISIBLE
    }

    private fun IntArray.isPermissionGranted(): Boolean =
        this.isNotEmpty() && this[0] == PERMISSION_GRANTED

    private fun startScanner() {
        qrCodeScanner.startScanning()
        hideMessage()
        contentContainer.visibility = GONE
        rescan.visibility = GONE
    }

    private fun showSuccessMessage(text: String) {
        scanMessage.text = text
        scanMessage.visibility = VISIBLE
        scanMessage.setBackgroundColor(getColor(R.color.colorOkTransparent))
    }

    private fun showErrorMessage(text: String) {
        scanMessage.text = text
        scanMessage.visibility = VISIBLE
        scanMessage.setBackgroundColor(getColor(R.color.colorErrorTransparent))
    }

    private fun hideMessage() {
        scanMessage.visibility = GONE
    }
}
