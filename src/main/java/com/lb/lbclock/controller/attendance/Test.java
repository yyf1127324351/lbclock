package com.lb.lbclock.controller.attendance;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Test {
    @RequestMapping("/test")
    public String test(){
        return "test";
    }
}
