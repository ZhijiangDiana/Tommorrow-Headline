import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.apis.behavior.IBehaviorClient;
import com.heima.apis.schedule.IScheduleClient;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.vos.ArticleInfoVO;
import com.heima.model.behavior.dtos.DislikeBehaviorDto;
import com.heima.utils.common.RvGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;

@Slf4j
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)

public class ArticleInfoControl {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private HotArticleService hotArticleService;

    @Autowired
    private CacheService cacheService;

    /**
     * 数据女工，启动！
     */
    @Test
    public void addInfoBatch() {
        List<ApArticle> apArticles = apArticleMapper.selectList(new LambdaQueryWrapper<ApArticle>()
                .select(ApArticle::getId));
        for (ApArticle apArticle : apArticles) {
            String idStr = apArticle.getId().toString();
            Integer views = (int) RvGenerator.generateLogNormalRandom(6.0, 2);
            Integer likes = generateLikes(views);
            Integer favorites = generateFavorites(views);
            cacheService.set(BehaviorConstants.ARTICLE_READ_COUNT + idStr, views.toString());
            cacheService.set(BehaviorConstants.ARTICLE_LIKE_CNT + idStr, likes.toString());
            cacheService.set(BehaviorConstants.ARTICLE_COLLECT_CNT + idStr, favorites.toString());
            cacheService.set(BehaviorConstants.HAS_WROTE + idStr, idStr);

            log.info("{}观看量{}，点赞{}，收藏{}", idStr, views, likes, favorites);
//            break;
        }

    }

    /**
     * 生成点赞数
     * @param views
     * @return
     */
    private int generateLikes(int views) {
        Random random = new Random();
        double likeRate = 0.01 + (random.nextDouble() * 0.04); // 1% - 5% 观看量变为点赞
        return (int) (views * likeRate);
    }

    /**
     * 生成收藏数
     * @param views
     * @return
     */
    private int generateFavorites(int views) {
        Random random = new Random();
        double favoriteRate = 0.005 + (random.nextDouble() * 0.02); // 0.5% - 2% 观看量变为收藏
        return (int) (views * favoriteRate);
    }

    /**
     * 将数据同步到数据库
     */
    @Test
    public void syncInfo() {
        hotArticleService.syncArticleInfo();
    }
}
