package ch.rethinc.gastrocheckin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import ch.rethinc.gastrocheckin.secretstore.GastroCheckinEncryptor
import ch.rethinc.gastrocheckin.secretstore.KeyDerivator
import ch.rethinc.gastrocheckin.secretstore.SecretKeyValidation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_define_secret.*

class DefineSecretActivity : AppCompatActivity() {

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, DefineSecretActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var gastroCheckinKeyStore: GastroCheckinKeyStore

    private lateinit var secretKeyValidation: SecretKeyValidation

    private lateinit var keyDerivator: KeyDerivator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_define_secret)
        gastroCheckinKeyStore = GastroCheckinKeyStore.getInstance(this)
        val user = FirebaseAuth.getInstance().currentUser!!
        val firestore = FirebaseFirestore.getInstance()
        secretKeyValidation =
            SecretKeyValidation(
                firestore,
                user,
                GastroCheckinEncryptor(GastroCheckinKeyStore.getInstance(this))
            )
        keyDerivator = KeyDerivator(SaltRepositoryFirebase(firestore, user))
    }

    override fun onResume() {
        super.onResume()
        val userPassword = gastroCheckinKeyStore.userPasssword
        if (userPassword != null) {
            showDisplayView(userPassword)
        } else {
            showCreateView()
        }
    }

    fun showDisplayView(secretetKey: String) {
        save_secret.visibility = GONE
        remove_secret.visibility = VISIBLE
        secret.isEnabled = false
        secret.setText(secretetKey)
        remove_secret.setOnClickListener {
            gastroCheckinKeyStore.secretKey = null
            gastroCheckinKeyStore.userPasssword = null
            showCreateView()
        }
    }

    fun showCreateView() {
        save_secret.visibility = VISIBLE
        remove_secret.visibility = GONE
        secret.isEnabled = true
        secret.text.clear()
        save_secret.setOnClickListener {
            val userPassword = secret.text.toString()

            Transformations.switchMap(keyDerivator.deriveKey(userPassword)) { resultSecretKey ->
                Transformations.map(isValid(resultSecretKey)) {isValid ->
                    resultSecretKey to isValid
                }
            }.observe(this, Observer { (resultSecretKey, isValid) ->
                if (isValid) {
                    gastroCheckinKeyStore.userPasssword = userPassword
                    gastroCheckinKeyStore.secretKey = resultSecretKey.getOrThrow()
                    finish()
                } else {
                    secret.setError(getString(R.string.password_error))
                }
            })

        }
    }

    private fun isValid(resultSecretKey: Result<ByteArray>): LiveData<Boolean> {
        val secretKey = resultSecretKey.getOrNull()
        return if (secretKey == null) {
            singleValue(false)
        } else {
            secretKeyValidation.isValid(secretKey)
        }
    }

    private fun singleValue(value: Boolean): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        liveData.postValue(value)
        return liveData
    }
}