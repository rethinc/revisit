package ch.rethinc.store

interface SymmetricCipher {

    fun encrypt(value: String): String

    fun decrypt(value: String?): String?

    fun removeKey()
}