import com.heima.admin.AdminApplication;
import com.heima.admin.service.AdUserOperationService;
import com.heima.model.common.enums.AdminOperationEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@SpringBootTest(classes = AdminApplication.class)
@RunWith(SpringRunner.class)
public class ServiceTest {

    @Autowired
    private AdUserOperationService adUserOperationService;

    @Test
    public void adUserOperationTest() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", "3");
        request.setRemoteAddr("127.0.0.1");
        adUserOperationService.recordOperation(request, AdminOperationEnum.LOGIN);
    }
}
