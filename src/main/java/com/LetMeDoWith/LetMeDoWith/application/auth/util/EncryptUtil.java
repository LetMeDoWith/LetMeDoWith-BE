package com.LetMeDoWith.LetMeDoWith.application.auth.util;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EncryptUtil {

    /**
     * n, e로 public key를 계산한다.
     *
     * @param modulus
     * @param exponent
     * @return 계산된 public key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public Key getRSAPublicKey(String modulus, String exponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        byte[] decodeN = Base64.getUrlDecoder().decode(modulus);
        byte[] decodeE = Base64.getUrlDecoder().decode(exponent);

        BigInteger n = new BigInteger(1, decodeN);
        BigInteger e = new BigInteger(1, decodeE);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);
        return keyFactory.generatePublic(keySpec);
    }
}
