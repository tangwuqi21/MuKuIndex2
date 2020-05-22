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

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OrderContractController.class);


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
        queryWrapper.in("ID", paramStr);
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
            OrderContractVO at = OrderContractVO.builder().attachmentList(iOrderAttachmentService.selectAttachmentList(a.getId()))
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
        PurcasingContract model = purcasingContractMapper.selectById(id);
        OrgUserDto userDto = commonService.getOrgUserById(model.getOrgId(), model.getCreateBy());
        OrderContract orderContract=this.selectOne(model.getContractId());
        BeanCopyUtil.copyPropertiesIgnoreNull(orderContract, orderContractVO);
        orderContractVO.setContractCompany(model.getContractCompany());
        orderContractVO.setAttachmentList(attachmentList);
        orderContractVO.setCreateName(userDto.getUserInfo().getName());
        orderContractVO.setDeptName(userDto.getGroupName());
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
            orderContractMapper.insertOrderContract(entity.getId(), dto.getContractCompany(), entity.getCreateBy(), entity.getOrgId());
            logger.info("addAttachment-添加合同附件信息开始");
            for (OrderAttachmentDTO model : dto.getAttachmentList()) {
                model.setParentId(entity.getId());
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
        OrderContract entity = this.selectOne(dto.getId());
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        entity.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
        orderContractMapper.updateById(entity);
        orderContractMapper.updateContract(dto.getId(), dto.getContractCompany(), TokenUtil.getUserInfo().getUserId(), entity.getOrgId());
        //这里进行合同附件的批量新增操作
        if (CollectionUtils.isEmpty(dto.getAttachmentList())) {
            return ResultVOUtil.returnFail(ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
        }
        logger.info("updateAttachment-修改合同附件信息开始");
        for (OrderAttachmentDTO model : dto.getAttachmentList()) {
            OrderAttachment orderAttachment = new OrderAttachment();
            model.setParentId(entity.getId());
            model.setAtttype(1);
            BeanCopyUtil.copyPropertiesIgnoreNull(model, orderAttachment);
            if (model.getId() != null) {
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
        List<Long> paramStr = orderContractMapper.getContractIdList(dto.getContractCompany());
        queryWrapper.in("ID", paramStr);
        dto.setContractCompany(null);
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        queryWrapper.setEntity(entity);
        List<OrderContract> resultList = orderContractMapper.selectList(queryWrapper);
        List<OrderContractVO> contractVOList = resultList.stream().map(a -> {
            //根据合同id去附件表里获取每个合同对应的附件
            OrgUserDto userDto = commonService.getOrgUserById(a.getOrgId(), a.getCreateBy());
            OrderContractVO at = OrderContractVO.builder().haveFile(iOrderAttachmentService.selectAttachmentList(a.getId()).size() > 0 ? "是" : "否")
                    .contractCode(a.getContractCode()).contractCompany(orderContractMapper.selectContractByCId(a.getId()).getContractCompany())
                    .contractName(a.getContractName())
                    .contractDate(a.getContractDate()).contractMoney(a.getContractMoney()).id(a.getId()).contractTypeName(a.getContractType() == 1 ? "采购合同" : "")
                    .createDate(a.getCreateDate())
                    .createName(userDto.getUserInfo().getName()).deptName(userDto.getGroupName()).build();
            return at;
        }).collect(Collectors.toList());
        return contractVOList;
    }

    @Override
    public ResponseEnvelope deleteOrderContract(Long id) {
        //物理删除送货明细附件表
        orderAttachmentMapper.deleteAttachmentByParentId(id, 1L);
        PurcasingContract entity = new PurcasingContract();
        entity.setId(id);
        QueryWrapper<PurcasingContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        entity = purcasingContractMapper.selectOne(queryWrapper);
        //删除采购合同表
        purcasingContractMapper.deleteById(id);
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
