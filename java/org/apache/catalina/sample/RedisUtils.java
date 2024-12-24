package org.apache.catalina.sample;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.apache.tomcat.dbcp.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

/**
 * @BelongsProject: tomcat
 * @Author: xiaochangbai
 * @CreateTime: 2024-12-24 17:02
 * @Description: TODO
 * @Version: 1.0
 */
public class RedisUtils {


    static RedisClient client = null;
   static StatefulRedisConnection<String, String> connection = null;
    public static void init(){
        RedisURI build = RedisURI.Builder.redis("172.26.110.108", 26379).withPassword("123456").withDatabase(1).build();
        client = RedisClient.create(build);
        connection = client.connect();
//        ClientOptions clientOptions = ClientOptions.create();
//        clientOptions.conne
//        client.setOptions(clientOptions);
    }

    public static String get(String key){
        if(true){
            for(int i=1;i<=100;i++){
                System.out.println("aaa:"+i);
            }
            return "hello";
        }
        long beginTime = System.currentTimeMillis();
//        StatefulRedisConnection<String, String> connection = client.connect();
        RedisStringCommands sync = connection.sync();
//        System.out.println("获取连接耗时:"+(System.currentTimeMillis()-beginTime));
        if((System.currentTimeMillis()-beginTime)>5){
            System.out.println("获取连接耗时:"+(System.currentTimeMillis()-beginTime));
        }
        beginTime = System.currentTimeMillis();
        if(new Random().nextInt(10)>6){
            key+= UUID.randomUUID().toString();
        }

        String value = (String) sync.get(key);
        System.out.println("获取值耗时:"+(System.currentTimeMillis()-beginTime));
        return value;
    }
}
