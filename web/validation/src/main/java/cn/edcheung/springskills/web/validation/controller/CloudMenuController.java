package cn.edcheung.springskills.web.validation.controller;

import cn.edcheung.springskills.db.persistence.dto.MenuDto;
import cn.edcheung.springskills.web.validation.bean.PageBean;
import cn.edcheung.springskills.web.validation.bean.ResultBean;
import cn.edcheung.springskills.web.validation.bean.ResultBeanBuilder;
import cn.edcheung.springskills.web.validation.model.CloudMenu;
import cn.edcheung.springskills.web.validation.service.ICloudMenuService;
import cn.edcheung.springskills.web.validation.util.TreeUtil;
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

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description CloudMenuController
 *
 * @author Edward Cheung
 * @date 2020/11/17
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/cloud/1.0/menu")
// 如果使用单参数校验，controller类上必须添加@Validated注解
@Validated
public class CloudMenuController {

    /**
     * 菜单编码前缀
     */
    private static final String MENU_CODE_PREFIX = "m:";

    @Autowired
    private ICloudMenuService cloudMenuService;

    @GetMapping("/list/{cloudCode}")
    public ResultBean<PageBean<MenuDto>> list(@NotBlank(message = "系统编码不能为空") @PathVariable String cloudCode,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false) String code,
                                              @RequestParam(required = false) Boolean status,
                                              @RequestParam(required = false, defaultValue = "0") boolean isDesc,
                                              @RequestParam(required = false, defaultValue = "1") int pageNum,
                                              @RequestParam(required = false, defaultValue = "10") int pageSize) {
        PageInfo<MenuDto> pageInfo = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> cloudMenuService.list(cloudCode, name, code, status, isDesc));
        return ResultBeanBuilder.success(new PageBean<>(pageInfo));
    }

    @PostMapping("/save")
    // 对象参数校验使用时，需要先在对象的校验属性上添加注解，然后在Controller方法的对象参数前添加@Validated 注解
    public ResultBean<?> save(@RequestBody @Validated CloudMenu menu) {
        // 设置菜单级别
        Long pid = menu.getPid();
        if (pid == null) {
            // pid为空则为一级菜单
            menu.setLevel(1);
        } else {
            // pid不为空则上级菜单级别加1
            cn.edcheung.springskills.db.persistence.entity.CloudMenu parentMenu = cloudMenuService.selectByPrimaryKey(pid);
            if (parentMenu == null) {
                return ResultBeanBuilder.error("上级菜单不存在");
            } else {
                menu.setLevel(parentMenu.getLevel() + 1);
                menu.setCloudCode(parentMenu.getCloudCode());
            }
        }
        // 菜单编码判断唯一
        String code = MENU_CODE_PREFIX + menu.getCode();
        menu.setCode(code);
        cn.edcheung.springskills.db.persistence.entity.CloudMenu cloudMenu = cloudMenuService.selectByCode(code);
        int result; // 数据库操作结果
        Date now = new Date();
        Long id = menu.getId();
        if (id == null) {
            // 插入
            if (cloudMenu != null) {
                return ResultBeanBuilder.error("菜单编码已存在");
            }
            menu.setStatus(Boolean.TRUE);
            menu.setIsDelete(Boolean.FALSE);
            menu.setGmtCreate(now);
            menu.setGmtModified(now);
            result = cloudMenuService.insert(menu);
        } else {
            // 更新
            if (!id.equals(cloudMenu.getId())) {
                return ResultBeanBuilder.error("菜单编码已存在");
            }
            // 不允许修改菜单编码和系统编码
            menu.setCode(null);
            menu.setCloudCode(null);
            menu.setGmtModified(now);
            result = cloudMenuService.updateByPrimaryKey(menu);
        }
        return result == 1 ? ResultBeanBuilder.success() : ResultBeanBuilder.error();
    }

    @GetMapping("/query/{id}")
    public ResultBean<cn.edcheung.springskills.db.persistence.entity.CloudMenu> query(@PathVariable Long id) {
        cn.edcheung.springskills.db.persistence.entity.CloudMenu menu = cloudMenuService.query(id);
        if (menu != null) {
            // 查询时去掉菜单编码前缀
            menu.setCode(menu.getCode().substring(MENU_CODE_PREFIX.length()));
        }
        return ResultBeanBuilder.success(menu);
    }

    @PostMapping("/delete")
    public ResultBean<?> delete(@RequestBody Set<Long> idSet) {
        boolean result = cloudMenuService.deleteBatchByIdSet(idSet);
        return result ? ResultBeanBuilder.success() : ResultBeanBuilder.error();
    }

    @PostMapping("/updateStatus/{id}")
    public ResultBean<?> updateStatus(@PathVariable Long id) {
        // TODO 是否修改下属功能状态
        int result = cloudMenuService.updateStatus(id);
        return result == 1 ? ResultBeanBuilder.success() : ResultBeanBuilder.error();
    }

    @GetMapping("/tree/{cloudCode}")
    public ResultBean<List<Map<String, Object>>> tree(@PathVariable String cloudCode) {
        List<Map<String, Object>> list = cloudMenuService.tree(cloudCode);
        if (list.size() > 0) {
            TreeUtil.listToTree(list, "id", "pid");
        }
        return ResultBeanBuilder.success(list);
    }
}
