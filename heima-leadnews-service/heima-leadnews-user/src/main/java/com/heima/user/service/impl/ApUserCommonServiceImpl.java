package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserRealname;
import com.heima.model.user.vos.ApUserInfoVO;
import com.heima.model.user.vos.ApUserListItemVO;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.mapper.ApUserRealnameMapper;
import com.heima.user.service.ApUserCommonService;
import com.heima.utils.thread.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApUserCommonServiceImpl implements ApUserCommonService {

    @Autowired
    private ApUserMapper apUserMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private IWemediaClient wemediaClient;

    /**
     * 获取用户主页信息
     * @return
     */
    @Override
    public ResponseResult getUserInfo() {
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        ApUser apUser = apUserMapper.selectById(userId);
        if (apUser == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        // 查找wmUserId
        Integer wmUserId = null;
        if (!apUser.getFlag().equals(ApUser.NORMAL_USER)) {
            ResponseResult resp = wemediaClient.getUserById(userId);
            if (resp.getCode() != AppHttpCodeEnum.SUCCESS.getCode())
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
            Object data = resp.getData();
            if (data != null) {
                LinkedHashMap map = (LinkedHashMap) data;
                wmUserId = (Integer) map.get("id");
            }
        }

        ApUserInfoVO apUserInfoVO = new ApUserInfoVO();

        // 填写用户名和头像
        apUserInfoVO.setName(apUser.getName());
        apUserInfoVO.setAvatar(apUser.getImage());

        // 填写是否实名
        apUserInfoVO.setVerified(apUser.getIdentityAuthentication());

        // 填写动态、关注、粉丝数
        apUserInfoVO.setPosts(0);
        apUserInfoVO.setFollowing(cacheService.zCount(BehaviorConstants.FOLLOW_LIST + userId, 0, Double.MAX_VALUE)
                .intValue());
        apUserInfoVO.setFollowers(cacheService.zCount(BehaviorConstants.FAN_LIST + wmUserId, 0, Double.MAX_VALUE)
                .intValue());

        // 填写阅读时间
        Integer readingMinutes = 0;
        String readingTime = "";
        if (readingMinutes / 60 != 0)
            readingTime = readingMinutes / 60 + "小时";
        readingTime += readingMinutes % 60 + "分钟";
        apUserInfoVO.setReadingTime(readingTime);

        return ResponseResult.okResult(apUserInfoVO);
    }

    /**
     * 获取关注列表
     * @return
     */
    @Override
    public ResponseResult getFollowingList() {
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        ApUser apUser = apUserMapper.selectById(userId);
        if (apUser == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        // 获取关注列表
        List<ApUser> users = cacheService.zRangeByScore(BehaviorConstants.FOLLOW_LIST + userId, 0, Double.MAX_VALUE)
                .stream()
                .map(x -> apUserMapper.selectOne(new LambdaQueryWrapper<ApUser>()
                        .eq(ApUser::getWmUserId, Integer.parseInt(x))))
                .collect(Collectors.toList());
        Collections.reverse(users);

        // 组装VO
        List<ApUserListItemVO> res = new ArrayList<>();
        for (ApUser user : users) {
            ApUserListItemVO apUserListItemVO = new ApUserListItemVO();
            apUserListItemVO.setName(user.getName());
            apUserListItemVO.setDescription(user.getDescription());
            apUserListItemVO.setAvatar(user.getImage());
            apUserListItemVO.setIsFollowing(true);
            apUserListItemVO.setIsMutual(false);

            res.add(apUserListItemVO);
        }

        return ResponseResult.okResult(res);
    }

    /**
     * 获取粉丝列表
     * @return
     */
    @Override
    public ResponseResult getFansList() {
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        ApUser apUser = apUserMapper.selectById(userId);
        if (apUser == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        // 查找wmUserId
        Integer wmUserId = null;
        List<ApUserListItemVO> res = new ArrayList<>();
        if (!apUser.getFlag().equals(ApUser.NORMAL_USER)) {
            wmUserId = apUser.getWmUserId();

            // 获取粉丝列表（apUser版）
            List<ApUser> userIds = cacheService.zRangeByScore(BehaviorConstants.FAN_LIST + wmUserId, 0, Double.MAX_VALUE)
                    .stream().map(x -> apUserMapper.selectById(Integer.parseInt(x)))
                    .collect(Collectors.toList());
            Collections.reverse(userIds);

            // 获取关注列表（apUser版）
            Set<Integer> followingUserIds = cacheService.zRangeAll(BehaviorConstants.FOLLOW_LIST + userId)
                    .stream()
                    .map(x -> apUserMapper.selectOne(new LambdaQueryWrapper<ApUser>()
                            .eq(ApUser::getWmUserId, Integer.parseInt(x))))
                    .map(ApUser::getId)
                    .collect(Collectors.toSet());

            // 组装VO
            for (ApUser user : userIds) {
                ApUserListItemVO apUserListItemVO = new ApUserListItemVO();
                apUserListItemVO.setName(user.getName());
                apUserListItemVO.setDescription(user.getDescription());
                apUserListItemVO.setAvatar(user.getImage());
                apUserListItemVO.setIsFollowing(followingUserIds.contains(user.getId()));
                apUserListItemVO.setIsMutual(false);

                res.add(apUserListItemVO);
            }
        }

        return ResponseResult.okResult(res);
    }
}
