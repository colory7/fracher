package com.colory7;

import com.colory7.controller.Pusher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.colory7.util.DateUtil.sleep;

@Component
@Slf4j
public class AfterRunner implements ApplicationRunner {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${task.pull-sleep}")
    private Long pullSleep;

    @Autowired
    private Pusher pushComponent;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(() -> {
            while (true) {
                try {
                    pushComponent.pullImageResultAndPush();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                sleep(pullSleep);
            }
        }).start();
    }


}
