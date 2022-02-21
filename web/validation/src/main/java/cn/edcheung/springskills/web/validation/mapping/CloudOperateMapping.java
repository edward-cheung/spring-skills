package cn.edcheung.springskills.web.validation.mapping;

import cn.edcheung.springskills.web.validation.model.CloudOperate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CloudOperateMapping extends BaseMapping<CloudOperate, cn.edcheung.springskills.db.persistence.entity.CloudOperate> {
}
