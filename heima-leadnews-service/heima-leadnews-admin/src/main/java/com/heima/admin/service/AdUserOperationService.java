package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.pojos.AdUserOperation;

import javax.servlet.http.HttpServletRequest;

public interface AdUserOperationService extends IService<AdUserOperation> {

    /**
     * 记录管理员操作日志
     * @param request
     * @param OpDescription
     */
    void recordOperation(HttpServletRequest request, String OpDescription);
}
