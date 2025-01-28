package com.heima.wemedia.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.apis.admin.IAdUserOperationClient;
import com.heima.model.admin.dtos.AdUserOperaionDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AdminOperationEnum;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/27-03:03:00
 */
@RestController
@RequestMapping("/api/v1/channel")
public class WmChannelController {

    @Autowired
    private WmChannelService wmChannelService;

    @Autowired
    private IAdUserOperationClient iAdUserOperationClient;

    @GetMapping("/channels")
    public ResponseResult listChannels() {
        return wmChannelService.listAllChannels();
    }

    @GetMapping("/del/{id}")
    public ResponseResult delChannel(HttpServletRequest request, @PathVariable Integer id) {
        wmChannelService.removeById(id);
        iAdUserOperationClient.addAdUserOperation(
                new AdUserOperaionDto(
                        Integer.parseInt(request.getHeader("userId")),
                        request.getRemoteAddr(),
                        AdminOperationEnum.REMOVE_CHANNEL
                )
        );
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/list")
    public ResponseResult pageChannels(@RequestBody WmChannelDto wmChannelDto) {
        return wmChannelService.pageQuery(wmChannelDto);
    }

    @PostMapping("/save")
    public ResponseResult addChannel(HttpServletRequest request, @RequestBody WmChannel wmChannel) {
        wmChannel.setIsDefault(true);
        wmChannel.setCreatedTime(new Date());
        wmChannelService.save(wmChannel);
        iAdUserOperationClient.addAdUserOperation(
                new AdUserOperaionDto(
                        Integer.parseInt(request.getHeader("userId")),
                        request.getRemoteAddr(),
                        AdminOperationEnum.ADD_CHANNEL
                )
        );
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/update")
    public ResponseResult modifyChannel(HttpServletRequest request, @RequestBody WmChannel wmChannel) {
        wmChannel.setIsDefault(true);
        wmChannel.setCreatedTime(new Date());
        wmChannelService.updateById(wmChannel);
        iAdUserOperationClient.addAdUserOperation(
                new AdUserOperaionDto(
                        Integer.parseInt(request.getHeader("userId")),
                        request.getRemoteAddr(),
                        AdminOperationEnum.MODIFY_CHANNEL
                )
        );
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
