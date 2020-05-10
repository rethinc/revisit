package ch.rethinc.store

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.Cipher.ENCRYPT_MODE

/**
 * Crypto-Stuff is explained here:
 * https://android.jlelse.eu/storing-data-securely-on-android-keystore-asymmetric-83b1dc5f47db
 */
class RsaAsymmetricCipher(
    private val asymmetricKeyProvider: AsymmetricKeyProvider
) : AsymmetricCipher {

    companion object {
        private const val cipherAlgorithm = "RSA/ECB/PKCS1Padding"
    }

    override fun removeKeyPair() {
        asymmetricKeyProvider.removeKeyPair()
    }

    override fun encrypt(plainText: ByteArray): ByteArray? {
        return doFinalCipher(plainText, asymmetricKeyProvider.getOrCreateKeyPair().public, ENCRYPT_MODE)
    }

    override fun decrypt(plainText: ByteArray): ByteArray? {
        return doFinalCipher(plainText, asymmetricKeyProvider.getOrCreateKeyPair().private, Cipher.DECRYPT_MODE)
    }

    private fun doFinalCipher(
        plainText: ByteArray,
        publicKey: Key,
        encryptMode: Int
    ): ByteArray? {
        val cipher = Cipher.getInstance(cipherAlgorithm)
        cipher.init(encryptMode, publicKey)
        return cipher.doFinal(plainText)
    }
}