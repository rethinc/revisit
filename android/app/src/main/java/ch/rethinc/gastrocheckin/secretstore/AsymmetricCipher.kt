package ch.rethinc.store

interface AsymmetricCipher {

    fun removeKeyPair()

    fun encrypt(plainText: ByteArray): ByteArray?

    fun decrypt(plainText: ByteArray): ByteArray?

}