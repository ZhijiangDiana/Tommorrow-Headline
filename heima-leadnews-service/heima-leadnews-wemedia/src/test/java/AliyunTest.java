import com.aliyuncs.exceptions.ClientException;
import com.heima.common.aliyun.ImageModerationService;
import com.heima.common.aliyun.TextModerationService;
import com.heima.common.aliyun.ModerationResult;
import com.heima.file.service.FileStorageService;
import com.heima.wemedia.WemediaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FilterOutputStream;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/28-18:54:28
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class AliyunTest {

    @Autowired
    private TextModerationService textModerationService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ImageModerationService imageModerationService;

    @Test
    public void testScanText() throws Exception {
//        TextModerationResult res = textModerationService.textJudge("你好骚啊");
        ModerationResult res = textModerationService.textJudge(
                "研究生入学考试的政治理论科目，分为马克思主义基本原理、毛泽东思想及中国特色社会主义理论体系概论、" +
                        "习近平新时代中国特色社会主义思想概论、中国近现代史纲要、思想道德与法治五个部分。");
        System.out.println(res);
    }

    @Test
    public void testScanImage() throws Exception {
        byte[] bytes = fileStorageService.downLoadFile("http://192.168.1.205:9000/leadnews-images/2024/12/28/1d3881a6f0ac481691b372236a7f8afa.png");
        ModerationResult res = imageModerationService.imageModeration(bytes);
//        ScanLocalImage.imageScan(bytes);
        System.out.println(res);
    }
}
