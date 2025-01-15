package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.wemedia.IWemediaUserClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserFollow;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.mapper.ApUserRelationMapper;
import com.heima.user.service.ApUserFollowService;
import com.heima.utils.thread.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ApUserFollowServiceImpl extends ServiceImpl<ApUserRelationMapper, ApUserFollow> implements ApUserFollowService {

    @Autowired
    private IWemediaUserClient wemediaUserClient;

    @Autowired
    private ApUserMapper apUserMapper;

    @Override
    public ResponseResult follow(UserRelationDto userRelationDto) {
        if (userRelationDto.getAuthorId() == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        ApUserFollow apUserFollow = new ApUserFollow();

        // 获取用户id
        ApUser apUser = apUserMapper.selectById(userId);
        apUserFollow.setFollowId(apUser.getId());
        // 远程调用wm微服务获取作者实体
        ResponseResult wmUser = wemediaUserClient.getUserById(userRelationDto.getAuthorId());
        if (wmUser == null || !wmUser.getCode().equals(200))
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        apUserFollow.setUserId(userId);
        if (UserRelationDto.OPERATION_FOLLOW.equals(userRelationDto.getOperation())) {
            // 检查是否已关注
            ApUserFollow relation = getOne(new LambdaQueryWrapper<ApUserFollow>()
                    .eq(ApUserFollow::getFollowId, apUserFollow.getFollowId())
                    .eq(ApUserFollow::getUserId, apUserFollow.getUserId())
            );
            if (relation == null) {
                // 执行关注逻辑
                apUserFollow.setFollowName(apUser.getName());
                apUserFollow.setLevel(ApUserFollow.LEVEL_SOMETIMES);
                apUserFollow.setIsNotice((short) 0);
                apUserFollow.setCreatedTime(new Date());

                save(apUserFollow);
            }
        } else if (UserRelationDto.OPERATION_UN_FOLLOW.equals(userRelationDto.getOperation())) {
            // 执行取消关注逻辑
            remove(new LambdaQueryWrapper<ApUserFollow>()
                    .eq(ApUserFollow::getFollowId, apUserFollow.getFollowId())
                    .eq(ApUserFollow::getUserId, apUserFollow.getUserId()));
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
