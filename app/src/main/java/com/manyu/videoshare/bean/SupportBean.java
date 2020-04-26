package com.manyu.videoshare.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;

import java.util.List;

public class SupportBean {

    /**
     * code : 200
     * msg : 请求成功
     * data : [{"id":1,"name":"微信","thumbnail":"http://b.bjyzbx.com/admin/20190423/3544b3fb83738789df0ec3bd7153198e.png"}]
     * timestamp : 1556076050
     */

    private int code;
    private String msg;
    private int timestamp;
    private List<DataBean> datas;
    private String data;
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
        datas = new Gson().fromJson(AuthCode.authcodeDecode(getData(),Constants.s),new TypeToken<List<DataBean>>(){}.getType());
        return datas;
    }

    public static class DataBean {
        /**
         * id : 1
         * name : 微信
         * thumbnail : http://b.bjyzbx.com/admin/20190423/3544b3fb83738789df0ec3bd7153198e.png
         */

        private int id;
        private String name;
        private String thumbnail;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
    }
}
