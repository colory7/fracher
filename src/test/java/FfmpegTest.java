import com.colory7.util.DateUtil;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FfmpegTest {


    @Test
    public void extractImageFromVideo() {
        System.out.println("提取音频文件");
        // 定义rtsp协议地址
        String rtspUrl = "rtsp://localhost:8554/xsk";

        //抓取资源
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(rtspUrl);
        // rtsp使用tcp协议传输数据
        frameGrabber.setOption("rtsp_transport", "tcp");
        //frameGrabber.delayedGrab(10000);
        Frame frame = null;

        try {
            // 开始抓取rtsp数据
            frameGrabber.start();
            // 输出文件编号
            long j = 1;

            // 循环抓取rtsp数据，保存到记录器recorder，recorder会写入临时文件
            while (true) {
                //抓取一个图片数据
                frame = frameGrabber.grabImage();
                // 判断是否抓取rtsp完成，rtsp停止直播则为完成
                if (frame == null) {
                    System.out.println("视频处理完成");
                    break;
                }

//                System.out.println(111);
//                System.out.println(frame.image);
                // 满足间隔时间，保存音频数据到记录器，记录器存储音频数据到文件
//                if (frame.timestamp / 250_000 >= j) {
//                    // FIXME 保存图片到minio
//
                    Java2DFrameConverter converter = new Java2DFrameConverter();
                    BufferedImage bi = converter.getBufferedImage(frame);
//
                    ByteArrayOutputStream os = new ByteArrayOutputStream(256 * 1024);
                    ImageIO.write(bi, "jpg", os);
//                    ByteArrayInputStream in = new ByteArrayInputStream(os.toByteArray());
//
//                    String fileName ="_"+ j + ".jpg";
//                    putMinio(fileName, in);
                    File outputfile = new File("/Users/mac/Desktop/tmp3/" + j + ".jpg");
                    ImageIO.write(bi, "jpg", outputfile);
//
//                    // FIXME 存储redis
//
//
                    j++;
//                }

//                Thread.sleep(50);

//                frame.close();

            }

            // 停止抓取rtsp数据
            frameGrabber.stop();
            // 释放抓取资源
            frameGrabber.release();

        } catch (Exception e) {
            // FIXME 异常处理
            e.printStackTrace();
        }
    }

    public void putMinio(String fileName, ByteArrayInputStream in) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        String endpoint = "http://106.37.72.221:9000";
        String accessKey = "minio";
        String secretKey = "minio@pass123!";
        String bucketName = "aivision";
        String today = DateUtil.today();

        MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();

        ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(today + "/" + fileName)
                .stream(in, in.available(), -1)
                //.contentType(file.getContentType())
                .build()
        );

        // TODO minio的图片路径存储到redis队列
        String url = endpoint + "/" + bucketName + "/" + today + fileName;
        System.out.println(url);
    }
}
