package com.heima.common.constants;

public class BehaviorConstants {

    /**
     * 点赞类redis主键
     */
    public static final String ARTICLE_LIKE = "article_like:";
    public static final String MOMENT_LIKE = "moment_like:";
    public static final String COMMENT_LIKE = "comment_like:";

    public static final Short ARTICLE_LIKE_CODE = 0;
    public static final Short MOMENT_LIKE_CODE = 1;
    public static final Short COMMENT_LIKE_CODE = 2;

    public static final Short LIKE_OPERATION = 0;
    public static final Short DISCARD_LIKE_OPERATION = 1;

    /**
     * 不喜欢类相关redis主键
     */
    public static final String ARTICLE_READ = "article_read:";
    public static final String ARTICLE_DISLIKE = "article_dislike:";

    public static final Short DISLIKE_OPERATION = 0;
    public static final Short DISCARD_DISLIKE_OPERATION = 1;

    /**
     * 关注类相关redis主键
     */
    public static final String FAN_LIST = "fans:";
    public static final String FOLLOW_LIST = "follows:";

    /**
     * 收藏相关redis主键
     */
    public static final String ARTICLE_COLLECTION = "article_collection:";
    public static final String MOMENT_COLLECTION = "moment_collection:";
    public static final String ARTICLE_BE_COLLECTED = "article_be_collected:";
    public static final String MOMENT_BE_COLLECTED = "moment_be_collected:";


}
