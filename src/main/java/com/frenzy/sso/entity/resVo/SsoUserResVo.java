package com.frenzy.sso.entity.resVo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.frenzy.sso.domain.SsoDept;
import com.frenzy.sso.domain.SsoRole;
import com.frenzy.sso.domain.SsoUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户对象 sys_user
 * 
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SsoUserResVo", description="")
public class SsoUserResVo extends SsoUser
{

    @ExcelProperty(value = "部门对象")
    @ApiModelProperty(value = "部门对象")
    private SsoDept dept;

    @ExcelProperty(value = "角色对象")
    @ApiModelProperty(value = "角色对象")
    private List<SsoRole> roles;

}
