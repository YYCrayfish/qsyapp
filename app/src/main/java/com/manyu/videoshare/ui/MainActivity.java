package com.manyu.videoshare.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.jaeger.library.StatusBarUtil;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.BaseApplication;
import com.manyu.videoshare.base.BaseSharePerence;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.AnalysisTimeBean;
import com.manyu.videoshare.dialog.AgreeDialog;
import com.manyu.videoshare.dialog.AgreementDialog;
import com.manyu.videoshare.permission.PermissionUtils;
import com.manyu.videoshare.permission.request.IRequestPermissions;
import com.manyu.videoshare.permission.request.RequestPermissions;
import com.manyu.videoshare.ui.fragment.MainFragment;
import com.manyu.videoshare.ui.fragment.MainFragment1;
import com.manyu.videoshare.ui.fragment.UserFragment;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import okhttp3.Call;

public class MainActivity extends BaseActivity {
    private Fragment currentFragment = new Fragment();
    private MainFragment1 mainFragment;
    private UserFragment userFragment;
    private RadioGroup radioGroup;
    private MainActivity activity;
    private boolean showMain = true;
    private boolean mainFirst = true;
    private boolean userFirst = true;
    private AgreeDialog dialog;
    private AgreementDialog agreementDialog;
    private IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        activity = this;
        StatusBarUtil.setTranslucentForImageViewInFragment(MainActivity.this, 0, null);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        requestPermissions();
        getAnalysisTime();
        ImmersionBar.with(this).statusBarColorInt(Color.BLACK).init();
    }


    private void getAnalysisTime() {
        HttpUtils.httpString(Constants.ANALYTIC, null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                Globals.log(resultData);
                AnalysisTimeBean timeBean = gson.fromJson(resultData, AnalysisTimeBean.class);
                LoadingDialog.closeLoadingDialog();
                BaseApplication.getInstance().setUserAnalysisTime(timeBean.getData());
            }
        });
    }

    //请求权限
    private boolean requestPermissions() {
        //需要请求的权限
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        //开始请求权限
        return requestPermissions.requestPermissions(
                this,
                permissions,
                PermissionUtils.ResultCode1);
    }

    @Override
    public void initView() {
        radioGroup = findViewById(R.id.main_radiogroup);


        if (mainFragment == null) {
            mainFragment = new MainFragment1();
        }
        switchFragment(mainFragment).commit();
        mainFirst = false;
    }

    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    public void initData() {
        if (BaseSharePerence.getInstance().getIsInstall()) {
            initDialog();
        } else {
            setNotify();
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.main_radio_a:
                        if (mainFragment == null) {
                            mainFragment = new MainFragment1();
                        }
//                        setAndroidNativeLightStatusBar(activity, false);
                        switchFragment(mainFragment).commit();
                        setShowMain(true);
                        if (!mainFirst) {
                            mainFragment.onResume();
                            ToolUtils.havingIntent(activity);
                        }
                        mainFirst = false;
                        break;
                    case R.id.main_radio_b:
                        if (userFragment == null) {
                            userFragment = new UserFragment();
                        }
//                        setAndroidNativeLightStatusBar(activity, false);
                        switchFragment(userFragment).commit();
                        setShowMain(false);
                        if (!userFirst) {
                            userFragment.onResume();
                            ToolUtils.havingIntent(activity);
                        }
                        userFirst = false;
                        break;
                }
            }
        });
        long oldTime = BaseSharePerence.getInstance().getLoginTime() / 1000;
        long times = 30 * 24 * 60 * 60;
        long currents = System.currentTimeMillis() / 1000;
        if (oldTime != 0) {
            if ((currents - oldTime) > times) {
                BaseSharePerence.getInstance().setLoginKey("0");
                BaseSharePerence.getInstance().setLoginTime(0);
                ToastUtils.showShort("登录已经过期，请重新登录");
            }
        }
    }

    private void initDialog() {
        if (dialog == null) {
            dialog = new AgreeDialog(this);
            dialog.setOnClickListener(new AgreeDialog.OnClickListener() {
                @Override
                public void onUserAgree() {
                    if (null != BaseSharePerence.getInstance().getUserAgree()) {
                        agreementDialog = new AgreementDialog(MainActivity.this, "用户协议", BaseSharePerence.getInstance().getUserAgree());
                        agreementDialog.show();
                    } else {
                        ToastUtils.showShort("APP初始化失败");
                    }
                }

                @Override
                public void onPrivacyPolicy() {
                    if (null != BaseSharePerence.getInstance().getPrivacyPolicy()) {
                        agreementDialog = new AgreementDialog(MainActivity.this, "隐私政策", BaseSharePerence.getInstance().getPrivacyPolicy());
                        agreementDialog.show();
                    } else {
                        ToastUtils.showShort("APP初始化失败");
                    }
                }

                @Override
                public void onYes() {
                    dialog.dismiss();
                    BaseSharePerence.getInstance().setIsInstall(false);
                    setNotify();
                }

                @Override
                public void onNo() {
                    dialog.dismiss();
                    initDialog();
                }
            });
        }
        dialog.show();
    }

    private FragmentTransaction switchFragment(Fragment targetFragment) {

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.main_fragment, targetFragment, targetFragment.getClass().getName());

        } else {
            transaction.hide(currentFragment)
                    .show(targetFragment);
        }
        currentFragment = targetFragment;
        return transaction;
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                // 构建Runnable对象，在runnable中更新界面
                Runnable runnableUi = new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShort("再次点击退出应用");
                    }
                };
                runnableUi.run();
                exitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setNotify() {
        boolean enabled = isNotificationEnabled(MainActivity.this);

        if (!enabled) {
            /**
             * 跳到通知栏设置界面
             * @param context
             */
            Intent localIntent = new Intent();
            //直接跳转到应用通知设置的代码：
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                localIntent.putExtra("app_package", this.getPackageName());
                localIntent.putExtra("app_uid", this.getApplicationInfo().uid);
            } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.setData(Uri.parse("package:" + this.getPackageName()));
            } else {
                //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", this.getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.setAction(Intent.ACTION_VIEW);
                    localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                    localIntent.putExtra("com.android.settings.ApplicationPkgName", this.getPackageName());
                }
            }
            this.startActivity(localIntent);
        }
    }

    /**
     * 获取通知权限
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isShowMain() {
        return showMain;
    }

    public void setShowMain(boolean showMain) {
        this.showMain = showMain;
    }
}
