import com.heima.model.user.dtos.RegisterDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.UserApplication;
import com.heima.user.service.ApUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = UserApplication.class)
@RunWith(SpringRunner.class)
public class ApBatchRegister {

    @Autowired
    private ApUserService apUserService;

    @Test
    public void batchRegister() {
        List<RegisterDto> regDto = getRegDto(20000000000L, 20000000100L);
        for (RegisterDto dto : regDto) {
            // 给密码加盐加密
            String salt = UUID.randomUUID().toString().replace("-", "");
            dto.setPassword(DigestUtils.md5DigestAsHex((dto.getPassword() + salt).getBytes()));

            ApUser apUser = new ApUser();
            BeanUtils.copyProperties(dto, apUser);
            apUser.setSalt(salt);
            apUser.setSex(ApUser.UNKNOWN);
            apUser.setIdentityAuthentication(false);
            apUser.setFlag(ApUser.ROBOT);
            apUser.setStatus(false);
            apUser.setImage("http://121.40.25.50:9050/leadnews/default_avatar.jpeg");
            apUser.setCreatedTime(new Date());

            apUserService.save(apUser);
//            break;
        }
    }

    private static List<RegisterDto> getRegDto(long start, long end) {
        List<RegisterDto> list = new ArrayList<>();
        for (long i = start; i < end; i++) {
            RegisterDto registerDto = new RegisterDto();
            String name = UUID.randomUUID().toString().replace("-", "").substring(0, 5).toUpperCase();
            registerDto.setName("用户" + name);
            registerDto.setPhone(String.valueOf(i));
            registerDto.setCode(114514);
            registerDto.setPassword("123456");

            list.add(registerDto);
        }

        return list;
    }
}
