import com.alibaba.fastjson2.JSON;
import org.junit.Test;

public class Fastjson2Test {

    @Test
    public void test() {
        String id="aa";
        Object obj = JSON.parse(id);

        String json=JSON.toJSONString(obj);
        System.out.println(json);
    }
}
