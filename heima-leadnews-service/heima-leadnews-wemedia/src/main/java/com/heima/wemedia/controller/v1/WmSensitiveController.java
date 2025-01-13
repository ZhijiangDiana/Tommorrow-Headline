package com.heima.wemedia.controller.v1;

import com.heima.apis.admin.IAdUserOperationClient;
import com.heima.model.admin.dtos.AdUserOperaionDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AdminOperationEnum;
import com.heima.model.wemedia.dtos.WmSensitiveDto;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/sensitive")
public class WmSensitiveController {

    @Autowired
    private WmSensitiveService wmSensitiveService;

    @Autowired
    private IAdUserOperationClient adUserOperationClient;

    @DeleteMapping("/del/{id}")
    public ResponseResult deleteSensitive(HttpServletRequest request, @PathVariable Integer id) {
        ResponseResult res = wmSensitiveService.deleteSensitive(id);
        adUserOperationClient.addAdUserOperation(new AdUserOperaionDto(
                Integer.parseInt(request.getHeader("userId")),
                request.getRemoteAddr(),
                AdminOperationEnum.REMOVE_SENSITIVES
        ));
        return res;
    }

    @PostMapping("/list")
    public ResponseResult listSensitive(@RequestBody WmSensitiveDto wmSensitiveDto) {
        return wmSensitiveService.pageQuery(wmSensitiveDto);
    }

    @PostMapping("/save")
    public ResponseResult saveSensitive(HttpServletRequest request, @RequestBody WmSensitive wmSensitive) {
        ResponseResult res = wmSensitiveService.insertSensitive(wmSensitive);
        adUserOperationClient.addAdUserOperation(new AdUserOperaionDto(
                Integer.parseInt(request.getHeader("userId")),
                request.getRemoteAddr(),
                AdminOperationEnum.ADD_SENSITIVES
        ));
        return res;
    }

    @PostMapping("/update")
    public ResponseResult updateSensitive(HttpServletRequest request, @RequestBody WmSensitive wmSensitive) {
        ResponseResult res = wmSensitiveService.updateSensitive(wmSensitive);
        adUserOperationClient.addAdUserOperation(new AdUserOperaionDto(
                Integer.parseInt(request.getHeader("userId")),
                request.getRemoteAddr(),
                AdminOperationEnum.MODIFY_SENSITIVES
        ));
        return res;
    }
}
