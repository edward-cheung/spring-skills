package cn.edcheung.springskills.web.validation.service.impl;

import cn.edcheung.springskills.db.persistence.dao.CloudMenuMapper;
import cn.edcheung.springskills.db.persistence.dao.CloudOperateMapper;
import cn.edcheung.springskills.db.persistence.dto.MenuDto;
import cn.edcheung.springskills.web.validation.mapping.CloudMenuMapping;
import cn.edcheung.springskills.web.validation.model.CloudMenu;
import cn.edcheung.springskills.web.validation.service.ICloudMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description ICloudMenuService
 *
 * @author Edward Cheung
 * @date 2020/11/23
 * @since JDK 1.8
 */
@Service
public class CloudMenuServiceImpl implements ICloudMenuService {

    @Autowired
    private CloudMenuMapper menuMapper;

    @Autowired
    private CloudMenuMapping cloudMenuMapping;

    @Autowired
    private CloudOperateMapper operateMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        if (id != null) {
            return menuMapper.deleteByPrimaryKey(id);
        }
        return 0;
    }

    @Override
    public int insert(CloudMenu record) {
        if (record != null) {
            return menuMapper.insert(cloudMenuMapping.sourceToTarget(record));
        }
        return 0;
    }

    @Override
    public int insertSelective(CloudMenu record) {
        if (record != null) {
            return menuMapper.insertSelective(cloudMenuMapping.sourceToTarget(record));
        }
        return 0;
    }

    @Override
    public cn.edcheung.springskills.db.persistence.entity.CloudMenu selectByPrimaryKey(Long id) {
        if (id != null) {
            return menuMapper.selectByPrimaryKey(id);
        }
        return null;
    }

    @Override
    public int updateByPrimaryKeySelective(CloudMenu record) {
        if (record != null) {
            return menuMapper.updateByPrimaryKeySelective(cloudMenuMapping.sourceToTarget(record));
        }
        return 0;
    }

    @Override
    public int updateByPrimaryKey(CloudMenu record) {
        if (record != null) {
            return menuMapper.updateByPrimaryKey(cloudMenuMapping.sourceToTarget(record));
        }
        return 0;
    }

    @Override
    public cn.edcheung.springskills.db.persistence.entity.CloudMenu selectByCode(String code) {
        if (code != null && code.length() > 0) {
            return menuMapper.selectByCode(code);
        }
        return null;
    }

    @Override
    public int deleteBatchById(Set<Long> idSet) {
        if (idSet != null && idSet.size() > 0) {
            return menuMapper.deleteBatchById(idSet);
        }
        return 0;
    }

    @Override
    public List<cn.edcheung.springskills.db.persistence.entity.CloudMenu> selectList(String cloudCode) {
        if (cloudCode != null && cloudCode.length() > 0) {
            return menuMapper.selectList(cloudCode);
        }
        return Collections.emptyList();
    }

    @Override
    public List<MenuDto> list(String cloudCode, String name, String code, Boolean status, boolean isDesc) {
        return menuMapper.list(cloudCode, name, code, status, isDesc);
    }

    @Override
    public cn.edcheung.springskills.db.persistence.entity.CloudMenu query(Long id) {
        if (id != null) {
            return menuMapper.query(id);
        }
        return null;
    }

    @Override
    public int updateStatus(Long id) {
        if (id != null) {
            return menuMapper.updateStatus(id);
        }
        return 0;
    }

    @Override
    public List<Map<String, Object>> tree(String cloudCode) {
        if (cloudCode != null && cloudCode.length() > 0) {
            return menuMapper.tree(cloudCode);
        }
        return Collections.emptyList();
    }

    @Override
    public int deleteByCloudCode(String cloudCode) {
        if (cloudCode != null && cloudCode.length() > 0) {
            return menuMapper.deleteByCloudCode(cloudCode);
        }
        return 0;
    }

    @Override
    public List<Map<String, Object>> getUserMenuList(String cloudCode) {
        if (cloudCode != null && cloudCode.length() > 0) {
            return menuMapper.getUserMenuList(cloudCode);
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchByIdSet(Set<Long> idSet) {
        if (!CollectionUtils.isEmpty(idSet)) {
            // 删除菜单
            int result = menuMapper.deleteBatchById(idSet);
            if (result == 0) {
                return false;
            }
            // 删除下属菜单功能
            operateMapper.deleteBatchByMenuIds(idSet);
        }
        return true;
    }
}
