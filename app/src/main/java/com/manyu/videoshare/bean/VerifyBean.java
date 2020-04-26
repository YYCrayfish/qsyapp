package com.manyu.videoshare.bean;

public class VerifyBean {

    /**
     * code : 200
     * msg : 请求成功
     * data : null
     * timestamp : 1548732655
     */

    private int code;
    private String msg;
    private Object data;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
