package cn.edcheung.springskills.web.validation.service;

import cn.edcheung.springskills.db.persistence.dto.MenuDto;
import cn.edcheung.springskills.web.validation.model.CloudMenu;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ICloudMenuService {

    int deleteByPrimaryKey(Long id);

    int insert(CloudMenu record);

    int insertSelective(CloudMenu record);

    cn.edcheung.springskills.db.persistence.entity.CloudMenu selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CloudMenu record);

    int updateByPrimaryKey(CloudMenu record);

    /**
     * 通过编码获取菜单
     */
    cn.edcheung.springskills.db.persistence.entity.CloudMenu selectByCode(String code);

    /**
     * 批量删除
     */
    int deleteBatchById(Set<Long> idSet);

    /**
     * 所有菜单
     */
    List<cn.edcheung.springskills.db.persistence.entity.CloudMenu> selectList(String cloudCode);

    /**
     * 分页
     */
    List<MenuDto> list(String cloudCode, String name, String code, Boolean status, boolean isDesc);

    /**
     * 查询
     */
    cn.edcheung.springskills.db.persistence.entity.CloudMenu query(Long id);

    /**
     * 修改状态
     */
    int updateStatus(Long id);

    /**
     * 菜单树查询
     */
    List<Map<String, Object>> tree(String cloudCode);

    /**
     * 根据系统编码删除菜单
     */
    int deleteByCloudCode(String cloudCode);

    /**
     * 根据系统编码获取用户菜单权限
     */
    List<Map<String, Object>> getUserMenuList(String cloudCode);

    /**
     * 根据id批量删除菜单及其下属功能
     */
    boolean deleteBatchByIdSet(Set<Long> idSet);
}
