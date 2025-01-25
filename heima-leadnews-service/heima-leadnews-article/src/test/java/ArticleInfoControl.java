import com.heima.apis.behavior.IBehaviorClient;
import com.heima.apis.schedule.IScheduleClient;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.model.behavior.dtos.DislikeBehaviorDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)

public class ArticleInfoControl {

    @Autowired
    private IBehaviorClient behaviorClient;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private HotArticleService hotArticleService;

    @Test
    public void syncInfo() {
        hotArticleService.syncArticleInfo();
    }
}
