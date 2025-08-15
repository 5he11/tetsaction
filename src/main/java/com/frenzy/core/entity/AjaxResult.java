package com.frenzy.core.entity;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.frenzy.core.constant.HttpStatus;
import com.frenzy.sso.domain.SsoUser;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * 操作消息提醒
 * 
 * @author ruoyi
 */
public class AjaxResult extends HashMap<String, Object>
{
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    public static final String CODE_TAG = "code";

    /** 返回内容 */
    public static final String MSG_TAG = "msg";

    /** 数据对象 */
    public static final String DATA_TAG = "data";
    private static final Logger log = LoggerFactory.getLogger(AjaxResult.class);

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public AjaxResult()
    {
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     * 
     * @param code 状态码
     * @param msg 返回内容
     */
    public AjaxResult(int code, String msg)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     * 
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public AjaxResult(int code, String msg, Object data)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        super.put(DATA_TAG, data);
    }


    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult error(String msg)
    {
        return new AjaxResult(HttpStatus.ERROR, msg, null);
    }


    public static HttpResponseMessage res(HttpRequestMessage request, int code, String msg, Object data)
    {
        AjaxResult ajaxResult = new AjaxResult(code, msg, data);
        String json = JSON.toJSONString(ajaxResult, SerializerFeature.WriteMapNullValue);

        return request.createResponseBuilder(com.microsoft.azure.functions.HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(json)
                .build();
    }


    /**
     * 返回成功消息
     * 
     * @return 成功消息
     */
    public static HttpResponseMessage success(HttpRequestMessage request)
    {
        return AjaxResult.success(request, "操作成功");
    }

    /**
     * 返回成功数据
     * 
     * @return 成功消息
     */
    public static HttpResponseMessage success(HttpRequestMessage request, Object data)
    {
        return AjaxResult.success(request, "操作成功", data);
    }



    /**
     * 返回成功消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static HttpResponseMessage success(HttpRequestMessage request, String msg, Object data)
    {
        return res(request, HttpStatus.SUCCESS, msg, data);//new AjaxResult(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回错误消息
     * 
     * @return
     */
    public static HttpResponseMessage error(HttpRequestMessage request)
    {
        return AjaxResult.error(request, "操作失败");
    }

    /**
     * 返回错误消息
     * 
     * @param msg 返回内容
     * @return 警告消息
     */
    public static HttpResponseMessage error(HttpRequestMessage request, String msg)
    {
        return AjaxResult.error(request, msg, null);
    }

    /**
     * 返回错误消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static HttpResponseMessage error(HttpRequestMessage request, String msg, Object data)
    {
        return res(request, HttpStatus.ERROR, msg, data);//new AjaxResult(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     * 
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     */
    public static HttpResponseMessage error(HttpRequestMessage request, int code, String msg)
    {
        return res(request, code, msg, null);//new AjaxResult(code, msg, null);
    }

    /**
     * 方便链式调用
     *
     * @param key 键
     * @param value 值
     * @return 数据对象
     */
    @Override
    public AjaxResult put(String key, Object value)
    {
        super.put(key, value);
        return this;
    }


    public static TableDataInfo getDataTable(List<?> list, int total)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
//        PageInfo pageInfo = new PageInfo(list);
//        rspData.setTotal(pageInfo.getTotal());
        rspData.setTotal(total);
        return rspData;
    }

}
