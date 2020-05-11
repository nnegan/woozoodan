package com.wzd.backend.v1.sample.controller;

import com.wzd.backend.v1.sample.model.SampleSearchModel;
import com.wzd.common.model.CommonDataModel;
import com.wzd.backend.v1.sample.model.SampleModel;
import com.wzd.backend.v1.sample.service.SampleService;
import com.wzd.common.model.PagenatedListModel;
import com.wzd.common.redis.RedisManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/v1/sample")
@Api(description = "샘플 코드")
public class SampleController {

    @Autowired
    private SampleService sampleService;

    // API 호출
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


    /**
     CRUD 샘플
     **/
    // 생성
    @PostMapping("/createuser")
    @ApiOperation(value="user 정보", notes="user 정보를 출력한다")
    public CommonDataModel<String> createuser(@RequestBody SampleModel sampleModel){
       sampleService.insertUser(sampleModel);
       return new CommonDataModel<String>("SUCCESS");
    }

    // 업데이트
    @PostMapping("/update")
    @ApiOperation(value="user 정보", notes="user 정보를 출력한다")
    public CommonDataModel<String> update(@RequestBody SampleModel sampleModel){

        sampleService.updateUser(sampleModel);
        return new CommonDataModel<String>("SUCCESS");
    }

    // 삭제
    @PostMapping("/delete/{userId}")
    @ApiOperation(value="user 정보", notes="user 정보를 출력한다")
    public CommonDataModel<String> update(@PathVariable String userId){

        sampleService.deleteUser(userId);
        return new CommonDataModel<String>("SUCCESS");
    }

    // 상세
    @GetMapping("/getUser/{userId}")
    @ApiOperation(value="user 정보", notes="user 정보를 출력한다")
    public CommonDataModel<SampleModel> getUser(@PathVariable String userId){
        CommonDataModel<SampleModel> resMap = new CommonDataModel<SampleModel>() ;
        log.info("SampleController.selectSampleuser userId=" + userId);

        resMap.setData(sampleService.selectSampleuser(userId));
        
        return resMap;
    }

    // 리스트 (페이징 처리 pageNo=1&rowsPerPage=5 )
    @GetMapping("/getuserlist")
    @ApiOperation(value="user 정보", notes="user 정보를 출력한다")
    public PagenatedListModel<SampleModel> getUser(@ModelAttribute SampleSearchModel sampleSearchModel){
        return sampleService.selectSampleuserList(sampleSearchModel);
    }

    /**
        REDIS 샘플
     **/

    @Autowired
    private RedisManager redisManager;

    @GetMapping("/setredis/{value}")
    public CommonDataModel<String>  setredis(@PathVariable String value){
        redisManager.put("test", value, 10, TimeUnit.MINUTES);
        return new CommonDataModel<String>("SUCCESS");
    }

    @GetMapping("/getredis")
    public CommonDataModel<String>  getredis(){
        return new CommonDataModel<String>((String)redisManager.getValue("test"));
    }

    @GetMapping("/delredis")
    public CommonDataModel<String>  delredis(){
        redisManager.delete("test");
        return new CommonDataModel<String>("SUCCESS");
    }
}

