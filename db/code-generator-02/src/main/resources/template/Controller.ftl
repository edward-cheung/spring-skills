package ${package_controller};

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ${package_service}.${file_name}Service;

/**
*
* ${file_name}Controller
*
* @author author
* @created Create Time: ${date?string('yyyy-MM-dd hh:mm:ss')}
*/


@RestController
@RequestMapping("/${module_name}")
@CrossOrigin
@Api(description = "相关的api")
public class ${file_name}Controller {

@Autowired
public ${file_name}Service ${file_name?uncap_first}Service;

}