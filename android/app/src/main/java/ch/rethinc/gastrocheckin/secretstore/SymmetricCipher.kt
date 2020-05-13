package ch.rethinc.store

interface SymmetricCipher {

    fun encrypt(cleartext: String): String

    fun decrypt(ciphertext: String?): String?

    fun removeKey()
}