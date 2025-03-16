package com.heima.user.feign;

import com.heima.apis.user.IUserClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.service.ApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class UserClient implements IUserClient {

    @Autowired
    private ApUserService apUserService;

    @Override
    @GetMapping("/api/v1/user/{id}")
    public ApUser findUserById(@PathVariable("id") Integer id) {
        return apUserService.getById(id);
    }

    @Override
    public List<ApUser> findUserByBatch(List<Integer> ids) {
        List<ApUser> apUsers = apUserService.listByIds(ids);
        LinkedHashMap<Integer, ApUser> temp = apUsers.stream().collect(Collectors.toMap(
                ApUser::getId,
                apUser -> apUser,
                (a, b) -> a,
                LinkedHashMap::new));

        apUsers = ids.stream()
                .map(temp::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return apUsers;
    }
}