package com.manyu.videoshare.bean;

import com.google.gson.Gson;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;

import java.util.List;

public class RecharegeTypeBean {


    /**
     * code : 200
     * msg : 请求成功
     * data : {"list":[{"id":12,"name":"1天","price_title":"特惠价￥6","price_desc":"6","amount":"6.00","type":1,"score":0,"status":1},{"id":1,"name":"30天","price_title":"特惠价￥29","price_desc":"￥29/月","amount":"29.00","type":1,"score":0,"status":1}],"desc":"开通VIP会员，享用所有VIP会员特权服务"}
     * timestamp : 1556179368
     */

    private int code;
    private String msg;
    private String data;
    private int timestamp;
    private DataBean datas; {
        this.timestamp = timestamp;
    }

    public DataBean getDatas() {
        this.datas = new Gson().fromJson(AuthCode.authcodeDecode(getData(),Constants.s),DataBean.class);
        return datas;
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
         * list : [{"id":12,"name":"1天","price_title":"特惠价￥6","price_desc":"6","amount":"6.00","type":1,"score":0,"status":1},{"id":1,"name":"30天","price_title":"特惠价￥29","price_desc":"￥29/月","amount":"29.00","type":1,"score":0,"status":1}]
         * desc : 开通VIP会员，享用所有VIP会员特权服务
         */

        private String desc;
        private List<ListBean> list;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * id : 12
             * name : 1天
             * price_title : 特惠价￥6
             * price_desc : 6
             * amount : 6.00
             * type : 1
             * score : 0
             * status : 1
             */

            private int id;
            private String name;
            private String price_title;
            private String price_desc;
            private String amount;
            private int type;
            private int score;
            private int status;

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

            public String getPrice_title() {
                return price_title;
            }

            public void setPrice_title(String price_title) {
                this.price_title = price_title;
            }

            public String getPrice_desc() {
                return price_desc;
            }

            public void setPrice_desc(String price_desc) {
                this.price_desc = price_desc;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }
    }
}
