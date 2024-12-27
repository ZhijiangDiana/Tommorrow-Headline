package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/26-20:47:50
 */
public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 图片上传
     * @param multipartFile
     * @return
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);

    ResponseResult getList(WmMaterialDto dto);

    ResponseResult deletePicture(Integer id);

    ResponseResult collectMaterial(Integer id);

    ResponseResult cancelCollectMaterial(Integer mid);
}
