package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/26.
 * CEO问答的实体类
 */
public class CEOQuestionsBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {
        public String id;
        public String time;
        public String title;
        public String content;
        public String username;
        public List<Answers> answers;
        public String photo;

        public static class Answers implements Serializable {
            public String id;
            public String time;
            public String useful;
            public String content;
            public String username;
            public List<Replies> replies;
            public String photo;

            public static class Replies {
                public String id;
                public String time;
                public String content;
                public String username;
                public String photo;

            }
        }
    }
}