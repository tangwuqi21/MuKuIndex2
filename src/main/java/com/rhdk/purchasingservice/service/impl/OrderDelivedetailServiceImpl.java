package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rhdk.purchasingservice.common.utils.BeanCopyUtil;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.mapper.OrderDelivedetailMapper;
import com.rhdk.purchasingservice.mapper.OrderDelivemiddleMapper;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivedetailDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivedetail;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivemiddle;
import com.rhdk.purchasingservice.pojo.query.AssetQuery;
import com.rhdk.purchasingservice.pojo.query.OrderDelivedetailQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivedetailVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import com.rhdk.purchasingservice.service.IOrderDelivedetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 送货明细 服务实现类
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Slf4j
@Transactional
@Service
public class OrderDelivedetailServiceImpl extends ServiceImpl<OrderDelivedetailMapper, OrderDelivedetail> implements IOrderDelivedetailService {


    @Autowired
    private OrderDelivedetailMapper orderDelivedetailMapper;

    @Autowired
    private OrderDelivemiddleMapper orderDelivemiddleMapper;

    @Autowired
    private AssetServiceFeign assetServiceFeign;

    @Override
    public ResponseEnvelope searchOrderDelivedetailListPage(OrderDelivedetailQuery dto) {
        Page<OrderDelivedetail> page = new Page<OrderDelivedetail>();
        page.setSize(dto.getPageSize());
        page.setCurrent(dto.getCurrentPage());
        QueryWrapper<OrderDelivedetail> queryWrapper = new QueryWrapper<OrderDelivedetail>();
        OrderDelivedetail entity = new OrderDelivedetail();
        entity.setMiddleId(dto.getMiddleId());
        queryWrapper.setEntity(entity);
        Map<String, Object> billInfoMap = orderDelivemiddleMapper.getContractInfoByMId(dto.getMiddleId());
        page = orderDelivedetailMapper.selectPage(page, queryWrapper);
        List<OrderDelivedetail> resultList = page.getRecords();
        List<Long> assetIds = new ArrayList<>();
        for(OrderDelivedetail a:resultList){
            assetIds.add(a.getAssetId());
        }
        //fegin调用资产服务，获取明细表格数据
        AssetQuery assetQuery=new AssetQuery();
        assetQuery.setAssetIds(assetIds);
        assetQuery.setAssetTemplId(dto.getModuleId());
        Map<String, List<Object>> map = (Map<String, List<Object>>) assetServiceFeign.searchEntityInfoPage(assetQuery, TokenUtil.getToken()).getData();
        Page pageResult = new Page<>();
        pageResult.setRecords(map.get("content"));
        pageResult.setSize(page.getSize());
        pageResult.setTotal(page.getTotal());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", pageResult);
        resultMap.put("title", map.get("title"));
        resultMap.put("contractInfo", billInfoMap);
        return ResultVOUtil.returnSuccess(resultMap);
    }

    @Override
    public ResponseEnvelope searchOrderDelivedetailOne(Long id) {

        return ResultVOUtil.returnSuccess(this.selectOne(id));
    }

    @Override
    @Transactional
    public ResponseEnvelope addOrderDelivedetail(OrderDelivedetailDTO dto) {
        OrderDelivedetail entity = new OrderDelivedetail();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        orderDelivedetailMapper.insert(entity);
        return ResultVOUtil.returnSuccess();
    }

    @Override
    public ResponseEnvelope updateOrderDelivedetail(OrderDelivedetailDTO dto) {
        OrderDelivedetail entity = this.selectOne(dto.getId());
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        orderDelivedetailMapper.updateById(entity);
        return ResultVOUtil.returnSuccess();
    }

    @Override
    public ResponseEnvelope deleteOrderDelivedetail(Long id) {
        orderDelivedetailMapper.deleteById(id);
        return ResultVOUtil.returnSuccess();
    }


    public OrderDelivedetail selectOne(Long id) {
        OrderDelivedetail entity = new OrderDelivedetail();
        entity.setId(id);
        QueryWrapper<OrderDelivedetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return orderDelivedetailMapper.selectOne(queryWrapper);
    }

}
