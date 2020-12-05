package com.zy.github.multiple.cache.example.service;

import com.zy.github.multiple.cache.example.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author zy 2020/12/3
 */
@Service
public class DemoService {

    @Cacheable(cacheNames = "testName2", key = "#id")
    public User test(String id){
        System.out.println("1111");
        User user = new User();
        user.setAge(11);
        user.setName("zzz");
        return user;
    }

//    @Cacheable(cacheNames = "testName3", key = "#id")
//    public User test3(String id){
//        System.out.println("333");
//        User user = new User();
//        user.setAge(22);
//        user.setName("xxx");
//        return user;
//    }
//
//    @Cacheable(cacheNames = "testName3", key = "#id")
//    public User test4(String id){
//        System.out.println("444");
//        User user = new User();
//        user.setAge(44);
//        user.setName("ccc");
//        return user;
//    }
}
