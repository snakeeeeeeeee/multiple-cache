package com.zy.github.multiple.cache.example.service;

import com.zy.github.multiple.cache.example.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zy 2020/12/3
 */
@Service
public class DemoService {

    @Cacheable(cacheNames = "testCache", key = "#id")
    public List<User> cacheTest(String id){
        User user = new User();
        user.setAge(22);
        user.setName("xxx");

        List<User> users = new ArrayList<>();
        users.add(user);
        return users;
    }

}
