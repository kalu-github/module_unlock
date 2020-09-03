package lib.kalu.unlock.fingerprints.auth;

import android.content.Context;
import android.hardware.biometrics.BiometricPrompt;

import androidx.annotation.NonNull;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import javax.crypto.Cipher;

import lib.kalu.unlock.fingerprints.util.FingerprintUtil;

/**
 * description: Android M == 6.0
 * create by kalu on 2020-04-01
 */
public class AuthAndroidM implements AuthInterface {

    private AuthAndroidM() {
    }

    @NonNull
    public static AuthAndroidM from(@NonNull Context context) {
        return new AuthAndroidM();
    }

    /**
     * 在 Android Q，Google 提供了 Api BiometricManager.canAuthenticate() 用来检测指纹识别硬件是否可用及是否添加指纹
     * 过尚未开放，标记为"Stub"(存根)
     * 所以暂时还是需要使用 Andorid 6.0 的 Api 进行判断
     */
    @Override
    public boolean authEnable(Context context) {

        // 硬件不可用
        if (!FingerprintUtil.isHardwareDetected(context)) {
            return false;
        }
        // 硬件无指纹
        else if (!FingerprintUtil.hasEnrolledFingerprints(context)) {
            return false;
        }
        // 可用有指纹
        else {
            return true;
        }
    }

    @Override
    public boolean authPrints(Context context) {

        if (FingerprintUtil.hasEnrolledFingerprints(context)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void authStartM(Context context, FingerprintManagerCompat.AuthenticationCallback authenticationCallback, CancellationSignal cancellationSignal) {

        // Android 6.0 指纹管理 实例化
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);

        // 取消扫描，每次取消后需要重新创建新示例
//        CancellationSignal cancellationSignal = new CancellationSignal();
//        if (null != onCancelListener) {
//            cancellationSignal.setOnCancelListener(onCancelListener);
//        }

        Cipher cipher = FingerprintUtil.createCipher();
        // LogUtil.e("authandroidm", "authStartM => cipher = "+cipher);
        FingerprintManagerCompat.CryptoObject cryptoObject = new FingerprintManagerCompat.CryptoObject(cipher);

        //调起指纹验证
        fingerprintManagerCompat.authenticate(cryptoObject, 0, cancellationSignal, authenticationCallback, null);
    }

    @Override
    public void authStartP(Context context, BiometricPrompt.AuthenticationCallback authenticationCallback, android.os.CancellationSignal cancellationSignal) {

    }
}
