package com.heima.article.controller.v1;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/channel")
public class ArticleChannelController {

    @Autowired
    private IWemediaClient wemediaClient;

    @GetMapping("/channels")
    public ResponseResult listChannels() {
        return wemediaClient.listChannels();
    }
}
