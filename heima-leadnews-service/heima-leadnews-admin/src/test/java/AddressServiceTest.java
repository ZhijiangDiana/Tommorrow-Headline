import com.heima.admin.AdminApplication;
import com.heima.admin.service.AdUserService;
import com.heima.common.baidu.AddressService;
import com.heima.model.admin.dtos.AddressDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest(classes = AdminApplication.class)
@RunWith(SpringRunner.class)
public class AddressServiceTest {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AdUserService adUserService;

    @Test
    public void ipAddressTest() throws IOException {
        AddressDto addressByIP = addressService.getAddressByIP("120.192.14.89");
        System.out.println(addressByIP);
    }

    @Test
    public void serverIPTest() throws IOException {
        AddressDto serverAddress = adUserService.getServerAddress();
        System.out.println(serverAddress);
    }
}
