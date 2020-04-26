package com.manyu.videoshare.ui.user;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.BaseApplication;
import com.manyu.videoshare.base.BaseSharePerence;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.RegisterBean;
import com.manyu.videoshare.bean.UserBean;
import com.manyu.videoshare.bean.VerifyBean;
import com.manyu.videoshare.dialog.ExitDialog;
import com.manyu.videoshare.dialog.SexDialog;
import com.manyu.videoshare.permission.PermissionUtils;
import com.manyu.videoshare.permission.request.IRequestPermissions;
import com.manyu.videoshare.permission.request.RequestPermissions;
import com.manyu.videoshare.permission.requestresult.IRequestPermissionsResult;
import com.manyu.videoshare.permission.requestresult.RequestPermissionsResultSetApp;
import com.manyu.videoshare.ui.account.LoginAcitivty;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.FileProviderUtils;
import com.manyu.videoshare.util.Glide4Engine;
import com.manyu.videoshare.util.GlideUtils;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.IntentUtils;
import com.manyu.videoshare.util.MyDateListener;
import com.manyu.videoshare.util.SystemProgramUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.InvokeParam;
import org.devio.takephoto.model.TException;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.permission.InvokeListener;
import org.devio.takephoto.permission.PermissionManager;
import org.devio.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static com.manyu.videoshare.util.SystemProgramUtils.REQUEST_CODE_CAIQIE;
import static com.manyu.videoshare.util.SystemProgramUtils.REQUEST_CODE_ZHAOPIAN;

public class UserMessageActivity extends BaseActivity implements View.OnClickListener,TakePhoto.TakeResultListener, InvokeListener {
    private LinearLayout btnChangeHead;
    private ImageView headIcon;
    private UserBean userBean;
    private Context context;
    private LinearLayout btnName;
    private TextView textName;
    private LinearLayout btnBirthday;
    private LinearLayout btnSignature;
    private LinearLayout btnSex;
    private TextView textBirthday;
    private TextView textSignature;
    private TextView textSex;
    private SexDialog sexDialog;
    private LoadingDialog mLoadingDialog;
    final private int NICKNAME = 4;
    final private int SIGNATURE = 5;
    TextView confirm;
    IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求
    IRequestPermissionsResult requestPermissionsResult = RequestPermissionsResultSetApp.getInstance();//动态权限请求结果处理
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);
        ToolUtils.setBar(this);

    }

    @Override
    public void initView() {
        context = this;
        TextView tv = findViewById(R.id.title_tv);
        tv.setText("编辑资料");
        confirm = findViewById(R.id.title_right);
        btnChangeHead = findViewById(R.id.message_btn_changeheand);
        headIcon = findViewById(R.id.message_img_head);
        btnName = findViewById(R.id.message_btn_name);
        btnBirthday = findViewById(R.id.message_btn_birthday);
        btnSignature = findViewById(R.id.message_btn_signature);
        btnSex = findViewById(R.id.message_btn_sex);
        textName = findViewById(R.id.message_text_name);
        textBirthday = findViewById(R.id.message_text_birthday);
        textSignature = findViewById(R.id.message_text_signature);
        textSex = findViewById(R.id.message_text_sex);

        confirm.setText("保存");
        //confirm.setVisibility(View.VISIBLE);
        confirm.setOnClickListener(this);
        btnChangeHead.setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
        btnName.setOnClickListener(this);
        btnBirthday.setOnClickListener(this);
        btnSignature.setOnClickListener(this);
        btnSex.setOnClickListener(this);
    }

    @Override
    public void initData() {
        getUserMessage();
    }
    //请求权限
    private boolean requestPermissions(){
        //需要请求的权限
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
        //开始请求权限
        return requestPermissions.requestPermissions(
                this,
                permissions,
                PermissionUtils.ResultCode1);
    }
    private boolean isChange(){
        if(userBean == null || userBean.getData() == null || userBean.getData().length() < 1){
            return false;
        }
        if(!textName.getText().toString().equals(userBean.getDatas().getNickname())){
            return true;
        }
        if(!textBirthday.getText().toString().equals(userBean.getDatas().getBirthday()) && textBirthday.getText().toString().length() > 0){
            return true;
        }
        if(!textSignature.getText().toString().equals(userBean.getDatas().getSignature())){
            return true;
        }
        String sex;
        if(userBean.getDatas().getSex() == 0){
            sex = "保密";
        }else{
            if(userBean.getDatas().getSex() == 1){
                sex = "男";
            }else{
                sex = "女";
            }
        }
        if(!textSex.getText().toString().equals(sex)){
            return true;
        }
        return false;
    }
    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()){
            case R.id.title_back:
                if(isChange()){
                    ExitDialog exitDialog = new ExitDialog(context,"当前用户信息已经改变，是否保存?", new ExitDialog.AnalysisUrlListener() {
                        @Override
                        public void analysis() {
                            changeSex(true);
                        }

                        @Override
                        public void clean() {
                            finish();
                        }
                    });
                    exitDialog.show();
                }else{
                    finish();
                }

                break;
            case R.id.title_right:
                if(isChange()){
                    changeSex(false);
                }
                break;
            case R.id.message_btn_changeheand:
                if(!requestPermissions()){
                    return;
                }
                LoadingDialog.showLoadingDialog(context);
                IntentUtils.JumpActivity(this,SelectPhoneActivity.class,REQUEST_CODE_CAIQIE);
                //SystemProgramUtils.zhaopian(UserMessageActivity.this);
                break;
            case R.id.message_btn_name:
                Bundle name = new Bundle();
                if(!TextUtils.isEmpty(userBean.getDatas().getNickname())){
                    name.putString("name",textName.getText().toString());
                }else{
                    name.putString("name","");
                }

                IntentUtils.JumpActivity(this,ChangeUserNameActivity.class,name,NICKNAME);
                break;
            case R.id.message_btn_birthday:
                setBirthday();
                break;
            case R.id.message_btn_signature:
                Bundle sign = new Bundle();
                sign.putString("sign",textSignature.getText().toString());
                IntentUtils.JumpActivity(this,SignatureActivity.class,sign,SIGNATURE);
                break;
            case R.id.message_btn_sex:
                sexDialog = new SexDialog(context, new SexDialog.AnalysisUrlListener() {
                    @Override
                    public void analysis(int num) {
                        //changeSex(num);
                        if(num == 0){
                            textSex.setText("保密");
                        }else{
                            if(num == 1){
                                textSex.setText("男");
                            }else{
                                textSex.setText("女");
                            }
                        }
                        if(isChange()){
                            confirm.setVisibility(View.VISIBLE);
                        }else{
                            confirm.setVisibility(View.GONE);
                        }
                    }
                });
                sexDialog.show();
                break;
        }
    }
    //用户授权操作结果（可能授权了，也可能未授权）
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //用户给APP授权的结果
        //判断grantResults是否已全部授权，如果是，执行相应操作，如果否，提醒开启权限
        if(requestPermissionsResult.doRequestPermissionsResult(this, permissions, grantResults)){
            //请求的权限全部授权成功，此处可以做自己想做的事了
            //输出授权结果
            Toast.makeText(UserMessageActivity.this,"授权成功，请重新点击刚才的操作！",Toast.LENGTH_LONG).show();
        }else{
            //输出授权结果
            Toast.makeText(UserMessageActivity.this,"请给APP授权，否则功能无法正常使用！",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            if(mLoadingDialog != null){
                mLoadingDialog.cancel();
            }
            LoadingDialog.closeLoadingDialog();
            return;
        }
        Uri filtUri;
        String destFileDir = Environment.getExternalStorageDirectory() + "/manyu/";
        File dir = new File(destFileDir);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        File outputFile = new File(Environment.getExternalStorageDirectory() + "/manyu/head_out.png");//mnt/sdcard/yueyou/tupian_out.jpg");//裁切后输出的图片
        switch (requestCode) {
            case NICKNAME:
                String name = data.getStringExtra("name");
                textName.setText(name);
                if(isChange()){
                    confirm.setVisibility(View.VISIBLE);
                }else{
                    confirm.setVisibility(View.GONE);
                }
                break;
            case SIGNATURE:
                String sign = data.getStringExtra("sign");
                textSignature.setText(sign);
                if(isChange()){
                    confirm.setVisibility(View.VISIBLE);
                }else{
                    confirm.setVisibility(View.GONE);
                }
                break;

            case SystemProgramUtils.REQUEST_CODE_PAIZHAO:
                //拍照完成，进行图片裁切
                File file = new File(Environment.getExternalStorageDirectory() + "/manyu/head.png");//"/mnt/sdcard/yueyou/tupian.jpg");
                filtUri = FileProviderUtils.uriFromFile(UserMessageActivity.this, file);
                //SystemProgramUtils.Caiqie(UserMessageActivity.this, filtUri, outputFile);
                TakePhoto takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
                CropOptions.Builder builder = new CropOptions.Builder();
                builder.setAspectX(150).setAspectY(150);
                builder.setOutputX(150).setOutputY(150);
                Uri output =  FileProviderUtils.uriFromFile(UserMessageActivity.this, outputFile);
                try {
                    takePhoto.onCrop(filtUri,output,builder.create());
                } catch (TException e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_CODE_ZHAOPIAN:
                //相册选择图片完毕，进行图片裁切
                if (data == null ||  data.getData()==null) {
                    return;
                }
                filtUri = data.getData();
                SystemProgramUtils.Caiqie(UserMessageActivity.this, filtUri, outputFile);
                /*List<Uri> result = Matisse.obtainResult(data);
                if(result != null) {
                    filtUri = result.get(0);
                    SystemProgramUtils.Caiqie(UserMessageActivity.this, filtUri, outputFile);
                }*/
                break;
            case REQUEST_CODE_CAIQIE:
                //图片裁切完成，显示裁切后的图片
                try {
                    //Uri uri = Uri.fromFile(outputFile);
                    //File file = new File(Environment.getExternalStorageDirectory(), "/yueyou/tmp.png");
                    //if(outputFile.exists()){
                        String path = Environment.getExternalStorageDirectory() + "/manyu/head_out.png";//"/mnt/sdcard/yueyou/tupian_out.jpg";//Environment.getExternalStorageDirectory() + "/yueyou/tmp.png";
                        path = data.getStringExtra("image");
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                        HttpUtils.uploadImage(Constants.UPDATEHEAD, path, new HttpUtils.HttpCallback() {
                            @Override
                            public void httpError(Call call, Exception e) {
                                LoadingDialog.closeLoadingDialog();
                            }

                            @Override
                            public void httpResponse(String resultData) {
                                Globals.log(resultData);
                                Gson gson = new Gson();
                                VerifyBean bean = gson.fromJson(resultData,VerifyBean.class);
                                Message msg = new Message();
                                msg.what = 0;
                                Bundle b = new Bundle();
                                b.putString("message",bean.getMsg());
                                msg.setData(b);
                                handler.sendMessage(msg);
                                if(bean.getCode() == 200){
                                    getUserMessage();
                                }
                                //LoadingDialog.closeLoadingDialog();
                            }
                        });
                   // }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                break;
        }
        if(mLoadingDialog != null){
            mLoadingDialog.cancel();
        }
    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    ToastUtils.showShort(msg.getData().getString("message"));
                    mLoadingDialog.cancel();
                    break;
                case 1:
                    mLoadingDialog = new LoadingDialog(context);
                    mLoadingDialog.show();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if(isChange()){
                ExitDialog exitDialog = new ExitDialog(context,"当前用户信息已经改变，是否保存?", new ExitDialog.AnalysisUrlListener() {
                    @Override
                    public void analysis() {
                        changeSex(true);
                    }

                    @Override
                    public void clean() {
                        finish();
                    }
                });
                exitDialog.show();
            }else{
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    private void setMessage(UserBean bean){
        if(BaseSharePerence.getInstance().getLoginKey().equals("0")){
            ToastUtils.showShort("当前用户登录过期，请重新登录");
            IntentUtils.JumpActivity(this,LoginAcitivty.class);
            finish();
        }
        if(!TextUtils.isEmpty(userBean.getDatas().getNickname())){
            textName.setText(userBean.getDatas().getNickname());
        }
        if(userBean.getDatas().getAvatar().contains("http")) {
            GlideUtils.loadCircleImage(context,userBean.getDatas().getAvatar(), headIcon);
        }
        if(!userBean.getDatas().getBirthday().contains("0000")){
            textBirthday.setText(userBean.getDatas().getBirthday());
        }
        if(userBean.getDatas().getSex() == 0){
            textSex.setText("保密");
        }else{
            if(userBean.getDatas().getSex() == 1){
                textSex.setText("男");
            }else{
                textSex.setText("女");
            }
        }
        if(!TextUtils.isEmpty(bean.getDatas().getSignature())){
            textSignature.setText(bean.getDatas().getSignature());
        }

    }
    public void setBirthday(){
        int oldyear = 1990;
        int oldmonth = 0;
        int oldDay = 1;
        if(null != userBean.getDatas().getBirthday() && !userBean.getDatas().getBirthday().contains("0000")){
            String oldBirth = userBean.getDatas().getBirthday();
            oldyear = Integer.valueOf(oldBirth.substring(0,4));
            oldBirth = oldBirth.substring(oldBirth.indexOf("-") + 1,oldBirth.length());
            oldmonth = Integer.valueOf(oldBirth.substring(0,oldBirth.indexOf("-"))) - 1;
            oldBirth = oldBirth.substring(oldBirth.indexOf("-")+1,oldBirth.length());
            oldDay = Integer.valueOf(oldBirth);

        }
        new MyDateListener(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String linea = "-";
                if(month < 10){
                    linea = "-0";
                }
                String lineb = "-";
                if(dayOfMonth < 10){
                    lineb = "-0";
                }
                String birth = year + linea + month + lineb + dayOfMonth;
                Globals.log("birth",birth);
                //changeBirth(birth);
                textBirthday.setText(birth);
                if(isChange()){
                    confirm.setVisibility(View.VISIBLE);
                }else{
                    confirm.setVisibility(View.GONE);
                }
            }
        },oldyear,oldmonth,oldDay).show();
    }
    private void getUserMessage(){
        LoadingDialog.showLoadingDialog(context);
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
                setMessage(userBean);

                LoadingDialog.closeLoadingDialog();
            }
        });
    }
    public void changeSex(final boolean close){
        Map<String,String> params = new HashMap<>();
        params.put("nickname",textName.getText().toString());
        int sex;
        if(textSex.getText().toString().equals("保密")){
            sex = 0;
        }else if(textSex.getText().toString().equals("男")){
            sex = 1;
        }else{
            sex = 2;
        }
        params.put("sex",sex+"");
        params.put("birthday",textBirthday.getText().toString());
        params.put("signature",textSignature.getText().toString());
        LoadingDialog.showLoadingDialog(this);
        HttpUtils.httpString(Constants.UPDATEMESSAGE,params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("登录失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                LoadingDialog.closeLoadingDialog();
                Gson gson = new Gson();
                RegisterBean bean = gson.fromJson(resultData,RegisterBean.class);
                if(bean.getCode() == 200){
                    ToastUtils.showShort("修改成功");
                    confirm.setVisibility(View.GONE);
                    if(close){
                        finish();
                    }else {
                        getUserMessage();
                    }

                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
                Globals.log(bean.getMsg());
            }
        });
    }
    public void changeBirth(String birth){
        Map<String,String> params = new HashMap<>();
        params.put("nickname",userBean.getDatas().getNickname());
        params.put("sex",userBean.getDatas().getSex()+"");
        params.put("birthday",birth);
        params.put("signature",userBean.getDatas().getSignature());
        LoadingDialog.showLoadingDialog(this);
        HttpUtils.httpString(Constants.UPDATEMESSAGE,params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("登录失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                LoadingDialog.closeLoadingDialog();
                Gson gson = new Gson();
                RegisterBean bean = gson.fromJson(resultData,RegisterBean.class);
                if(bean.getCode() == 200){
                    ToastUtils.showShort("修改成功");
                    getUserMessage();
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
                Globals.log(bean.getMsg());
            }
        });
    }

    @Override
    public void takeSuccess(TResult result) {

    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        return null;
    }
}
