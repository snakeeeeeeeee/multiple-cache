package com.zy.github.multiple.cache.example.controller;

import com.zy.github.multiple.cache.example.service.DemoService;
import com.zy.github.multiple.cache.example.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author z 2020/12/3
 */
@RestController
public class DemoController {
    @Autowired
    private DemoService demoService;


    @RequestMapping("cache-test")
    public List<User> demo(){
        return demoService.cacheTest("testId");
    }
}
