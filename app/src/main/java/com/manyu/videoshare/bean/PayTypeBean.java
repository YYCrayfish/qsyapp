package com.manyu.videoshare.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;

import java.util.List;

public class PayTypeBean {

    /**
     * code : 200
     * msg : 请求成功
     * data : [{"id":1,"payname":"wxpay","disc":"微信","realname":"微信支付","bgcolor":"#32A74D","status":1,"image":"http://v.bjyzbx.com/upload/ico/wxpay.png","image2":"http://v.bjyzbx.com/upload/avatar/20190226/5c74e6e2927c0.png"},{"id":2,"payname":"alipay","disc":"支付宝","realname":"支付宝支付","bgcolor":"#2FAEFF","status":1,"image":"http://v.bjyzbx.com/upload/ico/alipay.png","image2":"http://v.bjyzbx.com/upload/avatar/20190226/5c74e727bdf10.png"}]
     * timestamp : 1556180870
     */

    private int code;
    private String msg;
    private int timestamp;
    private String data;
    private List<DataBean> datas;

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

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<DataBean> getDatas() {
        this.datas = new Gson().fromJson(AuthCode.authcodeDecode(getData(),Constants.s),new TypeToken<List<DataBean>>(){}.getType());
        return datas;
    }

    public static class DataBean {
        /**
         * id : 1
         * payname : wxpay
         * disc : 微信
         * realname : 微信支付
         * bgcolor : #32A74D
         * status : 1
         * image : http://v.bjyzbx.com/upload/ico/wxpay.png
         * image2 : http://v.bjyzbx.com/upload/avatar/20190226/5c74e6e2927c0.png
         */

        private int id;
        private String payname;
        private String disc;
        private String realname;
        private String bgcolor;
        private int status;
        private String image;
        private String image2;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPayname() {
            return payname;
        }

        public void setPayname(String payname) {
            this.payname = payname;
        }

        public String getDisc() {
            return disc;
        }

        public void setDisc(String disc) {
            this.disc = disc;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public String getBgcolor() {
            return bgcolor;
        }

        public void setBgcolor(String bgcolor) {
            this.bgcolor = bgcolor;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getImage2() {
            return image2;
        }

        public void setImage2(String image2) {
            this.image2 = image2;
        }
    }
}
