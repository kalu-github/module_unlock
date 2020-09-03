package lib.kalu.unlock.fingerprints.util;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * description: 加密类，用于判定指纹合法性
 * create by kalu on 2020-04-01
 */
@RequiresApi(Build.VERSION_CODES.M)
public class FingerprintUtil {

    private FingerprintUtil() {

    }

    public final static Cipher createCipher() {

        // step1
        KeyStore androidKeyStore = null;
        try {
            androidKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            // LogUtil.e("fingerprintutil", "createCipher[step1] => " + e.getMessage(), e);
        }
        if (null == androidKeyStore) {
            return null;
        }

        // step2 对称加密， 创建 KeyGenerator 对象
        KeyGenerator androidKeyGenerator = null;
        try {
            androidKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            // LogUtil.e("fingerprintutil", "createCipher[step2] => " + e.getMessage(), e);
        }
        if (null == androidKeyGenerator) {
            return null;
        }

        // step3: 获得 KeyGenerator 对象后，就可以生成一个 Key
        boolean generateKey = false;
        try {
            androidKeyStore.load(null);
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder("fingerprintKeystoreAlias",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(true);
            }
            androidKeyGenerator.init(builder.build());
            androidKeyGenerator.generateKey();
            generateKey = true;
        } catch (CertificateException | NoSuchAlgorithmException | IOException | InvalidAlgorithmParameterException e) {
            // LogUtil.e("fingerprintutil", "createCipher[step3] => " + e.getMessage(), e);
        }
        if (!generateKey) {
            return null;
        }

        // step4
        Cipher defaultCipher = null;
        try {

            String transformation = KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7;
            defaultCipher = Cipher.getInstance(transformation);

            Key key = androidKeyStore.getKey("fingerprintKeystoreAlias", null);
            defaultCipher.init(Cipher.ENCRYPT_MODE | Cipher.DECRYPT_MODE, key);

        } catch (Exception e) {
            // LogUtil.e("fingerprintutil", "createCipher[step4] => " + e.getMessage(), e);
        }
        return defaultCipher;

    }

    /**
     * 是否硬件支持
     *
     * @param context
     * @return
     */
    public final static boolean isHardwareDetected(Context context) {

        boolean hardwareDetected = FingerprintManagerCompat.from(context).isHardwareDetected();
        return hardwareDetected;
    }

    /**
     * 是否有指纹数据
     *
     * @param context
     * @return
     */
    public final static boolean hasEnrolledFingerprints(Context context) {

        boolean hasEnrolledFingerprints = FingerprintManagerCompat.from(context).hasEnrolledFingerprints();
        return hasEnrolledFingerprints;
    }
}