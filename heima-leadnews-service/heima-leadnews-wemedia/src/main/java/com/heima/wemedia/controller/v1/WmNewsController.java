package com.heima.wemedia.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.apis.admin.IAdUserOperationClient;
import com.heima.model.admin.dtos.AdUserOperaionDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AdminOperationEnum;
import com.heima.model.wemedia.dtos.WmNewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsAuthPageDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/27-03:13:41
 */
@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    @Autowired
    private IAdUserOperationClient iAdUserOperationClient;

    @PostMapping("/list")
    public ResponseResult listNews(@RequestBody WmNewsPageReqDto dto) {
        return wmNewsService.pageListNews(dto);
    }

    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto){
        return wmNewsService.submit(dto);
    }

    @GetMapping("/one/{id}")
    public ResponseResult getOneNews(@PathVariable Integer id) {
        return wmNewsService.getOneNews(id);
    }

    @GetMapping("/del_news/{nid}")
    public ResponseResult deleteNews(@PathVariable Integer nid) {
        return wmNewsService.deleteNews(nid);
    }

    @PostMapping("/down_or_up")
    public ResponseResult releaseOrPulledDown(@RequestBody WmNews wmNews) {
        return wmNewsService.upOrDown(wmNews.getId(), wmNews.getEnable());
    }

    /**
     * 管理员查询文章列表
     * @param dto
     * @return
     */
    @PostMapping("/list_vo")
    public ResponseResult listVo(@RequestBody WmNewsAuthPageDto dto) {
        return wmNewsService.listVO(dto);
    }

    /**
     * 管理员查询单个文章
     * @param id
     * @return
     */
    @GetMapping("/one_vo/{id}")
    public ResponseResult oneVo(@PathVariable Integer id) {
        return wmNewsService.oneVO(id);
    }

    /**
     * 审核不通过
     * @param dto
     * @return
     */
    @PostMapping("/auth_fail")
    public ResponseResult authFail(HttpServletRequest request, @RequestBody WmNewsAuthDto dto) {
        ResponseResult res = wmNewsService.authFail(dto);
        iAdUserOperationClient.addAdUserOperation(
                new AdUserOperaionDto(
                        Integer.parseInt(request.getHeader("userId")),
                        request.getRemoteAddr(),
                        AdminOperationEnum.SCAN_UNACCEPTED
                )
        );
        return res;
    }

    /**
     * 审核通过
     * @param dto
     * @return
     */
    @PostMapping("/auth_pass")
    public ResponseResult authPass(HttpServletRequest request, @RequestBody WmNewsAuthDto dto) {
        ResponseResult res = wmNewsService.authPass(dto);
        iAdUserOperationClient.addAdUserOperation(
                new AdUserOperaionDto(
                        Integer.parseInt(request.getHeader("userId")),
                        request.getRemoteAddr(),
                        AdminOperationEnum.SCAN_ACCEPTED
                )
        );
        return res;
    }
}
