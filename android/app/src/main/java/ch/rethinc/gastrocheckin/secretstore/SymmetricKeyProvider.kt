package ch.rethinc.store

import javax.crypto.SecretKey

interface SymmetricKeyProvider {

    fun getOrCreateSecretKey(): SecretKey

    fun removeKey()
}