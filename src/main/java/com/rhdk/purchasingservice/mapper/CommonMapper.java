package com.rhdk.purchasingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface CommonMapper extends BaseMapper<T> {

    List<HashMap<String, Object>> getContractInfoList(@Param("contractName") String contractName);

    List<HashMap<String, Object>> getSupplyList(@Param("companyName") String companyName);

    List<HashMap<String, Object>> getAssetInfoList();
}
