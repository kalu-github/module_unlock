//package lib.kalu.unlock.fingerprints;
//
//import android.annotation.SuppressLint;
//import android.app.DialogFragment;
//import android.hardware.biometrics.BiometricPrompt;
//import android.hardware.fingerprint.FingerprintManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.View;
//
//import androidx.annotation.RequiresApi;
//import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
//
///**
// * description: 指纹
// * create by kalu on 2020/01/19
// */
//public class FingerprintDialog extends DialogFragment {
//
//    public static final String TAG = "lib.kalu.core.biometric.fingerprints.fingerprintdialog";
//    public static final String BUNDLE_ISFRAGMENT = "bundle_isfragment";
//    public static final String BUNDLE_FROM = "bundle_from";
//    public static final String BUNDLE_ANDROIDP = "bundle_androidp";  // 是否使用 android p 系统指纹框
//
//    public static final String BUNDLE_STATUS_FAIL = "bundle_status_fail";  // 错误码
//    public static final String BUNDLE_HELP = "bundle_help";
//
//    public static final String RESULT_SUCC = "1";
//    public static final String RESULT_CANCLE = "2";
//    public static final String RESULT_FAIL = "3";
//    public static final String RESULT_HELP_NO_HARDWARK = "4";
//    public static final String RESULT_HELP_TIMEOUT = "5";
//    public static final String RESULT_HELP_DISABLE = "6";
//    public static final String RESULT_HELP_CANCLE = "7";
//    public static final String RESULT_HELP_LATER = "8";
//    public static final String RESULT_HELP_UNKNOW = "9";
//
//    @Override
//    protected boolean isCancelEnable() {
//        return false;
//    }
//
//    @Override
//    protected int setGravity() {
//        return Gravity.CENTER;
//    }
//
//    @Override
//    protected int initView() {
//        return R.layout.lib_core_dialog_fingerprint;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.P)
//    @Override
//    protected void initData() {
//
//        // 容错
//        Bundle arguments = getArguments();
//        if (null == arguments) {
//            Bundle bundle = new Bundle();
//            setArguments(bundle);
//        }
//
//        // 容错
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//
//            // 强制销毁
//            dismissAllowingStateLoss();
//            return;
//        }
//
//        // 取消操作
//        getDialog().findViewById(R.id.dialog_fingerprint_cancle).setOnClickListener(new View.OnClickListener() {
//
//            @FastClick
//            @Override
//            public void onClick(View view1) {
//                onFail(RESULT_CANCLE);
//            }
//        });
//
//        // 开启指纹硬件扫描模块
//        // step1
//        boolean enableAndroidP = false;
//        Bundle argumentss = getArguments();
//        if (null != argumentss) {
//            enableAndroidP = argumentss.getBoolean(BUNDLE_ANDROIDP, false);
//        }
//
//        // step2
//        AuthInterface authInterface = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            if (enableAndroidP) {
//                authInterface = AuthAndroidP.from(getContext());
//            } else {
//                authInterface = AuthAndroidM.from(getContext());
//            }
//
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            authInterface = AuthAndroidM.from(getContext());
//        }
//
//        if (null == authInterface) {
//            dismiss();
//            return;
//        }
//
//        // 检测指纹硬件是否存在或者是否可用，若false，不再弹出指纹验证框
//        if (!authInterface.authEnable(getContext())) {
//            // 强制销毁
//            dismissAllowingStateLoss();
//            return;
//        }
//
//        // 没有指纹数据
//        if (!authInterface.authPrints(getContext())) {
//            onFail(RESULT_HELP_NO_HARDWARK);
//            return;
//        }
//
//        // 吊起指纹
//        if (enableAndroidP) {
//
//            // 初始化
//            Bundle arguments1 = getArguments();
//            arguments1.putString(BUNDLE_STATUS_FAIL, RESULT_FAIL);
//
//            // 监听器
//            android.os.CancellationSignal cancellationSignal = new android.os.CancellationSignal();
//
//            // 取消操作
//            getDialog().findViewById(R.id.dialog_fingerprint_cancle).setOnClickListener(null);
//            getDialog().findViewById(R.id.dialog_fingerprint_cancle).setOnClickListener(new View.OnClickListener() {
//
//                @FastClick
//                @Override
//                public void onClick(View view1) {
//
//                    if (null != cancellationSignal) {
//                        cancellationSignal.cancel();
//                    }
//
//                    onFail(RESULT_CANCLE);
//                }
//            });
//
//            // 唤醒指纹
//            authInterface.authStartP(getContext(), new BiometricPrompt.AuthenticationCallback() {
//
//                @SuppressLint("CheckResult")
//                @Override
//                public void onAuthenticationFailed() {
//                    super.onAuthenticationFailed();
//
//                    // 传值
//                    Bundle arguments = getArguments();
//                    String errMsgId = arguments.getString(BUNDLE_STATUS_FAIL, RESULT_FAIL);
//                    LogUtil.e("fingerprintdialog", "onFailP => errMsgId = " + errMsgId);
//                    onFail(errMsgId);
//                }
//
//                @Override
//                public void onAuthenticationError(int errorCode, CharSequence errString) {
//                    super.onAuthenticationError(errorCode, errString);
//
//                    LogUtil.e("fingerprintdialog", "onErrorP => errorCode = " + errorCode + ", errString = " + errString);
//
//                    onErrorP(errorCode, this, cancellationSignal);
//                }
//
//                @Override
//                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//                    super.onAuthenticationHelp(helpCode, helpString);
//                    LogUtil.e("fingerprintdialog", "onHelpP => helpCode = " + helpCode + ", helpString = " + helpString + ", model = " + Build.MODEL + ", manufacturer = " + Build.MANUFACTURER + ", release = " + Build.VERSION.RELEASE);
//
//                    // 关闭指纹验证：用户取消指纹识别: HUAWEI-TAS-AL00(mate30)
//                    if (1011 == helpCode && "10".equals(Build.VERSION.RELEASE.toLowerCase().trim()) && "huawei".equals(Build.MANUFACTURER.toLowerCase().trim()) && "tas-al00".equals(Build.MODEL.toLowerCase().trim())) {
//
//                        if (null != cancellationSignal) {
//                            cancellationSignal.cancel();
//                        }
//
//                        Bundle arguments = getArguments();
//                        arguments.putString(BUNDLE_STATUS_FAIL, RESULT_HELP_CANCLE);
//
//                        // 触发错误回调
//                        onAuthenticationFailed();
//                    }
//                }
//
//                @Override
//                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
//                    super.onAuthenticationSucceeded(result);
//                    LogUtil.e("fingerprintdialog", "onSuccP =>");
//
//                    onSucc();
//                }
//
//            }, cancellationSignal);
//
//        } else {
//
//            // 初始化
//            Bundle arguments1 = getArguments();
//            arguments1.putString(BUNDLE_STATUS_FAIL, RESULT_FAIL);
//
//            // 监听器
//            androidx.core.os.CancellationSignal cancellationSignal = new androidx.core.os.CancellationSignal();
//
//            // 取消操作
//            getDialog().findViewById(R.id.dialog_fingerprint_cancle).setOnClickListener(null);
//            getDialog().findViewById(R.id.dialog_fingerprint_cancle).setOnClickListener(new View.OnClickListener() {
//
//                @FastClick
//                @Override
//                public void onClick(View view1) {
//
//                    if (null != cancellationSignal) {
//                        cancellationSignal.cancel();
//                    }
//
//                    onFail(RESULT_CANCLE);
//                }
//            });
//
//            // 唤醒指纹
//            authInterface.authStartM(getContext(), new FingerprintManagerCompat.AuthenticationCallback() {
//
//                @Override
//                public void onAuthenticationFailed() {
//                    super.onAuthenticationFailed();
//
//                    // 传值
//                    Bundle arguments = getArguments();
//                    String errMsgId = arguments.getString(BUNDLE_STATUS_FAIL, RESULT_FAIL);
//                    LogUtil.e("fingerprintdialog", "onFailM => errMsgId = " + errMsgId);
//                    onFail(errMsgId);
//                }
//
//                @Override
//                public void onAuthenticationError(int errMsgId, CharSequence errString) {
//                    super.onAuthenticationError(errMsgId, errString);
//                    LogUtil.e("fingerprintdialog", "onErrorM => errMsgId = " + errMsgId + ", errString = " + errString);
//
//                    onErrorM(errMsgId, this, cancellationSignal);
//                }
//
//                @Override
//                public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
//                    super.onAuthenticationHelp(helpMsgId, helpString);
//                    LogUtil.e("fingerprintdialog", "onHelpM => helpMsgId = " + helpMsgId + ", helpString = " + helpString + ", model = " + Build.MODEL + ", manufacturer = " + Build.MANUFACTURER + ", release = " + Build.VERSION.RELEASE);
//
//                    // 关闭指纹验证：用户取消指纹识别: HUAWEI-TAS-AL00(mate30)
//                    if (1011 == helpMsgId && "10".equals(Build.VERSION.RELEASE.toLowerCase().trim()) && "huawei".equals(Build.MANUFACTURER.toLowerCase().trim()) && "tas-al00".equals(Build.MODEL.toLowerCase().trim())) {
//
//                        if (null != cancellationSignal) {
//                            cancellationSignal.cancel();
//                        }
//
//                        Bundle arguments = getArguments();
//                        arguments.putString(BUNDLE_STATUS_FAIL, RESULT_HELP_CANCLE);
//
//                        // 触发错误回调
//                        onAuthenticationFailed();
//                    }
//                }
//
//                @Override
//                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
//                    super.onAuthenticationSucceeded(result);
//                    LogUtil.e("fingerprintdialog", "onSuccM =>");
//
//                    onSucc();
//                }
//
//            }, cancellationSignal);
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.P)
//    private void onErrorP(int errMsgId, BiometricPrompt.AuthenticationCallback callback, android.os.CancellationSignal cancellationSignal) {
//
//        // 操作过于频繁，指纹传感器已停用
//        if (errMsgId == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT_PERMANENT) {
//            Bundle arguments = getArguments();
//            arguments.putString(BUNDLE_STATUS_FAIL, RESULT_HELP_DISABLE);
//
//            // 关闭指纹验证：用户取消指纹识别: HUAWEI-TAS-AL00(mate30)
//            if ("10".equals(Build.VERSION.RELEASE.toLowerCase().trim()) && "huawei".equals(Build.MANUFACTURER.toLowerCase().trim()) && "tas-al00".equals(Build.MODEL.toLowerCase().trim())) {
//                if (null != cancellationSignal) {
//                    cancellationSignal.cancel();
//                }
//            }
//
//            // 触发错误回调
//            callback.onAuthenticationFailed();
//        }
//        // 操作过于频繁，请稍后再试
//        else if (errMsgId == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
//            Bundle arguments = getArguments();
//            arguments.putString(BUNDLE_STATUS_FAIL, RESULT_HELP_LATER);
//
//            // 关闭指纹验证：用户取消指纹识别: HUAWEI-TAS-AL00(mate30)
//            if ("10".equals(Build.VERSION.RELEASE.toLowerCase().trim()) && "huawei".equals(Build.MANUFACTURER.toLowerCase().trim()) && "tas-al00".equals(Build.MODEL.toLowerCase().trim())) {
//
//                if (null != cancellationSignal) {
//                    cancellationSignal.cancel();
//                }
//            }
//
//            // 触发错误回调
//            callback.onAuthenticationFailed();
//        }
//        // 指纹操作已取消
//        else if (errMsgId == FingerprintManager.FINGERPRINT_ERROR_CANCELED) {
//            // Bundle arguments = getArguments();
//            // arguments.putString(BUNDLE_STATUS_FAIL, RESULT_HELP_CANCLE);
//
//            // 触发错误回调
//            // callback.onAuthenticationFailed();
//        }
//        // 其他
//        else {
//            Bundle arguments = getArguments();
//            arguments.putString(BUNDLE_STATUS_FAIL, RESULT_FAIL);
//
//            // 触发错误回调
//            callback.onAuthenticationFailed();
//        }
//    }
//
//    private void onErrorM(int errMsgId, FingerprintManagerCompat.AuthenticationCallback callback, androidx.core.os.CancellationSignal cancellationSignal) {
//
//        // 操作过于频繁，指纹传感器已停用
//        if (errMsgId == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT_PERMANENT) {
//            Bundle arguments = getArguments();
//            arguments.putString(BUNDLE_STATUS_FAIL, RESULT_HELP_DISABLE);
//
//            // 关闭指纹验证：用户取消指纹识别: HUAWEI-TAS-AL00(mate30)
//            if ("10".equals(Build.VERSION.RELEASE.toLowerCase().trim()) && "huawei".equals(Build.MANUFACTURER.toLowerCase().trim()) && "tas-al00".equals(Build.MODEL.toLowerCase().trim())) {
//                if (null != cancellationSignal) {
//                    cancellationSignal.cancel();
//                }
//            }
//
//            // 触发错误回调
//            callback.onAuthenticationFailed();
//        }
//        // 操作过于频繁，请稍后再试
//        else if (errMsgId == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
//            Bundle arguments = getArguments();
//            arguments.putString(BUNDLE_STATUS_FAIL, RESULT_HELP_LATER);
//
//            // 关闭指纹验证：用户取消指纹识别: HUAWEI-TAS-AL00(mate30)
//            if ("10".equals(Build.VERSION.RELEASE.toLowerCase().trim()) && "huawei".equals(Build.MANUFACTURER.toLowerCase().trim()) && "tas-al00".equals(Build.MODEL.toLowerCase().trim())) {
//
//                if (null != cancellationSignal) {
//                    cancellationSignal.cancel();
//                }
//            }
//
//            // 触发错误回调
//            callback.onAuthenticationFailed();
//        }
//        // 指纹操作已取消
//        else if (errMsgId == FingerprintManager.FINGERPRINT_ERROR_CANCELED) {
//            // Bundle arguments = getArguments();
//            // arguments.putString(BUNDLE_STATUS_FAIL, RESULT_HELP_CANCLE);
//
//            // 触发错误回调
//            // callback.onAuthenticationFailed();
//        }
//        // 其他
//        else {
//            Bundle arguments = getArguments();
//            arguments.putString(BUNDLE_STATUS_FAIL, RESULT_FAIL);
//
//            // 触发错误回调
//            callback.onAuthenticationFailed();
//        }
//    }
//
//    private void onFail(String resultMsg) {
//
//        boolean fragment = false;
//        String from = TAG;
//
//        Bundle argumentss = getArguments();
//        if (null != argumentss) {
//            fragment = argumentss.getBoolean(BUNDLE_ISFRAGMENT, false);
//            from = argumentss.getString(BUNDLE_FROM, TAG);
//        }
//
//        FragmentActivity activity = getActivity();
//
//        if (null != activity && activity instanceof BaseView) {
//            BaseView view = (BaseView) activity;
//            view.onDialogFragmentCallBack(TAG, from, resultMsg, fragment);
//        }
//
//        // 强制销毁
//        dismissAllowingStateLoss();
//    }
//
//    private void onSucc() {
//
//        boolean fragment = false;
//        String from = TAG;
//
//        Bundle argumentss = getArguments();
//        if (null != argumentss) {
//            fragment = argumentss.getBoolean(BUNDLE_ISFRAGMENT, false);
//            from = argumentss.getString(BUNDLE_FROM, TAG);
//        }
//
//        FragmentActivity activity = getActivity();
//
//        if (null != activity && activity instanceof BaseView) {
//            BaseView view = (BaseView) activity;
//            view.onDialogFragmentCallBack(TAG, from, RESULT_SUCC, fragment);
//        }
//
//        // 强制销毁
//        dismissAllowingStateLoss();
//    }
//
//    @Override
//    public void dismiss() {
//        // 强制销毁
//        dismissAllowingStateLoss();
//    }
//}
