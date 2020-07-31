package com.mukutech.seapersonservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mukutech.seapersonservice.common.utils.response.ResponseEnvelope;
import com.mukutech.seapersonservice.pojo.dto.TestDemoDTO;
import com.mukutech.seapersonservice.pojo.entity.TestDemo;


/**
 * <p>
 * 用户基础属性表 服务类
 * </p>
 *
 * @author LMYOU
 * @since 2020-07-22
 */
public interface ITestDemoService extends IService<TestDemo> {
    ResponseEnvelope searchTestDemoListPage(TestDemoDTO DTO);

    public ResponseEnvelope searchTestDemoOne(Long id);

    public ResponseEnvelope addTestDemo(TestDemoDTO DTO);

    public ResponseEnvelope updateTestDemo(TestDemoDTO DTO);

    public ResponseEnvelope deleteTestDemo(Long id);
}
