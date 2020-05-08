package com.manyu.videoshare.util;

public class Constants {
    public static boolean LOG_Controll = true;
    public static final int WAITINGTIME = 60;
    public static final String BACKPATH = "https://apiq.meituapp.com/index.php";
    public static String URL;
    static{
        if(LOG_Controll){
            URL = "api.dspqsy.com";
            PATH = "https://api.dspqsy.com/index.php";
        }else{
            URL = "apiw.3ssjx.com:85";
            PATH = "http://apiw.3ssjx.com:85/index.php";
        }
    }
    /**
     *
     https://api.dspqsy.com/index.php/v1/system/appinit
     备用
     https://apiq.meituapp.com/index.php/v1/system/appinit
     */
    public static String s = "s#KJomu3Nd1Fjc9sS4Y5Jh$rkA^%OpL75";
//    //正式
//    public static String PATH;// = "https://api.dspqsy.com/index.php";
//    测试
    public static String PATH = "http://qsyapi.bizhiwangapp.com/index.php";//"http://apiw2.3ssjx.com:85/index.php";//"http://apiw.3ssjx.com:85/index.php";

    //注册
    public static String REGISTER = PATH + "/v1/register/doRegister";
    //登录
    public static String LOGIN = PATH + "/v1/login/doLogin";
    //剩余解析次数
    public static String ANALYTIC = PATH + "/v1/dewater/getParseTimes";
    //本地去水印成功上报
    public static String SUCCEED_REMOVE_WATER_MARK = PATH + "/v1/dewater/minusParseTimes";
    //APP初始化信息
    public static String INITAPP = PATH + "/v1/system/appinit";
    //获取验证码
    public static String GETVERIFY = PATH + "/v1/sms/send";
    //解析
    public static String ANALYSIS = PATH + "/v1/dewater/parseContent";
    //主页答与问
    public static String FAQ = PATH + "/v1/faq/getFaqs";
    //主页支持APP
    public static String SUPPORT = PATH +"/v1/dewater/supportApps";
    //重置密码
    public static String FORGET = PATH + "/v1/login/resetPassword";
    //获取版本
    public static String VERSION = PATH + "/v1/update/get";
    //修改密码
    public static String CHANGEPS = PATH + "/v1/member/editPsw";
    //1注册 2找回 3换绑 4验证旧手机号  验证验证码
    public static String CHECKVERIFY = PATH + "/v1/sms/checkValidate";
    //用户信息
    public static String USERMESSAGE = PATH+ "/v1/member/info";
    //修改手机号码
    public static String CHANGEPHONE =PATH +"/v1/member/changeMobile";
    //意见反馈
    public static String FEEDBACK = PATH + "/v1/member/feedback";
    //VIP收费类型
    public static String VIPTYPE = PATH + "/v1/vip/setting";
    //支付方式
    public static String PAYTYPE = PATH + "/v1/pay/payway";
    //上传头像
    public static String UPDATEHEAD = PATH + "/v1/member/uploadAvatar";
    //修改个人资料
    public static String UPDATEMESSAGE = PATH + "/v1/member/edit";
    //下单
    public static String GETORDER = PATH + "/v1/vip/order";
    //订单查询
    public static String ORDERQUERY = PATH + "/v1/order/query";
    //邀请信息
    public static String INVITE = PATH + "/v1/member/invite";
    //实名认证提交审核
    public static String AUTONYM = PATH + "/v1/member/checkIdcard";
    //获取实名信息
    public static String GETIDCARD = PATH + "/v1/member/getIdcard";
}
