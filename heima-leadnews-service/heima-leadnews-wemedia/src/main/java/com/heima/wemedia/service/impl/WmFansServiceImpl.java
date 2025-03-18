package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.apis.user.IUserClient;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.common.redis.PipelineService;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.model.wemedia.vos.WmApUserVO;
import com.heima.utils.thread.ThreadLocalUtil;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmFansService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/16-14:55:55
 */
@Slf4j
@Service
public class WmFansServiceImpl implements WmFansService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private IUserClient userClient;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Override
    public ResponseResult pageListFans(PageRequestDto pageRequestDto) {
        // 查询用户id
        Integer wmUserId = ThreadLocalUtil.getUserId();
        if (wmUserId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // 检查参数
        pageRequestDto.checkParam();
        int start = (pageRequestDto.getPage() - 1) * pageRequestDto.getSize();
        int stop = start + pageRequestDto.getSize() - 1;

        // redis中获取信息
        String fansKey = BehaviorConstants.FAN_LIST + wmUserId;
        List<ZSetOperations.TypedTuple<String>> collect = cacheService.zReverseRangeWithScores(fansKey, start, stop)
                .stream()
                .filter(x -> Objects.nonNull(x.getValue()))
                .collect(Collectors.toList());
        int total = cacheService.zCount(fansKey, 0, Double.MAX_VALUE).intValue();
        // 提取用户id
        List<Integer> fansIds = collect.stream()
                .map(x -> Integer.parseInt(x.getValue()))
                .collect(Collectors.toList());
        // 提取对应的时间戳
        List<Long> followTimeStamps = collect.stream()
                .map(ZSetOperations.TypedTuple::getScore)
                .map(Double::longValue)
                .collect(Collectors.toList());

        // 获取关注列表
        WmUser wmUser = wmUserMapper.selectById(wmUserId);
        String followsKey = BehaviorConstants.FOLLOW_LIST + wmUser.getApUserId();
        Set<Integer> followsList = cacheService.zRangeAll(followsKey).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        Set<Integer> followsUserList = wmUserMapper.selectList(new LambdaQueryWrapper<WmUser>()
                        .in(WmUser::getId, followsList)
                        .select(WmUser::getApUserId)).stream()
                .map(WmUser::getApUserId)
                .collect(Collectors.toSet());

//        log.info("元组：{}", collect);
//        log.info("用户id：{}", fansIds);
//        log.info("时间戳：{}", followTimeStamps);

        // 判断是否为空
        List<WmApUserVO> res = new ArrayList<>();
        if (!fansIds.isEmpty()) {
            // 查询用户实体
            List<ApUser> apUsers = userClient.findUserByBatch(fansIds);

            log.info("用户实体：{}", apUsers);

            // 组装实体
            for (int i = 0; i < apUsers.size(); i++) {
                // 获取元素
                ApUser apUser = apUsers.get(i);
                Long followTimeStamp = followTimeStamps.get(i);

                // 组装实体
                WmApUserVO wmApUserVO = new WmApUserVO();
                BeanUtils.copyProperties(apUser, wmApUserVO);
                wmApUserVO.setFollowedTime(new Date(followTimeStamp));
                wmApUserVO.setIsFollowing(followsUserList.contains(apUser.getId()));

                // 加入列表
                res.add(wmApUserVO);
            }
        }

        // 组装响应体
        ResponseResult pageResponseResult = new PageResponseResult(
                pageRequestDto.getPage(),
                pageRequestDto.getSize(),
                total);
        pageResponseResult.setData(res);

        return pageResponseResult;
    }

    @Override
    public ResponseResult followOrUnFollowFans(UserRelationDto userRelationDto) {
        // 检查userId
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        WmUser thisUser = wmUserMapper.selectById(userId);

        // 修改请求
        WmUser destUser = wmUserMapper.selectOne(new LambdaQueryWrapper<WmUser>()
                .eq(WmUser::getApUserId, userRelationDto.getAuthorId())
                .select(WmUser::getId));
        if (destUser == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.USER_NOT_WEMEDIA_USER);

        userRelationDto.setAuthorId(destUser.getId());

        // 调用方法
        return userClient.followOrUnfollow(thisUser.getApUserId(), userRelationDto);
    }
}
