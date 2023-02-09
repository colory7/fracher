import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.colory7.BootServer;
import com.colory7.pojo.TaskImage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootServer.class)
public class SpringRedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Test
    public void test() {
        long result = redisTemplate.opsForList().rightPush("list_aa", "11");
        System.out.println(result);
    }

    @Test
    public void testJson() {
        TaskImage taskImageInfo = new TaskImage();
        taskImageInfo.setRoi("[[0.0, 0.0], [1920.0, 0.0], [1920.0, 1080.0], [0.0, 1080.0]]");
        taskImageInfo.setImageUrl("http://127.0.0.1/1/2/3");

        String json = JSON.toJSONString(taskImageInfo);

        Long result = redisTemplate.opsForList().rightPush("bb", json);
        System.out.println(result);
    }
//
//    @Test
//    public void testJson2() {
//        TaskImage taskImageInfo = new TaskImage();
//        taskImageInfo.setRoi("[[0.0, 0.0], [1920.0, 0.0], [1920.0, 1080.0], [0.0, 1080.0]]");
//        taskImageInfo.setImageUrl("http://127.0.0.1/1/2/3");
//
//        byte[] bs = JSON.toJSONBytes(taskImageInfo);
//
//        long result = redisTemplate.opsForList().rightPush("list_aa", bs);
//        System.out.println(result);
//    }

    @Test
    public void testJson3() {
        byte[] bs = "eyJpbWFnZVVybCI6Imh0dHA6Ly8xMjcuMC4wLjEvMS8yLzMiLCJyb2kiOiJbWzAuMCwgMC4wXSwgWzE5MjAuMCwgMC4wXSwgWzE5MjAuMCwgMTA4MC4wXSwgWzAuMCwgMTA4MC4wXV0ifQ=="
                .getBytes();
        System.out.println(JSON.toJSONString(bs));


    }

    @Test
    public void test4() {
        String imageResultJson = redisTemplate.opsForList().leftPop("list_aa");
        if (imageResultJson != null) {
            System.out.println(imageResultJson);
        }
        System.out.println("over");

    }

    @Test
    public void test5() {
        TaskImage taskImageInfo = new TaskImage();
        taskImageInfo.setTaskId("aa");
        taskImageInfo.setRoi("[[0.0, 0.0], [1920.0, 0.0], [1920.0, 1080.0], [0.0, 1080.0]]");
        taskImageInfo.setImageUrl("http://127.0.0.1/1/2/3");

        String json = JSON.toJSONString(taskImageInfo);

        Long result = redisTemplate.opsForList().rightPush("list_aa", json);
        System.out.println(result);


        String imageResultJson = redisTemplate.opsForList().leftPop("list_aa");

        TaskImage image= JSONObject.parseObject(imageResultJson, TaskImage.class);
        if (image != null) {
            System.out.println(image);
        }
        System.out.println("over");
    }
}
