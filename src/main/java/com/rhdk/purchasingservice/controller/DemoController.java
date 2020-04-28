package com.rhdk.purchasingservice.controller;
import com.rhdk.purchasingservice.common.utils.response.ResponseData;
import com.rhdk.purchasingservice.pojo.vo.DemoVo;
import com.rhdk.purchasingservice.service.DemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试 前端控制器
 *
 * @author LMYOU
 * @since 2020-04-27
 */
@Api(tags = {"测试API"})
@Controller
@RequestMapping("/demoService")
@ResponseBody
public class DemoController {

  @Autowired
  private DemoService demoService;



  @ApiOperation(value = "Web端巡查主题列表分页查询", notes = "巡查主题API")
  @RequestMapping(value = "/searchList", method = RequestMethod.POST)
  public ResponseData searchTPatrolThemeListPageWeb(@RequestBody DemoVo dto) {
    return demoService.searchTPatrolThemeListPage(dto);
  }

}
