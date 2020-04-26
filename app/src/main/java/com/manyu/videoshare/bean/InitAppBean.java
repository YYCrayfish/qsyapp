package com.manyu.videoshare.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;

import java.util.List;

public class InitAppBean {


    /**
     * code : 200
     * msg : 获取初始化成功
     * data : {"ip":"127.0.0.1","timestamp":1554876863,"start_page":{"id":6,"slide_id":1,"status":1,"type":1,"list_order":10000,"title":"3733游戏","image":"http://b.bjyzbx.com/admin/20190411/60b386df6c58524c7b4bebef44f9a2a1.jpg","url":"http://lianai.lhaihai.com/","target":"","description":"3733游戏","content":"3733游戏","more":null,"time":5,"show_num":218652,"click_num":14005,"package":"com.pieceapp.chatskills","bind_agent":"vivo,xiaomi","allow_agent":""},"copy_app":[{"id":1,"name":"QQ","thumb":"http://v.bjyzbx.com/upload/ico/qq.png","action":"com.tencent.mobileqq"},{"id":2,"name":"微信","thumb":"http://v.bjyzbx.com/upload/ico/wx.png","action":"com.tencent.mm"}],"kefu":{"qq_url":"","qq_number":"","qq_qun":"930669653"},"privacy":{"id":1,"update_time":1553845881,"content":"隐私政策内容","title":"隐私政策"},"agreement":{"id":2,"update_time":1553845846,"content":"协议内容","title":"用户协议"},"ad_step":5,"ios_check":false}
     * timestamp : 1554876863
     */

    private int code;
    private String msg;
    private String data;
    private int timestamp;
    private DataBean datas;
    public DataBean getDatas() {
        if(null == datas){
            this.datas = new Gson().fromJson(AuthCode.authcodeDecode(getData(),Constants.s),DataBean.class);
        }
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
         * ip : 127.0.0.1
         * timestamp : 1554876863
         * start_page : {"id":6,"slide_id":1,"status":1,"type":1,"list_order":10000,"title":"3733游戏","image":"http://b.bjyzbx.com/admin/20190411/60b386df6c58524c7b4bebef44f9a2a1.jpg","url":"http://lianai.lhaihai.com/","target":"","description":"3733游戏","content":"3733游戏","more":null,"time":5,"show_num":218652,"click_num":14005,"package":"com.pieceapp.chatskills","bind_agent":"vivo,xiaomi","allow_agent":""}
         * copy_app : [{"id":1,"name":"QQ","thumb":"http://v.bjyzbx.com/upload/ico/qq.png","action":"com.tencent.mobileqq"},{"id":2,"name":"微信","thumb":"http://v.bjyzbx.com/upload/ico/wx.png","action":"com.tencent.mm"}]
         * kefu : {"qq_url":"","qq_number":"","qq_qun":"930669653"}
         * privacy : {"id":1,"update_time":1553845881,"content":"隐私政策内容","title":"隐私政策"}
         * agreement : {"id":2,"update_time":1553845846,"content":"协议内容","title":"用户协议"}
         * ad_step : 5
         * ios_check : false
         */

        private String ip;
        private int timestamp;
        private StartPageBean start_page;
        private KefuBean kefu;
        private PrivacyBean privacy;
        private AgreementBean agreement;
        private int ad_step;
        private boolean ios_check;
        private List<CopyAppBean> copy_app;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public StartPageBean getStart_page() {
            return start_page;
        }

        public void setStart_page(StartPageBean start_page) {
            this.start_page = start_page;
        }

        public KefuBean getKefu() {
            return kefu;
        }

        public void setKefu(KefuBean kefu) {
            this.kefu = kefu;
        }

        public PrivacyBean getPrivacy() {
            return privacy;
        }

        public void setPrivacy(PrivacyBean privacy) {
            this.privacy = privacy;
        }

        public AgreementBean getAgreement() {
            return agreement;
        }

        public void setAgreement(AgreementBean agreement) {
            this.agreement = agreement;
        }

        public int getAd_step() {
            return ad_step;
        }

        public void setAd_step(int ad_step) {
            this.ad_step = ad_step;
        }

        public boolean isIos_check() {
            return ios_check;
        }

        public void setIos_check(boolean ios_check) {
            this.ios_check = ios_check;
        }

        public List<CopyAppBean> getCopy_app() {
            return copy_app;
        }

        public void setCopy_app(List<CopyAppBean> copy_app) {
            this.copy_app = copy_app;
        }

        public static class StartPageBean {
            /**
             * id : 6
             * slide_id : 1
             * status : 1
             * type : 1
             * list_order : 10000
             * title : 3733游戏
             * image : http://b.bjyzbx.com/admin/20190411/60b386df6c58524c7b4bebef44f9a2a1.jpg
             * url : http://lianai.lhaihai.com/
             * target :
             * description : 3733游戏
             * content : 3733游戏
             * more : null
             * time : 5
             * show_num : 218652
             * click_num : 14005
             * package : com.pieceapp.chatskills
             * bind_agent : vivo,xiaomi
             * allow_agent :
             */

            private int id;
            private int slide_id;
            private int status;
            private int type;
            private int list_order;
            private String title;
            private String image;
            private String url;
            private String target;
            private String description;
            private String content;
            private Object more;
            private int time;
            private int show_num;
            private int click_num;
            @SerializedName("package")
            private String packageX;
            private String bind_agent;
            private String allow_agent;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getSlide_id() {
                return slide_id;
            }

            public void setSlide_id(int slide_id) {
                this.slide_id = slide_id;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getList_order() {
                return list_order;
            }

            public void setList_order(int list_order) {
                this.list_order = list_order;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getTarget() {
                return target;
            }

            public void setTarget(String target) {
                this.target = target;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public Object getMore() {
                return more;
            }

            public void setMore(Object more) {
                this.more = more;
            }

            public int getTime() {
                return time;
            }

            public void setTime(int time) {
                this.time = time;
            }

            public int getShow_num() {
                return show_num;
            }

            public void setShow_num(int show_num) {
                this.show_num = show_num;
            }

            public int getClick_num() {
                return click_num;
            }

            public void setClick_num(int click_num) {
                this.click_num = click_num;
            }

            public String getPackageX() {
                return packageX;
            }

            public void setPackageX(String packageX) {
                this.packageX = packageX;
            }

            public String getBind_agent() {
                return bind_agent;
            }

            public void setBind_agent(String bind_agent) {
                this.bind_agent = bind_agent;
            }

            public String getAllow_agent() {
                return allow_agent;
            }

            public void setAllow_agent(String allow_agent) {
                this.allow_agent = allow_agent;
            }
        }

        public static class KefuBean {
            /**
             * qq_url :
             * qq_number :
             * qq_qun : 930669653
             */

            private String qq_url;
            private String qq_number;
            private String qq_qun;

            public String getQq_url() {
                return qq_url;
            }

            public void setQq_url(String qq_url) {
                this.qq_url = qq_url;
            }

            public String getQq_number() {
                return qq_number;
            }

            public void setQq_number(String qq_number) {
                this.qq_number = qq_number;
            }

            public String getQq_qun() {
                return qq_qun;
            }

            public void setQq_qun(String qq_qun) {
                this.qq_qun = qq_qun;
            }
        }

        public static class PrivacyBean {
            /**
             * id : 1
             * update_time : 1553845881
             * content : 隐私政策内容
             * title : 隐私政策
             */

            private int id;
            private int update_time;
            private String content;
            private String title;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getUpdate_time() {
                return update_time;
            }

            public void setUpdate_time(int update_time) {
                this.update_time = update_time;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }

        public static class AgreementBean {
            /**
             * id : 2
             * update_time : 1553845846
             * content : 协议内容
             * title : 用户协议
             */

            private int id;
            private int update_time;
            private String content;
            private String title;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getUpdate_time() {
                return update_time;
            }

            public void setUpdate_time(int update_time) {
                this.update_time = update_time;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }

        public static class CopyAppBean {
            /**
             * id : 1
             * name : QQ
             * thumb : http://v.bjyzbx.com/upload/ico/qq.png
             * action : com.tencent.mobileqq
             */

            private int id;
            private String name;
            private String thumb;
            private String action;

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

            public String getThumb() {
                return thumb;
            }

            public void setThumb(String thumb) {
                this.thumb = thumb;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }
        }
    }
}
