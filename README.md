# 使用方式
1 将工程编译到本地

2 引入依赖
```` pom
<dependency>
    <groupId>com.github.zy</groupId>
    <artifactId>multiple-cache</artifactId>
    <version>0.0.3</version>
</dependency>
````

3 在启动类上加上@EnableCaching
``` java
@EnableCaching
@SpringBootApplication
public class MultipleCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultipleCacheApplication.class, args);
    }

}
```
4 配置Cache的属性信息
``` yml
spring:
  redis:
    port:  # redis server port
    host:  # redis server host
    lettuce:
      pool:
        max-active: 50
        max-wait: 2000
        max-idle: 20
        min-idle: 5
#    cluster:
#      nodes:
#    lettuce:
#      pool:
#        max-active: 50
#        max-wait: 2000
#        max-idle: 20
#        min-idle: 5
  application:
    name: aaaaaaaaaa
multiple-cache:
#  redis:
#    - name: testCache #缓存名称
#      expire: 100 #缓存过期时间
#  caffeine:
#    - name: testCache #缓存名称
#      expireAfterAccess: 30 #缓存过期时间
#      initialCapacity: 100 #缓存初始化存储大小
#      maximumSize: 1000 #缓存最大存储大小
  multiple:
    - name: testCache #缓存名称
      caffeine:
        expireAfterAccess: 30  #缓存过期时间
        initialCapacity: 100 #缓存初始化存储大小
        maximumSize: 1000 #缓存最大存储大小
      redis:
        expire: 100 #缓存过期时间
```

5 使用方式没有任何变化，还是基于注解的形式即可。 
``` java
@RestController
public class DemoController {
    @Autowired
    private DemoService demoService;


    @RequestMapping("cache-test")
    public List<User> demo(){
        return demoService.cacheTest("testId");
    }
}

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

```
