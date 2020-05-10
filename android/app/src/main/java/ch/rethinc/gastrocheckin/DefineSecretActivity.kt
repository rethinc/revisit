package ch.rethinc.gastrocheckin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_define_secret.*

class DefineSecretActivity : AppCompatActivity() {

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, DefineSecretActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var gastroCheckinKeyStore: GastroCheckinKeyStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_define_secret)
        gastroCheckinKeyStore =GastroCheckinKeyStore.getInstance(this)
    }

    override fun onResume() {
        super.onResume()
        val secretKey = gastroCheckinKeyStore.secretKey
        if (secretKey != null) {
            showDisplayView(secretKey)
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
            showCreateView()
        }
    }

    fun showCreateView() {
        save_secret.visibility = VISIBLE
        remove_secret.visibility = GONE
        secret.isEnabled = true
        secret.text.clear()
        save_secret.setOnClickListener {
            gastroCheckinKeyStore.secretKey = secret.text.toString()
            finish()
        }
    }
}