package com.rhdk.purchasingservice.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.igen.acc.rpc.thrift.IOrgService;
import com.igen.acc.rpc.thrift.IUserService;
import com.igen.auth.thrift.IAuthService;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderContractDTO;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import com.rhdk.purchasingservice.service.IOrderContractService;

import org.springframework.stereotype.Controller;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 合同表 前端控制器
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Slf4j
@Api(tags = {"合同表API"})
@Controller
@RequestMapping("/purchasingservice/orderContract")
@ResponseBody
public class OrderContractController {

    @Autowired
    private IOrderContractService iOrderContractService;

    /**
     * 采购管理-合同列表查询
     * @param dto
     * @return
     */
    @ApiOperation(value = "合同表列表分页查询", notes = "合同表API")
    @RequestMapping(value = "/searchOrderContractListPage", method = RequestMethod.POST)
    public ResponseEnvelope searchOrderContractListPage(@RequestBody OrderContractQuery dto) {
        return iOrderContractService.searchOrderContractListPage(dto);
    }

    /**
     * 采购管理-合同明细查询
     * @param id
     * @return
     */
    @ApiOperation(value = "合同表详细查询", notes = "合同表API")
    @RequestMapping(value = "/searchOrderContractOne", method = RequestMethod.POST)
    public ResponseEnvelope searchOrderContractOne(Long id) {
        return iOrderContractService.searchOrderContractOne(id);
    }

    /**
     * 采购管理-合同添加
     * @param dto
     * @return
     */
    @ApiOperation(value = "合同表添加", notes = "合同表API")
    @RequestMapping(value = "/addOrderContract", method = RequestMethod.POST)
    public ResponseEnvelope addOrderContract(@RequestBody @Valid OrderContractDTO dto) {
        return iOrderContractService.addOrderContract(dto);
    }

    /**
     * 采购管理-合同更新
     * @param dto
     * @return
     */
    @ApiOperation(value = "合同表更新", notes = "合同表API")
    @RequestMapping(value = "/updateOrderContract", method = RequestMethod.POST)
    public ResponseEnvelope updateOrderContract(@RequestBody @Valid OrderContractDTO dto) {
        return iOrderContractService.updateOrderContract(dto);
    }

    /**
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "导出合同列表数据", notes = "合同表API")
    @RequestMapping(value = "/exportContractList", method = RequestMethod.POST)
    public void exportContractList(HttpServletResponse response, @RequestBody OrderContractDTO dto) {
        log.info("根据条件导出合同列表数据");
        try {
            Map<String, Object> map = new HashMap<>();
            Workbook workbook = null;
            //获取模板
            TemplateExportParams params = new TemplateExportParams("word/contractInfo.xlsx", 0);
            //获取数据源
            List<OrderContractVO> result = iOrderContractService.getContractInforList(dto);
            map.put("listMap", result);
            //写入模板
            workbook = ExcelExportUtil.exportExcel(params, map);
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("采购合同列表.xlsx", "UTF-8"));
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "合同明细删除", notes = "合同表API")
    @RequestMapping(value = "/deleteOrderContract", method = RequestMethod.POST)
    public ResponseEnvelope deleteOrderContract(Long id) throws Exception{
        return iOrderContractService.deleteOrderContract(id);
    }


    @ApiOperation(value = "合同批量删除", notes = "合同表API")
    @RequestMapping(value = "/deleteContractList", method = RequestMethod.POST)
    public ResponseEnvelope deleteContractList(@RequestParam("ids") List<Long> ids) throws Exception{
        return iOrderContractService.deleteContractList(ids);
    }
}
