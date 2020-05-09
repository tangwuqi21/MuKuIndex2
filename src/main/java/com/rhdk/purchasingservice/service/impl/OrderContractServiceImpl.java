package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.BeanCopyUtil;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.controller.OrderContractController;
import com.rhdk.purchasingservice.mapper.OrderContractMapper;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderContractDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderAttachment;
import com.rhdk.purchasingservice.pojo.entity.OrderContract;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import com.rhdk.purchasingservice.service.IOrderAttachmentService;
import com.rhdk.purchasingservice.service.IOrderContractService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
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

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OrderContractController.class);


    @Override
    public ResponseEnvelope searchOrderContractListPage(OrderContractDTO dto) {
        Page<OrderContract> page = new Page<OrderContract>();
        page.setSize(dto.getSize());
        page.setCurrent(dto.getCurrent());
        QueryWrapper<OrderContract> queryWrapper = new QueryWrapper<OrderContract>();
        OrderContract entity = new OrderContract();
        queryWrapper.orderByDesc("CREATE_DATE");
        if(!StringUtils.isEmpty(dto.getContractCompany())){
            String paramStr=dto.getContractCompany();
            queryWrapper.like("CONTRACT_COMPANY",paramStr);
            dto.setContractCompany(null);
        }
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        queryWrapper.setEntity(entity);
        page = orderContractMapper.selectPage(page, queryWrapper);
        List<OrderContract> resultList = page.getRecords();
        logger.info("getFileList-获取合同附件列表开始");
        List<OrderContractVO> contractVOList = resultList.stream().map(a -> {
            //根据合同id去附件表里获取每个合同对应的附件
            Map<String,String> userInfo=orderContractMapper.getUserNameByID(a.getCreateBy());
            OrderContractVO at = OrderContractVO.builder().attachmentList(iOrderAttachmentService.selectAttachmentList(a.getId()))
                    .contractCode(a.getContractCode()).contractCompany(a.getContractCompany()).contractName(a.getContractName())
                    .contractDate(a.getContractDate()).contractMoney(a.getContractMoney()).id(a.getId()).contractType(a.getContractType())
                    .createBy(a.getCreateBy()).createDate(a.getCreateDate()).updateBy(a.getUpdateBy()).updateDate(a.getUpdateDate())
                    .delFlag(a.getDelFlag()).createName(userInfo.get("userName")).deptName(userInfo.get("deptName")).build();
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
        OrderContractVO orderContractVO=new OrderContractVO();
        List<OrderAttachment> attachmentList=iOrderAttachmentService.selectAttachmentList(id);
        OrderContract model=this.selectOne(id);
        BeanCopyUtil.copyPropertiesIgnoreNull(model, orderContractVO);
        Map<String,String> userInfo=orderContractMapper.getUserNameByID(model.getCreateBy());
        orderContractVO.setAttachmentList(attachmentList);
        orderContractVO.setCreateName(userInfo.get("userName"));
        orderContractVO.setDeptName(userInfo.get("deptName"));
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
            orderContractMapper.insert(entity);
            logger.info("addContract-添加合同主体信息结束");
            //合同主体添加成功，进行上传文件的记录保存，并关联到对应合同主体
            logger.info("addAttachment-添加合同附件信息开始");
            for (OrderAttachmentDTO model : dto.getAttachmentList()) {
                model.setParentId(entity.getId());
            }
            List<Long> fileList = iOrderAttachmentService.insertAttachmentListOfIdList(dto.getAttachmentList());
            logger.info("addAttachment-添加合同附件信息结束");
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
        orderContractMapper.updateById(entity);
        //修改合同附件信息需要注意的是，先要把之前的合同附件信息全部物理删除，然后再进行新的附件信息添加
        Integer updateRows = iOrderAttachmentService.deleteAttachmentByParentId(dto.getId());
        if (updateRows > 0) {
            //这里进行合同附件的批量新增操作
            if (CollectionUtils.isEmpty(dto.getAttachmentList())) {
                return ResultVOUtil.returnFail(ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
            }
            logger.info("addAttachment-添加合同附件信息开始");
            for (OrderAttachmentDTO model : dto.getAttachmentList()) {
                model.setParentId(entity.getId());
            }
            try {
                iOrderAttachmentService.insertAttachmentListOfIdList(dto.getAttachmentList());
            } catch (Exception e) {
                e.printStackTrace();
                return ResultVOUtil.returnFail();
            }
            logger.info("addAttachment-添加合同附件信息结束");
            return ResultVOUtil.returnSuccess();
        } else {
            return ResultVOUtil.returnFail();
        }
    }

    public OrderContract selectOne(Long id) {
        OrderContract entity = new OrderContract();
        entity.setId(id);
        QueryWrapper<OrderContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return orderContractMapper.selectOne(queryWrapper);
    }

}
