package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.user.service.ApUserCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/user")
public class ApUserCommonController {

    @Autowired
    private ApUserCommonService apUserCommonService;

    @GetMapping("/info")
    public ResponseResult getUserInfo() {
        return apUserCommonService.getUserInfo();
    }

    /**
     * 用apId查找粉丝列表
     * @return
     */
    @GetMapping("/fans")
    public ResponseResult getFans() {
        return apUserCommonService.getFansList();
    }

    /**
     * 用apId查找关注列表
     * @return
     */
    @GetMapping("/following")
    public ResponseResult getFollowing() {
        return apUserCommonService.getFollowingList();
    }

    /**
     * 上传文件
     * @param multipartFile
     * @return
     */
    @PostMapping("/uploadFile")
    public ResponseResult uploadFile(MultipartFile multipartFile) {
        return apUserCommonService.uploadPicture(multipartFile);
    }

    /**
     * 使用自媒体用户id查询用户
     * @param wmuserId
     * @return
     */
    @PostMapping("/getUserByWmId/{wmuserId}")
    public ResponseResult getUserByWmId(@PathVariable Integer wmuserId) {
        return apUserCommonService.getUserByWmId(wmuserId);
    }
}
