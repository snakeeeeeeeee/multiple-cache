package com.zy.github.multiple.cache.example.controller;

import com.zy.github.multiple.cache.example.service.DemoService;
import com.zy.github.multiple.cache.example.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * @author z 2020/12/3
 */
@RestController
public class DemoController {
    @Autowired
    private DemoService demoService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @RequestMapping("cache-test")
    public List<User> demo(){
        redisTemplate.opsForValue().set("12222", 33);
        demoService.cacheTest2(999);
        demoService.cacheTest3(new HashMap());
        return demoService.cacheTest("testId");
    }
}
