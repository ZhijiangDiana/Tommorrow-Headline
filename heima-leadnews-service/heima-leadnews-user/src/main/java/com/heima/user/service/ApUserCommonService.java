package com.heima.user.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.vos.ApUserInfoVO;
import org.springframework.web.multipart.MultipartFile;

public interface ApUserCommonService {

    /**
     * 获取用户主页信息
     * @return
     */
    ResponseResult getUserInfo();

    /**
     * 获取关注列表
     * @return
     */
    ResponseResult getFollowingList();

    /**
     * 获取粉丝列表
     * @return
     */
    ResponseResult getFansList();

    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);
}
