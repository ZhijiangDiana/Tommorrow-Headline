package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserOperationMapper;
import com.heima.admin.service.AdUserOperationService;
import com.heima.admin.service.AdUserService;
import com.heima.common.baidu.AddressService;
import com.heima.model.admin.dtos.AdUserOperaionDto;
import com.heima.model.admin.dtos.AddressDto;
import com.heima.model.admin.pojos.AdUserOperation;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AdminOperationEnum;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.net.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@Service
public class AdUserOperationServiceImpl extends ServiceImpl<AdUserOperationMapper, AdUserOperation> implements AdUserOperationService {

    @Lazy
    @Autowired
    private AdUserService adUserService;

    @Autowired
    private AddressService addressService;

    @Override
    @Transactional
    public ResponseResult recordOperation(AdUserOperaionDto dto) {
        // 记录操作日志
        AdUserOperation adUserOperation = new AdUserOperation();

        // 记录管理员id
        adUserOperation.setUserId(dto.getAdminId());

        // 添加用户ip及其归属地等信息
        String ipAddress = dto.getIp();
        if (ipAddress.equals("0:0:0:0:0:0:0:1"))
            ipAddress = "127.0.0.1";
        adUserOperation.setIp(ipAddress);
        AddressDto address;
        try {
            if (IPUtils.isPrivateIP(ipAddress)) {
                // 若为本地ip，则返回服务器地址
                address = adUserService.getServerAddress();
            } else {
                // 若为外部ip，则查找地址
                address = addressService.getAddressByIP(ipAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        adUserOperation.setAddress(address.getAddress());

        // 添加操作类型及描述
        AdminOperationEnum op = dto.getOp();
        adUserOperation.setType(op.getType());
        adUserOperation.setDescription(op.getDescription());

        // 添加操作时间
        adUserOperation.setCreatedTime(new Date());

        // 保存
        save(adUserOperation);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
