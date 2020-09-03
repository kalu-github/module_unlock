package lib.kalu.unlock.fingerprints.auth;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import java.util.concurrent.Executor;

import javax.crypto.Cipher;

import lib.kalu.unlock.fingerprints.util.FingerprintUtil;

/**
 * description: Android M == 9.0
 * create by kalu on 2020-04-01
 */
@RequiresApi(api = Build.VERSION_CODES.P)
public class AuthAndroidP implements AuthInterface {

    private AuthAndroidP() {
    }

    @NonNull
    public static AuthAndroidP from(@NonNull Context context) {
        return new AuthAndroidP();
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
    }

    @Override
    public void authStartP(Context context, BiometricPrompt.AuthenticationCallback authenticationCallback, android.os.CancellationSignal cancellationSignal) {

        // 取消扫描，每次取消后需要重新创建新示例
//        android.os.CancellationSignal cancellationSignal = new android.os.CancellationSignal();
//        if (null != onCancelListener) {
//            cancellationSignal.setOnCancelListener(onCancelListener);
//        }

        Cipher cipher = FingerprintUtil.createCipher();
        BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(cipher);

        // 拉起指纹验证模块，等待验证
        BiometricPrompt.Builder builder = new BiometricPrompt.Builder(context);
        builder.setTitle("Title");
        builder.setSubtitle("Subtitle");
        builder.setDescription("Description");
        builder.setNegativeButton("取消", new Executor() {
            @Override
            public void execute(Runnable command) {
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("authandroidp", "authStartP => which = " + which);
            }
        });
        BiometricPrompt biometricPrompt = builder.build();
        biometricPrompt.authenticate(cryptoObject, cancellationSignal, context.getMainExecutor(), authenticationCallback);
    }
}