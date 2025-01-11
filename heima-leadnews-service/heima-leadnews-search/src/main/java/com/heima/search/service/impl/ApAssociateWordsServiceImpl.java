package com.heima.search.service.impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.search.pojos.ApAssociateWords;
import com.heima.search.service.ApAssociateWordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ApAssociateWordsServiceImpl implements ApAssociateWordsService {

    private static final int MAX_PAGE_SIZE = 10;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseResult search(UserSearchDto userSearchDto) {
        if (userSearchDto == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        // 分页检查
        userSearchDto.setPageSize(Math.max(userSearchDto.getPageSize(), MAX_PAGE_SIZE));

        Query query = Query.query(Criteria
                .where("associateWords")
                .regex(".*?\\" + userSearchDto.getSearchWords() + ".*"))
                .with(Sort.by(Sort.Direction.DESC, "createdTime"))
                .limit(userSearchDto.getPageSize());
        List<ApAssociateWords> apAssociateWords = mongoTemplate.find(query, ApAssociateWords.class);
        return ResponseResult.okResult(apAssociateWords);
    }
}
