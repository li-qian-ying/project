package com.neusoft.nep.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neusoft.nep.dto.LoginRequest;
import com.neusoft.nep.dto.RegisterRequest;
import com.neusoft.nep.entity.DecisionUser;
import com.neusoft.nep.entity.GridWorker;
import com.neusoft.nep.entity.PublicSupervisor;
import com.neusoft.nep.entity.SystemAdmin;
import com.neusoft.nep.mapper.DecisionUserMapper;
import com.neusoft.nep.mapper.GridWorkerMapper;
import com.neusoft.nep.mapper.PublicSupervisorMapper;
import com.neusoft.nep.mapper.SystemAdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PublicSupervisorMapper supervisorMapper;
    private final GridWorkerMapper workerMapper;
    private final SystemAdminMapper adminMapper;
    private final DecisionUserMapper decisionMapper;

    public Map<String, Object> login(LoginRequest request) {
        if (request.getRole() == null || request.getAccount() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("角色、账号和密码不能为空");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("role", request.getRole());
        result.put("account", request.getAccount());

        switch (request.getRole()) {
            case "neps":
                PublicSupervisor supervisor = supervisorMapper.selectOne(Wrappers.<PublicSupervisor>lambdaQuery()
                        .eq(PublicSupervisor::getPhone, request.getAccount())
                        .eq(PublicSupervisor::getPassword, request.getPassword()));
                requireUser(supervisor);
                result.put("id", supervisor.getId());
                result.put("name", supervisor.getRealName());
                result.put("phone", maskPhone(supervisor.getPhone()));
                result.put("age", supervisor.getAge());
                result.put("gender", supervisor.getGender());
                break;
            case "nepg":
                GridWorker worker = workerMapper.selectOne(Wrappers.<GridWorker>lambdaQuery()
                        .eq(GridWorker::getWorkerCode, request.getAccount())
                        .eq(GridWorker::getPassword, request.getPassword()));
                requireUser(worker);
                result.put("id", worker.getId());
                result.put("name", worker.getRealName());
                result.put("province", worker.getProvince());
                result.put("city", worker.getCity());
                result.put("workStatus", worker.getWorkStatus());
                break;
            case "nepm":
                SystemAdmin admin = adminMapper.selectOne(Wrappers.<SystemAdmin>lambdaQuery()
                        .eq(SystemAdmin::getAdminCode, request.getAccount())
                        .eq(SystemAdmin::getPassword, request.getPassword()));
                requireUser(admin);
                result.put("id", admin.getId());
                result.put("name", admin.getRealName());
                break;
            case "nepv":
                DecisionUser decision = decisionMapper.selectOne(Wrappers.<DecisionUser>lambdaQuery()
                        .eq(DecisionUser::getDecisionCode, request.getAccount())
                        .eq(DecisionUser::getPassword, request.getPassword()));
                requireUser(decision);
                result.put("id", decision.getId());
                result.put("name", decision.getRealName());
                break;
            default:
                throw new IllegalArgumentException("未知登录角色");
        }
        return result;
    }

    public Map<String, Object> register(RegisterRequest request) {
        if (request.getPhone() == null || !request.getPhone().matches("1\\d{10}")) {
            throw new IllegalArgumentException("请输入正确的11位手机号");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("密码不能少于6位");
        }
        if (request.getRealName() == null || request.getRealName().trim().isEmpty()) {
            throw new IllegalArgumentException("请输入真实姓名");
        }
        Long count = supervisorMapper.selectCount(Wrappers.<PublicSupervisor>lambdaQuery()
                .eq(PublicSupervisor::getPhone, request.getPhone()));
        if (count > 0) {
            throw new IllegalArgumentException("该手机号已经注册");
        }
        PublicSupervisor supervisor = new PublicSupervisor();
        supervisor.setPhone(request.getPhone());
        supervisor.setPassword(request.getPassword());
        supervisor.setRealName(request.getRealName().trim());
        supervisor.setAge(request.getAge());
        supervisor.setGender(request.getGender());
        supervisor.setCreateTime(LocalDateTime.now());
        supervisorMapper.insert(supervisor);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", supervisor.getId());
        result.put("role", "neps");
        result.put("account", supervisor.getPhone());
        result.put("name", supervisor.getRealName());
        result.put("phone", maskPhone(supervisor.getPhone()));
        result.put("age", supervisor.getAge());
        result.put("gender", supervisor.getGender());
        return result;
    }

    private void requireUser(Object user) {
        if (user == null) {
            throw new IllegalArgumentException("账号、密码或角色不正确");
        }
    }

    private String maskPhone(String phone) {
        return phone != null && phone.length() == 11
                ? phone.substring(0, 3) + "****" + phone.substring(7)
                : phone;
    }
}
