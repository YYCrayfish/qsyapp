package com.manyu.videoshare.bean;

import com.google.gson.Gson;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;

public class OrderBean {

    /**
     * code : 200
     * msg : 下单成功
     * data : {"order_id":"QSY15566080302828803250001","amount":"6.00","product_name":"1天","product_desc":"1天|6.00元","pay_data":{"payway":"wxpay","paytype":"wap","order_id":"QSY15566080302828803250001","amount":6,"token":"http://local.api.videowater.com/index.php/Pay/nowpay/gotoweixin.html?order_id=QSY15566080302828803250001×tamp=1556608031&now_token=weixin%253A%252F%252Fwap%252Fpay%253Fprepayid%25253Dwx30150947145596ed9e5805e52198794894%2526package%253D3091505904%2526noncestr%253D1556608187%2526sign%253Dcae4bdeeabe67bb45473bebfd3023a69&return_url=http%253A%252F%252Flocal.api.videowater.com%252Findex.php%252FPay%252Fnowpay%252Freturnurl%252Forder_id%252FQSY15566080302828803250001.html","status":1}}
     * timestamp : 1556608031
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
        String text = AuthCode.authcodeDecode(getData(),Constants.s);
        this.datas = new Gson().fromJson(text,DataBean.class);
        return datas;
    }

    public static class DataBean {
        /**
         * order_id : QSY15566080302828803250001
         * amount : 6.00
         * product_name : 1天
         * product_desc : 1天|6.00元
         * pay_data : {"payway":"wxpay","paytype":"wap","order_id":"QSY15566080302828803250001","amount":6,"token":"http://local.api.videowater.com/index.php/Pay/nowpay/gotoweixin.html?order_id=QSY15566080302828803250001×tamp=1556608031&now_token=weixin%253A%252F%252Fwap%252Fpay%253Fprepayid%25253Dwx30150947145596ed9e5805e52198794894%2526package%253D3091505904%2526noncestr%253D1556608187%2526sign%253Dcae4bdeeabe67bb45473bebfd3023a69&return_url=http%253A%252F%252Flocal.api.videowater.com%252Findex.php%252FPay%252Fnowpay%252Freturnurl%252Forder_id%252FQSY15566080302828803250001.html","status":1}
         */

        private String order_id;
        private String amount;
        private String product_name;
        private String product_desc;
        private PayDataBean pay_data;

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getProduct_desc() {
            return product_desc;
        }

        public void setProduct_desc(String product_desc) {
            this.product_desc = product_desc;
        }

        public PayDataBean getPay_data() {
            return pay_data;
        }

        public void setPay_data(PayDataBean pay_data) {
            this.pay_data = pay_data;
        }

        public static class PayDataBean {
            /**
             * payway : wxpay
             * paytype : wap
             * order_id : QSY15566080302828803250001
             * amount : 6
             * token : http://local.api.videowater.com/index.php/Pay/nowpay/gotoweixin.html?order_id=QSY15566080302828803250001×tamp=1556608031&now_token=weixin%253A%252F%252Fwap%252Fpay%253Fprepayid%25253Dwx30150947145596ed9e5805e52198794894%2526package%253D3091505904%2526noncestr%253D1556608187%2526sign%253Dcae4bdeeabe67bb45473bebfd3023a69&return_url=http%253A%252F%252Flocal.api.videowater.com%252Findex.php%252FPay%252Fnowpay%252Freturnurl%252Forder_id%252FQSY15566080302828803250001.html
             * status : 1
             */

            private String payway;
            private String paytype;
            private String order_id;
            private float amount;
            private String token;
            private int status;

            public String getPayway() {
                return payway;
            }

            public void setPayway(String payway) {
                this.payway = payway;
            }

            public String getPaytype() {
                return paytype;
            }

            public void setPaytype(String paytype) {
                this.paytype = paytype;
            }

            public String getOrder_id() {
                return order_id;
            }

            public void setOrder_id(String order_id) {
                this.order_id = order_id;
            }

            public float getAmount() {
                return amount;
            }

            public void setAmount(float amount) {
                this.amount = amount;
            }

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
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
