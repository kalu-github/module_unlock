package lib.kalu.unlock.fingerprints.auth;

import android.content.Context;
import android.hardware.biometrics.BiometricPrompt;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

/**
 * description: interface
 * create by kalu on 2020-04-01
 */
public interface AuthInterface {

    /**
     * 检测指纹硬件是否可用，及是否添加指纹
     *
     * @param context
     * @return
     */
    boolean authEnable(Context context);

    boolean authPrints(Context context);

    /**
     * 初始化并调起指纹验证
     */
    void authStartM(Context context, FingerprintManagerCompat.AuthenticationCallback authenticationCallback, androidx.core.os.CancellationSignal cancellationSignal);

    /**
     * 初始化并调起指纹验证
     */
    void authStartP(Context context, BiometricPrompt.AuthenticationCallback authenticationCallback, android.os.CancellationSignal cancellationSignal);
}
