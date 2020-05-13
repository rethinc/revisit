// We need KeyPairGeneratorSpec since we support devices < API Level 23
@file:Suppress("DEPRECATION")

package ch.rethinc.store

import android.content.Context
import android.security.KeyPairGeneratorSpec
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.util.*
import javax.security.auth.x500.X500Principal

class AsymmetricKeyProviderAndroid(
    private val keyType: Algorithm,
    private val keyAlias: String,
    private val context: Context
) : AsymmetricKeyProvider {

    enum class Algorithm(val standardName: String) {
        RSA("RSA")
    }

    companion object {
        private const val providerName = "AndroidKeyStore"
    }

    private val keyStore: KeyStore
        get() {
            val keyStore = KeyStore.getInstance(providerName)
            keyStore.load(null)
            return keyStore
        }

    override fun getOrCreateKeyPair(): KeyPair =
        keyStore.getOrCreateKeyPair(keyAlias, context)

    override fun removeKeyPair() {
        this.keyStore.removeKeyPair(keyAlias)
    }

    private fun KeyStore.getOrCreateKeyPair(alias: String, context: Context): KeyPair {
        if (this.containsAlias(alias)) {
            return this.getKeyPair(alias)
        }

        return this.createKeyPair(alias, keyType, context)
    }

    private fun KeyStore.getKeyPair(alias: String): KeyPair {
        val privateKey = this.getKey(alias, null) as? PrivateKey
            ?: throw IllegalArgumentException("No valid key for alias $alias found")

        val publicKey = this.getCertificate(alias).publicKey

        return KeyPair(publicKey, privateKey)
    }

    private fun KeyStore.createKeyPair(
        alias: String,
        keyType: Algorithm,
        context: Context
    ): KeyPair {
        val certificateStartDate = Calendar.getInstance()
        val certificateEndDate = Calendar.getInstance()
        certificateEndDate.add(Calendar.YEAR, 30)
        val spec = KeyPairGeneratorSpec.Builder(context)
            .setAlias(alias)
            .setSubject(X500Principal("CN=$alias"))
            .setSerialNumber(BigInteger.ONE)
            .setStartDate(certificateStartDate.time)
            .setEndDate(certificateEndDate.time)
            .build()
        val keyPairGenerator =
            KeyPairGenerator.getInstance(
                keyType.standardName,
                this.provider
            )
        keyPairGenerator.initialize(spec)
        return keyPairGenerator.generateKeyPair()
    }

    private fun KeyStore.removeKeyPair(alias: String) {
        this.deleteEntry(alias)
    }
}