package cn.edcheung.springskills.web.validation.service.impl;

import cn.edcheung.springskills.db.persistence.dao.CloudOperateMapper;
import cn.edcheung.springskills.web.validation.mapping.CloudOperateMapping;
import cn.edcheung.springskills.web.validation.model.CloudOperate;
import cn.edcheung.springskills.web.validation.service.ICloudOperateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Description CloudOperateServiceImpl
 *
 * @author Edward Cheung
 * @date 2022/2/9
 * @since JDK 1.8
 */
@Service
public class CloudOperateServiceImpl implements ICloudOperateService {

    @Autowired
    private CloudOperateMapper operateMapper;

    @Autowired
    private CloudOperateMapping cloudOperateMapping;

    @Override
    public int deleteByPrimaryKey(Long id) {
        if (id != null) {
            return operateMapper.deleteByPrimaryKey(id);
        }
        return 0;
    }

    @Override
    public int insert(CloudOperate record) {
        if (record != null) {
            return operateMapper.insert(cloudOperateMapping.sourceToTarget(record));
        }
        return 0;
    }

    @Override
    public int insertSelective(CloudOperate record) {
        if (record != null) {
            return operateMapper.insertSelective(cloudOperateMapping.sourceToTarget(record));
        }
        return 0;
    }

    @Override
    public cn.edcheung.springskills.db.persistence.entity.CloudOperate selectByPrimaryKey(Long id) {
        if (id != null) {
            return operateMapper.selectByPrimaryKey(id);
        }
        return null;
    }

    @Override
    public int updateByPrimaryKeySelective(CloudOperate record) {
        if (record != null) {
            return operateMapper.updateByPrimaryKeySelective(cloudOperateMapping.sourceToTarget(record));
        }
        return 0;
    }

    @Override
    public int updateByPrimaryKey(CloudOperate record) {
        if (record != null) {
            return operateMapper.updateByPrimaryKey(cloudOperateMapping.sourceToTarget(record));
        }
        return 0;
    }

    @Override
    public cn.edcheung.springskills.db.persistence.entity.CloudOperate selectByCode(String code) {
        if (code != null && code.length() > 0) {
            return operateMapper.selectByCode(code);
        }
        return null;
    }

    @Override
    public int deleteBatchById(Set<Long> idSet) {
        if (idSet != null && idSet.size() > 0) {
            return operateMapper.deleteBatchById(idSet);
        }
        return 0;
    }

    @Override
    public List<cn.edcheung.springskills.db.persistence.entity.CloudOperate> selectList(String cloudCode) {
        if (cloudCode != null && cloudCode.length() > 0) {
            return operateMapper.selectList(cloudCode);
        }
        return Collections.emptyList();
    }

    @Override
    public List<cn.edcheung.springskills.db.persistence.entity.CloudOperate> list(String cloudCode,
                                                                                  String menuCode,
                                                                                  String menuName,
                                                                                  String operateName,
                                                                                  String operateUrl,
                                                                                  String operateCode,
                                                                                  Boolean operateStatus,
                                                                                  boolean isDesc) {
        return operateMapper.list(cloudCode, menuCode, menuName, operateName, operateUrl, operateCode, operateStatus, isDesc);
    }

    @Override
    public cn.edcheung.springskills.db.persistence.entity.CloudOperate query(Long id) {
        if (id != null) {
            return operateMapper.query(id);
        }
        return null;
    }

    @Override
    public int updateStatus(Long id) {
        if (id != null) {
            return operateMapper.updateStatus(id);
        }
        return 0;
    }

    @Override
    public int deleteByCloudCode(String cloudCode) {
        if (cloudCode != null && cloudCode.length() > 0) {
            return operateMapper.deleteByCloudCode(cloudCode);
        }
        return 0;
    }

    @Override
    public int deleteBatchByMenuIds(Set<Long> menuIdSet) {
        if (menuIdSet != null && menuIdSet.size() > 0) {
            return operateMapper.deleteBatchByMenuIds(menuIdSet);
        }
        return 0;
    }
}
