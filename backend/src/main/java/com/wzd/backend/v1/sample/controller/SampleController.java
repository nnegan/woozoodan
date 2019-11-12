package com.wzd.backend.v1.sample.controller;

import com.wzd.common.model.CommonDataModel;
import com.wzd.backend.v1.sample.model.SampleModel;
import com.wzd.backend.v1.sample.service.SampleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/sample")
@Api(description = "샘플 코드")
public class SampleController {

    @Autowired
    private SampleService sampleService;

    @GetMapping("/hello")
    @ApiOperation(value="Hello", notes="Hello를 출력한다")
    public void hello(){
        log.debug("Hello World Candy");
    }

    @GetMapping("/hellomodel")
    @ApiOperation(value="Hello", notes="Hello를 출력한다 json으로")
    public CommonDataModel<String> modelUsehello(){
        return new CommonDataModel<String>("SUCCESS Candy");
    }

    @GetMapping("/getUser/{userId}")
    @ApiOperation(value="user 정보", notes="user 정보를 출력한다")
    public CommonDataModel<SampleModel> getUser(@PathVariable String userId){
        CommonDataModel<SampleModel> resMap = new CommonDataModel<SampleModel>() ;
        log.info("SampleController.selectSampleuser userId=" + userId);

        resMap.setData(sampleService.selectSampleuser(userId));
        
        return resMap;
    }
}
