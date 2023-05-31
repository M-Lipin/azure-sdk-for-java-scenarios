package com.azure.cosmos.sdk.scenarios.helpers;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;

public class CosmosClientHelper {

    public static CosmosClient buildClient(Account account){
        return new CosmosClientBuilder()
                .endpoint(account.getHost())
                .key(account.getKey())
                .buildClient();
    }

    public static void closeConnectionAndCleanupResources(CosmosClient cosmosClient){

        var databasePropertySets = cosmosClient.readAllDatabases();

        for (var databasePropertySet:
                databasePropertySets) {
            var databaseId = databasePropertySet.getId();
            var database = cosmosClient.getDatabase(databaseId);
            database.delete();
        }

        cosmosClient.close();
    }
}
