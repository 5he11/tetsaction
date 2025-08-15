package com.frenzy.core.service;

import cn.hutool.core.collection.CollectionUtil;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

@Service
public abstract class AbstractCosmosService<T> {

    @Autowired
    private CosmosContainer cosmosContainer;

    // 存储实体类的类型，用于反射调用静态方法
    private final Class<T> entityClass;

    // 通过构造函数传递实体类类型
    public AbstractCosmosService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    // 插入数据
    public T save(T item) {
        CosmosItemResponse<T> response = cosmosContainer.createItem(item);
        return response.getItem();
    }

    // 查询数据
    public List<T> list(String query) {
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        //  Set populate query metrics to get metrics around query executions
        queryOptions.setQueryMetricsEnabled(true);
        return cosmosContainer.queryItems(query, queryOptions, entityClass).stream().toList();
    }

    // 查询数据
    public T getOne(String query) {
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        //  Set populate query metrics to get metrics around query executions
        queryOptions.setQueryMetricsEnabled(true);
        List<T> list = cosmosContainer.queryItems(query, queryOptions, entityClass).stream().toList();
        if (CollectionUtil.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    // 根据 ID 查询单个数据
    public <T> T getById(String id) {
        try{
            return (T) cosmosContainer.readItem(id, new PartitionKey(this.getPartitionKeyFromItem()), entityClass).getItem();
        }catch (Exception e){
            return null;
        }
    }

    // 删除数据，使用 CosmosItemRequestOptions 来传递请求选项
    public void delete(String id) {
        // 删除数据项
        cosmosContainer.deleteItem(id, new PartitionKey(this.getPartitionKeyFromItem()), new CosmosItemRequestOptions());
    }

    // 更新数据
    public <T> T update(T updatedItem) {
        try {
            Method method = this.getIdMethodFromItem();
            if (method != null) {
                // 执行方法
                String id = (String) method.invoke(updatedItem);
                String partitionKey = this.getPartitionKeyFromItem();
                CosmosItemResponse<T> response = cosmosContainer.replaceItem(updatedItem, id, new PartitionKey(partitionKey), new CosmosItemRequestOptions());
                return response.getItem();
            }
        }catch (Exception e){
            throw new RuntimeException("Failed from item: " + e.getMessage(), e);
        }
        return null;
    }

    public String getListStringInSql(List<String> list){
        return " (" +
                String.join(",", list.stream()
                        .map(id -> "'" + id + "'")
                        .toArray(String[]::new)) +
                ")";
    }


    public String getPartitionKeyFromItem() {
        try {
            // 获取实体类的静态方法 getPartitionKey
            if (entityClass == null) {
                throw new IllegalArgumentException("Entity class cannot be null");
            }

            // 确保反射前，实体类已经正确初始化
            Object entityInstance = entityClass.getDeclaredConstructor().newInstance();

            Method method = null;
            try {
                // 使用反射获取getPartitionKey方法
                method = entityClass.getMethod("getPartitionKey");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                // Handle the case where the method does not exist
            }

            if (method != null) {
                // 执行方法
                String partitionKey = (String) method.invoke(entityInstance);
                return partitionKey;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get getPartitionKey from item: " + e.getMessage(), e);
        }
        return "";
    }


    public Method getIdMethodFromItem() {
        try {
            // 获取实体类的静态方法 getPartitionKey
            if (entityClass == null) {
                throw new IllegalArgumentException("Entity class cannot be null");
            }

            Method method = null;
            try {
                // 使用反射获取getPartitionKey方法
                method = entityClass.getMethod("getId");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                // Handle the case where the method does not exist
            }

            return method;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get id from item: " + e.getMessage(), e);
        }
    }


}
