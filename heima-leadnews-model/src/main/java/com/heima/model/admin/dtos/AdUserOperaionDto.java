package com.heima.model.admin.dtos;

import com.heima.model.common.enums.AdminOperationEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdUserOperaionDto {
    private Integer adminId;
    private String ip;
    private AdminOperationEnum op;
}
