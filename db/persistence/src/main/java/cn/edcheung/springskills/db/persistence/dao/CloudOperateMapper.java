package cn.edcheung.springskills.db.persistence.dao;

import cn.edcheung.springskills.db.persistence.entity.CloudOperate;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface CloudOperateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CloudOperate record);

    int insertSelective(CloudOperate record);

    CloudOperate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CloudOperate record);

    int updateByPrimaryKey(CloudOperate record);

    /**
     * 通过编码获取菜单
     */
    CloudOperate selectByCode(@Param("code") String code);

    /**
     * 批量删除
     */
    int deleteBatchById(@Param("idSet") Set<Long> idSet);

    /**
     * 所有权限
     */
    List<CloudOperate> selectList(@Param("cloudCode") String cloudCode);

    /**
     * 分页
     */
    List<CloudOperate> list(@Param("cloudCode") String cloudCode,
                            @Param("menuCode") String menuCode,
                            @Param("menuName") String menuName,
                            @Param("operateName") String operateName,
                            @Param("operateUrl") String operateUrl,
                            @Param("operateCode") String operateCode,
                            @Param("operateStatus") Boolean operateStatus,
                            @Param("isDesc") boolean isDesc);

    /**
     * 查询
     */
    CloudOperate query(@Param("id") Long id);

    /**
     * 修改状态
     */
    int updateStatus(@Param("id") Long id);

    /**
     * 根据系统编码删除菜单操作
     */
    int deleteByCloudCode(String cloudCode);

    /**
     * 根据菜单id批量删除菜单操作
     */
    int deleteBatchByMenuIds(@Param("menuIdSet") Set<Long> menuIdSet);
}