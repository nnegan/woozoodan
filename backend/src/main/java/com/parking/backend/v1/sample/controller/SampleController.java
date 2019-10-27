package com.parking.backend.v1.sample.controller;

import com.parking.backend.common.model.CommonDataModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/sample")
public class SampleController {

    @GetMapping("/hello")
    public void hello(){
        log.debug("Hello World");
    }

    @GetMapping("/hellomodel")
    public CommonDataModel<String> modelUsehello(){
        return new CommonDataModel<String>("SUCCESS");
    }

}
