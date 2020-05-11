package com.wzd.backend.v1.sample.model;

import com.wzd.common.model.AbstractPagenateSearchModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@SuppressWarnings("serial")
@Alias("SampleSearchModel")
@ApiModel(description =  "Sample 모델")
public class SampleSearchModel extends AbstractPagenateSearchModel {

    @ApiModelProperty( value = "사용자 아이디")
    private String userId;

    @ApiModelProperty( value = "사용자 번호")
    private int userNo;

    @ApiModelProperty( value = "사용자 모바일번호")
    private String mobileNo;

    @ApiModelProperty( value = "등록일")
    private String createdAt;

    @ApiModelProperty( value = "수정일")
    private String modifiedAt;

    @Override
    public String toString() {
        return "SampleModel [userId=" + userId + ", userNo=" + userNo + ", mobileNo=" + mobileNo + "]";
    }
}
