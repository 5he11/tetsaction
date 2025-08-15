package com.frenzy.core.enums;

/**
 * @author yannkeynes
 * @description: 字段类型
 * @date 2021/2/16 12:13 下午
 */
public enum EnumFieldType {

    TextSignleLine("TextSignleLine", "单行文本(text)"),
    FormSelectSignle("FormSelectSignle", "表单下拉列表"),
    TextMutiLineNoHtml("TextMutiLineNoHtml", "多行文本(不支持HTML)"),
    NumberInt("NumberInt", "数字(int)"),
    NumberBigDecimal("NumberBigDecimal", "高精度数(BigDecimal)"),
    LocalDateTime("LocalDateTime", "日期时间(LocalDateTime)"),
    Authen("Authen", "是否选择器"),
    DictionaryRadio("DictionaryRadio", "字典单选框(radio)"),
    DictionaryCheckbox("DictionaryCheckbox", "字典复选框(checkbox)"),
    UpLoadFile("UpLoadFile", "七牛上传文件"),
    UpLoadPic("UpLoadPic", "七牛上传图片"),
    TextMutiLineUseHtml("TextMutiLineUseHtml", "多行文本(支持HTML)"),

    JSON("JSON", "JSON"),
    DictionarySelect("DictionarySelect", "字典下拉列表(select)"),
    DictionarySelectMutiTag("DictionarySelectMutiTag", "字典多标签(checkbox)"),
    FormInner("FormInner", "表单内联"),
    PasswordMd5("PasswordMd5", "密码(text)"),
    BaiduMapPoint("BaiduMapPoint", "百度地图坐标单个"),
    BaiduMapArea("BaiduMapArea", "百度地图坐标组面"),
    BaiduMapLine("BaiduMapLine", "百度地图坐标组线"),
    FormSelectMuti("FormSelectMuti", "表单多选"), //去掉
    TextSelectMutiTag("TextSelectMutiTag", "多标签(checkbox)"),
    ProvenceCityArea("ProvenceCityArea", "省市区联动"),
    FormSelectMutiTag("FormSelectMutiTag", "表单多标签"), //去掉
    SelectByLevel("SelectByLevel", "部门选择"),
    FromSelectMutiTable("FromSelectMutiTable", "表单多选列表"),
    QrCode("QrCode", "二维码(text)"),
    LocalDate("LocalDate", "日期(LocalDate)"),
    LocalTime("LocalTime", "时间(LocalTime)"),
    TextRadio("TextRadio", "单选框(radio)"),
    TextCheckbox("TextCheckbox", "复选框(checkbox)"),
    Email("Email", "电子邮箱(text)"), //去掉
    TextSelect("TextSelect", "下拉列表(select)"),
    NumberDouble("NumberDouble", "数字(Double)"),
    DateTime("DateTime", "日期时间(text)"),
    DateYYYYMMDD("DateYYYYMMDD", "日期(text)"),
    TimeOnly("TimeOnly", "时间(text)"),
    TimeStamp("TimeStamp", "时间戳(text)"),
    FromSelectMutiComponent("FromSelectMutiComponent", "表单多选组件"),

    CascadeSelect("CascadeSelect", "级联选择"),
    Slider("Slider", "滑块"),
    Grade("Grade", "评分"),
    Password("Password", "密码"),
    FormSelectSingleComponent("FormSelectSingleComponent", "表单单选组件"),
    ColorSelect("ColorSelect", "颜色选择"),
    AliUpLoadFile("AliUpLoadFile", "阿里云上传文件"),
    AliUpLoadPic("AliUpLoadPic", "阿里云上传图片"),
    TencentMapPoint("TencentMapPoint", "腾讯地图坐标单个"),
    TencentMapArea("TencentMapArea", "腾讯地图坐标组面"),
    TencentMapLine("TencentMapLine", "腾讯地图坐标组线"),
    ;

    /**
     * 枚举值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;

    private EnumFieldType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 通过value获取Enum
     *
     * @param desc
     * @return
     */
    public static EnumFieldType getEnum(String desc) {
        EnumFieldType resultEnum = null;
        EnumFieldType[] enumAry = EnumFieldType.values();
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
    public static EnumFieldType getEnum(int value) {
        EnumFieldType resultEnum = null;
        EnumFieldType[] enumAry = EnumFieldType.values();
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
        return EnumFieldType.getEnum(value).getDesc();
    }

    /**
     * 获取枚举值
     *
     * @param desc
     * @return
     */
    public static String getValueByDesc(String desc) {
        return EnumFieldType.getEnum(desc).getValue();
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
