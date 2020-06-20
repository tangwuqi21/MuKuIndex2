package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.igen.acc.domain.dto.OrgUserDto;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.BeanCopyUtil;
import com.rhdk.purchasingservice.common.utils.NumberUtils;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.mapper.OrderContractMapper;
import com.rhdk.purchasingservice.mapper.PurcasingContractMapper;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderContractDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderContract;
import com.rhdk.purchasingservice.pojo.entity.PurcasingContract;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import com.rhdk.purchasingservice.service.CommonService;
import com.rhdk.purchasingservice.service.IOrderContractService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同表 服务实现类
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Slf4j
@Transactional
@Service
public class OrderContractServiceImpl extends ServiceImpl<OrderContractMapper, OrderContract>
    implements IOrderContractService {

  @Autowired private OrderContractMapper orderContractMapper;

  @Autowired private CommonService commonService;

  @Autowired private PurcasingContractMapper purcasingContractMapper;

  @Autowired private AssetServiceFeign assetServiceFeign;

  private static org.slf4j.Logger logger = LoggerFactory.getLogger(OrderContractServiceImpl.class);

  @Override
  public IPage<OrderContractVO> searchOrderContractListPage(OrderContractQuery dto, Long orgId) {
    logger.info("searchOrderContractListPage-获取合同id列表开始");
    Page page = new Page();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    IPage<OrderContractVO> recordsList = null;
    List<Long> paramStr = orderContractMapper.getContractIdList(dto);
    logger.info("searchOrderContractListPage-获取合同id列表结束，获取了" + paramStr.size() + "条");
    if (paramStr.size() > 0) {
      dto.setContractIds(paramStr);
    } else {
      return recordsList;
    }
    recordsList = orderContractMapper.selectContractList(page, dto, orgId);
    List<OrderContractVO> resultList = recordsList.getRecords();
    // 一次取出所有合同信息
    List<OrderContractVO> contractVOList = orderContractMapper.selectContractById(null, null);
    Map<Long, OrderContractVO> contractVOMap = listToMap(contractVOList);
    resultList.stream()
        .forEach(
            a -> {
              OrgUserDto userDto = commonService.getOrgUserById(a.getOrgId(), a.getCreateBy());
              OrderContractVO mo = new OrderContractVO();
              if (contractVOMap.get(a.getId()) != null) {
                mo = contractVOMap.get(a.getId());
              }
              OrderAttachmentDTO attachmentDTO = new OrderAttachmentDTO();
              attachmentDTO.setParentId(mo.getOrderId());
              attachmentDTO.setAtttype(1);
              a.setAttachmentList(
                  assetServiceFeign.selectListByParentId(attachmentDTO, dto.getToken()).getData());
              a.setContractCompany(mo.getContractCompany());
              a.setId(mo.getOrderId());
              a.setCreateName(userDto.getUserInfo().getName());
              a.setDeptName(userDto.getGroupName());
            });
    logger.info("getFileList-获取合同附件列表结束");
    recordsList.setRecords(resultList);
    return recordsList;
  }

  public Map<Long, OrderContractVO> listToMap(List<OrderContractVO> contractVOList) {
    Map<Long, OrderContractVO> result = new HashMap<>();
    contractVOList.forEach(
        temp -> {
          result.put(temp.getContractId(), temp);
        });
    return result;
  }

  @Override
  public ResponseEnvelope searchOrderContractOne(Long id) {
    OrderContractVO orderContractVO = new OrderContractVO();
    OrderContractQuery dto = new OrderContractQuery();
    dto.setToken(TokenUtil.getToken());
    dto.setId(id);
    IPage<OrderContractVO> result = null;
    try {
      result = searchOrderContractListPage(dto, TokenUtil.getUserInfo().getOrganizationId());
      if (result != null && result.getRecords().size() > 0) {
        orderContractVO = result.getRecords().get(0);
      }
    } catch (Exception e) {
      throw new RuntimeException("获取单个合同详情出错！合同id为：" + id);
    }
    return ResultVOUtil.returnSuccess(orderContractVO);
  }

  @Override
  @Transactional
  public ResponseEnvelope addOrderContract(OrderContractDTO dto) {
    try {
      OrderContract entity = new OrderContract();
      if (CollectionUtils.isEmpty(dto.getAttachmentList())) {
        return ResultVOUtil.returnFail(
            ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
      }
      BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
      logger.info("addContract-添加合同主体信息开始");
      entity.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
      // 这里自动生成合同业务编码，规则为：HT+时间戳
      String code = NumberUtils.createCode("HT");
      entity.setContractCode(code);
      orderContractMapper.insert(entity);
      logger.info("addContract-添加合同主体信息结束");
      // 合同主体添加成功，进行上传文件的记录保存，并关联到对应合同主体
      // 添加到采购合同表中
      PurcasingContract purcasingContract = new PurcasingContract();
      purcasingContract.setContractId(entity.getId());
      purcasingContract.setContractCompany(dto.getContractCompany());
      purcasingContract.setOrgId(entity.getOrgId());
      purcasingContractMapper.insert(purcasingContract);
      logger.info("addAttachment-添加合同附件信息开始");
      for (OrderAttachmentDTO model : dto.getAttachmentList()) {
        model.setParentId(purcasingContract.getId());
        model.setAtttype(1);
      }
      Integer num1 =
          assetServiceFeign.addBeatchAtta(dto.getAttachmentList(), TokenUtil.getToken()).getCode();
      logger.info("addAttachment-添加合同附件信息结束");
      if (num1 == 0) {
        return ResultVOUtil.returnSuccess();
      } else {
        return ResultVOUtil.returnFail();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResultVOUtil.returnFail();
    }
  }

  @Override
  @Transactional
  public ResponseEnvelope updateOrderContract(OrderContractDTO dto) {
    PurcasingContract model = purcasingContractMapper.selectById(dto.getId());
    model.setContractCompany(dto.getContractCompany());
    model.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
    logger.info("updateAttachment-修改采购合同信息开始");
    purcasingContractMapper.updateById(model);
    logger.info("updateAttachment-修改采购合同信息结束");
    logger.info("updateAttachment-修改关联合同主体信息开始");
    OrderContract orderContract = new OrderContract();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, orderContract);
    orderContract.setId(model.getContractId());
    orderContractMapper.updateById(orderContract);
    logger.info("updateAttachment-修改关联合同主体信息结束");

    // 这里进行合同附件的批量新增操作
    if (CollectionUtils.isEmpty(dto.getAttachmentList())) {
      return ResultVOUtil.returnFail(
          ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
    }
    logger.info("updateAttachment-修改合同附件信息开始");
    // 1.先将之前的附件列表统一删除，
    OrderAttachmentDTO orderAttachmentDTO = new OrderAttachmentDTO();
    orderAttachmentDTO.setParentId(model.getId());
    orderAttachmentDTO.setAtttype(1);
    try {
      assetServiceFeign.deleteAttachmentByParentId(orderAttachmentDTO, TokenUtil.getToken());
    } catch (Exception e) {
      throw new RuntimeException("删除采购合同附件失败，合同id为：" + model.getId());
    }
    // 2.重新上传附件
    if (dto.getAttachmentList().size() > 0) {
      for (OrderAttachmentDTO model2 : dto.getAttachmentList()) {
        model2.setParentId(model.getId());
        model2.setAtttype(1);
      }
      assetServiceFeign.addBeatchAtta(dto.getAttachmentList(), TokenUtil.getToken()).getCode();
    }
    logger.info("updateAttachment-修改合同附件信息结束");
    return ResultVOUtil.returnSuccess();
  }

  @Override
  public List<OrderContractVO> getContractInforList(OrderContractQuery dto, Long orgId) {
    List<OrderContractVO> contractVOList = new ArrayList<>();
    Page page = new Page();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    IPage<OrderContractVO> recordsList = null;
    List<Long> paramStr = orderContractMapper.getContractIdList(dto);
    logger.info("getContractInforList-获取合同id列表结束，获取了" + paramStr.size() + "条");
    if (paramStr.size() > 0) {
      dto.setContractIds(paramStr);
    } else {
      return contractVOList;
    }
    recordsList = orderContractMapper.selectContractList(page, dto, orgId);
    contractVOList = recordsList.getRecords();
    // 一次取出所有合同信息
    List<OrderContractVO> contractVOList2 = orderContractMapper.selectContractById(null, null);
    Map<Long, OrderContractVO> contractVOMap = listToMap(contractVOList2);
    contractVOList.forEach(
        a -> {
          // 根据合同id去附件表里获取每个合同对应的附件
          OrgUserDto userDto = commonService.getOrgUserById(a.getOrgId(), a.getCreateBy());
          OrderContractVO contractVO = new OrderContractVO();
          if (contractVOMap.get(a.getId()) != null) {
            contractVO = contractVOMap.get(a.getId());
          }
          OrderAttachmentDTO attachmentDTO = new OrderAttachmentDTO();
          attachmentDTO.setParentId(a.getId());
          attachmentDTO.setAtttype(1);
          List<Map<String, Object>> fileList =
              assetServiceFeign.selectListByParentId(attachmentDTO, dto.getToken()).getData();
          String haveFile = fileList.size() > 0 ? "是" : "否";
          if (contractVO != null) {
            a.setId(contractVO.getOrderId());
            a.setContractCompany(contractVO.getContractCompany());
          }
          a.setHaveFile(haveFile);
          a.setContractTypeName(a.getContractType() == 1 ? "采购合同" : "其他");
          if (userDto != null) {
            a.setCreateName(userDto.getUserInfo().getName());
            a.setDeptName(userDto.getGroupName());
          }
        });
    return contractVOList;
  }

  @Override
  public ResponseEnvelope deleteOrderContract(Long id) {
    // 物理删除送货明细附件表
    logger.info("deleteOrderContract-删除附件表信息开始");
    OrderAttachmentDTO orderAttachmentDTO = new OrderAttachmentDTO();
    orderAttachmentDTO.setParentId(id);
    orderAttachmentDTO.setAtttype(1);
    try {
      assetServiceFeign.deleteAttachmentByParentId(orderAttachmentDTO, TokenUtil.getToken());
    } catch (Exception e) {
      throw new RuntimeException("删除采购合同附件失败，合同id为：" + id);
    }
    logger.info("deleteOrderContract-删除附件表信息结束");
    PurcasingContract entity = new PurcasingContract();
    entity.setId(id);
    QueryWrapper<PurcasingContract> queryWrapper = new QueryWrapper<>();
    queryWrapper.setEntity(entity);
    logger.info("deleteOrderContract-删除采购合同表信息开始");
    entity = purcasingContractMapper.selectOne(queryWrapper);
    // 删除采购合同表
    try {
      purcasingContractMapper.deleteById(id);
      logger.info("deleteOrderContract-删除采购合同表信息结束");
      // 物理删除合同表
      orderContractMapper.deleteById(entity.getContractId());
    } catch (Exception e) {
      throw new RuntimeException("删除采购合同失败，合同id为：" + id);
    }
    return ResultVOUtil.returnSuccess();
  }

  @Override
  public ResponseEnvelope deleteContractList(List<Long> ids) {
    try {
      for (Long id : ids) {
        deleteOrderContract(id);
      }
    } catch (Exception e) {
      throw new RuntimeException("批量删除采购合同失败，报错信息为：" + e.getMessage());
    }
    return ResultVOUtil.returnSuccess();
  }
}
