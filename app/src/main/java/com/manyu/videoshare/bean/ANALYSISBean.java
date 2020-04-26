package com.manyu.videoshare.bean;

import com.google.gson.Gson;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;

public class ANALYSISBean {

    /**
     * code : 200
     * msg : 请求成功
     * data : {"poster":"https://p9.pstatp.com/large/1e7f40005c7ac9549b6ab.jpg","video_url":"http://v6-dy.ixigua.com/cc328ab61ddf2644de14d4bfdb8541f5/5cbfe2f2/video/m/220fbcf00e76a7f40c1bc925d9a4cb1dbee1161d492b0000b361bb921f4f/?rc=anJmN3I1OXBrbDMzNGkzM0ApQHRAb0c5NTczNjU0NDQ4NDc3PDNAKXUpQGczdylAZmxkamV6aGhkZjs0QGcyMWduZ29nbl8tLTYtL3NzLW8jbyMyMzI1NC0yLS0wLi4vLS4vaTpiLW8jOmAtbyNtbCtiK2p0OiMvLl4%3D#http://dy.fxw.la/"}
     * timestamp : 1556075692
     */

    private int code;
    private String msg;
    private String data;
    private DataBean datas;
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
        return new Gson().fromJson(AuthCode.authcodeDecode(getData(),Constants.s),DataBean.class);
    }

    public static class DataBean {
        /**
         * poster : https://p9.pstatp.com/large/1e7f40005c7ac9549b6ab.jpg
         * video_url : http://v6-dy.ixigua.com/cc328ab61ddf2644de14d4bfdb8541f5/5cbfe2f2/video/m/220fbcf00e76a7f40c1bc925d9a4cb1dbee1161d492b0000b361bb921f4f/?rc=anJmN3I1OXBrbDMzNGkzM0ApQHRAb0c5NTczNjU0NDQ4NDc3PDNAKXUpQGczdylAZmxkamV6aGhkZjs0QGcyMWduZ29nbl8tLTYtL3NzLW8jbyMyMzI1NC0yLS0wLi4vLS4vaTpiLW8jOmAtbyNtbCtiK2p0OiMvLl4%3D#http://dy.fxw.la/
         */

        private String poster;
        private String video_url;

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        public String getVideo_url() {
            return video_url;
        }

        public void setVideo_url(String video_url) {
            this.video_url = video_url;
        }
    }
}
