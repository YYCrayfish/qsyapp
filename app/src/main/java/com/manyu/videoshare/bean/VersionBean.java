package com.manyu.videoshare.bean;

import com.google.gson.Gson;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;

public class VersionBean {

    /**
     * code : 200
     * msg : 请求成功
     * data : {"verid":1,"versions":"V1.0.1","url":"http://d1.apk8.com/app/yzy_2.apk","size":"10240","content":"1、bug修复","type":1,"banagent":"1"}
     * timestamp : 1548322101
     */

    private int code;
    private String msg;
    private String data;
    private int timestamp;

    public DataBean getDatas() {
        return new Gson().fromJson(AuthCode.authcodeDecode(getData(),Constants.s),DataBean.class);
    }

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

    public static class DataBean {
        /**
         * verid : 1
         * versions : V1.0.1
         * url : http://d1.apk8.com/app/yzy_2.apk
         * size : 10240
         * content : 1、bug修复
         * type : 1
         * banagent : 1
         */

        private int verid;
        private String versions;
        private String url;
        private String size;
        private String content;
        private int type;
        private String banagent;

        public int getVerid() {
            return verid;
        }

        public void setVerid(int verid) {
            this.verid = verid;
        }

        public String getVersions() {
            return versions;
        }

        public void setVersions(String versions) {
            this.versions = versions;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getBanagent() {
            return banagent;
        }

        public void setBanagent(String banagent) {
            this.banagent = banagent;
        }
    }
}
