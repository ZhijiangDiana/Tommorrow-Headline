package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.pojos.AdUserOperation;
import com.heima.model.common.enums.AdminOperationEnum;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface AdUserOperationService extends IService<AdUserOperation> {

    /**
     *
     * @param request
     * @param op
     * @throws IOException
     */
    void recordOperation(HttpServletRequest request, AdminOperationEnum op) throws IOException;
}
