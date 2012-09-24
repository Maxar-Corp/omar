package org.ossim.omar.core

import java.security.*
import javax.crypto.*
import javax.crypto.spec.*



class DESCodec {
    public static encode = { String target ->
        def cipher = getCipher(Cipher.ENCRYPT_MODE)
        return cipher.doFinal(target.bytes).encodeBase64()
    }

    public static decode = { String target ->
        def cipher = getCipher(Cipher.DECRYPT_MODE)
        return new String(cipher.doFinal(target.decodeBase64()))
    }

    private static getCipher(mode) {
        def keySpec = new DESKeySpec(getPassword())
        def cipher = Cipher.getInstance("DES")
        def keyFactory = SecretKeyFactory.getInstance("DES")
        cipher.init(mode, keyFactory.generateSecret(keySpec))
        return cipher
    }

    private static getPassword() { "secret15".getBytes("UTF-8") }
}

