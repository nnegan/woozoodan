package com.wzd.backend.v1.sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JspSamplecontroller {

    @RequestMapping("/sample")
    public String hellojsp(Model model){
        System.out.println("Call Success");
        model.addAttribute("name", "JSP Hello Test");

        return "sample";
    }
}
