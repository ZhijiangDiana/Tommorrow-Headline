import com.alibaba.fastjson.JSON;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.CodeLoginDto;
import com.heima.model.user.dtos.RegisterDto;
import com.heima.user.UserApplication;
import com.heima.user.service.ApUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest(classes = UserApplication.class)
@RunWith(SpringRunner.class)
public class ApUserTest {

    @Autowired
    private ApUserService apUserService;

    @Test
    public void sendVerifyCode() {
        ResponseResult res = apUserService.sendVerifyCode("15069096266");
        log.info(JSON.toJSONString(res));
    }

    @Test
    public void register() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setPhone("15069096266");
        registerDto.setPassword("123456");
        registerDto.setName("Firefly");
        registerDto.setCode(107808);

        ResponseResult res = apUserService.register(registerDto);

        log.info(JSON.toJSONString(res));
    }

    @Test
    public void loginByCode() {
        CodeLoginDto codeLoginDto = new CodeLoginDto();
        codeLoginDto.setPhone("15069096266");
        codeLoginDto.setCode(123456);
        ResponseResult res = apUserService.loginByCode(codeLoginDto);
        log.info(JSON.toJSONString(res));
    }
}
