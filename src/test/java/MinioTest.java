import com.colory7.BootServer;
import com.colory7.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.bytedeco.javacv.FrameFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootServer.class)
public class MinioTest {

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioConfig minioConfig;

    @Test
    public void getPresignedObjectUrl() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        GetPresignedObjectUrlArgs build = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(minioConfig.getBucketName())
                .object("2022-08-22")
//                .object("100j.jpg")
                .build();

//        minioClient.setTimeout(1,1,1);
        String url= minioClient.getPresignedObjectUrl(build);
        System.out.println(url);
    }

    @Test
    public void getObjectInfo() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        StatObjectResponse response= minioClient.statObject(StatObjectArgs.builder().bucket(minioConfig.getBucketName()).object(
                "2022-08-22"
//                "100j.jpg"
        ).build());
        System.out.println(response);

    }

    @Test
    public void getObjectInfo2() throws Exception {
        minioClient.putObject(PutObjectArgs.builder().bucket(minioConfig.getBucketName()).object("20220802").build());
    }

    @Test
    public void getObjectInfo3() throws Exception {
        // archive/2022/06/00793e85350141849f8ff559b60d1430_CLSX10-1·3-004-089.jpg
        String path="2022/06/00793e85350141849f8ff559b60d1430_CLSX10-1·3-004-089.jpg";
        GetObjectResponse result = minioClient.getObject(GetObjectArgs.builder().bucket("archive")
                .object(path)
                .build());
        System.out.println(result.available());
    }

}
