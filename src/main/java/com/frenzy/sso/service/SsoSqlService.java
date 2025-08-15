package com.frenzy.sso.service;

import cn.hutool.core.collection.CollectionUtil;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.frenzy.core.config.CosmosConfig;
import com.frenzy.core.entity.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 登录校验方法
 * 
 * @author yf
 */
@Slf4j
@Component
public class SsoSqlService
{

    @Autowired
    private CosmosContainer cosmosContainer;


    public List<PageData> getSqlList(String keyName, String fieldName, int num, String where){
        String sql = "select TOP "+num+" c.id,c."+fieldName+" as showValue from c where c.partitionKey = '"+ CosmosConfig.getPartitionKeyPrefix() + keyName+"'";
        sql = sql + where;

        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        //  Set populate query metrics to get metrics around query executions
        queryOptions.setQueryMetricsEnabled(true);
        return (List<PageData>)cosmosContainer.queryItems(sql, queryOptions, PageData.class).stream().toList();
    }


    public int getSqlListCount(String sql){

        CosmosQueryRequestOptions countOptions = new CosmosQueryRequestOptions();
//        countOptions.setMaxItemCount(1); // 仅返回计数结果

        CosmosPagedIterable<JsonNode> countResult = cosmosContainer.queryItems(
                sql,
                countOptions,
                JsonNode.class
        );
        int totalCount = countResult.iterator().next().asInt();

return totalCount;
    }


    public List<PageData> getSqlList(String sql){
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        queryOptions.setQueryMetricsEnabled(true);
        return cosmosContainer.queryItems(sql, queryOptions, PageData.class).stream().toList();
    }

    public PageData getSqlOne(String sql){
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        //  Set populate query metrics to get metrics around query executions
        queryOptions.setQueryMetricsEnabled(true);
        List<PageData> list = cosmosContainer.queryItems(sql, queryOptions, PageData.class).stream().toList();
        if (CollectionUtil.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }


    public PageData getSqlOne(String keyName, String id, String where){
        String sql = "select * from c where c.partitionKey = '"+ CosmosConfig.getPartitionKeyPrefix() + keyName+"' and c.id = '"+id+"'";
        sql = sql + where;

        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        //  Set populate query metrics to get metrics around query executions
        queryOptions.setQueryMetricsEnabled(true);
        List<PageData> list = cosmosContainer.queryItems(sql, queryOptions, PageData.class).stream().toList();
        if (CollectionUtil.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    public PageData getSqlOneByField(String keyName, String fieldName, String fieldValue){
        String sql = "select * from c where c.partitionKey = '"+ CosmosConfig.getPartitionKeyPrefix() + keyName+"' and c."+fieldName+" = '"+fieldValue+"'";

        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        //  Set populate query metrics to get metrics around query executions
        queryOptions.setQueryMetricsEnabled(true);
        List<PageData> list = cosmosContainer.queryItems(sql, queryOptions, PageData.class).stream().toList();
        if (CollectionUtil.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    // 插入数据
    public PageData save(PageData item) {
        CosmosItemResponse<PageData> response = cosmosContainer.createItem(item);
        return response.getItem();
    }

    // 更新数据
    public PageData update(PageData updatedItem) {
        try {
            String id = (String) updatedItem.get("id");
            String partitionKey = (String) updatedItem.get("partitionKey");
            CosmosItemResponse<PageData> response = cosmosContainer.replaceItem(updatedItem, id, new PartitionKey(partitionKey), new CosmosItemRequestOptions());
            return response.getItem();
        }catch (Exception e){
            throw new RuntimeException("Failed from item: " + e.getMessage(), e);
        }
    }




    // 删除数据，使用 CosmosItemRequestOptions 来传递请求选项
    public void delete(String id, String keyName) {
        // 删除数据项
        cosmosContainer.deleteItem(id, new PartitionKey(CosmosConfig.getPartitionKeyPrefix() + keyName), new CosmosItemRequestOptions());
    }

}
