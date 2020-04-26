package com.manyu.videoshare.ui.function;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.BaseSharePerence;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.ANALYSISBean;
import com.manyu.videoshare.bean.UserBean;
import com.manyu.videoshare.dialog.ClipDialog;
import com.manyu.videoshare.ui.FullVideoActivity;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.IntentUtils;
import com.manyu.videoshare.util.MD5Utils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static com.manyu.videoshare.util.ToolUtils.formatTime;

public class VideoExtractActivity extends BaseActivity implements View.OnClickListener {

    private EditText etUrl;
    private TextView btnExtract;
    private ImageView btnClean;
    private LinearLayout videoLayout;
    private ImageView videoBack;
    private ImageView btnStart;
    private VideoView mediaPlayer = null;
    private TextView currentText;
    private TextView totalText;
    private SeekBar seekBar;
    private ImageView btnFull;
    private ClipDialog clipDialog;
    private ProgressBar progressBar;
    private Button save;

    private String oldText = "";
    private UserBean userBean;
    private int viodeWidth;
    private int videoHeigh;
    private String downLoadUrl = "";
    private boolean startanalysis = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_extract);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        RelativeLayout rlATitle = findViewById(R.id.rl_title);
        rlATitle.setBackgroundColor(getResources().getColor(R.color.login_blue));
        ToolUtils.setBar(this);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setText("下载视频");
        findViewById(R.id.title_back).setOnClickListener(this);
        etUrl = findViewById(R.id.et_url);
        btnExtract = findViewById(R.id.btn_extract);
        btnClean = findViewById(R.id.btn_del);
        videoLayout = findViewById(R.id.main_layout_video);
        videoBack = findViewById(R.id.main_video_back);
        btnStart = findViewById(R.id.main_surface_start);
        mediaPlayer = findViewById(R.id.main_surfaceView);
        seekBar = findViewById(R.id.seekbar);
        currentText = findViewById(R.id.textView_playtime);
        totalText = findViewById(R.id.textView_totaltime);
        btnFull = findViewById(R.id.imageView_fullscreen);
        progressBar = findViewById(R.id.main_surface_progress);
        save = findViewById(R.id.main_btn_save);

        btnExtract.setOnClickListener(this);
        btnClean.setOnClickListener(this);
        videoLayout.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnFull.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    @Override
    public void initData() {

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            // 表示手指拖动seekbar完毕，手指离开屏幕会触发以下方法
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 让计时器延时执行
                handReset();
            }

            // 在手指正在拖动seekBar，而手指未离开屏幕触发的方法
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 让计时器取消计时
                mediaPlayer.pause();
                //timer.cancel();
                handler.removeCallbacks(runs);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                Log.e("aaaa", progress + "   " + mediaPlayer.getCurrentPosition());
                if (fromUser) {
                    Globals.log(mediaPlayer.getDuration() + "");
                    int playtime = progress * mediaPlayer.getDuration() / 100;
                    Globals.log(formatTime(playtime));
                    mediaPlayer.seekTo(playtime);
                    currentText.setText(formatTime(playtime));
                    seekBar.setProgress(progress);
                    Globals.log(formatTime(mediaPlayer.getCurrentPosition()));
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!BaseSharePerence.getInstance().getLoginKey().equals("0")) {
            getUserMessage();
        } else {
            videoLayout.setVisibility(View.GONE);
        }
        final String clip = ToolUtils.getClipData();

        if (clip != null && clip.length() > 0 && !BaseSharePerence.getInstance().getLoginKey().equals("0") && clip.contains("http")) {
            if (!oldText.equals(clip)) {
                if (null != clipDialog && clipDialog.isShowing()) {
                    clipDialog.cancel();
                }
                clipDialog = new ClipDialog(this, clip, new ClipDialog.AnalysisUrlListener() {
                    @Override
                    public void analysis() {
                        etUrl.setText(clip);
                        oldText = clip;
                        btnExtract.performClick();
                    }

                    @Override
                    public void clean() {
                        ToolUtils.cleanClipData();
                    }
                });
                if (!clipDialog.isShowing())
                    clipDialog.show();
            }

        }
    }

    public void okHttpDownLoadApk(String url, final Handler handler) {
        final boolean[] first = {true};
        final String path = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator + "/";
        if (!url.contains("http")) {
            url = "http://" + url;
        }
        String md5 = "/去水印_" + MD5Utils.stringToMD5(url) + ".mp4";
        String name = path + md5;
        File dirs = new File((path));
        if (!dirs.exists()) {
            dirs.mkdirs();
        }
        File file = new File(name);
        if (file.exists()) {
            ToastUtils.showShort("视频已存在，无须下载");
            return;
        }
        LoadingDialog.showLoadingDialog(this);
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(path, md5) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(File response, int id) {

                    }

                    @Override
                    public void inProgress(final float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        int pro = (int) (100 * progress);
                        final String text = "开始下载";//正在下载中......" + pro + "%";
                        Log.e("pro", text);
                        if (first[0]) {
                            //ToastUtils.showShort(text);
                            first[0] = false;
                        }
                        //Toast.makeText(activity,text,Toast.LENGTH_SHORT).show();
                        /*Message msg = new Message();
                        msg.what = 0;
                        Bundle b = new Bundle();
                        b.putString("msg",text);
                        msg.setData(b);
                        handler.sendMessage(msg);*/
                        if (pro >= 100) {
                            //videoLayout.setVisibility(View.GONE);
                            ToastUtils.showLong("下载成功！文件保存在" + path);
                            LoadingDialog.closeLoadingDialog();
                            /*Message msg2 = new Message();
                            msg2.what = 1;
                            Bundle b2 = new Bundle();
                            b2.putString("path","下载成功！文件保存在" + path);
                            msg2.setData(b2);
                            handler.sendMessage(msg2);*/


                        }
                    }
                });
    }

    private void getUserMessage() {

        HttpUtils.httpString(Constants.USERMESSAGE, null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("获取信息失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                Globals.log(resultData);
                userBean = gson.fromJson(resultData, UserBean.class);
                Globals.log(AuthCode.authcodeDecode(userBean.getData(), Constants.s));


                LoadingDialog.closeLoadingDialog();
            }
        });
    }

    public void handReset() {
        handler.postDelayed(runs, 1000);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String message = msg.getData().getString("msg");
                    ToastUtils.showShort(message);
                    break;
                case 1:
                    ToastUtils.showShort(msg.getData().getString("path"));
                    break;
            }
            return false;
        }
    });

    public Bitmap getNetVideoBitmap(String videoUrl) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
            int hei = bitmap.getHeight();
            int wid = bitmap.getWidth();
            videoBack.setImageBitmap(bitmap);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    private void initMediaPlayer(String filePath) {
        Globals.log("videoUrl", filePath);
        mediaPlayer.setZOrderMediaOverlay(true);
        mediaPlayer.setVideoPath(filePath);
        //mediaPlayer.setZOrderOnTop(true);
        //media.setMediaController(new MediaController(this));
        //mediaPlayer.requestFocus();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                btnStart.setVisibility(View.VISIBLE);
                videoBack.setVisibility(View.VISIBLE);
                totalText.setText(formatTime(mp.getDuration()));
                progressBar.setVisibility(View.GONE);
                videoHeigh = mp.getVideoHeight();
                viodeWidth = mp.getVideoWidth();
                Log.e("time", formatTime(mp.getDuration()) + "__" + formatTime(mp.getCurrentPosition()));
                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        // seekTo 方法完成时的回调
                        mediaPlayer.start();
                    }
                });
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnStart.setImageResource(R.drawable.main_video_stop);
                btnStart.setVisibility(View.VISIBLE);
                seekBar.setProgress(100);
                handler.removeCallbacks(runs);
            }
        });
    }

    public void analysisUrl(String url) {
        startanalysis = true;
        Map<String, String> params = new HashMap<>();
        params.put("content", url);
        LoadingDialog.showLoadingDialog(this);
        HttpUtils.httpString(Constants.ANALYSIS, params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("解析失败，连接不到服务器");
                startanalysis = false;
            }

            @Override
            public void httpResponse(String resultData) {
                Globals.log(resultData);
                Gson gson = new Gson();
                ANALYSISBean bean = gson.fromJson(resultData, ANALYSISBean.class);
                LoadingDialog.closeLoadingDialog();
                progressBar.setVisibility(View.VISIBLE);
                if (bean.getCode() == 200) {
                    videoLayout.setVisibility(View.VISIBLE);
                    //GlideUtils.loadImg(getActivity(),bean.getDatas().getPoster(),videoBack);
                    getNetVideoBitmap(bean.getDatas().getVideo_url());
                    downLoadUrl = bean.getDatas().getVideo_url();
                    initMediaPlayer(downLoadUrl);
                    getUserMessage();
                } else {
                    ToastUtils.showShort(bean.getMsg());
                }
                startanalysis = false;
            }
        });
    }

    long his_time = 0;
    Runnable runs = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                int currentPlayer = mediaPlayer.getCurrentPosition();
                if (currentPlayer > 0) {
                    mediaPlayer.getCurrentPosition();
                    currentText.setText(formatTime(currentPlayer));

                    // 让seekBar也跟随改变
                    int progress = (int) ((currentPlayer / (float) mediaPlayer
                            .getDuration()) * 100);

                    seekBar.setProgress(progress);
                    if (formatTime(currentPlayer).equals(formatTime(mediaPlayer
                            .getDuration())) || his_time == currentPlayer) {
                        btnStart.setVisibility(View.VISIBLE);
                        btnStart.setImageResource(R.drawable.main_video_stop);
                    } else {
                        his_time = currentPlayer;
                        handReset();
                    }
                } else {
                    currentText.setText("00:00");
                    seekBar.setProgress(0);
                    btnStart.setImageResource(R.drawable.main_video_stop);
                }
            }
        }
    };

    private static final int BAIDU_READ_PHONE_STATE = 100;

    //请求权限
    public void showContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, BAIDU_READ_PHONE_STATE);
        } else {
            okHttpDownLoadApk(downLoadUrl, handler);
        }
    }

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    //okHttpDownLoadApk(downLoadUrl,handler);
                } else {
                    // 没有获取到权限，做特殊处理
                    ToastUtils.showShort("获取存储权限失败，请手动开启");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_extract:
                String text = etUrl.getText().toString().trim();
                if (startanalysis) {
                    ToastUtils.showShort("正在解析视频中，请勿重复点击");
                    return;
                }
                if (TextUtils.isEmpty(text)) {
                    ToastUtils.showShort("请输入要解析的链接");
                    return;
                }
                if (!text.contains("http")) {
                    ToastUtils.showShort("请输入正确的视频链接地址");
                    return;
                }
                mediaPlayer.pause();
//                analysisUrl(text);
                initMediaPlayer(text);
                downLoadUrl = text;
                videoLayout.setVisibility(View.VISIBLE);
                mediaPlayer.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_del:
                etUrl.setText("");
                break;
            case R.id.main_surface_start:
                if (mediaPlayer.isPlaying()) {
                    btnStart.setImageResource(R.drawable.main_video_stop);
                    mediaPlayer.pause();
                    handler.removeCallbacks(runs);
                } else {
                    mediaPlayer.start();
                    currentText.setText(formatTime(mediaPlayer.getCurrentPosition()));
                    int progress = (int) ((mediaPlayer.getCurrentPosition() / (float) mediaPlayer
                            .getDuration()) * 100);
                    seekBar.setProgress(progress);
                    btnStart.setVisibility(View.GONE);
                    btnStart.setImageResource(R.drawable.main_video_play);
                    videoBack.setVisibility(View.GONE);
                    handReset();
                }
                break;
            case R.id.main_layout_video:
                btnStart.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer.isPlaying()) {
                            btnStart.setVisibility(View.GONE);
                        }
                    }
                }, 1500);
                break;
            case R.id.imageView_fullscreen:
                Bundle bforVideo = new Bundle();
                bforVideo.putString("url", downLoadUrl);
                bforVideo.putInt("width", viodeWidth);
                bforVideo.putInt("heigh", videoHeigh);
                IntentUtils.JumpActivity(this, FullVideoActivity.class, bforVideo);
                break;
            case R.id.main_btn_save:
                showContacts();

                break;
        }
    }
}
