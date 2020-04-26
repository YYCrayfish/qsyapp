package com.manyu.videoshare.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebView;

import com.manyu.videoshare.base.BaseSharePerence;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PhoneInfo {
    public static Context context;
    public PhoneInfo(Context context){
        this.context=context;
    }


    @SuppressLint("MissingPermission")
    public String uuid(){
        String androidId = "";
        String tmDevice = "";
        String tmSerial = "";
        try {
            androidId += android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String deviceId = tm.getDeviceId();
                if (deviceId != null) {
                    tmDevice += deviceId;
                }
                String ssNum = tm.getSimSerialNumber();
                if (ssNum != null) {
                    tmSerial += ssNum;
                }
            }
        } catch (Exception e) {
        }
        if (TextUtils.isEmpty(tmSerial)) {
            tmSerial = android.os.Build.SERIAL;
        }
        UUID uuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return uuid.toString();

    }
    public String systemVersion(){
        try{
            return "Android "+android.os.Build.VERSION.RELEASE;
        }catch (Exception e){

            return "";
        }
    }
    public String appId(){
        return "fierteronzd";
    }
    public String device(){
        try{
            return android.os.Build.MODEL;
        }catch (Exception e){

            return "";
        }
    }
    public String packages(){
        try {
            return context.getPackageName();
        }catch (Exception e){
            return "";
        }

    }
    public String from(){
        return "3";
    }

    public String timestamp(){
        //PHP少4位
        String time= new Date().getTime()+"";
        return time.substring(0,time.length()-3);
    }
    public String agentname(){
        /*try {
            return AnalyticsConfig.getChannel(context);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return ToolUtils.getChannel();
    }

    public String userua(){
        String userua = BaseSharePerence.getInstance().getUserUA();
        if (!"".equals(userua)) {
            return userua;
        }
        try {
            WebView web = new WebView(context);
            web.layout(0, 0, 0, 0);
            userua = web.getSettings().getUserAgentString();
            BaseSharePerence.getInstance().setUserUA(userua);
            return userua;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public String mac_address(){

        String macAddress =null;
        String str ="";
        try{
            //linux下查询网卡mac地址的命令
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir =new InputStreamReader(pp.getInputStream());
            LineNumberReader input =new LineNumberReader(ir);for(; null != str;) {
                str = input.readLine();
                if(str !=null) {
                    macAddress = str.trim();// 去空格
                    break;
                }
            }
        }catch(Exception ex) {
            ex.printStackTrace();
            macAddress="";
        }
        if(macAddress!=null&&"".equals(macAddress)){
            return macAddress;
        }
        try {
            WifiManager wifiManager =
                    (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());
            macAddress = info.getMacAddress();
        }catch (Exception e){
            e.printStackTrace();
            return "1";
        }

        return macAddress;

    }






    public String verid(){

        int code = 0;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return code+"";
    }
    public String verName(){

        PackageManager manager = context.getPackageManager();
        String  code = "";
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return code+"";
    }
    public static String appkey(){
        return "56lj6e24xwvfghawer";
    }

    public void appPhoneInfo(Map<String,String> map){
        map.put("uuid",this.uuid());
        map.put("system_version",this.systemVersion());
        map.put("device",this.device());
        map.put("package",this.packages());
        map.put("from",this.from());
        map.put("timestamp",this.timestamp());
        map.put("agentname",this.agentname());
        map.put("userua",this.userua());
        map.put("mac_address",this.mac_address());
        map.put("appid",this.appId());
        map.put("verid",this.verid());
        map.put("params","{}");
        String token = BaseSharePerence.getInstance().getLoginKey();
        if(!"".equals(token)){
            map.put("token",token);
        }
    }

    /**
     * 添加接口的固定参数
     * @return
     */
    public  Map<String,String> appPhoneInfo(){
        Map<String,String> map=new HashMap<>();
        map.put("uuid",this.uuid());
        map.put("system_version",this.systemVersion());
        map.put("device",this.device());
        map.put("package",this.packages());
        map.put("from",this.from());
        map.put("timestamp",this.timestamp());
        map.put("agentname",this.agentname());
        map.put("userua",this.userua());
        map.put("mac_address",this.mac_address());
        map.put("appid",this.appId());
        map.put("verid",this.verid());
        map.put("params","{}");
        String token = BaseSharePerence.getInstance().getLoginKey();
        if(!"".equals(token)){
            map.put("token",token);
        }
        return map;
    }
    public static Map<String,String> addSign(Map<String,String> map){
        Set keys= map.keySet();
        List<String> list = new ArrayList<String>(keys);
        Collections.sort(list);
        String sign="";
        for (String key : list){
            sign += key + "=" + map.get(key) + "&";
        }
        sign+="key="+appkey();
        map.put("sign",MD5Utils.generate(sign));
        return map;
    }

    public static String addSign(String paramsMap){
        Map<String,String> publicMap= new PhoneInfo(context).appPhoneInfo();
        if(paramsMap!=null){
            publicMap.put("params",paramsMap);
        }
        Set keys= publicMap.keySet();
        List<String> list = new ArrayList<String>(keys);
        Collections.sort(list);
        String sign="";
        for (String key : list){
            sign+=key+"="+publicMap.get(key)+"&";
        }
        sign+="key="+appkey();
        String md5 = MD5Utils.generate(sign);
        publicMap.put("sign",md5.toUpperCase());
        return publicMap.get("sign");
    }
}
