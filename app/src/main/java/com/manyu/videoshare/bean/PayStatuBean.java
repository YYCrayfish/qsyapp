package com.manyu.videoshare.bean;

import com.google.gson.Gson;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;

public class PayStatuBean {

    /**
     * code : 200
     * msg : 查询成功
     * data : {"order_id":"P15543687667494200090001","mem_id":9,"status":1,##充值成功标志，1为待处理，2为成功，3为失败 "amount":3}
     * timestamp : 1554368843
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
        this.datas = new Gson().fromJson(AuthCode.authcodeDecode(getData(),Constants.s),DataBean.class);
        return datas;
    }

    public static class DataBean {
        /**
         * order_id : P15543687667494200090001
         * mem_id : 9
         * status : 1
         * amount : 3
         */

        private String order_id;
        private int mem_id;
        private int status;
        private double amount;

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public int getMem_id() {
            return mem_id;
        }

        public void setMem_id(int mem_id) {
            this.mem_id = mem_id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }
}
