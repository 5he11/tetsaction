package com.frenzy.sso.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.frenzy.sso.domain.SsoDept;
import com.frenzy.sso.domain.SsoMenu;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类
 * 
 * @author ruoyi
 */
public class TreeSelect implements Serializable
{
    public static final long serialVersionUID = 1L;

    /** 节点ID */
    public String id;

    /** 节点名称 */
    public String label;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<TreeSelect> children;

    public TreeSelect()
    {

    }

    public TreeSelect(SsoDept dept)
    {
        this.id = dept.getId();
        this.label = dept.getDeptName();
        this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(SsoMenu menu)
    {
        this.id = menu.getId();
        this.label = menu.getMenuName();
        this.children = menu.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }


}
