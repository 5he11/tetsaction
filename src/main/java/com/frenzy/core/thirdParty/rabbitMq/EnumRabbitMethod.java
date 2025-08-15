package com.frenzy.core.thirdParty.rabbitMq;


/**
* @author yannkeynes
* @description: AuthStatus枚举
* 创建时间：2022-08-05
*/
public enum EnumRabbitMethod {

    RabbitMethod_test("RabbitMethod_test", "测试压力"),
    RabbitMethod_ems_print_order("RabbitMethod_ems_print_order", "ems打印面单"),

    ;

    /**
    * 枚举值
    */
    private String value;

    /**
    * 描述
    */
    private String desc;

    private EnumRabbitMethod(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
    * 通过value获取Enum
    *
    * @param desc
    * @return
    */
    public static EnumRabbitMethod getEnumByDesc(String desc) {
        EnumRabbitMethod resultEnum = null;
        EnumRabbitMethod[] enumAry = EnumRabbitMethod.values();
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
    public static EnumRabbitMethod getEnumByValue(String value) {
        EnumRabbitMethod resultEnum = null;
        EnumRabbitMethod[] enumAry = EnumRabbitMethod.values();
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
        return EnumRabbitMethod.getEnumByValue(value).getDesc();
    }

    /**
    * 获取枚举值
    *
    * @param desc
    * @return
    */
    public static String getValueByDesc(String desc) {
        return EnumRabbitMethod.getEnumByDesc(desc).getValue();
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
