package dev.rkashapov.base.security

import korlibs.crypto.AES
import korlibs.crypto.CipherMode
import korlibs.crypto.CipherPadding
import korlibs.crypto.get
import org.springframework.stereotype.Service

@Service
class AESService {

    private val cipherMode = CipherMode.ECB

    fun encrypt(plainMessage: ByteArray, cipherKey: ByteArray): ByteArray {
        val crypt = AES(cipherKey)[cipherMode, CipherPadding.NoPadding]

        return crypt.encrypt(plainMessage)
    }

    fun decrypt(encryptedMessage: ByteArray, cipherKey: ByteArray): ByteArray {
        val crypt = AES(cipherKey)[cipherMode, CipherPadding.NoPadding]

        return crypt.decrypt(encryptedMessage)
    }

}
