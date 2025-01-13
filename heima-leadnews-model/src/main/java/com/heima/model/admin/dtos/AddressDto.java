package com.heima.model.admin.dtos;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddressDto {
    private String address;
    private Double x;
    private Double y;
}
