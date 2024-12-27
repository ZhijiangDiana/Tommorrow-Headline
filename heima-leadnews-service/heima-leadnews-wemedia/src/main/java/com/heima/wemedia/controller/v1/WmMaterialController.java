package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/26-20:45:46
 */
@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController {

    @Autowired
    private WmMaterialService wmMaterialService;

    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        return wmMaterialService.uploadPicture(multipartFile);
    }

    @PostMapping("/list")
    public ResponseResult findList(@RequestBody WmMaterialDto dto) {
        return wmMaterialService.getList(dto);
    }

    @GetMapping("/del_picture/{id}")
    public ResponseResult deletePicture(@PathVariable Integer id) {
        return wmMaterialService.deletePicture(id);
    }

    @GetMapping("/collect/{id}")
    public ResponseResult collectPicture(@PathVariable Integer id) {
        return wmMaterialService.collectMaterial(id);
    }

    @GetMapping("/cancel_collect/{mid}")
    public ResponseResult cancelCollectPicture(@PathVariable Integer mid) {
        return wmMaterialService.cancelCollectMaterial(mid);
    }
}
