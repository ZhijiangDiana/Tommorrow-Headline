import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/26-00:49:30
 */
@Slf4j
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleFreemarkerTest {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;

    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;

    @Test
    public void test() throws IOException, TemplateException {
        // 1. 获取文章内容
        ApArticleContent content = apArticleContentMapper.selectOne(
                Wrappers.<ApArticleContent>lambdaQuery()
                        .eq(ApArticleContent::getArticleId, "1383827995813531650L"));
        if (StringUtils.isNotBlank(content.getContent())) {
            // 2. 文章内容通过freemarker自动生成html文件
            Template template = configuration.getTemplate("article.ftl");
            // 数据模型
            Map<String, Object> cont = new HashMap<>();
            cont.put("content", JSONArray.parseArray(content.getContent()));
            StringWriter out = new StringWriter();
            // 合成
            template.process(cont, out);
            // 3. 把html上传到minio中
            InputStream in = new ByteArrayInputStream(out.toString().getBytes());
            String path = fileStorageService.uploadHtmlFile("", content.getArticleId() + ".html", in);
            // 4. 修改ap_article表，保存static_url字段
            apArticleService.update(Wrappers.<ApArticle>lambdaUpdate()
                    .eq(ApArticle::getId, content.getArticleId())
                    .set(ApArticle::getStaticUrl, path));

        }
    }

    @Test
    public void reloadAllHtml() {
        List<ApArticle> apArticles = apArticleMapper.selectList(Wrappers.<ApArticle>lambdaQuery());
        log.info("需要更新{}条数据", apArticles.size());
        for (ApArticle apArticle : apArticles) {
            ApArticleContent content = apArticleContentMapper.selectOne(
                    Wrappers.<ApArticleContent>lambdaQuery()
                            .eq(ApArticleContent::getArticleId, apArticle.getId().toString() + "L"));
            articleFreemarkerService.buildArticleToMinio(apArticle, content.getContent());
        }
    }
}
