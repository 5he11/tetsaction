package com.frenzy.sso.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.frenzy.core.domain.BaseCosmosDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 岗位表 sys_post
 * 
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SsoPost", description="")
public class SsoPost extends BaseCosmosDomain
{

    public SsoPost() {
        super();
    }


    @NotBlank(message = "岗位编码不能为空")
    @Size(min = 0, max = 64, message = "岗位编码长度不能超过64个字符")
    @ExcelProperty(value = "岗位编码")
    @ApiModelProperty(value = "岗位编码")
    private String postCode;


    @NotBlank(message = "岗位名称不能为空")
    @Size(min = 0, max = 50, message = "岗位名称长度不能超过50个字符")
    @ExcelProperty(value = "岗位名称")
    @ApiModelProperty(value = "岗位名称")
    private String postName;


    @NotBlank(message = "显示顺序不能为空")
    @ExcelProperty(value = "岗位排序")
    @ApiModelProperty(value = "岗位排序")
    private String postSort;


    @ExcelProperty(value = "状态")
    @ApiModelProperty(value = "状态")
    private String status;


    @ExcelProperty(value = "用户是否存在此岗位标识 默认不存在")
    @ApiModelProperty(value = "用户是否存在此岗位标识 默认不存在")
    private boolean flag = false;


}
