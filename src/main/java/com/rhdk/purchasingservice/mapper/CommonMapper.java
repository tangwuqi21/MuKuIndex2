package com.rhdk.purchasingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rhdk.purchasingservice.pojo.entity.AssetTmplInfo;
import com.rhdk.purchasingservice.pojo.entity.Customer;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.util.HashMap;
import java.util.List;


/**
 * 测试 Mapper 接口
 *
 * @author LMYOU
 * @since 2020-04-27
 */
public interface CommonMapper extends BaseMapper<Customer> {

    List<HashMap<String, Object>> getSupplyList(@Param("companyName") String companyName);

    List<Long> getIdsBySupplierName(@Param("companyName") String companyName);

    List<HashMap<String, Object>> getAssetInfoList();

    List<HashMap<String, Object>> getCommonPrpts();

    List<HashMap<String, Object>> getModulePrptsById(@Param("moduleId") Long moduleId);

    AssetTmplInfo getAssetTemplInfo(@Param("moduleId") Long moduleId);

    Customer getCustomerInfo(@Param("supplierId") Long supplierId);
}
