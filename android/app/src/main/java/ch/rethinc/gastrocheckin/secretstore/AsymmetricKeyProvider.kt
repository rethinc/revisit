package ch.rethinc.store

import java.security.KeyPair

interface AsymmetricKeyProvider {

    fun getOrCreateKeyPair(): KeyPair

    fun removeKeyPair()
}