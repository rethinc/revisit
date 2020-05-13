package ch.rethinc.gastrocheckin.secretstore

import java.security.SecureRandom

object Random {

    fun secureRandom(): SecureRandom =
        SecureRandom.getInstance("SHA1PRNG")

    fun bytes(numBytes: Int): ByteArray {
        val bytes = ByteArray(numBytes)
        val secureRandom = secureRandom()
        secureRandom.nextBytes(bytes)
        return bytes
    }
}