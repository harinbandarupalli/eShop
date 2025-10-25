package com.bilbo.store.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EncryptionUtilTest {

    @Test
    void encryptDecrypt() {
        String originalString = "test string";
        String encryptedString = EncryptionUtil.encrypt(originalString);
        String decryptedString = EncryptionUtil.decrypt(encryptedString);

        assertEquals(originalString, decryptedString);
    }
}
