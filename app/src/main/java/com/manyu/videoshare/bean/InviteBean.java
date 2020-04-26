package com.manyu.videoshare.bean;

import com.google.gson.Gson;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;

import java.util.List;

public class InviteBean {

    /**
     * code : 200
     * msg : 请求成功
     * data : {"invite_url":"https://h5.gaoeng.com/v1/invite/videowater?invite_data=ZWRlMjFEbXpkbUozR3BHQ0ppbmkyVmhDMXBnUUYvY3V0bVBKTHpRcE5ocGZsa2tSblhLak9wQQ==","invite_code":"00YZ","invite_level":{"invite_count":0,"next_level":1,"next_msg":"再推广1人，获得[LV1]称号","next_image":"http://v.bjyzbx.com/vipconfig/level-1.png"},"new_level":{"level":"http://v.bjyzbx.com/vipconfig/level-0.png","centage":0},"share_level":[{"name":"LV1","image":"http://v.bjyzbx.com/vipconfig/level-1.png","invite":1,"score":100,"search":2,"rule":"邀请1人后，每日免费搜索次数 +2"},{"name":"LV2","image":"http://v.bjyzbx.com/vipconfig/level-2.png","invite":3,"score":120,"search":5,"rule":"邀请3人后，每日免费搜索次数 +5"},{"name":"LV3","image":"http://v.bjyzbx.com/vipconfig/level-3.png","invite":10,"score":150,"search":30,"rule":"邀请10人后，每日免费搜索次数 +30"},{"name":"LV4","image":"http://v.bjyzbx.com/vipconfig/level-4.png","invite":15,"score":200,"search":50,"rule":"邀请15人后，每日免费搜索次数 +50"},{"name":"LV5","image":"http://v.bjyzbx.com/vipconfig/level-5.png","invite":30,"score":300,"search":999,"rule":"邀请50人后，每日免费搜索次数无限"}],"msg":"规则说明：每邀请给1名好友成功下载App并注册时输入您的邀请码，可增加100积分。"}
     * timestamp : 1557307167
     */

    private int code;
    private String msg;
    private String data;
    private int timestamp;
    private DataBean datas;
    public int getCode() {
        return code;
    }

    public DataBean getDatas() {
        this.datas = new Gson().fromJson(AuthCode.authcodeDecode(getData(),Constants.s),DataBean.class);
        return datas;
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
         * invite_url : https://h5.gaoeng.com/v1/invite/videowater?invite_data=ZWRlMjFEbXpkbUozR3BHQ0ppbmkyVmhDMXBnUUYvY3V0bVBKTHpRcE5ocGZsa2tSblhLak9wQQ==
         * invite_code : 00YZ
         * invite_level : {"invite_count":0,"next_level":1,"next_msg":"再推广1人，获得[LV1]称号","next_image":"http://v.bjyzbx.com/vipconfig/level-1.png"}
         * new_level : {"level":"http://v.bjyzbx.com/vipconfig/level-0.png","centage":0}
         * share_level : [{"name":"LV1","image":"http://v.bjyzbx.com/vipconfig/level-1.png","invite":1,"score":100,"search":2,"rule":"邀请1人后，每日免费搜索次数 +2"},{"name":"LV2","image":"http://v.bjyzbx.com/vipconfig/level-2.png","invite":3,"score":120,"search":5,"rule":"邀请3人后，每日免费搜索次数 +5"},{"name":"LV3","image":"http://v.bjyzbx.com/vipconfig/level-3.png","invite":10,"score":150,"search":30,"rule":"邀请10人后，每日免费搜索次数 +30"},{"name":"LV4","image":"http://v.bjyzbx.com/vipconfig/level-4.png","invite":15,"score":200,"search":50,"rule":"邀请15人后，每日免费搜索次数 +50"},{"name":"LV5","image":"http://v.bjyzbx.com/vipconfig/level-5.png","invite":30,"score":300,"search":999,"rule":"邀请50人后，每日免费搜索次数无限"}]
         * msg : 规则说明：每邀请给1名好友成功下载App并注册时输入您的邀请码，可增加100积分。
         */

        private String invite_url;
        private String invite_code;
        private InviteLevelBean invite_level;
        private NewLevelBean new_level;
        private String msg;
        private List<ShareLevelBean> share_level;

        public String getInvite_url() {
            return invite_url;
        }

        public void setInvite_url(String invite_url) {
            this.invite_url = invite_url;
        }

        public String getInvite_code() {
            return invite_code;
        }

        public void setInvite_code(String invite_code) {
            this.invite_code = invite_code;
        }

        public InviteLevelBean getInvite_level() {
            return invite_level;
        }

        public void setInvite_level(InviteLevelBean invite_level) {
            this.invite_level = invite_level;
        }

        public NewLevelBean getNew_level() {
            return new_level;
        }

        public void setNew_level(NewLevelBean new_level) {
            this.new_level = new_level;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<ShareLevelBean> getShare_level() {
            return share_level;
        }

        public void setShare_level(List<ShareLevelBean> share_level) {
            this.share_level = share_level;
        }

        public static class InviteLevelBean {
            /**
             * invite_count : 0
             * next_level : 1
             * next_msg : 再推广1人，获得[LV1]称号
             * next_image : http://v.bjyzbx.com/vipconfig/level-1.png
             */

            private int invite_count;
            private int next_level;
            private String next_msg;
            private int parse_times;
            private int parse_times_total;
            private String next_image;
            private String current_level;

            public int getParse_times() {
                return parse_times;
            }

            public String getCurrent_level() {
                return current_level;
            }

            public void setCurrent_level(String current_level) {
                this.current_level = current_level;
            }

            public void setParse_times(int parse_times) {
                this.parse_times = parse_times;
            }

            public int getParse_times_total() {
                return parse_times_total;
            }

            public void setParse_times_total(int parse_times_total) {
                this.parse_times_total = parse_times_total;
            }

            public int getInvite_count() {
                return invite_count;
            }

            public void setInvite_count(int invite_count) {
                this.invite_count = invite_count;
            }

            public int getNext_level() {
                return next_level;
            }

            public void setNext_level(int next_level) {
                this.next_level = next_level;
            }

            public String getNext_msg() {
                return next_msg;
            }

            public void setNext_msg(String next_msg) {
                this.next_msg = next_msg;
            }

            public String getNext_image() {
                return next_image;
            }

            public void setNext_image(String next_image) {
                this.next_image = next_image;
            }
        }

        public static class NewLevelBean {
            /**
             * level : http://v.bjyzbx.com/vipconfig/level-0.png
             * centage : 0
             */

            private String level;
            private int centage;

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public int getCentage() {
                return centage;
            }

            public void setCentage(int centage) {
                this.centage = centage;
            }
        }

        public static class ShareLevelBean {
            /**
             * name : LV1
             * image : http://v.bjyzbx.com/vipconfig/level-1.png
             * invite : 1
             * score : 100
             * search : 2
             * rule : 邀请1人后，每日免费搜索次数 +2
             */

            private String name;
            private String image;
            private int invite;
            private int score;
            private int search;
            private String rule;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public int getInvite() {
                return invite;
            }

            public void setInvite(int invite) {
                this.invite = invite;
            }

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }

            public int getSearch() {
                return search;
            }

            public void setSearch(int search) {
                this.search = search;
            }

            public String getRule() {
                return rule;
            }

            public void setRule(String rule) {
                this.rule = rule;
            }
        }
    }
}
