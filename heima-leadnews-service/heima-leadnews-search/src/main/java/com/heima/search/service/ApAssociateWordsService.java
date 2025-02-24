package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;

import java.util.List;

public interface ApAssociateWordsService {

    /**
     * 搜索联想词
     * @param userSearchDto
     * @return
     */
    ResponseResult search(UserSearchDto userSearchDto);
}
