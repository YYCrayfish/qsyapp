package com.manyu.videoshare.bean;

import com.google.gson.Gson;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;

import java.io.Serializable;

public class Idcardbean{


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

    public static class DataBean implements Serializable {
        private String id;
        private int is_authentication;
        private String name;
        private String images;
        private String id_card;
        private String msg;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getIs_authentication() {
            return is_authentication;
        }

        public void setIs_authentication(int is_authentication) {
            this.is_authentication = is_authentication;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImages() {
            return images;
        }

        public void setImages(String images) {
            this.images = images;
        }

        public String getId_card() {
            return id_card;
        }

        public void setId_card(String id_card) {
            this.id_card = id_card;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
