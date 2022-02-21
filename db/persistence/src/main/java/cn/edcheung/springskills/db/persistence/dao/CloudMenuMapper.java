package cn.edcheung.springskills.db.persistence.dao;

import cn.edcheung.springskills.db.persistence.dto.MenuDto;
import cn.edcheung.springskills.db.persistence.entity.CloudMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CloudMenuMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CloudMenu record);

    int insertSelective(CloudMenu record);

    CloudMenu selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CloudMenu record);

    int updateByPrimaryKey(CloudMenu record);

    /**
     * 通过编码获取菜单
     */
    CloudMenu selectByCode(@Param("code") String code);

    /**
     * 批量删除
     */
    int deleteBatchById(@Param("idSet") Set<Long> idSet);

    /**
     * 所有菜单
     */
    List<CloudMenu> selectList(@Param("cloudCode") String cloudCode);

    /**
     * 分页
     */
    List<MenuDto> list(@Param("cloudCode") String cloudCode, @Param("name") String name, @Param("code") String code,
                       @Param("status") Boolean status, @Param("isDesc") boolean isDesc);

    /**
     * 查询
     */
    CloudMenu query(@Param("id") Long id);

    /**
     * 修改状态
     */
    int updateStatus(@Param("id") Long id);

    /**
     * 菜单树查询
     */
    List<Map<String, Object>> tree(@Param("cloudCode") String cloudCode);

    /**
     * 根据系统编码删除菜单
     */
    int deleteByCloudCode(String cloudCode);

    /**
     * 根据系统编码获取用户菜单权限
     */
    List<Map<String, Object>> getUserMenuList(@Param("cloudCode") String cloudCode);
}