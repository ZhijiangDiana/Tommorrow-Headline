package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.RealnameDto;
import com.heima.user.service.ApUserRealnameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/31-01:04:03
 */
@RestController
@RequestMapping("/api/v1/realname")
public class ApUserRealnameController {

    @Autowired
    private ApUserRealnameService apUserRealnameService;

    @PostMapping("/getStatus")
    public ResponseResult getRealnameStatus() {
        return apUserRealnameService.getRealnameStatus();
    }

    @PostMapping("/recoIdCard")
    public ResponseResult recoIdCard(@RequestBody RealnameDto realnameDto) {
        return apUserRealnameService.recoIdCardInfo(realnameDto.getIdCardFront());
    }

    @PostMapping("/submit")
    public ResponseResult submit(@RequestBody RealnameDto realnameDto) {
        return apUserRealnameService.submitRealname(realnameDto);
    }

    @PostMapping("/restart")
    public ResponseResult restartRealname() {
        return apUserRealnameService.restartRealname();
    }
}
