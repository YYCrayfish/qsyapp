package com.manyu.videoshare.ui.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.jude.rollviewpager.RollPagerView;
import com.manyu.videoshare.R;
import com.manyu.videoshare.adapter.AskandAnswerAdapter;
import com.manyu.videoshare.adapter.RollPagerAdapter;
import com.manyu.videoshare.adapter.SupportAdapter;
import com.manyu.videoshare.base.BaseFragment;
import com.manyu.videoshare.base.BaseSharePerence;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.ANALYSISBean;
import com.manyu.videoshare.bean.AskandAnswerBean;
import com.manyu.videoshare.bean.SupportBean;
import com.manyu.videoshare.bean.UserBean;
import com.manyu.videoshare.bean.VersionBean;
import com.manyu.videoshare.dialog.ClipDialog;
import com.manyu.videoshare.dialog.NoticesDialog;
import com.manyu.videoshare.dialog.UpdateDialog;
import com.manyu.videoshare.permission.PermissionUtils;
import com.manyu.videoshare.permission.request.IRequestPermissions;
import com.manyu.videoshare.permission.request.RequestPermissions;
import com.manyu.videoshare.permission.requestresult.IRequestPermissionsResult;
import com.manyu.videoshare.permission.requestresult.RequestPermissionsResultSetApp;
import com.manyu.videoshare.ui.FullVideoActivity;
import com.manyu.videoshare.ui.MainActivity;
import com.manyu.videoshare.ui.ShareActivity;
import com.manyu.videoshare.ui.TeachingActivity;
import com.manyu.videoshare.ui.user.UserMessageActivity;
import com.manyu.videoshare.ui.vip.RechargeActivity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.manyu.videoshare.util.ToolUtils.formatTime;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class MainFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private RecyclerView recyclerView;
    private AskandAnswerAdapter askandAnswerAdapter;
    private EditText editUrl;
    private Button btnAnalysis;
    private ImageView btnStart;

    private VideoView mediaPlayer = null;
    private TextView currentText;
    private TextView totalText;
    private SeekBar seekBar;
    private ImageView videoBack;
    private ImageView btnFull;
    private Button save;
    private String downLoadUrl = "";
    private LinearLayout videoLayout;
    private ProgressBar progressBar;
    private ImageView btnDel;
    private RecyclerView gridView;
    private SupportAdapter gridViewAdapter;
    private List<SupportBean.DataBean> gridviewData;
    private ClipDialog clipDialog;
    private String oldText = "";
    private LinearLayout btnTeaching;
    private MainActivity activity;
    private UserBean userBean;
    private UpdateDialog updateDialog = null;
    private RollPagerView rollPagerView;
    private RollPagerAdapter rollPagerAdapter;
    private int viodeWidth;
    private int videoHeigh;
    private boolean startanalysis = false;
    IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求
    IRequestPermissionsResult requestPermissionsResult = RequestPermissionsResultSetApp.getInstance();//动态权限请求结果处理
    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        activity = (MainActivity) getActivity();
        initView();
        initData();
        return view;
    }
    //请求权限
    private boolean requestPermissions(){
        //需要请求的权限
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
        //开始请求权限
        return requestPermissions.requestPermissions(
                getActivity(),
                permissions,
                PermissionUtils.ResultCode1);
    }
   /* //用户授权操作结果（可能授权了，也可能未授权）
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //用户给APP授权的结果
        //判断grantResults是否已全部授权，如果是，执行相应操作，如果否，提醒开启权限
        if(requestPermissionsResult.doRequestPermissionsResult(getActivity(), permissions, grantResults)){
            //请求的权限全部授权成功，此处可以做自己想做的事了
            //输出授权结果
            //Toast.makeText(UserMessageActivity.this,"授权成功，请重新点击刚才的操作！",Toast.LENGTH_LONG).show();
        }else{
            //输出授权结果
            //Toast.makeText(UserMessageActivity.this,"请给APP授权，否则功能无法正常使用！",Toast.LENGTH_LONG).show();
        }
    }*/
    @Override
    public void onResume() {
        super.onResume();
        if(!BaseSharePerence.getInstance().getLoginKey().equals("0")){
            getUserMessage();
        }else{
            videoLayout.setVisibility(View.GONE);
        }
        if(activity !=null && !activity.isShowMain()){
            return;
        }
        initGridview();
        initRoll();
        getTestData();
        final String clip = ToolUtils.getClipData();

        if(clip != null && clip.length() >0 && !BaseSharePerence.getInstance().getLoginKey().equals("0") && clip.contains("http")){
            if(!oldText.equals(clip)) {
                if(null != clipDialog && clipDialog.isShowing()){
                    clipDialog.cancel();
                }
                if(null != updateDialog && updateDialog.isShowing()){
                    return;
                }
                clipDialog = new ClipDialog(getActivity(), clip, new ClipDialog.AnalysisUrlListener() {
                    @Override
                    public void analysis() {
                        editUrl.setText(clip);
                        oldText = clip;
                        btnAnalysis.performClick();
                    }

                    @Override
                    public void clean() {
                        ToolUtils.cleanClipData();
                    }
                });
                if(!clipDialog.isShowing())
                    clipDialog.show();
            }

        }
    }

    private void initView(){
        recyclerView = view.findViewById(R.id.main_ask_list);
        mediaPlayer = view.findViewById(R.id.main_surfaceView);
        editUrl = view.findViewById(R.id.main_edit_url);
        btnAnalysis = view.findViewById(R.id.main_btn_analysis);
        btnStart = view.findViewById(R.id.main_surface_start);
        videoBack = view.findViewById(R.id.main_video_back);
        currentText = view.findViewById(R.id.textView_playtime);
        totalText = view.findViewById(R.id.textView_totaltime);
        seekBar = view.findViewById(R.id.seekbar);
        btnFull = view.findViewById(R.id.imageView_fullscreen);
        save = view.findViewById(R.id.main_btn_save);
        videoLayout = view.findViewById(R.id.main_layout_video);
        progressBar = view.findViewById(R.id.main_surface_progress);
        btnDel = view.findViewById(R.id.main_btn_del);
        gridView = view.findViewById(R.id.main_recycleview);
        btnTeaching = view.findViewById(R.id.main_btn_teaching);
        rollPagerView = view.findViewById(R.id.rollPagerView);

        btnStart.setOnClickListener(this);
        btnAnalysis.setOnClickListener(this);
        save.setOnClickListener(this);
        btnFull.setOnClickListener(this);
        videoLayout.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnTeaching.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        gridView.setLayoutManager(new GridLayoutManager(getActivity(),5));
        /*recyclerView.setFocusableInTouchMode(false);
        recyclerView.requestFocus();*/



    }
    private void initRoll(){
        List<Integer> mRollList = new ArrayList<>();
        mRollList.add(R.drawable.home_banner);
//        mRollList.add(R.drawable.main_bannar_2);
        int wid = ToolUtils.getScreenWidth();
        int hei = wid * 360 /750;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(wid,hei);
        rollPagerView.setLayoutParams(layoutParams);
        rollPagerAdapter = new RollPagerAdapter(mRollList,getActivity());
        if(mRollList.size()<=1){
            rollPagerView.setHintView(null);
        }
        rollPagerView.setAdapter(rollPagerAdapter);
        rollPagerView.setPlayDelay(8000);
    }
    private void initData(){

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
                if (fromUser) {
                    Globals.log(mediaPlayer.getDuration()+"");
                    int playtime = progress * mediaPlayer.getDuration() / 100;
                    Globals.log(formatTime(playtime));
                    mediaPlayer.seekTo(playtime);
                    currentText.setText(formatTime(playtime));
                    seekBar.setProgress(progress);
                    Globals.log(formatTime(mediaPlayer.getCurrentPosition()));
                }
            }
        });
        checkVersion();
    }
    private void initGridview(){
        HttpUtils.httpString(Constants.SUPPORT,null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
            }

            @Override
            public void httpResponse(String resultData) {
                Globals.log(resultData);
                Gson gson = new Gson();
                SupportBean bean = gson.fromJson(resultData,SupportBean.class);
                if(bean.getCode() == 200){
                    if(bean.getDatas().size() > 1){
                        gridViewAdapter = new SupportAdapter(getActivity(),bean.getDatas());
                        gridView.setAdapter(gridViewAdapter);
                        return;
                    }
                    List<SupportBean.DataBean> dataBeans = new ArrayList<>();
                    dataBeans.add(bean.getDatas().get(0));
                    dataBeans.add(bean.getDatas().get(0));
                    dataBeans.add(bean.getDatas().get(0));
                    dataBeans.add(bean.getDatas().get(0));
                    dataBeans.add(bean.getDatas().get(0));
                    dataBeans.add(bean.getDatas().get(0));
                    dataBeans.add(bean.getDatas().get(0));
                    dataBeans.add(bean.getDatas().get(0));
                    dataBeans.add(bean.getDatas().get(0));
                    dataBeans.add(bean.getDatas().get(0));
                    gridViewAdapter = new SupportAdapter(getActivity(),dataBeans);
                    gridView.setAdapter(gridViewAdapter);
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(getActivity());
        switch (v.getId()){
            case R.id.main_btn_analysis:
                if(startanalysis){
                    ToastUtils.showShort("正在解析视频中，请勿重复点击");
                    return;
                }
                if(BaseSharePerence.getInstance().getLoginKey().equals("0")){
                    ToastUtils.showShort("当前用户未登录，请先登录后在使用");
                    return;
                }
                String text = editUrl.getText().toString();
                if(TextUtils.isEmpty(text)){
                    ToastUtils.showShort("请输入要解析的链接");
                    return;
                }
                if(!text.contains("http")){
                    ToastUtils.showShort("请输入正确的视频链接地址");
                    return;
                }
                /*if(null == userBean){
                    getUserMessage();
                    ToastUtils.showShort("正在获取用户信息，请稍候在试");
                    return;
                }*/
                if(null == userBean.getDatas().getVip_end_time()){
                    if(!(userBean.getDatas().getParse_times() > 0)){
                        //ToastUtils.showShort("当前解析次数不够且不是会员，无法解析");
                        NoticesDialog noticesDialog = new NoticesDialog(getActivity(), new NoticesDialog.AnalysisUrlListener() {
                            @Override
                            public void analysis() {
                                IntentUtils.JumpActivity(getActivity(),RechargeActivity.class);
                            }

                            @Override
                            public void share() {
                                IntentUtils.JumpActivity(getActivity(),ShareActivity.class);
                            }
                        });
                        noticesDialog.show();
                        return;
                    }
                }
                //editUrl.setFocusable(false);
                //editUrl.setFocusableInTouchMode(false);
                downLoadUrl = "";
                mediaPlayer.pause();
                mediaPlayer.setVisibility(View.GONE);
                videoLayout.setVisibility(View.GONE);
                analysisUrl(text);
                mediaPlayer.setVisibility(View.VISIBLE);
                break;
            case R.id.main_surface_start:
                if(mediaPlayer.isPlaying()){
                    btnStart.setImageResource(R.drawable.main_video_stop);
                    mediaPlayer.pause();
                    handler.removeCallbacks(runs);
                }else {
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
            case R.id.main_btn_save:
                showContacts();

                break;
            case R.id.main_layout_video:
                btnStart.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mediaPlayer.isPlaying()){
                            btnStart.setVisibility(View.GONE);
                        }
                    }
                },1500);
                break;
            case R.id.main_btn_del:
                editUrl.setText("");
                break;
            case R.id.main_btn_teaching:
                IntentUtils.JumpActivity(getActivity(),TeachingActivity.class);

                break;
            case R.id.imageView_fullscreen:
                Bundle bforVideo = new Bundle();
                bforVideo.putString("url",downLoadUrl);
                bforVideo.putInt("width",viodeWidth);
                bforVideo.putInt("heigh",videoHeigh);
                IntentUtils.JumpActivity(getActivity(),FullVideoActivity.class,bforVideo);
                break;
                default:
                    break;
        }
    }
    public void analysisUrl(String url){
        startanalysis = true;
        Map<String,String> params = new HashMap<>();
        params.put("content",url);
        LoadingDialog.showLoadingDialog(getActivity());
        HttpUtils.httpString(Constants.ANALYSIS,params, new HttpUtils.HttpCallback() {
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
                ANALYSISBean bean = gson.fromJson(resultData,ANALYSISBean.class);
                LoadingDialog.closeLoadingDialog();
                progressBar.setVisibility(View.VISIBLE);
                if(bean.getCode() == 200){
                    videoLayout.setVisibility(View.VISIBLE);
                    //GlideUtils.loadImg(getActivity(),bean.getDatas().getPoster(),videoBack);
                    getNetVideoBitmap(bean.getDatas().getVideo_url());
                    downLoadUrl = bean.getDatas().getVideo_url();
                    initMediaPlayer(downLoadUrl);
                    getUserMessage();
                }else{
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
                    if(formatTime(currentPlayer).equals(formatTime(mediaPlayer
                            .getDuration())) || his_time == currentPlayer){
                        btnStart.setVisibility(View.VISIBLE);
                        btnStart.setImageResource(R.drawable.main_video_stop);
                    }else{
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
    public void handReset(){
        handler.postDelayed(runs,1000);
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
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
        Globals.log("videoUrl",filePath);
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
                Log.e("time",formatTime(mp.getDuration())+"__"+formatTime(mp.getCurrentPosition()));
                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        // seekTo 方法完成时的回调
                            mediaPlayer.start();
                    }
                });
            }
        });

    }

    private void getTestData(){
        HttpUtils.httpString(Constants.FAQ,null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
            }

            @Override
            public void httpResponse(String resultData) {
                Globals.log(resultData);
                Gson gson = new Gson();
                AskandAnswerBean bean = gson.fromJson(resultData,AskandAnswerBean.class);
                if(bean.getCode() == 200){
                    askandAnswerAdapter = new AskandAnswerAdapter(getContext(),bean.getDatas());
                    recyclerView.setAdapter(askandAnswerAdapter);
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
            }
        });

        /*AskandAnswerBean bean1 = new AskandAnswerBean();
        bean1.setAnswer("能解析99%以上视频，但若原作者上传的视频带有水印则无法去除。原视频是否有水印请查看对应app播放时是否有水印");
        bean1.setQuestion("提取的视频还有水印？");
        data.add(bean1);
        AskandAnswerBean bean2 = new AskandAnswerBean();
        bean2.setAnswer("请尝试在浏览器中打开视频链接，若浏览器无法打开则无法解析。");
        bean2.setQuestion("提示视频解析失败？");
        data.add(bean2);
        AskandAnswerBean bean3 = new AskandAnswerBean();
        bean3.setAnswer("提取视频下载失败？");
        bean3.setQuestion("请确认是否为图集，图集可以直接在浏览器中打开链接并长按保存。");
        data.add(bean3);*/

    }
    private static final int BAIDU_READ_PHONE_STATE =100;
    //请求权限
    public void showContacts(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, BAIDU_READ_PHONE_STATE);
        }else{
            okHttpDownLoadApk(downLoadUrl,handler);
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
    public void okHttpDownLoadApk(String url, final Handler handler) {
        final boolean[] first = {true};
        final String path = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                +File.separator+"Camera"+File.separator + "/";
        if(!url.contains("http")){
            url = "http://" + url;
        }
        String md5 = "/去水印_" + MD5Utils.stringToMD5(url)  + ".mp4";
        String name = path + md5;
        File dirs = new File((path));
        if(!dirs.exists()){
            dirs.mkdirs();
        }
        File file = new File(name);
        if(file.exists()){
            ToastUtils.showShort("视频已存在，无须下载");
            return;
        }
        LoadingDialog.showLoadingDialog(activity);
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
                        Log.e("pro",text);
                        if(first[0]) {
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
                        if(pro>=100) {
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
    private void getUserMessage(){

        HttpUtils.httpString(Constants.USERMESSAGE,null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("获取信息失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                Globals.log(resultData);
                userBean = gson.fromJson(resultData,UserBean.class);
                Globals.log(AuthCode.authcodeDecode(userBean.getData(),Constants.s));


                LoadingDialog.closeLoadingDialog();
            }
        });
    }
    public void checkVersion(){
        HttpUtils.httpString(Constants.VERSION,null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
            }

            @Override
            public void httpResponse(String resultData) {
                Globals.log(resultData);
                Gson gson = new Gson();
                VersionBean bean = gson.fromJson(resultData,VersionBean.class);
                if(bean.getCode() == 200){
                    String versionName = ToolUtils.getVersionName(getActivity());
                    VersionBean.DataBean dataBean = bean.getDatas();
                    if(null == dataBean){
                        return;
                    }
                    int compare = versionName.compareTo(dataBean.getVersions());
                    if(compare < 0 && dataBean.getType() == 1){
                        //ToastUtils.showShort("有最新版本！");
                        updateDialog = new UpdateDialog(getActivity(),dataBean,1);
                        updateDialog.show();
                    }
                }
            }
        });
    }
}

