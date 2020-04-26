package com.manyu.videoshare.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;

public class UserBean {

    /**
     * code : 200
     * msg : 请求成功
     * data : {"id":322,"nickname":"18750263810","member_login":"18750263810","sex":0,"birthday":"0000-00-00","last_login_time":1556075651,"balance":"0.00","create_time":1556075651,"update_time":0,"member_status":1,"uuid":"","agentname":"1","avatar":"","last_login_ip":"127.0.0.1","reg_ip":"127.0.0.1","mobile":"18750263810","avatar_original":"","last_doing_time":1556075651,"avatar_check":0,"invite_count":0,"package":"1","verid":"oppe","is_official":"","signature":"","score":10,"score_time":0,"sign_days":0,"vip_end_time":null,"invite_code":"00YJ"}
     * timestamp : 1556246454
     */

    private int code;
    private String msg;
    private DataBean datas;
    private String data;
    private int timestamp;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public DataBean getDatas() {
        this.datas = new Gson().fromJson(AuthCode.authcodeDecode(getData(),Constants.s),DataBean.class);
        return datas;
    }

    public static class DataBean {
        /**
         * id : 322
         * nickname : 18750263810
         * member_login : 18750263810
         * sex : 0
         * birthday : 0000-00-00
         * last_login_time : 1556075651
         * balance : 0.00
         * create_time : 1556075651
         * update_time : 0
         * member_status : 1
         * uuid :
         * agentname : 1
         * avatar :
         * last_login_ip : 127.0.0.1
         * reg_ip : 127.0.0.1
         * mobile : 18750263810
         * avatar_original :
         * last_doing_time : 1556075651
         * avatar_check : 0
         * invite_count : 0
         * package : 1
         * verid : oppe
         * is_official :
         * signature :
         "score": 10, //当前积分（剩余解析次数）
         "score_time": 0, //最后签到时间
         "sign_days": 0, //连续签到天数
         "vip_end_time": null, //null未开通过vip， vip结束时间戳
         "invite_code": "00YJ"  //邀请码
         */

        private int id;
        private String nickname;
        private String member_login;
        private int sex;
        private String birthday;
        private int last_login_time;
        private String balance;
        private int create_time;
        private int update_time;
        private int member_status;
        private String uuid;
        private String agentname;
        private String avatar;
        private String last_login_ip;
        private String reg_ip;
        private String mobile;
        private String avatar_original;
        private int last_doing_time;
        private int avatar_check;
        private int invite_count;
        @SerializedName("package")
        private String packageX;
        private String verid;
        private String is_official;
        private String signature;
        private int score;
        private int parse_times;
        private int score_time;
        private int sign_days;
        private String vip_end_time;
        private String invite_code;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public int getParse_times() {
            return parse_times;
        }

        public void setParse_times(int parse_times) {
            this.parse_times = parse_times;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getMember_login() {
            return member_login;
        }

        public void setMember_login(String member_login) {
            this.member_login = member_login;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public int getLast_login_time() {
            return last_login_time;
        }

        public void setLast_login_time(int last_login_time) {
            this.last_login_time = last_login_time;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }

        public int getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(int update_time) {
            this.update_time = update_time;
        }

        public int getMember_status() {
            return member_status;
        }

        public void setMember_status(int member_status) {
            this.member_status = member_status;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getAgentname() {
            return agentname;
        }

        public void setAgentname(String agentname) {
            this.agentname = agentname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getLast_login_ip() {
            return last_login_ip;
        }

        public void setLast_login_ip(String last_login_ip) {
            this.last_login_ip = last_login_ip;
        }

        public String getReg_ip() {
            return reg_ip;
        }

        public void setReg_ip(String reg_ip) {
            this.reg_ip = reg_ip;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAvatar_original() {
            return avatar_original;
        }

        public void setAvatar_original(String avatar_original) {
            this.avatar_original = avatar_original;
        }

        public int getLast_doing_time() {
            return last_doing_time;
        }

        public void setLast_doing_time(int last_doing_time) {
            this.last_doing_time = last_doing_time;
        }

        public int getAvatar_check() {
            return avatar_check;
        }

        public void setAvatar_check(int avatar_check) {
            this.avatar_check = avatar_check;
        }

        public int getInvite_count() {
            return invite_count;
        }

        public void setInvite_count(int invite_count) {
            this.invite_count = invite_count;
        }

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getVerid() {
            return verid;
        }

        public void setVerid(String verid) {
            this.verid = verid;
        }

        public String getIs_official() {
            return is_official;
        }

        public void setIs_official(String is_official) {
            this.is_official = is_official;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getScore_time() {
            return score_time;
        }

        public void setScore_time(int score_time) {
            this.score_time = score_time;
        }

        public int getSign_days() {
            return sign_days;
        }

        public void setSign_days(int sign_days) {
            this.sign_days = sign_days;
        }

        public String getVip_end_time() {
            return vip_end_time;
        }

        public void setVip_end_time(String vip_end_time) {
            this.vip_end_time = vip_end_time;
        }

        public String getInvite_code() {
            return invite_code;
        }

        public void setInvite_code(String invite_code) {
            this.invite_code = invite_code;
        }
    }
}
