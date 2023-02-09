import com.colory7.dto.TaskCreate;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class WebClientTest {
    @Test
    public void test() {

        String jsonStr="{\n" +
                "  \"task_id\": \"cf6eccaecb6df38a80ae9ff5wewed519wewe91d2e\",\n" +
                "  \"url_type\":\"file\",\n" +
                "  \"url\": \"/Users/mac/Downloads/11.mp4\",\n" +
                "  \"roi\":\"[[0.0, 0.0], [1920.0, 0.0], [1920.0, 1080.0], [0.0, 1080.0]]\"\n" +
                "}";
        TaskCreate taskCreate = new TaskCreate();
        Mono<String> test = WebClient.create("http://127.0.0.1:8080/ai-vision/dynamic/tasks")
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(taskCreate)
//                .body(Mono.just(jsonStr), String.class)
                .retrieve()
                .bodyToMono(String.class);


        test.timeout(Duration.ofMillis(5000)).subscribe(response -> {
                    System.out.println("monitorTask() debug ok: \n" + response);
                }, e -> {
                    StringBuilder sb = new StringBuilder();
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        sb.append(stackTraceElement);
                        sb.append("\n");
                    }
                    System.out.println("monitorTask() debug error: \n" + sb.toString());
                }
        );

        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
