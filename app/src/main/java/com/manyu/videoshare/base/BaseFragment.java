package com.manyu.videoshare.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by Maisi on 2015/1/26.
 */
public class BaseFragment extends Fragment {
    protected static final long DEFAULT_REFRESH_TIME = 2000;
    private static final int MSG_SHOW_TOAST = 999999;
    private static final int MSG_SHOW_TOAST_FLAG_TIME = 999998;
    private BaseFragmentHandler mBaseHandler;
    private volatile boolean isUpdateRunning = false;
    private String mFragmentTitle;
    private View mView;
    private Toast mToast;

    public BaseFragment() {
    }
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    protected void openSoftInput(View view) {
        if (view == null) {

            return;
        }

        try {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void closeSoftInput(View view) {
        if (view == null) {
            return;
        }

        try {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showlog(String msg) {
        //Globals.log(msg);
    }

    protected void showlog(String tag, String msg) {
        //Globals.log(tag, msg);
    }

    public String getmFragmentTitle() {
        return mFragmentTitle;
    }

    public void setmFragmentTitle(String mFragmentTitle) {
        this.mFragmentTitle = mFragmentTitle;
    }

    public void showCustomToast(String text) {

        mToast.show();
    }

    public void showCustomToast(String text, int flag) {

        mToast.setDuration(Toast.LENGTH_LONG);

        mToast.show();
    }

    protected void showToast(String message) {
        if (mBaseHandler != null && isUpdateRunning) {
            Message msg = mBaseHandler.obtainMessage(MSG_SHOW_TOAST, message);
            mBaseHandler.sendMessage(msg);
        }
    }


    /**
     * instead of {@link #showToastLong(String)} since 2017-3-4
     */
    @Deprecated
    protected void showToast(String message, int duration) {
        showToastLong(message);
    }

    protected void showToastLong(String message) {
        if (mBaseHandler != null && isUpdateRunning) {
            Message msg = mBaseHandler.obtainMessage(MSG_SHOW_TOAST_FLAG_TIME, message);
            msg.arg1 = Toast.LENGTH_LONG;
            mBaseHandler.sendMessage(msg);
        }
    }

    @Override
    public void onResume() {
        if (mBaseHandler != null) {
            mBaseHandler.removeCallbacksAndMessages(null);
        }
        mBaseHandler = new BaseFragmentHandler(getActivity());
        isUpdateRunning = true;
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        isUpdateRunning = false;
        if (mBaseHandler != null) {
            mBaseHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBaseHandler = null;
    }

    public void setKeepScreenON() {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void setKeepScreenOFF() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    static class BaseFragmentHandler extends Handler {
        private Toast toast = null;

        WeakReference<Activity> mReference;

        public BaseFragmentHandler(Activity context) {
            mReference = new WeakReference<Activity>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mReference.get() == null) {
                return;
            }
            final Activity context = mReference.get();

            switch (msg.what) {
                case MSG_SHOW_TOAST: {
                    try {
                        Toast.makeText(context, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case MSG_SHOW_TOAST_FLAG_TIME: {
//                    try {
//                        Toast.makeText(context, (String) msg.obj, msg.arg1).show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
                }
            }
        }
    }

    public final void exitSelf() {
        try {
            // 设置退出动画并退出
            FragmentManager fragmentManager = null;
            if (getParentFragment() != null) {
                fragmentManager = getParentFragment().getChildFragmentManager();
            } else {
                fragmentManager = getFragmentManager();
            }
            fragmentManager.beginTransaction().remove(this)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResourcesString(int id) {
        try {
            return getResources().getString(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getResourcesColor(int id) {
        return getResources().getColor(id);
    }

    public Drawable getResourcesDrawable(int id) {
        return getResources().getDrawable(id);
    }
}
