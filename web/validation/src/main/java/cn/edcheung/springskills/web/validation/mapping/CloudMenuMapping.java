package cn.edcheung.springskills.web.validation.mapping;

import cn.edcheung.springskills.web.validation.model.CloudMenu;
import org.mapstruct.Mapper;

/*+
 * 1. 直接使用
 * @Mapper
 * public interface CarMap {
 *     CarMap CAR_MAP = Mappers.getMapper(CarMap.class);
 * }
 *
 * 2. 整合Spring
 * 设置componentModel = "spring"，需要使用的地方直接通过@Resource注入即可
 * @Mapper(componentModel = "spring")
 * public interface CarMap {
 * ...
 * }
 */
@Mapper(componentModel = "spring")
public interface CloudMenuMapping extends BaseMapping<CloudMenu, cn.edcheung.springskills.db.persistence.entity.CloudMenu> {

//    @Mapping(target = "gender", source = "sex")
//    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
//    @Override
//    UserVo sourceToTarget(User var1);
//
//    @Mapping(target = "sex", source = "gender")
//    @Mapping(target = "password", ignore = true)
//    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
//    @Override
//    User targetToSource(UserVo var1);
//
//    default List<UserConfig> strConfigToListUserConfig(String config) {
//        return JSON.parseArray(config, UserConfig.class);
//    }
//
//    default String listUserConfigToStrConfig(List<UserConfig> list) {
//        return JSON.toJSONString(list);
//    }
}