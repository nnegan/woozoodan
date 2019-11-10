package com.wzd.backend.v1.sample.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description =  "Sample 모델")
public class SampleModel {

    @ApiModelProperty( value = "사용자 아이디")
    private String userId;

    @ApiModelProperty( value = "사용자 번호")
    private int userNo;

    @ApiModelProperty( value = "사용자 모바일번호")
    private String mobileNo;

}
