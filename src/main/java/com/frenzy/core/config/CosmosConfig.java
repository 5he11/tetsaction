package com.frenzy.core.config;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import com.azure.cosmos.models.ThroughputProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosmosConfig {

    @Value("${azure.cosmos.endpoint}")
    private String cosmosDbEndpoint;

    @Value("${azure.cosmos.key}")
    private String cosmosDbKey;

    @Value("${azure.cosmos.database}")
    private String databaseName;

    @Value("${azure.cosmos.container}")
    private String containerName;

    @Value("${azure.cosmos.partition-key-prefix}")
    private String partitionKeyPrefix;

    public static String staticPartitionKeyPrefix;

    @Bean
    public CosmosClient cosmosClient() {
        return new CosmosClientBuilder()
                .endpoint(cosmosDbEndpoint)
                .key(cosmosDbKey)
                .directMode()
                .consistencyLevel(ConsistencyLevel.SESSION)
                .buildClient();
    }

    @Bean
    public CosmosDatabase cosmosDatabase(CosmosClient cosmosClient) {
        CosmosDatabaseResponse databaseResponse = cosmosClient.createDatabaseIfNotExists(databaseName);
        return cosmosClient.getDatabase(databaseResponse.getProperties().getId());
    }

    @Bean
    public CosmosContainer cosmosContainer(CosmosDatabase cosmosDatabase) {
        //  Create container if not exists
        CosmosContainerProperties containerProperties =
                new CosmosContainerProperties(containerName, "/partitionKey");

        CosmosContainerResponse containerResponse = cosmosDatabase.createContainerIfNotExists(containerProperties);
        return cosmosDatabase.getContainer(containerResponse.getProperties().getId());
    }

    @PostConstruct
    public void init() {
        staticPartitionKeyPrefix = partitionKeyPrefix;
    }

    public static String getPartitionKeyPrefix() {
        return staticPartitionKeyPrefix;
    }

}
