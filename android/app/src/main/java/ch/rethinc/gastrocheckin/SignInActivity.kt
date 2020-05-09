package ch.rethinc.gastrocheckin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    companion object {
        private const val signInRequest = 1

        fun launch(context: Context) {
            val intent = Intent(context, SignInActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser != null) {
            MainActivity.launch(this)
            finish()
            return
        }

        val emailProvider = AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build()

        val intent = AuthUI
            .getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(arrayListOf(emailProvider))
            .build()
        startActivityForResult(intent, signInRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != signInRequest) {
            return
        }

        if (resultCode == Activity.RESULT_OK) {
            MainActivity.launch(this)
        }
        finish()
    }
}
