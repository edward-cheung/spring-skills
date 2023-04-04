package cn.edcheung.springskills.web.validation.controller;

import cn.edcheung.springskills.web.validation.model.CloudOperate;
import cn.edcheung.springskills.web.validation.mvc.bean.PageBean;
import cn.edcheung.springskills.web.validation.mvc.bean.ResultBean;
import cn.edcheung.springskills.web.validation.mvc.bean.ResultBeanBuilder;
import cn.edcheung.springskills.web.validation.service.ICloudOperateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Set;

/**
 * Description CloudOperateController
 *
 * @author Edward Cheung
 * @date 2020/11/18
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/cloud/1.0/operate")
public class CloudOperateController {

    /**
     * 菜单操作编码前缀
     */
    private static final String OPERATE_CODE_PREFIX = "o:";

    @Autowired
    private ICloudOperateService cloudOperateService;

    @GetMapping("/list/{cloudCode}")
    public ResultBean<PageBean<CloudOperate>> list(@PathVariable String cloudCode,
                                                   @RequestParam(required = false) String menuCode,
                                                   @RequestParam(required = false) String menuName,
                                                   @RequestParam(required = false) String operateName,
                                                   @RequestParam(required = false) String operateUrl,
                                                   @RequestParam(required = false) String operateCode,
                                                   @RequestParam(required = false) Boolean operateStatus,
                                                   @RequestParam(required = false, defaultValue = "0") boolean isDesc,
                                                   @RequestParam(required = false, defaultValue = "1") int pageNum,
                                                   @RequestParam(required = false, defaultValue = "10") int pageSize) {
        PageInfo<CloudOperate> pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() ->
                cloudOperateService.list(cloudCode, menuCode, menuName, operateName, operateUrl, operateCode, operateStatus, isDesc));
        return ResultBeanBuilder.success(new PageBean<>(pageInfo));
    }

    @PostMapping("/save")
    public ResultBean<?> save(@RequestBody @Validated CloudOperate operate) {
        // 菜单操作编码判断唯一
        String code = OPERATE_CODE_PREFIX + operate.getCode();
        operate.setCode(code);
        cn.edcheung.springskills.db.persistence.entity.CloudOperate cloudOperate = cloudOperateService.selectByCode(code);
        int result; // 数据库操作结果
        Date now = new Date();
        Long id = operate.getId();
        if (id == null) {
            // 插入
            if (cloudOperate != null) {
                return ResultBeanBuilder.error("功能编码已存在");
            }
            operate.setStatus(Boolean.TRUE);
            operate.setIsDelete(Boolean.FALSE);
            operate.setGmtCreate(now);
            operate.setGmtModified(now);
            result = cloudOperateService.insert(operate);
        } else {
            // 更新
            if (!id.equals(cloudOperate.getId())) {
                return ResultBeanBuilder.error("功能编码已存在");
            }
            // 不允许修改菜单操作编码和系统编码
            operate.setCode(null);
            operate.setCloudCode(null);
            operate.setGmtModified(now);
            result = cloudOperateService.updateByPrimaryKey(operate);
        }
        return result == 1 ? ResultBeanBuilder.success() : ResultBeanBuilder.error();
    }

    @GetMapping("/query/{id}")
    public ResultBean<cn.edcheung.springskills.db.persistence.entity.CloudOperate> query(@PathVariable Long id) {
        cn.edcheung.springskills.db.persistence.entity.CloudOperate operate = cloudOperateService.query(id);
        if (operate != null) {
            // 查询时去掉菜单操作编码前缀
            operate.setCode(operate.getCode().substring(OPERATE_CODE_PREFIX.length()));
        }
        return ResultBeanBuilder.success(operate);
    }

    @PostMapping("/delete")
    public ResultBean<?> delete(@RequestBody Set<Long> idSet) {
        int result = cloudOperateService.deleteBatchById(idSet);
        return result > 0 ? ResultBeanBuilder.success() : ResultBeanBuilder.error();
    }

    @PostMapping("/updateStatus/{id}")
    public ResultBean<?> updateStatus(@PathVariable Long id) {
        int result = cloudOperateService.updateStatus(id);
        return result == 1 ? ResultBeanBuilder.success() : ResultBeanBuilder.error();
    }

}
