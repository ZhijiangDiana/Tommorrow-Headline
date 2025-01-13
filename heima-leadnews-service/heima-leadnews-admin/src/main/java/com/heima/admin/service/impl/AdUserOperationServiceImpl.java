package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserOperationMapper;
import com.heima.admin.service.AdUserOperationService;
import com.heima.model.admin.pojos.AdUserOperation;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AdUserOperationServiceImpl extends ServiceImpl<AdUserOperationMapper, AdUserOperation> implements AdUserOperationService {

    @Override
    public void recordOperation(HttpServletRequest request, String OpDescription) {
        // TODO
    }
}
