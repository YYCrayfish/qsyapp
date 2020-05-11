package com.manyu.videoshare.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;
import com.manyu.videoshare.ui.NoIntentActivity;
import com.manyu.videoshare.util.IntentUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.umeng.analytics.MobclickAgent;
import com.manyu.videoshare.R;

import io.microshow.rxffmpeg.RxFFmpegInvoke;


/**
 * Created by admin on 2017/3/8.
 * <p>
 * <p>
 * 在 lowMemory 的时候，调用 Glide.cleanMemroy() 清理掉所有的内存缓存。
 * 在 App 被置换到后台的时候，调用 Glide.cleanMemroy() 清理掉所有的内存缓存。
 * 在其它情况的 onTrimMemroy() 回调中，直接调用 Glide.trimMemory() 方法来交给 Glide 处理内存情况。
 */
public abstract class BaseActivity extends FragmentActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private View refresh;
    private View bindingView;
    private View spinLoading;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState, persistentState);

    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View mBaseView = LayoutInflater.from(this).inflate(R.layout.activity_base, null, false);
        bindingView = LayoutInflater.from(this).inflate(layoutResID, null, false);
        // content
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        bindingView.setLayoutParams(params);
        RelativeLayout mContainer = (RelativeLayout) mBaseView.findViewById(R.id.container);
        mContainer.addView(bindingView);
        getWindow().setContentView(mBaseView);
        refresh = mBaseView.findViewById(R.id.ll_error_refresh);
        spinLoading = mBaseView.findViewById(R.id.spin_kit);
        StatusBarUtil.setTranslucentForImageViewInFragment(BaseActivity.this, 0, null);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 点击失败布局重新加载
        /*refresh.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showLoading();
                onRefresh();
            }
        });*/
        showContentView();

        setStatusBar();
        initView();
        initData();
    }

    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.white));

        //com.jaeger.library.StatusBarUtil.setTranslucent(this, com.jaeger.library.StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);
    }


    public abstract void initView();

    public abstract void initData();


    /**
     * 失败后点击刷新 ---点击失败默认布局
     */
    protected void onRefresh() {

    }


    /**
     * 加载布局
     */
    protected void showContentView() {
        if (spinLoading.getVisibility() == View.VISIBLE) {
            spinLoading.setVisibility(View.GONE);
        }

        if (refresh.getVisibility() != View.GONE) {
            refresh.setVisibility(View.GONE);
        }
        if (bindingView.getVisibility() != View.VISIBLE) {
            bindingView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 适用于整个界面只有一个接口的情况下----覆盖整个UI
     */
    public void showLoading() {
        if (spinLoading.getVisibility() != View.VISIBLE) {
            spinLoading.setVisibility(View.VISIBLE);
        }

        if (bindingView.getVisibility() != View.GONE) {
            bindingView.setVisibility(View.GONE);
        }
        if (refresh.getVisibility() != View.GONE) {
            refresh.setVisibility(View.GONE);
        }
    }


    /**
     * 适用于整个界面只有一个接口的情况下----覆盖整个UI
     */
    protected void showError() {
        if (spinLoading.getVisibility() == View.VISIBLE) {
            spinLoading.setVisibility(View.GONE);
        }

        if (refresh.getVisibility() != View.VISIBLE) {
            refresh.setVisibility(View.VISIBLE);
        }
        if (bindingView.getVisibility() != View.GONE) {
            bindingView.setVisibility(View.GONE);
        }
    }


    /**
     * Android 获取 屏幕状态栏高度和标题栏高度 避免出现0的情况
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top; //态栏的高度

        Window window = getWindow();
        int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;  // 标题栏高度


    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        ToolUtils.havingIntent(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        RxFFmpegInvoke.getInstance().exit();
        super.onDestroy();
        // 必须调用该方法，防止内存泄漏

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent intent = new Intent();
            setResult(99, intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
