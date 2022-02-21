package ${package_impl};

import ${package_mapper}.${file_name}Mapper;
import ${package_service}.${file_name}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
*
* ${file_name}Impl
*
* @author author
* @created Create Time: ${date?string('yyyy-MM-dd hh:mm:ss')}
*/
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class ${file_name}Impl implements ${file_name}Service {
@Autowired
private ${file_name}Mapper ${file_name?uncap_first}Mapper;

}
