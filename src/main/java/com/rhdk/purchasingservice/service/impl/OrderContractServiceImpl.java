package com.rhdk.purchasingservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Sets;
import com.igen.acc.domain.dto.OrgUserDto;
import com.igen.acc.domain.dto.UserInfoDto;
import com.rhdk.purchasingservice.common.utils.BeanCopyUtil;
import com.rhdk.purchasingservice.common.utils.NumberUtils;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.mapper.ContractCustMapper;
import com.rhdk.purchasingservice.mapper.MotionMapper;
import com.rhdk.purchasingservice.mapper.OrderContractMapper;
import com.rhdk.purchasingservice.mapper.PurcasingContractMapper;
import com.rhdk.purchasingservice.pojo.dto.ContractCustDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderContractDTO;
import com.rhdk.purchasingservice.pojo.dto.QueryContractCustDTO;
import com.rhdk.purchasingservice.pojo.entity.ContractCust;
import com.rhdk.purchasingservice.pojo.entity.Motion;
import com.rhdk.purchasingservice.pojo.entity.OrderContract;
import com.rhdk.purchasingservice.pojo.entity.PurcasingContract;
import com.rhdk.purchasingservice.pojo.query.ContractCustQuery;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;
import com.rhdk.purchasingservice.pojo.vo.CustomerInfoVO;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import com.rhdk.purchasingservice.service.CommonService;
import com.rhdk.purchasingservice.service.IOrderContractService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

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

  @Autowired private MotionMapper motionMapper;

  @Autowired private ContractCustMapper contractCustMapper;

  private static org.slf4j.Logger logger = LoggerFactory.getLogger(OrderContractServiceImpl.class);

  @Override
  public IPage<OrderContractVO> searchOrderContractListPage(OrderContractQuery dto, Long orgId) {
    logger.info("searchOrderContractListPage-获取合同id列表开始");
    Page page = new Page();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    IPage<OrderContractVO> recordsList = null;
    dto.setOrgId(orgId);
    List<Long> paramStr = new ArrayList<>();
    if (dto.getCustId() != null) {
      paramStr = orderContractMapper.getContractIdByCust(dto);
    }
    logger.info("searchOrderContractListPage-获取合同id列表结束，获取了" + paramStr.size() + "条");
    if (paramStr.size() > 0) {
      dto.setContractIds(paramStr);
    }
    recordsList = orderContractMapper.selectContractList(page, dto, orgId);
    List<OrderContractVO> resultList = recordsList.getRecords();
    // 获取采购合同id
    if (CollectionUtils.isEmpty(resultList)) {
      return recordsList;
    }
    List<PurcasingContract> contractVOList = purcasingContractMapper.getOrderIds(resultList);
    Map<Long, Long> contractVOMap = listToMap2(contractVOList);
    // 一次取出该合同下的所有商业伙伴信息
    resultList.stream()
        .forEach(
            a -> {
              OrgUserDto userDto = commonService.getOrgUserById(a.getOrgId(), a.getCreateBy());
              // 封装参数调用资产服务查询合同伙伴姓名拼接
              ContractCust contractCust = new ContractCust();
              contractCust.setContractId(a.getId());
              QueryWrapper<ContractCust> queryWrapper = new QueryWrapper<>();
              queryWrapper.setEntity(contractCust);
              List<ContractCust> contractCusts = contractCust.selectList(queryWrapper);
              String company = "";
              if (!CollectionUtils.isEmpty(contractCusts)) {
                company = orderContractMapper.selectCustName(contractCusts);
              }
              if (!StringUtils.isEmpty(a.getContractMoney())) {
                a.setContractMoney(NumberUtils.fmtTwo(a.getContractMoney()));
              }
              a.setContractCompany(company);
              a.setId(contractVOMap.get(a.getId()));
              a.setContractTypeName(a.getContractType() == 1 ? "采购合同" : "其他");
              a.setCreateName(userDto.getUserInfo().getName());
              a.setDeptName(userDto.getGroupName());
              OrderAttachmentDTO attachmentDTO = new OrderAttachmentDTO();
              attachmentDTO.setParentId(a.getId());
              attachmentDTO.setAtttype(1);
              a.setAttachmentList(
                  assetServiceFeign.selectListByParentId(attachmentDTO, dto.getToken()).getData());
              // 获取议案信息
              if (!StringUtils.isEmpty(a.getMotionId())) {
                Motion entity2 = new Motion();
                entity2.setId(a.getMotionId());
                QueryWrapper<Motion> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.setEntity(entity2);
                Motion motion = motionMapper.selectOne(queryWrapper2);
                a.setMotionDate(motion.getMotionDate());
                a.setMotionName(motion.getMotionName());
                a.setMotionNo(motion.getMotionNo());
              }
            });
    logger.info("getFileList-获取合同附件列表结束");
    recordsList.setRecords(resultList);
    return recordsList;
  }

  public Map<Long, Long> listToMap2(List<PurcasingContract> contractVOList) {
    Map<Long, Long> result = new HashMap<>();
    contractVOList.forEach(
        temp -> {
          result.put(temp.getContractId(), temp.getId());
        });
    return result;
  }

  @Override
  public ResponseEnvelope searchOrderContractOne(Long id) {
    if (id == null) {
      throw new RuntimeException("获取详情，id不能为空！");
    }
    OrderContractVO orderContractVO = new OrderContractVO();
    PurcasingContract purcasingContract = purcasingContractMapper.selectById(id);
    // 获取合同基本信息
    OrderContract orderContract = orderContractMapper.selectById(purcasingContract.getContractId());
    BeanCopyUtil.copyPropertiesIgnoreNull(orderContract, orderContractVO);
    OrgUserDto userDto =
        commonService.getOrgUserById(orderContract.getOrgId(), orderContract.getCreateBy());
    orderContractVO.setCreateName(userDto.getUserInfo().getName());
    orderContractVO.setDeptName(userDto.getGroupName());
    orderContractVO.setId(id);
    OrderAttachmentDTO attachmentDTO = new OrderAttachmentDTO();
    attachmentDTO.setParentId(id);
    attachmentDTO.setAtttype(1);
    orderContractVO.setAttachmentList(
        assetServiceFeign.selectListByParentId(attachmentDTO, TokenUtil.getToken()).getData());
    // 获取议案基本信息
    if (!StringUtils.isEmpty(orderContract.getMotionId())) {
      Motion entity2 = new Motion();
      entity2.setId(orderContract.getMotionId());
      QueryWrapper<Motion> queryWrapper2 = new QueryWrapper<>();
      queryWrapper2.setEntity(entity2);
      Motion motion = motionMapper.selectOne(queryWrapper2);
      orderContractVO.setMotionDate(motion.getMotionDate());
      orderContractVO.setMotionName(motion.getMotionName());
      orderContractVO.setMotionNo(motion.getMotionNo());
    }
    // 查询项目经理名称
    UserInfoDto userInfo =
        commonService.getUserInfo(Long.valueOf(orderContract.getProjectManagerId()));
    orderContractVO.setProjectManagerName(userInfo.getUserInfo().getUsername());
    // 获取商业伙伴基本信息
    // 查询客户合同表构建fegin参数
    List<QueryContractCustDTO> qContracusts =
        orderContractMapper.selectParamByContractId(
            orderContract.getId(), TokenUtil.getUserInfo().getOrganizationId());
    // 调用feign接口查询客户信息
    ResponseEnvelope custInfoByCIdAndBId =
        assetServiceFeign.getCustInfoByCIdAndBId(TokenUtil.getToken(), qContracusts);
    List<Map> data = (List<Map>) custInfoByCIdAndBId.getData();
    // 遍历合作伙伴信息，添加到合同对象中
    List<CustomerInfoVO> customerInfoVOList = new ArrayList<>();
    data.forEach(
        tem -> {
          CustomerInfoVO customerInfoVO =
              JSON.parseObject(JSON.toJSONString(tem), CustomerInfoVO.class);
          customerInfoVO.setCustId(Long.valueOf(tem.get("id").toString()));
          customerInfoVO.setCusName(tem.get("cusName").toString());
          customerInfoVOList.add(customerInfoVO);
        });
    orderContractVO.setCustomerInfoVO(customerInfoVOList);
    return ResultVOUtil.returnSuccess(orderContractVO);
  }

  @Override
  @Transactional
  public ResponseEnvelope addOrderContract(OrderContractDTO dto) {
    OrderContract entity = new OrderContract();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    logger.info("addContract-添加合同主体信息开始");
    entity.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
    // 这里自动生成合同业务编码，规则为：HT+时间戳
    String code = NumberUtils.createCode("HT");
    entity.setContractCode(code);
    PurcasingContract purcasingContract = new PurcasingContract();
    try {
      orderContractMapper.insert(entity);
      logger.info("addContract-添加合同主体信息结束");
      // 合同主体添加成功，进行上传文件的记录保存，并关联到对应合同主体
      // 添加到采购合同表中
      purcasingContract.setContractId(entity.getId());
      // purcasingContract.setContractCompany(dto.getContractCompany());
      purcasingContract.setOrgId(entity.getOrgId());
      purcasingContractMapper.insert(purcasingContract);
    } catch (Exception e) {
      e.printStackTrace();
      return ResultVOUtil.returnFail();
    }
    logger.info("addAttachment-添加合同附件信息开始");
    if (!CollectionUtils.isEmpty(dto.getAttachmentList())) {
      for (OrderAttachmentDTO model : dto.getAttachmentList()) {
        model.setParentId(purcasingContract.getId());
        model.setAtttype(1);
      }
      try {
        assetServiceFeign.addBeatchAtta(dto.getAttachmentList(), TokenUtil.getToken()).getCode();
      } catch (Exception e) {
        throw new RuntimeException("保存附件出错，请稍后重试！");
      }
    }
    // 保存商业伙伴信息
    List<ContractCustDTO> contractCustList = dto.getContractCustList();
    // 遍历保存合同伙伴信息
    for (ContractCustDTO contractCust : contractCustList) {
      ContractCust contractCust1 = new ContractCust();
      BeanCopyUtil.copyPropertiesIgnoreNull(contractCust, contractCust1);
      contractCust1.setContractId(entity.getId());
      contractCust1.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
      contractCust1.insert();
    }
    logger.info("addAttachment-添加合同附件信息结束");
    return ResultVOUtil.returnSuccess();
  }

  @Override
  @Transactional
  @LcnTransaction
  public ResponseEnvelope updateOrderContract(OrderContractDTO dto) {
    if (dto.getId() == null) {
      throw new RuntimeException("修改合同详情，id不能为空！");
    }
    PurcasingContract model = purcasingContractMapper.selectById(dto.getId());
    // model.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
    // logger.info("updateAttachment-修改采购合同信息开始");
    // purcasingContractMapper.updateById(model);
    // logger.info("updateAttachment-修改采购合同信息结束");
    // logger.info("updateAttachment-修改关联合同主体信息开始");
    OrderContract orderContract = new OrderContract();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, orderContract);
    orderContract.setId(model.getContractId());
    orderContractMapper.updateById(orderContract);
    logger.info("updateAttachment-修改关联合同主体信息结束");
    // 这里进行合同附件的批量新增操作
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
    if (!CollectionUtils.isEmpty(dto.getAttachmentList())) {
      // 2.重新上传附件
      if (dto.getAttachmentList().size() > 0) {
        for (OrderAttachmentDTO model2 : dto.getAttachmentList()) {
          model2.setParentId(model.getId());
          model2.setAtttype(1);
        }
        assetServiceFeign.addBeatchAtta(dto.getAttachmentList(), TokenUtil.getToken()).getCode();
      }
    }
    logger.info("updateAttachment-修改合同附件信息结束");
    // 这里处理商业伙伴更新信息
    if (!CollectionUtils.isEmpty(dto.getContractCustList())) {
      dto.setContractId(model.getContractId());
      updateContractCust(dto);
    }

    return ResultVOUtil.returnSuccess();
  }

  // 这里对商业伙伴信息进行变更，已经存在的商业伙伴信息进行更新，未存在的商业伙伴进行删除，新增的进行插入
  public void updateContractCust(OrderContractDTO dto) {
    // 构建查询参数
    QueryWrapper<ContractCust> wrapper = new QueryWrapper<>();
    ContractCust contractCust = new ContractCust();
    Long orgId = TokenUtil.getUserInfo().getOrganizationId();
    contractCust.setOrgId(orgId);
    contractCust.setContractId(dto.getContractId());
    wrapper.setEntity(contractCust);
    // 根据合同id查询合同伙伴列表
    List<ContractCust> dbContractCusts = contractCust.selectList(wrapper);
    // 计算已保存的合同伙伴id
    Set<Long> custIdsOld = new HashSet<>();
    for (int i = 0; i < dbContractCusts.size(); i++) {
      custIdsOld.add(dbContractCusts.get(i).getCustId());
    }

    List<ContractCustDTO> paramContractCustList = dto.getContractCustList();
    // 计算预更新合同伙伴id
    Set<Long> custIdsNew = new HashSet<>();
    for (ContractCustDTO contractCustV1 : paramContractCustList) {
      custIdsNew.add(contractCustV1.getCustId());
    }

    // 查询出参数中存在,数据库中不存在的,进行插入
    Sets.SetView<Long> differenceInsert = Sets.difference(custIdsNew, custIdsOld);
    paramContractCustList.stream()
        .forEach(
            insert -> {
              if (differenceInsert.contains(insert.getCustId())) {
                ContractCust contractCustSave = new ContractCust();
                BeanCopyUtil.copyPropertiesIgnoreNull(insert, contractCustSave);
                contractCustSave.setContractId(dto.getContractId());
                contractCustSave.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
                contractCustSave.insert();
              }
            });

    // 查询出参数和数据库中都存在的，进行更新
    Sets.SetView<Long> intersectionUpdate = Sets.intersection(custIdsOld, custIdsNew);
    dbContractCusts.stream()
        .forEach(
            old -> {
              if (intersectionUpdate.contains(old.getCustId())) {
                // 需要更新
                paramContractCustList.stream()
                    .forEach(
                        update -> {
                          if (update.getCustId() == old.getCustId()) {
                            ContractCust contractCustUpdate = new ContractCust();
                            BeanCopyUtil.copyPropertiesIgnoreNull(update, contractCustUpdate);
                            contractCustUpdate.setId(old.getId());
                            contractCustUpdate.setContractId(dto.getContractId());
                            contractCustUpdate.setOrgId(
                                TokenUtil.getUserInfo().getOrganizationId());
                            contractCustUpdate.updateById();
                          }
                        });
              }
            });

    // 查询出数据库中存在,参数中不存在的,进行删除
    Sets.SetView<Long> difference = Sets.difference(custIdsOld, custIdsNew);
    difference.stream()
        .forEach(
            delete -> {
              ContractCust contractCustDel = new ContractCust();
              QueryWrapper<ContractCust> qeryWrapper = new QueryWrapper<>();
              contractCustDel.setCustId(delete.longValue());
              contractCustDel.setContractId(dto.getContractId());
              qeryWrapper.setEntity(contractCustDel);
              contractCustDel.delete(qeryWrapper);
            });
  }

  @Override
  public List<OrderContractVO> getContractInforList(OrderContractQuery dto, Long orgId) {
    List<OrderContractVO> contractVOList = new ArrayList<>();
    List<Long> paramStr = new ArrayList<>();
    if (dto.getCustId() != null) {
      paramStr = orderContractMapper.getContractIdByCust(dto);
    }
    if (paramStr.size() > 0) {
      dto.setContractIds(paramStr);
    }
    dto.setOrgId(orgId);
    contractVOList = orderContractMapper.selectContractList2(dto);
    List<PurcasingContract> purcasingContracts =
        purcasingContractMapper.getOrderIds(contractVOList);
    Map<Long, Long> contractVOMap = listToMap2(purcasingContracts);
    contractVOList.forEach(
        a -> {
          // 封装参数调用资产服务查询合同伙伴姓名拼接
          ContractCust contractCust = new ContractCust();
          contractCust.setContractId(a.getId());
          QueryWrapper<ContractCust> queryWrapper = new QueryWrapper<>();
          queryWrapper.setEntity(contractCust);
          List<ContractCust> contractCusts = contractCust.selectList(queryWrapper);
          String company = "";
          if (!CollectionUtils.isEmpty(contractCusts)) {
            company = orderContractMapper.selectCustName(contractCusts);
            a.setContractCompany(company);
          }
          // 根据合同id去附件表里获取每个合同对应的附件
          OrgUserDto userDto = commonService.getOrgUserById(a.getOrgId(), a.getCreateBy());
          a.setId(contractVOMap.get(a.getId()));
          OrderAttachmentDTO attachmentDTO = new OrderAttachmentDTO();
          attachmentDTO.setParentId(a.getId());
          attachmentDTO.setAtttype(1);
          List<Map<String, Object>> fileList =
              assetServiceFeign.selectListByParentId(attachmentDTO, dto.getToken()).getData();
          String haveFile = fileList.size() > 0 ? "是" : "否";
          a.setHaveFile(haveFile);
          a.setContractTypeName(a.getContractType() == 1 ? "采购合同" : "其他");
          if (userDto != null) {
            a.setCreateName(userDto.getUserInfo().getName());
            a.setDeptName(userDto.getGroupName());
          }
          if (!StringUtils.isEmpty(a.getContractMoney())) {
            a.setContractMoney(NumberUtils.fmtTwo(a.getContractMoney()));
          }
          // 获取议案信息
          if (!StringUtils.isEmpty(a.getMotionId())) {
            Motion entity2 = new Motion();
            entity2.setId(a.getMotionId());
            QueryWrapper<Motion> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.setEntity(entity2);
            Motion motion = motionMapper.selectOne(queryWrapper2);
            a.setMotionName(motion.getMotionName());
            a.setMotionNo(motion.getMotionNo());
          }
        });
    return contractVOList;
  }

  @Override
  @Transactional
  @LcnTransaction
  public ResponseEnvelope deleteOrderContract(Long id) {
    if (id == null) {
      throw new RuntimeException("删除合同记录，id不能为空！");
    }
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

  @Override
  public Page<CustomerInfoVO> searchContractCustListPage(ContractCustQuery dto) {
    PurcasingContract model = purcasingContractMapper.selectById(dto.getContractId());
    // 分页查询商业伙伴合同
    Page<ContractCust> page = new Page<ContractCust>();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    QueryWrapper<ContractCust> queryWrapper = new QueryWrapper<ContractCust>();
    ContractCust entity = new ContractCust();
    entity.setContractId(model.getContractId());
    queryWrapper.setEntity(entity);
    Page<ContractCust> contractCustPage = contractCustMapper.selectPage(page, queryWrapper);

    // 重新定义分页对象
    Page<CustomerInfoVO> customerInfoPages = new Page<>();
    customerInfoPages.setCurrent(page.getCurrent());
    customerInfoPages.setSize(page.getSize());
    customerInfoPages.setTotal(page.getTotal());
    List<CustomerInfoVO> customerinfoList = new ArrayList<>();

    // 遍历分页结果，构建feign参数
    List<ContractCust> records = contractCustPage.getRecords();
    List<QueryContractCustDTO> qContracusts = new ArrayList<>();
    records.stream()
        .forEach(
            temp -> {
              QueryContractCustDTO queryContractCustDTO = new QueryContractCustDTO();
              BeanCopyUtil.copyPropertiesIgnoreNull(temp, queryContractCustDTO);
              qContracusts.add(queryContractCustDTO);
            });

    // feign调用资产服务查询合作伙伴信息
    String token = TokenUtil.getToken();
    ResponseEnvelope custInfoByCIdAndBId =
        assetServiceFeign.getCustInfoByCIdAndBId(token, qContracusts);
    List<Map> data = (List<Map>) custInfoByCIdAndBId.getData();
    // 遍历合作伙伴信息，添加到新分页对象中
    data.forEach(
        tem -> {
          CustomerInfoVO customerInfoVO =
              JSON.parseObject(JSON.toJSONString(tem), CustomerInfoVO.class);
          customerInfoVO.setCustId(customerInfoVO.getId());
          // 将主键id赋给feign查询对象
          records.stream()
              .forEach(
                  dbtemp -> {
                    if (customerInfoVO.getId() == dbtemp.getCustId()) {
                      customerInfoVO.setId(dbtemp.getId());
                    }
                  });
          customerinfoList.add(customerInfoVO);
        });
    customerInfoPages.setRecords(customerinfoList);
    return customerInfoPages;
  }
}
