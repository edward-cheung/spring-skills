package cn.edcheung.springskills.web.validation.service;

import cn.edcheung.springskills.web.validation.model.CloudOperate;

import java.util.List;
import java.util.Set;

public interface ICloudOperateService {

    int deleteByPrimaryKey(Long id);

    int insert(CloudOperate record);

    int insertSelective(CloudOperate record);

    cn.edcheung.springskills.db.persistence.entity.CloudOperate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CloudOperate record);

    int updateByPrimaryKey(CloudOperate record);

    /**
     * 通过编码获取菜单
     */
    cn.edcheung.springskills.db.persistence.entity.CloudOperate selectByCode(String code);

    /**
     * 批量删除
     */
    int deleteBatchById(Set<Long> idSet);

    /**
     * 所有权限
     */
    List<cn.edcheung.springskills.db.persistence.entity.CloudOperate> selectList(String cloudCode);

    /**
     * 分页
     */
    List<cn.edcheung.springskills.db.persistence.entity.CloudOperate> list(String cloudCode,
                                                                           String menuCode,
                                                                           String menuName,
                                                                           String operateName,
                                                                           String operateUrl,
                                                                           String operateCode,
                                                                           Boolean operateStatus,
                                                                           boolean isDesc);

    /**
     * 查询
     */
    cn.edcheung.springskills.db.persistence.entity.CloudOperate query(Long id);

    /**
     * 修改状态
     */
    int updateStatus(Long id);

    /**
     * 根据系统编码删除菜单操作
     */
    int deleteByCloudCode(String cloudCode);

    /**
     * 根据菜单id批量删除菜单操作
     */
    int deleteBatchByMenuIds(Set<Long> menuIdSet);
}
