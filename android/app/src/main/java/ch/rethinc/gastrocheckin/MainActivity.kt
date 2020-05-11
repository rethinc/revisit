package ch.rethinc.gastrocheckin

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import ch.rethinc.gastrocheckin.secretstore.GastroCheckinEncryptor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val cameraPermissionRequest = 1

        fun launch(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var keyStore: GastroCheckinKeyStore

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseUser: FirebaseUser

    private lateinit var visitRepository: VisitRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        if (user == null) {
            SignInActivity.launch(this)
            finish()
            return
        }
        firebaseUser = user

        keyStore = GastroCheckinKeyStore.getInstance(this)

        visitRepository = VisitRepositoryFirebase(
            FirebaseFirestore.getInstance(),
            firebaseUser,
            GastroCheckinEncryptor.createInstance(this, firebaseUser)
        )
    }

    override fun onResume() {
        super.onResume()
        scanNext.setOnClickListener {
            askForPermissionAndStartScanner()
        }
        if (keyStore.secretKey == null) {
            DefineSecretActivity.launch(this)
            return
        }

        qrCodeScanner.onScanQrCode = this::qrCodeScanned
        askForPermissionAndStartScanner()
    }

    override fun onPause() {
        super.onPause()
        qrCodeScanner.onScanQrCode = null
        qrCodeScanner.stopScanning()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                signOut()
                true
            }
            R.id.define_secret -> {
                DefineSecretActivity.launch(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        SignInActivity.launch(this)
        finish()
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

            name.text = person.name
            phone.text = person.phone
            contentContainer.visibility = VISIBLE
            val visit = Visit.fromPerson(person)
            visitRepository.save(visit)
                .observe(this, Observer { result ->
                    if (result.isSuccess) {
                        showSuccessMessage()
                    } else {
                        Log.e(
                            MainActivity::class.java.name,
                            "Error while saving visit",
                            result.exceptionOrNull()
                        )
                        showErrorMessage()
                    }
                })
        } else {
            showErrorMessage()
        }

        scanNext.visibility = VISIBLE
        introMessageContainer.visibility = GONE
    }

    private fun IntArray.isPermissionGranted(): Boolean =
        this.isNotEmpty() && this[0] == PERMISSION_GRANTED

    private fun startScanner() {
        qrCodeScanner.startScanning()
        hideMessage()
        contentContainer.visibility = GONE
        scanNext.visibility = GONE
        introMessageContainer.visibility = VISIBLE
    }

    private fun showSuccessMessage() {
        scanMessage.text = getString(R.string.ok)
        scanMessage.visibility = VISIBLE
        scanMessage.setBackgroundColor(getColor(R.color.colorOkTransparent))
    }

    private fun showErrorMessage() {
        scanMessage.text = getString(R.string.error)
        scanMessage.visibility = VISIBLE
        scanMessage.setBackgroundColor(getColor(R.color.colorErrorTransparent))
    }

    private fun hideMessage() {
        scanMessage.visibility = GONE
    }
}
