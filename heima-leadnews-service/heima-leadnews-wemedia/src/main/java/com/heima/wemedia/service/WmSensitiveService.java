package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmSensitiveDto;
import com.heima.model.wemedia.pojos.WmSensitive;

public interface WmSensitiveService extends IService<WmSensitive> {

    /**
     * 删除敏感词
     * @param id
     * @return
     */
    ResponseResult deleteSensitive(Integer id);

    /**
     * 分页查询敏感词
     * @param dto
     * @return
     */
    ResponseResult pageQuery(WmSensitiveDto dto);

    /**
     * 新增敏感词
     * @param wmSensitive
     * @return
     */
    ResponseResult insertSensitive(WmSensitive wmSensitive);

    /**
     * 修改敏感词
     * @param wmSensitive
     * @return
     */
    ResponseResult updateSensitive(WmSensitive wmSensitive);
}
