//package com.frenzy.sso.service.impl;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.lang.UUID;
//import cn.hutool.core.util.StrUtil;
//import com.frenzy.core.service.AbstractCosmosService;
//import com.frenzy.core.constant.Constants;
//import com.frenzy.core.constant.HttpStatus;
//import com.frenzy.core.enums.EnumYesNoType;
//import com.frenzy.core.exception.ServiceException;
//import com.frenzy.core.utils.StringUtils;
//import com.frenzy.sso.domain.SsoMenu;
//import com.frenzy.sso.domain.SsoRole;
//import com.frenzy.sso.domain.SsoUser;
//import com.frenzy.sso.service.ISsoMenuService;
//import com.frenzy.sso.service.SsoRoleService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//
///**
// * 冷暖大作战Service业务层处理
// *
// * @author yannkeynes
// * @date 2024-10-14
// */
//@Service
//@Component
//public class SsoMenuServiceImpl extends AbstractCosmosService<SsoMenu> implements ISsoMenuService
//{
//    public SsoMenuServiceImpl() {
//        super(SsoMenu.class);
//    }
//
//    @Autowired
//    private SsoRoleService roleService;
//
//
//    /**
//     * 根据用户查询系统菜单列表
//     *
//     * @param ssoUser 用户ID
//     * @return 菜单列表
//     */
//    @Override
//    public List<SsoMenu> selectMenuList(SsoUser ssoUser)
//    {
//        return selectMenuList(new SsoMenu(), ssoUser);
//    }
//
//    /**
//     * 查询系统菜单列表
//     *
//     * @param menu 菜单信息
//     * @return 菜单列表
//     */
//    @Override
//    public List<SsoMenu> selectMenuList(SsoMenu menu, SsoUser ssoUser)
//    {
//        // 管理员显示所有菜单信息
//        String sql = "select * from c where 1=1";
//        if (StrUtil.isNotEmpty(menu.getMenuName())){
//            sql = sql + "AND c.menuName like concat('%', '"+menu.getMenuName()+"', '%')";
//        }
//        if (StrUtil.isNotEmpty(menu.getMenuName())){
//            sql = sql + "AND c.visible = '"+menu.getMenuName()+"' ";
//        }
//        if (StrUtil.isNotEmpty(menu.getStatus())){
//            sql = sql + "AND c.status  = '"+menu.getStatus()+"' ";
//        }
//        return this.list(sql);
//    }
//
//    /**
//     * 根据用户ID查询权限
//     *
//     * @param ssoUser 用户ID
//     * @return 权限列表
//     */
//    @Override
//    public Set<String> selectMenuPermsByUserId(SsoUser ssoUser)
//    {
//        List<String> perms = menuMapper.selectMenuPermsByUserId(ssoUser);
//        Set<String> permsSet = new HashSet<>();
//        for (String perm : perms)
//        {
//            if (StringUtils.isNotEmpty(perm))
//            {
//                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
//            }
//        }
//        return permsSet;
//    }
//
//    /**
//     * 根据用户ID查询菜单
//     *
//     * @param ssoUser 用户名称
//     * @return 菜单列表
//     */
//    @Override
//    public List<SsoMenu> selectMenuTreeByUserId(SsoUser ssoUser)
//    {
//        List<SsoMenu> menus = null;
//        if (StrUtil.equals(ssoUser.getFlagAdmin(),EnumYesNoType.YES.getValue()))
//        {
//            menus = menuMapper.selectMenuTreeAll();
//        }
//        else
//        {
//            menus = menuMapper.selectMenuTreeByUserId(ssoUser);
//        }
//        return getChildPerms(menus, 0);
//    }
//
//    /**
//     * 根据角色ID查询菜单树信息
//     *
//     * @param roleId 角色ID
//     * @return 选中菜单列表
//     */
//    @Override
//    public List<Long> selectMenuListByRoleId(String roleId)
//    {
//        SsoRole role = roleService.equals(roleId);
//        return menuMapper.selectMenuListByRoleId(roleId, role.isMenuCheckStrictly());
//    }
//
//    /**
//     * 构建前端路由所需要的菜单
//     *
//     * @param menus 菜单列表
//     * @return 路由列表
//     */
//    @Override
//    public List<RouterVo> buildMenus(List<SsoMenu> menus)
//    {
//        List<RouterVo> routers = new LinkedList<RouterVo>();
//        for (SsoMenu menu : menus)
//        {
//            RouterVo router = new RouterVo();
//            router.setHidden("1".equals(menu.getVisible()));
//            router.setName(getRouteName(menu));
//            router.setPath(getRouterPath(menu));
//            router.setComponent(getComponent(menu));
//            router.setQuery(menu.getQuery());
//            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
//            List<SsoMenu> cMenus = menu.getChildren();
//            if (!cMenus.isEmpty() && cMenus.size() > 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType()))
//            {
//                router.setAlwaysShow(true);
//                router.setRedirect("noRedirect");
//                router.setChildren(buildMenus(cMenus));
//            }
//            else if (isMenuFrame(menu))
//            {
//                router.setMeta(null);
//                List<RouterVo> childrenList = new ArrayList<RouterVo>();
//                RouterVo children = new RouterVo();
//                children.setPath(menu.getPath());
//                children.setComponent(menu.getComponent());
//                children.setName(StringUtils.capitalize(menu.getPath()));
//                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
//                children.setQuery(menu.getQuery());
//                childrenList.add(children);
//                router.setChildren(childrenList);
//            }
//            else if (menu.getParentId().intValue() == 0 && isInnerLink(menu))
//            {
//                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
//                router.setPath("/inner");
//                List<RouterVo> childrenList = new ArrayList<RouterVo>();
//                RouterVo children = new RouterVo();
//                String routerPath = innerLinkReplaceEach(menu.getPath());
//                children.setPath(routerPath);
//                children.setComponent(UserConstants.INNER_LINK);
//                children.setName(StringUtils.capitalize(routerPath));
//                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
//                childrenList.add(children);
//                router.setChildren(childrenList);
//            }
//            routers.add(router);
//        }
//        return routers;
//    }
//
//    /**
//     * 构建前端所需要树结构
//     *
//     * @param menus 菜单列表
//     * @return 树结构列表
//     */
//    @Override
//    public List<SsoMenu> buildMenuTree(List<SsoMenu> menus)
//    {
//        List<SsoMenu> returnList = new ArrayList<SsoMenu>();
//        List<Long> tempList = new ArrayList<Long>();
//        for (SsoMenu dept : menus)
//        {
//            tempList.add(dept.getMenuId());
//        }
//        for (Iterator<SsoMenu> iterator = menus.iterator(); iterator.hasNext();)
//        {
//            SsoMenu menu = (SsoMenu) iterator.next();
//            // 如果是顶级节点, 遍历该父节点的所有子节点
//            if (!tempList.contains(menu.getParentId()))
//            {
//                recursionFn(menus, menu);
//                returnList.add(menu);
//            }
//        }
//        if (returnList.isEmpty())
//        {
//            returnList = menus;
//        }
//        return returnList;
//    }
//
//    /**
//     * 构建前端所需要下拉树结构
//     *
//     * @param menus 菜单列表
//     * @return 下拉树结构列表
//     */
//    @Override
//    public List<TreeSelect> buildMenuTreeSelect(List<SsoMenu> menus)
//    {
//        List<SsoMenu> menuTrees = buildMenuTree(menus);
//        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
//    }
//
//    /**
//     * 根据菜单ID查询信息
//     *
//     * @param menuId 菜单ID
//     * @return 菜单信息
//     */
//    @Override
//    public SsoMenu selectMenuById(String menuId)
//    {
//        return this.getOne("SELECT TOP 1 * FROM c where (c.children.id ='"+menuId+"' or c.id = '"+menuId+"') and  and c.partitionKey = '"+this.getPartitionKeyFromItem()+"'");
//    }
//
//    /**
//     * 根据菜单ID查询该菜单下子菜单列表
//     *
//     * @param menuId 菜单ID
//     * @return 菜单信息
//     */
//    @Override
//    public List<SsoMenu> selectChildMenuById(String menuId){
//        return this.selectMenuById(menuId).getChildren();
//    }
//
//    /**
//     * 是否存在菜单子节点
//     *
//     * @param menuId 菜单ID
//     * @return 结果
//     */
//    @Override
//    public boolean hasChildByMenuId(String menuId)
//    {
//        if (CollectionUtil.isNotEmpty(this.selectChildMenuById(menuId))){
//            return true;
//        }else{
//            return false;
//        }
//    }
//
//    /**
//     * 查询菜单使用数量
//     *
//     * @param menuId 菜单ID
//     * @return 结果
//     */
//    @Override
//    public boolean checkMenuExistRole(String menuId)
//    {
//        int result = roleMenuMapper.checkMenuExistRole(menuId);
//        return result > 0 ? true : false;
//    }
//
//    /**
//     * 新增保存菜单信息
//     *
//     * @param menu 菜单信息
//     * @return 结果
//     */
//    @Override
//    public SsoMenu insertMenu(SsoMenu menu)
//    {
//        menu.setId(UUID.fastUUID().toString());
//        this.save(menu);
//        return menu;
//    }
//
//    /**
//     * 修改保存菜单信息
//     *
//     * @param menu 菜单信息
//     * @return 结果
//     */
//    @Override
//    public SsoMenu updateMenu(SsoMenu menu)
//    {
//        return this.update(menu);
//    }
//
//    /**
//     * 删除菜单管理信息
//     *
//     * @param menuId 菜单ID
//     * @return 结果
//     */
//    @Override
//    public int deleteMenuById(Long menuId)
//    {
//        return menuMapper.deleteMenuById(menuId);
//    }
//
//    /**
//     * 校验菜单名称是否唯一
//     *
//     * @param menu 菜单信息
//     * @return 结果
//     */
//    @Override
//    public String checkMenuNameUnique(SsoMenu menu)
//    {
//        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
//        SsoMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
//        if (StringUtils.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue())
//        {
//            return UserConstants.NOT_UNIQUE;
//        }
//        return UserConstants.UNIQUE;
//    }
//
//    /**
//     * 获取路由名称
//     *
//     * @param menu 菜单信息
//     * @return 路由名称
//     */
//    public String getRouteName(SsoMenu menu)
//    {
//        String routerName = StringUtils.capitalize(menu.getPath());
//        // 非外链并且是一级目录（类型为目录）
//        if (isMenuFrame(menu))
//        {
//            routerName = StringUtils.EMPTY;
//        }
//        return routerName;
//    }
//
//    /**
//     * 获取路由地址
//     *
//     * @param menu 菜单信息
//     * @return 路由地址
//     */
//    public String getRouterPath(SsoMenu menu)
//    {
//        String routerPath = menu.getPath();
//        // 内链打开外网方式
//        if (menu.getParentId().intValue() != 0 && isInnerLink(menu))
//        {
//            routerPath = innerLinkReplaceEach(routerPath);
//        }
//        // 非外链并且是一级目录（类型为目录）
//        if (0 == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
//                && UserConstants.NO_FRAME.equals(menu.getIsFrame()))
//        {
//            routerPath = "/" + menu.getPath();
//        }
//        // 非外链并且是一级目录（类型为菜单）
//        else if (isMenuFrame(menu))
//        {
//            routerPath = "/";
//        }
//        return routerPath;
//    }
//
//    /**
//     * 获取组件信息
//     *
//     * @param menu 菜单信息
//     * @return 组件信息
//     */
//    public String getComponent(SsoMenu menu)
//    {
//        String component = UserConstants.LAYOUT;
//        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu))
//        {
//            component = menu.getComponent();
//        }
//        else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu))
//        {
//            component = UserConstants.INNER_LINK;
//        }
//        else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu))
//        {
//            component = UserConstants.PARENT_VIEW;
//        }
//        return component;
//    }
//
//    /**
//     * 是否为菜单内部跳转
//     *
//     * @param menu 菜单信息
//     * @return 结果
//     */
//    public boolean isMenuFrame(SsoMenu menu)
//    {
//        return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType())
//                && menu.getIsFrame().equals(UserConstants.NO_FRAME);
//    }
//
//    /**
//     * 是否为内链组件
//     *
//     * @param menu 菜单信息
//     * @return 结果
//     */
//    public boolean isInnerLink(SsoMenu menu)
//    {
//        return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StringUtils.ishttp(menu.getPath());
//    }
//
//    /**
//     * 是否为parent_view组件
//     *
//     * @param menu 菜单信息
//     * @return 结果
//     */
//    public boolean isParentView(SsoMenu menu)
//    {
//        return menu.getParentId().intValue() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
//    }
//
//    /**
//     * 根据父节点的ID获取所有子节点
//     *
//     * @param list 分类表
//     * @param parentId 传入的父节点ID
//     * @return String
//     */
//    public List<SsoMenu> getChildPerms(List<SsoMenu> list, int parentId)
//    {
//        List<SsoMenu> returnList = new ArrayList<SsoMenu>();
//        for (Iterator<SsoMenu> iterator = list.iterator(); iterator.hasNext();)
//        {
//            SsoMenu t = (SsoMenu) iterator.next();
//            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
//            if (t.getParentId() == parentId)
//            {
//                recursionFn(list, t);
//                returnList.add(t);
//            }
//        }
//        return returnList;
//    }
//
//    /**
//     * 递归列表
//     *
//     * @param list
//     * @param t
//     */
//    private void recursionFn(List<SsoMenu> list, SsoMenu t)
//    {
//        // 得到子节点列表
//        List<SsoMenu> childList = getChildList(list, t);
//        t.setChildren(childList);
//        for (SsoMenu tChild : childList)
//        {
//            if (hasChild(list, tChild))
//            {
//                recursionFn(list, tChild);
//            }
//        }
//    }
//
//    /**
//     * 得到子节点列表
//     */
//    private List<SsoMenu> getChildList(List<SsoMenu> list, SsoMenu t)
//    {
//        List<SsoMenu> tlist = new ArrayList<SsoMenu>();
//        Iterator<SsoMenu> it = list.iterator();
//        while (it.hasNext())
//        {
//            SsoMenu n = (SsoMenu) it.next();
//            if (n.getParentId().longValue() == t.getMenuId().longValue())
//            {
//                tlist.add(n);
//            }
//        }
//        return tlist;
//    }
//
//    /**
//     * 判断是否有子节点
//     */
//    private boolean hasChild(List<SsoMenu> list, SsoMenu t)
//    {
//        return getChildList(list, t).size() > 0 ? true : false;
//    }
//
//    /**
//     * 内链域名特殊字符替换
//     *
//     * @return
//     */
//    public String innerLinkReplaceEach(String path)
//    {
//        return StringUtils.replaceEach(path, new String[] { Constants.HTTP, Constants.HTTPS },
//                new String[] { "", "" });
//    }
//
//
//    @Override
//    public void checkDelMenuGroup(String menuId){
//        List<SsoMenu> childMenu = this.selectChildMenuById(menuId);
//        childMenu.forEach(SsoMenu -> {
//            if (this.checkMenuExistRole(menuId))
//            {
//                throw new ServiceException("菜单已分配,不允许删除", HttpStatus.NOT_FOUND);
//            }
//            this.checkDelMenuGroup(SsoMenu.getMenuId());
//        });
//    }
//
//    @Override
//    public void delMenuGroup(String menuId){
//        this.delete(menuId);
//    }
//
//
//}
