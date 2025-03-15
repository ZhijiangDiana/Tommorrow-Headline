package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmStatisticsDto;
import com.heima.wemedia.service.WmStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/14-23:38:13
 */
@RestController
@RequestMapping("/api/v1/statistics")
public class WmStatisticsController {

    @Autowired
    private WmStatisticsService wmStatisticsService;

    @PostMapping("/overall")
    public ResponseResult overall(@RequestBody WmStatisticsDto dto) {
        return wmStatisticsService.newsDimension(dto);
    }

    @PostMapping("/news")
    public ResponseResult news(@RequestBody WmStatisticsDto dto) {
        return wmStatisticsService.newsPage(dto);
    }
}
