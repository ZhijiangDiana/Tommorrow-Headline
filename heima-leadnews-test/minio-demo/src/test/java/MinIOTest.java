import com.heima.file.service.FileStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.shimokitazawa.MinIOApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/25-16:05:38
 */
@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void test() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("C:\\Users\\14838\\Desktop\\2EBD0530670B2EABA128C729018AD4E9.jpg");
        String path = fileStorageService.uploadImgFile("", "cat.jpg", fis);
        System.out.println(path);
    }

//    public static void main(String[] args) {
//        try {
//            FileInputStream fis = new FileInputStream("C:\\Users\\14838\\Desktop\\7499b896ad62005a68718d623c60e9ef.gif");
//
//            // 1、获取minio的链接信息
//            MinioClient client = MinioClient.builder()
//                    .credentials("minio", "minio123")
//                    .endpoint("http://192.168.1.205:9000")
//                    .build();
//
//            // 上传
//            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
//                    .object("Anno.gif")
//                    .contentType("image/gif")
//                    .bucket("leadnews")
//                    .stream(fis, fis.available(), -1)
//                    .build();
//            client.putObject(putObjectArgs);
//
//            System.out.println("http://192.168.1.205:9000/leadnews/Anno.gif");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//
//    }
}