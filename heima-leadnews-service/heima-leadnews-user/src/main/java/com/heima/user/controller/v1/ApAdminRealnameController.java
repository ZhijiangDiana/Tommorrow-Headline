package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.ApRealnameDto;
import com.heima.user.service.ApUserRealnameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/4/17-23:41:46
 */
@RestController
@RequestMapping("/api/v1/auth")
public class ApAdminRealnameController {

    @Autowired
    private ApUserRealnameService apUserRealnameService;

    @PostMapping("/list")
    public ResponseResult pageList(@RequestBody ApRealnameDto apRealnameDto) {
        return apUserRealnameService.pageListRequest(apRealnameDto);
    }

    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody ApRealnameDto apRealnameDto) {
        return apUserRealnameService.authRequestFail(apRealnameDto);
    }

    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody ApRealnameDto apRealnameDto) {
        return apUserRealnameService.authRequestPass(apRealnameDto);
    }
}
