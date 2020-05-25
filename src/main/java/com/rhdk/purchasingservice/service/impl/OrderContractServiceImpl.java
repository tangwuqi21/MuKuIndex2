package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.igen.acc.domain.dto.OrgUserDto;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.BeanCopyUtil;
import com.rhdk.purchasingservice.common.utils.NumberUtils;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.controller.OrderContractController;
import com.rhdk.purchasingservice.mapper.OrderAttachmentMapper;
import com.rhdk.purchasingservice.mapper.OrderContractMapper;
import com.rhdk.purchasingservice.mapper.PurcasingContractMapper;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderContractDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderAttachment;
import com.rhdk.purchasingservice.pojo.entity.OrderContract;
import com.rhdk.purchasingservice.pojo.entity.PurcasingContract;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import com.rhdk.purchasingservice.service.CommonService;
import com.rhdk.purchasingservice.service.IOrderAttachmentService;
import com.rhdk.purchasingservice.service.IOrderContractService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 合同表 服务实现类
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Slf4j
@Transactional
@Service
public class OrderContractServiceImpl extends ServiceImpl<OrderContractMapper, OrderContract> implements IOrderContractService {


    @Autowired
    private OrderContractMapper orderContractMapper;

    @Autowired
    private IOrderAttachmentService iOrderAttachmentService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private OrderAttachmentMapper orderAttachmentMapper;

    @Autowired
    private PurcasingContractMapper purcasingContractMapper;

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OrderContractServiceImpl.class);


    @Override
    public ResponseEnvelope searchOrderContractListPage(OrderContractQuery dto) {
        Page<OrderContract> page = new Page<OrderContract>();
        page.setSize(dto.getPageSize());
        page.setCurrent(dto.getCurrentPage());
        QueryWrapper<OrderContract> queryWrapper = new QueryWrapper<OrderContract>();
        OrderContract entity = new OrderContract();
        queryWrapper.orderByDesc("CREATE_DATE");
        logger.info("searchOrderContractListPage-获取合同id列表开始");
        List<Long> paramStr = orderContractMapper.getContractIdList(dto.getContractCompany());
        logger.info("searchOrderContractListPage-获取合同id列表结束，获取了"+paramStr.size()+"条");
        if(paramStr.size()>0){
            queryWrapper.in("ID", paramStr);
        }
        dto.setContractCompany(null);
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        queryWrapper.setEntity(entity);
        page = orderContractMapper.selectPage(page, queryWrapper);
        List<OrderContract> resultList = page.getRecords();
        logger.info("getFileList-获取合同附件列表开始");
        List<OrderContractVO> contractVOList = resultList.stream().map(a -> {
            //根据合同id去附件表里获取每个合同对应的附件
            OrgUserDto userDto = commonService.getOrgUserById(a.getOrgId(), a.getCreateBy());
            OrderContractVO mo=orderContractMapper.selectContractByCId(a.getId());
            OrderContractVO at = OrderContractVO.builder().attachmentList(iOrderAttachmentService.selectAttachmentList(mo.getOrderId()))
                    .contractCode(a.getContractCode()).contractCompany(mo.getContractCompany()).contractName(a.getContractName())
                    .contractDate(a.getContractDate()).contractMoney(a.getContractMoney()).id(mo.getOrderId()).contractType(a.getContractType())
                    .createBy(a.getCreateBy()).createDate(a.getCreateDate()).updateBy(a.getUpdateBy()).updateDate(a.getUpdateDate())
                    .delFlag(a.getDelFlag()).createName(userDto.getUserInfo().getName()).deptName(userDto.getGroupName()).build();
            return at;
        }).collect(Collectors.toList());
        logger.info("getFileList-获取合同附件列表结束");
        Page<OrderContractVO> page2 = new Page<OrderContractVO>();
        page2.setRecords(contractVOList);/**/
        page2.setSize(page.getSize());
        page2.setCurrent(page.getCurrent());
        page2.setTotal(page.getTotal());
        page2.setOrders(page.getOrders());
        return ResultVOUtil.returnSuccess(page2);
    }

    @Override
    public ResponseEnvelope searchOrderContractOne(Long id) {
        OrderContractVO orderContractVO = new OrderContractVO();
        List<OrderAttachment> attachmentList = iOrderAttachmentService.selectAttachmentList(id);
        logger.info("searchOrderContractOne-获取采购合同信息开始");
        PurcasingContract model = purcasingContractMapper.selectById(id);
        logger.info("searchOrderContractOne-获取采购合同信息："+model.toString()+"结束");
        OrgUserDto userDto = commonService.getOrgUserById(model.getOrgId(), model.getCreateBy());
        logger.info("searchOrderContractOne-获取关联合同主体信息开始");
        OrderContract orderContract=this.selectOne(model.getContractId());
        logger.info("searchOrderContractOne-获取关联合同主体信息："+orderContract.toString()+"结束");
        BeanCopyUtil.copyPropertiesIgnoreNull(orderContract, orderContractVO);
        orderContractVO.setContractCompany(model.getContractCompany());
        orderContractVO.setAttachmentList(attachmentList);
        orderContractVO.setCreateName(userDto.getUserInfo().getName());
        orderContractVO.setDeptName(userDto.getGroupName());
        orderContractVO.setId(id);
        return ResultVOUtil.returnSuccess(orderContractVO);
    }

    @Override
    @Transactional
    public ResponseEnvelope addOrderContract(OrderContractDTO dto) {
        try {
            OrderContract entity = new OrderContract();
            if (CollectionUtils.isEmpty(dto.getAttachmentList())) {
                return ResultVOUtil.returnFail(ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
            }
            BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
            logger.info("addContract-添加合同主体信息开始");
            entity.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
            //这里自动生成合同业务编码，规则为：HT+时间戳
            String code = NumberUtils.createCode("HT");
            entity.setContractCode(code);
            orderContractMapper.insert(entity);
            logger.info("addContract-添加合同主体信息结束");
            //合同主体添加成功，进行上传文件的记录保存，并关联到对应合同主体
            //添加到采购合同表中
            PurcasingContract purcasingContract=new PurcasingContract();
            purcasingContract.setContractId(entity.getId());
            purcasingContract.setContractCompany(dto.getContractCompany());
            purcasingContract.setOrgId(entity.getOrgId());
            purcasingContractMapper.insert(purcasingContract);
           logger.info("addAttachment-添加合同附件信息开始");
            for (OrderAttachmentDTO model : dto.getAttachmentList()) {
                model.setParentId(purcasingContract.getId());
            }
            List<Long> fileList = iOrderAttachmentService.insertAttachmentListOfIdList(dto.getAttachmentList());
            logger.info("addAttachment-添加合同附件信息结束,附件添加了"+fileList.size()+"条");
            if (fileList.size() > 0) {
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
        OrderContract orderContract=new OrderContract();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, orderContract);
        orderContract.setId(model.getContractId());
        orderContractMapper.updateById(orderContract);
        logger.info("updateAttachment-修改关联合同主体信息结束");

        //这里进行合同附件的批量新增操作
        if (CollectionUtils.isEmpty(dto.getAttachmentList())) {
            return ResultVOUtil.returnFail(ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
        }
        logger.info("updateAttachment-修改合同附件信息开始");
        for (OrderAttachmentDTO model2 : dto.getAttachmentList()) {
            OrderAttachment orderAttachment = new OrderAttachment();
            model2.setParentId(model.getId());
            model2.setAtttype(1);
            BeanCopyUtil.copyPropertiesIgnoreNull(model2, orderAttachment);
            if (model2.getId() != null) {
                orderAttachmentMapper.updateById(orderAttachment);
            } else {
                orderAttachmentMapper.insert(orderAttachment);
            }
        }
        logger.info("updateAttachment-修改合同附件信息结束");
        return ResultVOUtil.returnSuccess();
    }


    @Override
    public List<OrderContractVO> getContractInforList(OrderContractDTO dto) {
        QueryWrapper<OrderContract> queryWrapper = new QueryWrapper<OrderContract>();
        OrderContract entity = new OrderContract();
        queryWrapper.orderByDesc("CREATE_DATE");
        logger.info("getContractInforList-获取导出合同主体id列表信息开始");
        List<Long> paramStr = orderContractMapper.getContractIdList(dto.getContractCompany());
        if(paramStr.size()>0){
            queryWrapper.in("ID", paramStr);
        }
        dto.setContractCompany(null);
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        queryWrapper.setEntity(entity);
        List<OrderContract> resultList = orderContractMapper.selectList(queryWrapper);
        logger.info("getContractInforList-获取导出合同主体id列表信息结束，获取了"+paramStr.size()+"条数据");
        List<OrderContractVO> contractVOList = resultList.stream().map(a -> {
            //根据合同id去附件表里获取每个合同对应的附件
            OrgUserDto userDto = commonService.getOrgUserById(a.getOrgId(), a.getCreateBy());
            OrderContractVO contractVO=orderContractMapper.selectContractByCId(a.getId());
            OrderContractVO at = OrderContractVO.builder().haveFile(iOrderAttachmentService.selectAttachmentList(a.getId()).size() > 0 ? "是" : "否")
                    .contractCode(a.getContractCode())
                    .contractCompany(contractVO.getContractCompany())
                    .contractName(a.getContractName())
                    .contractDate(a.getContractDate()).contractMoney(a.getContractMoney())
                    .id(contractVO.getOrderId()).contractTypeName(a.getContractType() == 1 ? "采购合同" : "")
                    .createDate(a.getCreateDate())
                    .createName(userDto.getUserInfo().getName()).deptName(userDto.getGroupName()).build();
            return at;
        }).collect(Collectors.toList());
        return contractVOList;
    }

    @Override
    public ResponseEnvelope deleteOrderContract(Long id) {
        //物理删除送货明细附件表
        logger.info("deleteOrderContract-删除附件表信息开始");
        orderAttachmentMapper.deleteAttachmentByParentId(id, 1L);
        logger.info("deleteOrderContract-删除附件表信息结束");
        PurcasingContract entity = new PurcasingContract();
        entity.setId(id);
        QueryWrapper<PurcasingContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        logger.info("deleteOrderContract-删除采购合同表信息开始");
        entity = purcasingContractMapper.selectOne(queryWrapper);
        //删除采购合同表
        purcasingContractMapper.deleteById(id);
        logger.info("deleteOrderContract-删除采购合同表信息结束");
        //物理删除合同表
        orderContractMapper.deleteById(entity.getContractId());
        return ResultVOUtil.returnSuccess();
    }

    @Override
    public ResponseEnvelope deleteContractList(List<Long> ids) {
        for (Long id : ids) {
            deleteOrderContract(id);
        }
        return ResultVOUtil.returnSuccess();
    }

    public OrderContract selectOne(Long id) {
        OrderContract entity = new OrderContract();
        entity.setId(id);
        QueryWrapper<OrderContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return orderContractMapper.selectOne(queryWrapper);
    }

}
