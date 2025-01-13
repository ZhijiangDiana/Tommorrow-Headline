package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.AdUserOperaionDto;
import com.heima.model.admin.pojos.AdUserOperation;
import com.heima.model.common.dtos.ResponseResult;

public interface AdUserOperationService extends IService<AdUserOperation> {

    /**
     * 添加管理员操作日志
     * @param dto
     */
    ResponseResult recordOperation(AdUserOperaionDto dto);
}
