
package com.manyu.videoshare.base;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.jaeger.library.StatusBarUtil;
import com.manyu.videoshare.R;
import com.manyu.videoshare.view.Progressbar;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.microshow.rxffmpeg.RxFFmpegInvoke;


/**
 * Created by admin on 2017/3/8.
 * <p>
 * <p>
 * 在 lowMemory 的时候，调用 Glide.cleanMemroy() 清理掉所有的内存缓存。
 * 在 App 被置换到后台的时候，调用 Glide.cleanMemroy() 清理掉所有的内存缓存。
 * 在其它情况的 onTrimMemroy() 回调中，直接调用 Glide.trimMemory() 方法来交给 Glide 处理内存情况。
 */
public abstract class BaseVideoActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = BaseVideoActivity.class.getSimpleName();

    private TextView tvTitle;
    private View bindingView;
    private Button tvRight;
    private RelativeLayout rlTitle;
    private Progressbar mProgress;
    private LinearLayout clProgress;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState, persistentState);
        ImmersionBar.with(this).statusBarDarkFont(false).statusBarColorInt(Color.BLACK).init();
        setToolBarColor(Color.BLACK);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View mBaseView = LayoutInflater.from(this).inflate(R.layout.activity_base_video, null, false);
        bindingView = LayoutInflater.from(this).inflate(layoutResID, null, false);
        // content
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        bindingView.setLayoutParams(params);
        LinearLayout mContainer = mBaseView.findViewById(R.id.container);
        mContainer.addView(bindingView);
        getWindow().setContentView(mBaseView);
        tvTitle = mBaseView.findViewById(R.id.title_tv);
        tvTitle.setText(getTitleTv());
        tvRight = mBaseView.findViewById(R.id.title_right);
        tvRight.setText("下一步");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setTextColor(getResources().getColor(R.color.white));
        rlTitle = findViewById(R.id.rl_title);
        rlTitle.setBackgroundColor(getResources().getColor(R.color.tran));
        tvTitle.setTextColor(getResources().getColor(R.color.white));
        findViewById(R.id.title_back).setOnClickListener(this);
        tvRight.setOnClickListener(this);
        mProgress = findViewById(R.id.progressbar);
        clProgress = findViewById(R.id.cl_progress);
        StatusBarUtil.setTranslucentForImageViewInFragment(BaseVideoActivity.this, 0, null);
        setStatusBar();
        initView();
        initData();
    }


    public void setToolBarColor(@ColorInt int bg) {
        rlTitle.setBackgroundColor(bg);
    }

    protected abstract String getTitleTv();

    protected void setTitleRight(String titleRight) {
        tvRight.setText(titleRight);
    }

    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.white));
    }

    protected void setProgressBarValue(int proess) {
        if (clProgress.getVisibility() == View.GONE) {
            clProgress.setVisibility(View.VISIBLE);
        }
        tvRight.setEnabled(false);
        // 最多只能到99，不允许到100
        mProgress.setProgress(proess > 99 ? 99 : proess);
    }

    protected void progressEnd() {
        tvRight.setEnabled(true);
        clProgress.setVisibility(View.GONE);
    }

    public abstract void initView();

    public abstract void initData();

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
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
}
