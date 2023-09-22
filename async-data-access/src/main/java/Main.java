

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;


public class Main {
    public static void main(String[] args)  throws InterruptedException{
        RedisClient redisClient = RedisClient.create("redis://localhost:6379");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisAsyncCommands<String, String> asyncCommands = connection.async();

        asyncCommands.set("key", "Hello, Redis!");
        RedisFuture<String> future = asyncCommands.get("user1");
        future.whenComplete((result,ex) -> {
            System.out.println("The result is : "+ result);
        });
        Thread.sleep(10000);
        connection.close();
        redisClient.shutdown();
    }
}
