package com.zy.github.multiple.cache.example;

import com.zy.github.multiple.cache.CachedValueRebuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zy 2020/12/6
 */
@Component
public class DemoCachedValueRebuilder implements CachedValueRebuilder<String, Object> {

    @Override
    public Object rebuild(String key, Object value) {
        if (value instanceof User) {
            VIPUser v = new VIPUser();
            BeanUtils.copyProperties(value, v);
            v.setLevel(1);
            return v;
        }
        if (value instanceof List) {
            List nv = (List) value;
            List nnv = new ArrayList();
            for (Object o : nv) {
                if (o instanceof User) {
                    VIPUser v = new VIPUser();
                    User va = (User)o;
                    BeanUtils.copyProperties(value, va);
                    v.setLevel(1);
                    nnv.add(v);
                }
            }
            return nnv;
        }
        return value;
    }
}
