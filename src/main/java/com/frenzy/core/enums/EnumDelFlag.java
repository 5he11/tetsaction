package com.frenzy.core.enums;

/**
 * @author yannkeynes
 * @description: 表单分组
 * @date 2021/2/16 12:13 下午
 */
public enum EnumDelFlag {

    NORMAL("0", "正常"),

    DEL("2", "已删除"),

    ;

    /**
     * 枚举值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;

    private EnumDelFlag(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 通过value获取Enum
     *
     * @param desc
     * @return
     */
    public static EnumDelFlag getEnumByDesc(String desc) {
        EnumDelFlag resultEnum = null;
        EnumDelFlag[] enumAry = EnumDelFlag.values();
        for (int i = 0; i < enumAry.length; i++) {
            if (enumAry[i].getDesc().equals(desc)) {
                resultEnum = enumAry[i];
                break;
            }
        }
        return resultEnum;
    }

    /**
     * 通过value获取Enum
     *
     * @param value
     * @return
     */
    public static EnumDelFlag getEnumByValue(String value) {
        EnumDelFlag resultEnum = null;
        EnumDelFlag[] enumAry = EnumDelFlag.values();
        for (int i = 0; i < enumAry.length; i++) {
            if (enumAry[i].getValue().equals(value)) {
                resultEnum = enumAry[i];
                break;
            }
        }
        return resultEnum;
    }

    /**
     * 获取描述
     *
     * @param value
     * @return
     */
    public static String getDesc(String value) {
        return EnumDelFlag.getEnumByValue(value).getDesc();
    }

    /**
     * 获取枚举值
     *
     * @param desc
     * @return
     */
    public static String getValueByDesc(String desc) {
        return EnumDelFlag.getEnumByDesc(desc).getValue();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
