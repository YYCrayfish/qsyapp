package com.manyu.videoshare.bean;

public class RegisterBean {

    /**
     * code : 200
     * msg : 登录成功
     * data : {"token":"62cb747e17755fa40cd583d12ee19a3962cb747e17755fa40cd583d12ee19a39"}
     * timestamp : 1548148245
     */

    private int code;
    private String msg;
    private DataBean data;
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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public static class DataBean {
        /**
         * token : 62cb747e17755fa40cd583d12ee19a3962cb747e17755fa40cd583d12ee19a39
         */

        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
