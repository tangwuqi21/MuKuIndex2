package com.rhdk.purchasingservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rhdk.purchasingservice.common.utils.response.ResponseData;
import com.rhdk.purchasingservice.pojo.entity.Demo;
import com.rhdk.purchasingservice.pojo.vo.DemoVo;


/**
 * 测试 服务类
 *
 * @author LMYOU
 * @since 2020-04-27
 */
public interface DemoService extends IService<Demo> {
  /**
   * 获取分页列表数据
   * @param pojo
   * @return
   */
   ResponseData searchTPatrolThemeListPage(DemoVo pojo);



}
