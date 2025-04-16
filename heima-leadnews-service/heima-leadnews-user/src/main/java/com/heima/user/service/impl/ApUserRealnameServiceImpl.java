package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.aliyun.RecognizeIdentityCard;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.RealnameDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserRealname;
import com.heima.model.user.vos.IdCardTextVO;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.mapper.ApUserRealnameMapper;
import com.heima.user.service.ApUserRealnameService;
import com.heima.utils.thread.ThreadLocalUtil;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/31-00:59:03
 */
@Service
public class ApUserRealnameServiceImpl extends ServiceImpl<ApUserRealnameMapper, ApUserRealname> implements ApUserRealnameService {

    @Autowired
    private RecognizeIdentityCard recognizeIdentityCard;

    @Autowired
    private ApUserRealnameMapper apUserRealnameMapper;

    @Autowired
    private ApUserMapper apUserMapper;

    @Override
    public ResponseResult getRealnameStatus() {
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // 判断是否已存在实名项
        ApUserRealname apUserRealname = apUserRealnameMapper.selectOne(new LambdaQueryWrapper<ApUserRealname>()
                .eq(ApUserRealname::getUserId, userId)
                .select(ApUserRealname::getId, ApUserRealname::getName, ApUserRealname::getStatus,
                        ApUserRealname::getReason, ApUserRealname::getCreatedTime, ApUserRealname::getSubmitedTime));

        // 返回实名状态
        if (apUserRealname != null)
            return ResponseResult.okResult(apUserRealname);

        // 查询当前用户
        ApUser apUser = apUserMapper.selectById(userId);

        // 组装实体
        ApUserRealname insertRealname = new ApUserRealname();
        insertRealname.setUserId(userId);
        insertRealname.setName(apUser.getName());
        insertRealname.setStatus(ApUserRealname.CREATING);
        insertRealname.setReason("");
        Date now = new Date();
        insertRealname.setCreatedTime(now);
        insertRealname.setUpdatedTime(now);

        // 提交
        apUserRealnameMapper.insert(insertRealname);

        return ResponseResult.okResult(insertRealname);
    }

    @Override
    public ResponseResult recoIdCardInfo(String idCard) {
        if (StringUtil.isBlank(idCard))
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        IdCardTextVO idCardTextVO = new IdCardTextVO();
        try {
            idCardTextVO = recognizeIdentityCard.recoIdCard(idCard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        if (idCardTextVO.getIdCardNumber() == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.IMAGE_NOT_LEGAL);


        return ResponseResult.okResult(idCardTextVO);
    }

    @Override
    public ResponseResult submitRealname(RealnameDto realnameDto) {
        if (realnameDto == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // 组装实体
        ApUserRealname apUserRealname = new ApUserRealname();
        apUserRealname.setRealName(realnameDto.getName());
        apUserRealname.setIdno(realnameDto.getIdCardNumber());
        apUserRealname.setFontImage(realnameDto.getIdCardFront());
        apUserRealname.setBackImage(realnameDto.getIdCardBack());
        apUserRealname.setStatus(ApUserRealname.NEED_VERIFY);
        Date now = new Date();
        apUserRealname.setSubmitedTime(now);
        apUserRealname.setUpdatedTime(now);

        // 提交修改
        apUserRealnameMapper.update(apUserRealname, new LambdaQueryWrapper<ApUserRealname>()
                .eq(ApUserRealname::getUserId, userId));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult restartRealname() {
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // 提交修改
        apUserRealnameMapper.delete(new LambdaQueryWrapper<ApUserRealname>()
                .eq(ApUserRealname::getUserId, userId));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
