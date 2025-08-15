//package com.frenzy;
//
//
//import cn.hutool.core.lang.UUID;
//import com.frenzy.core.enums.EnumYesNoType;
//import com.frenzy.core.utils.SecurityUtils;
//import com.frenzy.core.utils.YFLocalDateTimeUtil;
//import com.frenzy.sso.domain.*;
//import com.frenzy.sso.service.*;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//@SpringBootTest
//@Slf4j
//public class SysInit {
//
//    @Autowired
//    private SsoUserService userService;
//    @Autowired
//    private SsoMenuService menuService;
//    @Autowired
//    private SsoConfigService configService;
//    @Autowired
//    private SsoRoleService roleService;
//    @Autowired
//    private SsoPostService postService;
//    @Autowired
//    private SsoDeptService deptService;
//    @Autowired
//    private YfDictionaryGroupService dictionaryGroupService;
//
//
//
//    @Test
//    public void init(){
////        this.addUser();
////        this.addMenu();
////        this.addConfig();
//        this.addRole();
//        this.addPost();
//        this.addDept();
//        this.addDict();
//    }
//
//
//
//
//    @Test
//    public void addUser(){
//        SsoUser ssoUser = new SsoUser();
//        ssoUser.setNewValue();
//        ssoUser.setUserName("yknet");
//        ssoUser.setPassword(SecurityUtils.encryptPassword("123456"));
//        ssoUser.setCreateTime(YFLocalDateTimeUtil.now());
//        ssoUser.setFlagAdmin(EnumYesNoType.YES.getValue());
//        userService.save(ssoUser);
//    }
//
//    @Test
//    public void addMenu(){
//        String menuFormId = this.addMenuForm();
//        this.addMenuMenu(menuFormId);
//        this.addMenuDict(menuFormId);
//        this.addMenuForm(menuFormId);
//
//        String menuSysId = this.addMenuSystem();
//        this.addMenuSysConfig(menuSysId);
//        this.addMenuSysUser(menuSysId);
//        this.addMenuSysRole(menuSysId);
//        this.addMenuSysDept(menuSysId);
//        this.addMenuSysPost(menuSysId);
//
//    }
//
//
//    @Test
//    public void addConfig(){
//        SsoConfig ssoConfig = new SsoConfig();
//        ssoConfig.setConfigName("用户管理-账号初始密码");
//        ssoConfig.setConfigKey("sys.user.initPassword");
//        ssoConfig.setConfigValue("123456");
//        ssoConfig.setNewValue();
//        configService.save(ssoConfig);
//    }
//
//    @Test
//    public void addRole(){
//        SsoRole ssoRole = new SsoRole();
//        ssoRole.setRoleName("管理员");
//        ssoRole.setAdminFlag(EnumYesNoType.YES.getValue());
//        ssoRole.setRoleKey("admin");
//        ssoRole.setRoleSort("10");
//        ssoRole.setNewValue();
//        roleService.save(ssoRole);
//    }
//
//    @Test
//    public void addPost(){
//        SsoPost ssoPost = new SsoPost();
//        ssoPost.setPostName("职员");
//        ssoPost.setPostCode("WORKER");
//        ssoPost.setStatus(EnumYesNoType.YES.getValue());
//        ssoPost.setPostSort("10");
//        ssoPost.setNewValue();
//        postService.save(ssoPost);
//    }
//
//    @Test
//    public void addDept(){
//        SsoDept ssoDept = new SsoDept();
//        ssoDept.setDeptName("系统");
//        ssoDept.setParentId("0");
//        ssoDept.setAncestors("0");
//        ssoDept.setStatus(EnumYesNoType.YES.getValue());
//        ssoDept.setOrderNum("10");
//        ssoDept.setNewValue();
//        deptService.save(ssoDept);
//    }
//
//    @Test
//    public void addDict(){
//        List<YfDictionary> dictionaryList = new ArrayList<>();
//        YfDictionary nan = new YfDictionary();
//        nan.setId(UUID.fastUUID().toString());
//        nan.setDictKey("NanNv");
//        nan.setDictValue("NanNv_Nan");
//        nan.setShowValue("男");
//        dictionaryList.add(nan);
//
//        YfDictionary nv = new YfDictionary();
//        nv.setId(UUID.fastUUID().toString());
//        nv.setDictKey("NanNv");
//        nv.setDictValue("NanNv_Nv");
//        nv.setShowValue("女");
//        dictionaryList.add(nv);
//
//        YfDictionaryGroup dictionaryGroup = new YfDictionaryGroup();
//        dictionaryGroup.setNewValue();
//        dictionaryGroup.setDictKey("NanNv");
//        dictionaryGroup.setDictList(dictionaryList);
//        dictionaryGroupService.save(dictionaryGroup);
//    }
//
//
//
//
//
//
//
//    public String addMenuForm(){
//        SsoMenu ssoMenu = new SsoMenu();
//        ssoMenu.setNewValue();
//        ssoMenu.setMenuName("表单系统");
//        ssoMenu.setParentId("0");
//        ssoMenu.setOrderNum("10");
//        ssoMenu.setPath("fzcms");
//        ssoMenu.setIsFrame("1");
//        ssoMenu.setIsCache("0");
//        ssoMenu.setStatus("0");
//        ssoMenu.setMenuType("M");
//        ssoMenu.setIcon("code");
//        ssoMenu.setCreateTime(YFLocalDateTimeUtil.now());
//        menuService.save(ssoMenu);
//        return ssoMenu.getId();
//    }
//
//
//    public void addMenuMenu(String parentId){
//        SsoMenu ssoMenu = new SsoMenu();
//        ssoMenu.setNewValue();
//        ssoMenu.setMenuName("菜单管理");
//        ssoMenu.setParentId(parentId);
//        ssoMenu.setOrderNum("2");
//        ssoMenu.setPath("menu");
//        ssoMenu.setComponent("system/menu/index");
//        ssoMenu.setIsFrame("1");
//        ssoMenu.setIsCache("0");
//        ssoMenu.setStatus("0");
//        ssoMenu.setMenuType("C");
//        ssoMenu.setIcon("tree-table");
//        ssoMenu.setCreateTime(YFLocalDateTimeUtil.now());
//        menuService.save(ssoMenu);
//    }
//
//    public void addMenuDict(String parentId){
//        SsoMenu ssoMenu = new SsoMenu();
//        ssoMenu.setNewValue();
//        ssoMenu.setMenuName("字典管理");
//        ssoMenu.setParentId(parentId);
//        ssoMenu.setOrderNum("3");
//        ssoMenu.setPath("yfdict");
//        ssoMenu.setComponent("yfcms/dict/index");
//        ssoMenu.setIsFrame("1");
//        ssoMenu.setIsCache("0");
//        ssoMenu.setStatus("0");
//        ssoMenu.setMenuType("C");
//        ssoMenu.setIcon("dict");
//        ssoMenu.setCreateTime(YFLocalDateTimeUtil.now());
//        menuService.save(ssoMenu);
//    }
//
//    public void addMenuForm(String parentId){
//        SsoMenu ssoMenu = new SsoMenu();
//        ssoMenu.setNewValue();
//        ssoMenu.setMenuName("表单管理");
//        ssoMenu.setParentId(parentId);
//        ssoMenu.setOrderNum("1");
//        ssoMenu.setPath("form");
//        ssoMenu.setComponent("yfcms/form/index");
//        ssoMenu.setIsFrame("1");
//        ssoMenu.setIsCache("0");
//        ssoMenu.setStatus("0");
//        ssoMenu.setMenuType("C");
//        ssoMenu.setIcon("excel");
//        ssoMenu.setCreateTime(YFLocalDateTimeUtil.now());
//        menuService.save(ssoMenu);
//    }
//
//
//
//    public String addMenuSystem(){
//        SsoMenu ssoMenu = new SsoMenu();
//        ssoMenu.setNewValue();
//        ssoMenu.setMenuName("系统管理");
//        ssoMenu.setParentId("0");
//        ssoMenu.setOrderNum("20");
//        ssoMenu.setPath("sys");
//        ssoMenu.setIsFrame("1");
//        ssoMenu.setIsCache("0");
//        ssoMenu.setStatus("0");
//        ssoMenu.setMenuType("M");
//        ssoMenu.setIcon("system");
//        ssoMenu.setCreateTime(YFLocalDateTimeUtil.now());
//        menuService.save(ssoMenu);
//        return ssoMenu.getId();
//    }
//
//
//    public void addMenuSysUser(String parentId){
//        SsoMenu ssoMenu = new SsoMenu();
//        ssoMenu.setNewValue();
//        ssoMenu.setMenuName("用户管理");
//        ssoMenu.setParentId(parentId);
//        ssoMenu.setOrderNum("20");
//        ssoMenu.setPath("user");
//        ssoMenu.setComponent("system/user/index");
//        ssoMenu.setIsFrame("1");
//        ssoMenu.setIsCache("0");
//        ssoMenu.setStatus("0");
//        ssoMenu.setMenuType("C");
//        ssoMenu.setIcon("user");
//        ssoMenu.setCreateTime(YFLocalDateTimeUtil.now());
//        menuService.save(ssoMenu);
//    }
//
//    public void addMenuSysConfig(String parentId){
//        SsoMenu ssoMenu = new SsoMenu();
//        ssoMenu.setNewValue();
//        ssoMenu.setMenuName("参数设置");
//        ssoMenu.setParentId(parentId);
//        ssoMenu.setOrderNum("10");
//        ssoMenu.setPath("config");
//        ssoMenu.setComponent("system/config/index");
//        ssoMenu.setIsFrame("1");
//        ssoMenu.setIsCache("0");
//        ssoMenu.setStatus("0");
//        ssoMenu.setMenuType("C");
//        ssoMenu.setIcon("edit");
//        ssoMenu.setCreateTime(YFLocalDateTimeUtil.now());
//        menuService.save(ssoMenu);
//    }
//
//
//    public void addMenuSysRole(String parentId){
//        SsoMenu ssoMenu = new SsoMenu();
//        ssoMenu.setNewValue();
//        ssoMenu.setMenuName("角色管理");
//        ssoMenu.setParentId(parentId);
//        ssoMenu.setOrderNum("30");
//        ssoMenu.setPath("role");
//        ssoMenu.setComponent("system/role/index");
//        ssoMenu.setIsFrame("1");
//        ssoMenu.setIsCache("0");
//        ssoMenu.setStatus("0");
//        ssoMenu.setMenuType("C");
//        ssoMenu.setIcon("peoples");
//        ssoMenu.setCreateTime(YFLocalDateTimeUtil.now());
//        menuService.save(ssoMenu);
//    }
//
//
//    public void addMenuSysPost(String parentId){
//        SsoMenu ssoMenu = new SsoMenu();
//        ssoMenu.setNewValue();
//        ssoMenu.setMenuName("岗位管理");
//        ssoMenu.setParentId(parentId);
//        ssoMenu.setOrderNum("50");
//        ssoMenu.setPath("post");
//        ssoMenu.setComponent("system/post/index");
//        ssoMenu.setIsFrame("1");
//        ssoMenu.setIsCache("0");
//        ssoMenu.setStatus("0");
//        ssoMenu.setMenuType("C");
//        ssoMenu.setIcon("post");
//        ssoMenu.setCreateTime(YFLocalDateTimeUtil.now());
//        menuService.save(ssoMenu);
//    }
//
//
//    public void addMenuSysDept(String parentId){
//        SsoMenu ssoMenu = new SsoMenu();
//        ssoMenu.setNewValue();
//        ssoMenu.setMenuName("部门管理");
//        ssoMenu.setParentId(parentId);
//        ssoMenu.setOrderNum("40");
//        ssoMenu.setPath("dept");
//        ssoMenu.setComponent("system/dept/index");
//        ssoMenu.setIsFrame("1");
//        ssoMenu.setIsCache("0");
//        ssoMenu.setStatus("0");
//        ssoMenu.setMenuType("C");
//        ssoMenu.setIcon("tree");
//        ssoMenu.setCreateTime(YFLocalDateTimeUtil.now());
//        menuService.save(ssoMenu);
//    }
//
//
//
//}
