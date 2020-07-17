package com.wzd.backend.v1.sample.controller;

import com.wzd.backend.v1.sample.model.SampleSearchModel;
import com.wzd.common.model.CommonDataModel;
import com.wzd.backend.v1.sample.model.SampleModel;
import com.wzd.backend.v1.sample.service.SampleService;
import com.wzd.common.model.PagenatedListModel;
import com.wzd.common.mq.MQUtils;
import com.wzd.common.mq.model.MessageHeader;
import com.wzd.common.redis.RedisManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
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

    /**
        RabbitMQ 샘플
     */
    @Autowired
    private MQUtils mqUtils;

    /**
        테스트 전 준비사항
        sudo docker run -d --name rabbitmq -p 5672:5672 -p 8080:15672 --restart=unless-stopped -e RABBITMQ_DEFAULT_USER=username -e RABBITMQ_DEFAULT_PASS=password rabbitmq:management
        콘솔에서 생성 -->  wzd.test.exchange -> wzd.test.queues 바인딩 필요함
     */
    @GetMapping("/mqsender/{message}")
    public void mqsender(@PathVariable String message){
        try {
            mqUtils.mqSender(MessageHeader.EX_DIRECT, "wzd.test.exchange", "", "", "traceid1", message);
        } catch (Throwable e) {
            throw new RuntimeException("Could not send MQ", e);
        }
    }

    @RabbitListener(queues="wzd.test.queues")
    public void mqListner(@Payload byte[] messageBody) {
        log.info("mqListner Start");

        if (messageBody == null || messageBody.length == 0) {
            log.info("Queue businessContractRegister messageBody is null");
            return;
        }

        String messageStr;
        try {
            messageStr = new String(messageBody, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
            messageStr = new String(messageBody);
        }
        try {
            log.info("Queue businessContractRegister messageStr : {}", messageStr);

        } catch (Exception e) {
            log.error("Error : {}", e.getMessage());
        }

    }
}

