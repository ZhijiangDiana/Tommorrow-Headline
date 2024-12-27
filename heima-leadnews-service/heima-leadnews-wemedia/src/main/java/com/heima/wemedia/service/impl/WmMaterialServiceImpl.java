package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.ThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import javafx.scene.paint.Material;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/26-20:50:40
 */
@Slf4j
@Service
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    /**
     * 图片上传
     * @param multipartFile
     * @return
     */
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.getSize() == 0)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        String fileName = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = multipartFile.getOriginalFilename();
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));

        String fileId;
        try {
            fileId = fileStorageService.uploadImgFile("", fileName + postfix, multipartFile.getInputStream());
            log.info("上传图片到MinIO中，fileId: {}", fileId);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("{} - 上传文件失败", getClass().getName());
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }

        WmUser wmUser = (WmUser) ThreadLocalUtil.getObject();
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(wmUser.getId());
        wmMaterial.setUrl(fileId);
        wmMaterial.setType(WemediaConstants.WM_MATERIAL_PIC);
        wmMaterial.setIsCollection((short) 0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);

        return ResponseResult.okResult(wmMaterial);
    }

    @Override
    public ResponseResult getList(WmMaterialDto dto) {
        dto.checkParam();

        // 分页查询
        IPage page = new Page(dto.getPage(), dto.getSize());
        WmUser user = (WmUser) ThreadLocalUtil.getObject();

        LambdaQueryWrapper<WmMaterial> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WmMaterial::getUserId, user.getId())  // 按照用户查询
                .orderByDesc(WmMaterial::getCreatedTime);  // 按照时间排序
        // 是否收藏
        if (dto.getIsCollection() != null && dto.getIsCollection() == 1)
            lqw.eq(WmMaterial::getIsCollection, dto.getIsCollection());
        // 分页
        page = page(page, lqw);

        // 返回结果
        PageResponseResult res = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        res.setData(page.getRecords());
        return res;
    }

    @Override
    public ResponseResult deletePicture(Integer id) {
        // 检查参数
        if (id == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        // 检查是否存在
        WmMaterial material = wmMaterialMapper.selectById(id);
        if (material == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);

        // 检查此图片是否被使用
        LambdaQueryWrapper<WmNewsMaterial> query = new LambdaQueryWrapper<WmNewsMaterial>()
                .select(WmNewsMaterial::getId)
                .eq(WmNewsMaterial::getMaterialId, id);
        List<WmNewsMaterial> res = wmNewsMaterialMapper.selectList(query);
        if (!res.isEmpty())
            return ResponseResult.errorResult(AppHttpCodeEnum.MATERIAL_HAS_REFERENCE);
        WmUser wmUser = (WmUser) ThreadLocalUtil.getObject();
        if (!wmUser.getId().equals(material.getUserId()))
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);

        // 执行删除
        removeById(id);
        fileStorageService.delete(material.getUrl());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult collectMaterial(Integer id) {
        // 参数检验
        if (id == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        WmUser wmUser = (WmUser) ThreadLocalUtil.getObject();
        WmMaterial material = wmMaterialMapper.selectOne(new LambdaQueryWrapper<WmMaterial>()
                .select(WmMaterial::getId, WmMaterial::getUserId)
                .eq(WmMaterial::getId, id));
        if (!wmUser.getId().equals(material.getUserId()))
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        // 执行修改
        material.setIsCollection((short) 1);
        wmMaterialMapper.updateById(material);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult cancelCollectMaterial(Integer mid) {
        // 参数检验
        if (mid == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        WmUser wmUser = (WmUser) ThreadLocalUtil.getObject();
        WmMaterial material = wmMaterialMapper.selectOne(new LambdaQueryWrapper<WmMaterial>()
                .select(WmMaterial::getId, WmMaterial::getUserId)
                .eq(WmMaterial::getId, mid));
        if (!wmUser.getId().equals(material.getUserId()))
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        // 执行修改
        material.setIsCollection((short) 0);
        wmMaterialMapper.updateById(material);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
