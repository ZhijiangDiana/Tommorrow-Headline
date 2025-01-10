package com.heima.search.test;

import com.heima.search.SearchApplication;
import com.heima.search.service.ApUserSearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = SearchApplication.class)
@RunWith(SpringRunner.class)
public class ArticleESTest {

    @Autowired
    private ApUserSearchService apUserSearchService;

    @Test
    public void importAllTest(){

    }

    @Test
    public void addSearchHistoryTest1(){
        apUserSearchService.addSearchHistory("大喵", 114);
    }

    @Test
    public void addSearchHistoryTest2() throws InterruptedException {
        for (int i = 0; i < 15; i++) {
            apUserSearchService.addSearchHistory("键帽欠爱了" + i, 114);
            Thread.sleep(2000);
        }
    }

    @Test
    public void addSearchHistoryTest3() throws InterruptedException {
        apUserSearchService.addSearchHistory("键帽欠爱了7", 114);
        Thread.sleep(5000);
        apUserSearchService.addSearchHistory("喵姆喵姆~", 114);
    }

}
