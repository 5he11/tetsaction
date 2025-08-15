package com.frenzy.sso.action;

import cn.hutool.core.lang.UUID;
import com.frenzy.core.constant.UserConstants;
import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.utils.StringUtils;
import com.frenzy.core.utils.ValidationUtil;
import com.frenzy.sso.domain.SsoMenu;
import com.frenzy.sso.entity.TreeSelect;
import com.frenzy.sso.service.SsoMenuService;
import com.frenzy.sso.service.SsoUserService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SsoMenuAction {

    public static final String API_PREFIX = "sso/menu";

    @Autowired
    private SsoMenuService menuService;
    @Autowired
    private SsoUserService userService;


    @ApiOperation(value = "获取菜单列表")
    @FunctionName("/menuTestAdd")
    public Object menuTestAdd(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/menuTestAdd", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        SsoMenu menu2000 = new SsoMenu();
        menu2000.setId(UUID.fastUUID().toString());
        menu2000.setMenuName("表单系统");
        menu2000.setParentId("0");
        menu2000.setOrderNum("0");
        menu2000.setPath("fzcms");
        menu2000.setMenuType("M");
        menu2000.setIcon("code");
        menu2000.setStatus("0");
        menuService.save(menu2000);

        SsoMenu menu = new SsoMenu();
        menu.setId(UUID.fastUUID().toString());
        menu.setMenuName("菜单管理");
        menu.setParentId(menu2000.getId());
        menu.setOrderNum("3");
        menu.setPath("menu");
        menu.setComponent("system/menu/index");
        menu.setMenuType("C");
        menu.setIcon("tree-table");
        menu.setStatus("0");
        menuService.save(menu);

        return AjaxResult.success(request);
    }





    @ApiOperation(value = "获取菜单列表")
    @FunctionName("ssoMenuList")
    public Object ssoMenuList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/list", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoMenu>> request,
            final ExecutionContext context) {
        SsoMenu menu = new SsoMenu();
        if (!request.getBody().isEmpty()){
            menu = request.getBody().get();
        }

        List<SsoMenu> menus = menuService.selectMenuList(menu, userService.getLoginUser(request));
        return AjaxResult.success(request, menus);
    }


    @ApiOperation(value = "根据菜单编号获取详细信息")
    @FunctionName("ssoMenuGetInfo")
    public Object ssoMenuGetInfo(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/info/{menuId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("menuId") String menuId, // 绑定路径参数
            final ExecutionContext context) {
        return AjaxResult.success(request, menuService.selectMenuById(menuId));
    }





    @ApiOperation(value = "获取菜单下拉树列表")
    @FunctionName("ssoMenuTreeselect")
    public Object ssoMenuTreeselect(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/treeselect", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
//        SsoMenu menu = request.getBody().get();
        SsoMenu menu = new SsoMenu();
        List<SsoMenu> menus = menuService.selectMenuList(menu, userService.getLoginUser(request));
        List<TreeSelect> res = menuService.buildMenuTreeSelect(menus);
        return AjaxResult.success(request, res);
    }



    @ApiOperation(value = "加载对应角色菜单列表树")
    @FunctionName("ssoMenuRoleMenuTreeselect")
    public Object ssoMenuRoleMenuTreeselect(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/roleMenuTreeselect/{roleId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("roleId") String roleId, // 绑定路径参数
            final ExecutionContext context) {
        List<SsoMenu> menus = menuService.selectMenuList(userService.getLoginUser(request));
        HashMap<String,Object> map = new HashMap<>();
        map.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        map.put("menus", menuService.buildMenuTreeSelect(menus));
        return AjaxResult.success(request, map);
    }




    @ApiOperation(value = "新增菜单")
    @FunctionName("ssoMenuAdd")
    public Object ssoMenuAdd(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/add", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoMenu>> request,
            final ExecutionContext context) {

        SsoMenu menu = request.getBody().get();
        ValidationUtil.validate(menu);

        if (!menuService.checkMenuNameUnique(menu))
        {
            return AjaxResult.error(request, "新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return AjaxResult.error( request,"新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menuService.insertMenu(menu);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "修改菜单")
    @FunctionName("ssoMenuEdit")
    public Object ssoMenuEdit(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/edit", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoMenu>> request,
            final ExecutionContext context) {

        SsoMenu menu = request.getBody().get();
        ValidationUtil.validate(menu);

        if (!menuService.checkMenuNameUnique(menu))
        {
            return AjaxResult.error(request, "修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return AjaxResult.error(request, "修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        else if (menu.getId().equals(menu.getParentId()))
        {
            return AjaxResult.error(request, "修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menuService.updateMenu(menu);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "删除菜单")
    @FunctionName("ssoMenuRemove")
    public Object ssoMenuRemove(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/{menuId}", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("menuId") String menuId, // 绑定路径参数
            final ExecutionContext context) {

        if (menuService.checkMenuExistRole(menuId))
        {
            return AjaxResult.error(request, "菜单已分配,不允许删除");
        }

        menuService.checkDelMenuGroup(menuId);
        menuService.delMenuGroup(menuId);
        menuService.deleteMenuById(menuId);

        return AjaxResult.success(request);
    }



}
