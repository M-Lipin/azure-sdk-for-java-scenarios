package com.azure.cosmos.sdk.scenarios.helpers;

import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.PartitionKey;

public class CreateItemRequest<T> {

    public CreateItemRequest(T item, String itemId, String partitionKey) {
        this.item = item;
        this.itemId = itemId;
        this.partitionKey = new PartitionKey(partitionKey);
        this.requestOptions = new CosmosItemRequestOptions();
    }

    public CreateItemRequest(T item, String itemId, String partitionKey, CosmosItemRequestOptions requestOptions) {
        this.item = item;
        this.itemId = itemId;
        this.partitionKey = new PartitionKey(partitionKey);
        this.requestOptions = requestOptions;
    }

    public T getItem() {
        return item;
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

    private T item;

    private String itemId;

    private PartitionKey partitionKey;

    private CosmosItemRequestOptions requestOptions;
}
