package com.zy.github.multiple.cache.example.controller;

import com.zy.github.multiple.cache.example.service.DemoService;
import com.zy.github.multiple.cache.example.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zy 2020/12/3
 */
@RestController
public class DemoController {
    @Autowired
    private DemoService demoService;
    @Autowired
    private CacheManager cacheManager;

    @RequestMapping("cache-test")
    public User demo(){
        User test = demoService.test("1111");
        /*User test3 = demoService.test3("2222");
        User test4 = demoService.test4("4444");
        System.out.println(test);
        System.out.println(test3);
        System.out.println(test4);*/
        return test;
    }
}
