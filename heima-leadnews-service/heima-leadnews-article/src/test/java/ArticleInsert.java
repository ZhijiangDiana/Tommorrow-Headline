import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleInsert {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleService apArticleService;

    @Autowired
    private FileStorageService fileStorageService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private String basePath = "C:\\leadnews_workspace\\article_insert_workspace";

    private class Info {
        String searchWord = "原神纳塔";
        int channelId = 4;
        String channelName = "二游";
        Long authorId = 1102L;
        String authorName = "admin";
    }

    private Info info = new Info();

    @Test
    public void insertFromJson() throws IOException, ParseException {
        basePath += File.separator + info.searchWord;

        InputStreamReader inputStreamReader = new InputStreamReader(
                new FileInputStream(basePath + File.separator + "article_final_" + info.searchWord + ".json"));
        String jsonStr = IOUtils.toString(inputStreamReader);
        JSONArray articles = JSON.parseArray(jsonStr);
//        System.out.println(articles.get(0).toString());
        for (int i = 0; i < articles.size(); i++) {
            JSONObject article = articles.getJSONObject(i);

            // 若数据库已有相同文章，则跳过
            ApArticle dbArticle = apArticleMapper.selectOne(new LambdaQueryWrapper<ApArticle>()
                    .select(ApArticle::getTitle)
                    .eq(ApArticle::getTitle, article.getString("title")));
            if (dbArticle != null && StringUtils.isNotBlank(dbArticle.getTitle()))
                continue;

            // 获取id
            Integer id = article.getInteger("id");

            // 构造dto
            ArticleDto articleDto = new ArticleDto();
            articleDto.setTitle(article.getString("title"));
            articleDto.setAuthorId(info.authorId);
            articleDto.setAuthorName(info.authorName);
            articleDto.setChannelId(info.channelId);
            articleDto.setChannelName(info.channelName);
            articleDto.setLayout((short) 1);
            articleDto.setImages(article.getString("img_url"));
            articleDto.setLabels(info.searchWord);

            JSONObject content = article.getJSONObject("content");
            articleDto.setNation(content.getString("nation"));
            articleDto.setProvince(content.getString("province"));
            String publishTimeStr = content.getString("publish_time");
            if (StringUtils.isBlank(publishTimeStr))
                continue;
            Date publishTime = sdf.parse(publishTimeStr);
            articleDto.setCreatedTime(publishTime);
            articleDto.setPublishTime(publishTime);
            articleDto.setDescription(article.getString("description"));
            articleDto.setContent(content.getString("content"));

            // 上传图片
            Integer totalImg = article.getInteger("total_img");
            // 先上传封面
            String fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
            String imgBasePath = basePath + File.separator + "article_img";
            String coverPath = imgBasePath + File.separator + id + ".jpg";
            String url = fileStorageService.uploadImgFile("", fileName, Files.newInputStream(Paths.get(coverPath)));
            articleDto.setImages(url);

            // 上传正文图片
            String contStr = articleDto.getContent();
            if (StringUtils.isNotBlank(contStr)) {
                Queue<String> urls = new LinkedList<>();
                for (int j = 0; j < totalImg; j++) {
                    fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
                    String imgPath = imgBasePath + File.separator + id + "_"+ j + ".jpg";
                    String content_url = fileStorageService.uploadImgFile("", fileName, Files.newInputStream(Paths.get(imgPath)));
                    urls.offer(content_url);
                }
                JSONArray conFragment = JSON.parseArray(contStr);
                for (int j = 0; j < conFragment.size(); j++) {
                    JSONObject fragment = conFragment.getJSONObject(j);
                    if ("image".equals(fragment.getString("type")))
                        fragment.put("value", urls.poll());
                }
                contStr = JSON.toJSONString(conFragment);
            } else {
                continue;
            }
            articleDto.setContent(contStr);

            try {
                ResponseResult res = apArticleService.saveArticle(articleDto);
                if (!res.getCode().equals(200))
                    throw new RuntimeException();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("index={}文章插入失败", id);
            }

            log.info("index={}已完成", i);

//            break;
        }
    }
}
