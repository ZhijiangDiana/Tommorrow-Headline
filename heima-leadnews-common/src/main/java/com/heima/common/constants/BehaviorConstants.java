package com.heima.common.constants;

public class BehaviorConstants {

    /**
     * 点赞类redis主键
     */
    // 以下三个常量表示文章视角下的点赞数据，即文章被哪些用户点赞，文章的点赞数
    public static final String ARTICLE_LIKE_CNT = "article_like_cnt:";
    public static final String MOMENT_LIKE_CNT = "moment_like_cnt:";
    public static final String COMMENT_LIKE_CNT = "comment_like_cnt:";
    // 以下三个常量表示用户视角下的点赞数据，即用户点赞了哪些文章
    public static final String USER_ARTICLE_LIKE = "user_article_like:";
    public static final String USER_MOMENT_LIKE = "user_moment_like:";
    public static final String USER_COMMENT_LIKE = "user_comment_like:";


    /**
     * 不喜欢类相关redis主键
     */
    // 表示文章视角下的不喜欢数据
    public static final String ARTICLE_DISLIKE_CNT = "article_dislike_cnt:";
    // 表示用户视角下的不喜欢数据
    public static final String USER_ARTICLE_DISLIKE = "user_article_dislike:";


    /**
     * 文章阅读量redis主键
     */
    // 表示文章视角下的阅读数据
    public static final String ARTICLE_READ_COUNT = "article_read_cnt:";
    // 表示文章视角下的阅读数据
    public static final String USER_ARTICLE_READ = "user_article_read:";


    /**
     * 关注类相关redis主键
     */
    // 表示作者视角下的关注数据，即粉丝数
    public static final String FAN_LIST = "fans:";
    // 表示用户视角下的关注数据，即关注作者数
    public static final String FOLLOW_LIST = "follows:";


    /**
     * 收藏相关redis主键
     */
    // 以下两个常量表示用户视角下的收藏数据数据
    public static final String USER_ARTICLE_COLLECT = "user_article_collection:";
    public static final String USER_MOMENT_COLLECTION = "user_moment_collection:";
    // 以下两个常量表示文章视角下的收藏数据数据
    public static final String ARTICLE_COLLECT_CNT = "article_collected_cnt:";
    public static final String MOMENT_COLLECT_CNT = "moment_collected_cnt:";


}
