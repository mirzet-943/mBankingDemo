package com.mirzet.mbanking.Helpers;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

public class AccountUtils {

    private static final String PIN_ALIAS = "mbanking_pincode";

    private KeyStore loadKeyStore() {
        try {
            final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore;
        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | CertificateException
                | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String encode(@NonNull Context context, String alias, String input, boolean isAuthorizationRequared) {
        try {
            final Cipher cipher = getEncodeCipher(context, alias, isAuthorizationRequared);
            final byte[] bytes = cipher.doFinal(input.getBytes());
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }

    // More information about this hack
    // from https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.html
    // from https://code.google.com/p/android/issues/detail?id=197719
    private Cipher getEncodeCipher(@NonNull Context context, String alias, boolean isAuthenticationRequired) {
        final Cipher cipher = getCipherInstance();
        final KeyStore keyStore = loadKeyStore();
        generateKeyIfNecessary(context, keyStore, alias, isAuthenticationRequired);
        initEncodeCipher(cipher, alias, keyStore);
        return cipher;

    }

    private boolean generateKeyIfNecessary(@NonNull Context context, @NonNull KeyStore keyStore, String alias,
                                           boolean isAuthenticationRequired) {
        try {
            return keyStore.containsAlias(alias) || generateKey(context, alias, isAuthenticationRequired);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean generateKey(Context context, String keystoreAlias, boolean isAuthenticationRequired) {
        return generateKey(keystoreAlias, isAuthenticationRequired);
    }

    private String decode(String encodedString, Cipher cipher) {
        try {
            final byte[] bytes = Base64.decode(encodedString, Base64.NO_WRAP);
            return new String(cipher.doFinal(bytes));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String decode(String alias, String encodedString) {
        try {
            final Cipher cipher = getCipherInstance();
            initDecodeCipher(cipher, alias);
            final byte[] bytes = Base64.decode(encodedString, Base64.NO_WRAP);
            return new String(cipher.doFinal(bytes));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean generateKey(String keystoreAlias, boolean isAuthenticationRequired) {
        try {
            final KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
            keyGenerator.initialize(
                    new KeyGenParameterSpec.Builder(keystoreAlias,
                            KeyProperties.PURPOSE_ENCRYPT |
                                    KeyProperties.PURPOSE_DECRYPT)
                            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                            .setUserAuthenticationRequired(isAuthenticationRequired)
                            .build());
            keyGenerator.generateKeyPair();
            return true;

        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException exc) {
            exc.printStackTrace();
            return false;
        }
    }

    private Cipher getCipherInstance() {
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void initDecodeCipher(Cipher cipher, String alias) {
        try {
            final KeyStore keyStore = loadKeyStore();
            final PrivateKey key = (PrivateKey) keyStore.getKey(alias, null);
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException
                | InvalidKeyException e) {
            e.printStackTrace();
        }

    }
    public boolean encodePin(Context context, String pin) {
        try {
            final String encoded = encode(context, PIN_ALIAS, pin, false);
            PreferencesSettings.saveToPref(context,encoded);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "Error while storing pincode: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    public boolean checkPin(Context context, String encodedPin, String pin) {
        try {
            final String pinCode = decode(PIN_ALIAS, encodedPin);
            return (pinCode.equals(pin));
        } catch (Exception e) {

            Toast.makeText(context, "Error while checking pincode: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void initEncodeCipher(Cipher cipher, String alias, KeyStore keyStore) {
        try {
            final PublicKey key = keyStore.getCertificate(alias).getPublicKey();
            final PublicKey unrestricted = KeyFactory.getInstance(key.getAlgorithm()).generatePublic(
                    new X509EncodedKeySpec(key.getEncoded()));
            final OAEPParameterSpec spec = new OAEPParameterSpec("SHA-256", "MGF1",
                    MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.ENCRYPT_MODE, unrestricted, spec);
        } catch (KeyStoreException | InvalidKeySpecException |
                NoSuchAlgorithmException | InvalidKeyException |
                InvalidAlgorithmParameterException e) {
        }
    }

    public boolean isKeystoreContainAlias(String alias) {
        final KeyStore keyStore = loadKeyStore();
        try {
            return keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();

        }
        return false;
    }

    public boolean isPinCodeEncryptionKeyExist() {
        try {
            final boolean isExist = isKeystoreContainAlias(PIN_ALIAS);
            return isExist;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteKey(String alias) {
        final KeyStore keyStore = loadKeyStore();
        try {
            keyStore.deleteEntry(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }
}
