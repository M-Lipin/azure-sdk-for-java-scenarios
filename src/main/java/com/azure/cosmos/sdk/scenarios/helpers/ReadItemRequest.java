package com.azure.cosmos.sdk.scenarios.helpers;

import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.PartitionKey;

public class ReadItemRequest {
    public ReadItemRequest(Class type, String itemId, String partitionKey) {
        this.type = type;
        this.itemId = itemId;
        this.partitionKey = new PartitionKey(partitionKey);
        this.requestOptions = new CosmosItemRequestOptions();
    }

    public ReadItemRequest(Class type, String itemId, String partitionKey, CosmosItemRequestOptions requestOptions) {
        this.type = type;
        this.itemId = itemId;
        this.partitionKey = new PartitionKey(partitionKey);
        this.requestOptions = requestOptions;
    }

    public String getItemId() {
        return itemId;
    }

    public PartitionKey getPartitionKey() {
        return partitionKey;
    }

    public CosmosItemRequestOptions getRequestOptions() {
        return requestOptions;
    }

    public Class getType() {
        return type;
    }

    private Class type;

    private String itemId;

    private PartitionKey partitionKey;

    private CosmosItemRequestOptions requestOptions;
}
