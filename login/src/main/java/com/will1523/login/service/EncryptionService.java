package com.will1523.login.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    private final KeyPair keyPair;

    public EncryptionService() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        this.keyPair = keyGen.generateKeyPair();
    }

    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            javax.crypto.spec.OAEPParameterSpec oaepParams = new javax.crypto.spec.OAEPParameterSpec(
                "SHA-256", 
                "MGF1", 
                java.security.spec.MGF1ParameterSpec.SHA256, 
                javax.crypto.spec.PSource.PSpecified.DEFAULT
            );
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate(), oaepParams);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }
}
