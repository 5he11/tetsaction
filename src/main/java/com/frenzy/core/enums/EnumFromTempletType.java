package com.frenzy.core.enums;

/**
 * @author yannkeynes
 * @description: 模板类型
 * @date 2021/2/16 12:13 下午
 */
public enum EnumFromTempletType {

    LIST("LIST", "列表"),
    ADD("ADD", "新增"),
    EDIT("EDIT", "修改"),
    INFO("INFO", "详情"),
    LIST_CARD("LIST_CARD", "卡片列表"),
    LIST_LEVEL("LIST_LEVEL", "层级列表"),
    ORDER_INFO("ORDER_INFO", "订单详情"),
    USER_INFO("USER_INFO", "用户详情"),

    ;
    /**
     * 枚举值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;

    private EnumFromTempletType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 通过value获取Enum
     *
     * @param desc
     * @return
     */
    public static EnumFromTempletType getEnum(String desc) {
        EnumFromTempletType resultEnum = null;
        EnumFromTempletType[] enumAry = EnumFromTempletType.values();
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
    public static EnumFromTempletType getEnum(int value) {
        EnumFromTempletType resultEnum = null;
        EnumFromTempletType[] enumAry = EnumFromTempletType.values();
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
    public static String getDesc(int value) {
        return EnumFromTempletType.getEnum(value).getDesc();
    }

    /**
     * 获取枚举值
     *
     * @param desc
     * @return
     */
    public static String getValueByDesc(String desc) {
        return EnumFromTempletType.getEnum(desc).getValue();
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
