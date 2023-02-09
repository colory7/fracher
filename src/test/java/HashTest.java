import org.junit.Test;

import java.util.HashMap;
import java.util.List;

public class HashTest {

    @Test
    public void test() {
        HashMap<String, String> aa = new HashMap<>();
        String cc="77";
        String bb = "22";
        aa.put("aa", "11");
        aa.put(bb, cc);

        System.out.println(aa.get(bb));
        cc="88";
        System.out.println(aa.get(bb));

        List imageResults=null;
        if (imageResults == null || imageResults.size() == 0) {
            System.out.println("ss");
        }
    }

    @Test
    public void tes() {

    }
}
