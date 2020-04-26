package com.manyu.videoshare.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;

import java.util.List;

public class AskandAnswerBean {

    /**
     * code : 200
     * msg : 请求成功
     * data : [{"id":3,"q":"<p&Q：提取视频下载失败？<\/p&","answer":"<p&A：请确认是否为图集，图集可以直接在浏览器中打开链接并长按保存。<\/p&"},{"id":2,"q":"<p&Q：提示视频解析失败？<\/p&","answer":"<p&A：请尝试在浏览器中打开视频链接，若浏览器无法打开则无法解析。<\/p&"},{"id":1,"q":"<p&Q：提取的视频还有水印？ <\/p&","answer":"<p&A：能解析99%以上视频，但若原作者上传的视频带有水印则无法去除。原视频是否有水印请查看对应app播放时是否有水印<\/p&"}]
     * timestamp : 1556075980
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
        String value = AuthCode.authcodeDecode(getData(),Constants.s);
        this.datas = new Gson().fromJson(value,new TypeToken<List<DataBean>>(){}.getType());
        return datas;
    }

    public static class DataBean {
        /**
         * id : 3
         * q : <p&Q：提取视频下载失败？</p&
         * answer : <p&A：请确认是否为图集，图集可以直接在浏览器中打开链接并长按保存。</p&
         */

        private int id;
        private String q;
        private String answer;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getQ() {
            return q;
        }

        public void setQ(String q) {
            this.q = q;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

    }
}
