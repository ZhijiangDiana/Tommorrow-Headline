package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.ApRealnameDto;
import com.heima.model.user.dtos.RealnameDto;
import com.heima.model.user.pojos.ApUserRealname;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/31-00:55:56
 */
public interface ApUserRealnameService extends IService<ApUserRealname> {

    /**
     * 用户开始实名
     * @return
     */
    ResponseResult getRealnameStatus();

    /**
     * 将身份证正面转为文字信息
     * @param idCard
     * @return
     */
    ResponseResult recoIdCardInfo(String idCard);

    /**
     * 提交实名认证信息
     * @param realnameDto
     * @return
     */
    ResponseResult submitRealname(RealnameDto realnameDto);

    /**
     * 重新开始实名认证
     * @return
     */
    ResponseResult restartRealname();

    /**
     * 管理员分页查询实名请求
     * @param apRealnameDto
     * @return
     */
    ResponseResult pageListRequest(ApRealnameDto apRealnameDto);

    /**
     * 审核实名不通过
     * @param apRealnameDto
     * @return
     */
    ResponseResult authRequestFail(ApRealnameDto apRealnameDto);

    /**
     * 审核实名通过
     * @param apRealnameDto
     * @return
     */
    ResponseResult authRequestPass(ApRealnameDto apRealnameDto);


}
