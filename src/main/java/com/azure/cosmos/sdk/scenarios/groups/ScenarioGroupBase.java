package com.azure.cosmos.sdk.scenarios.groups;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.*;
import com.azure.cosmos.sdk.scenarios.helpers.Account;
import com.azure.cosmos.sdk.scenarios.helpers.CosmosClientHelper;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public abstract class ScenarioGroupBase {
    protected final Marker scenarioGroupStartMarker = MarkerFactory.getMarker("SCENARIO-GROUP-START");
    protected final Marker scenarioStart = MarkerFactory.getMarker("SCENARIO-START");
    protected final Marker scenarioPreparationStep = MarkerFactory.getMarker("SCENARIO-PREPARATION-STEP");
    protected final Marker scenarioExecutionStep = MarkerFactory.getMarker("SCENARIO-EXECUTION-STEP");
    protected final Marker scenarioEnd = MarkerFactory.getMarker("SCENARIO-END");
    protected final Marker scenarioGroupEndMarker = MarkerFactory.getMarker("SCENARIO-GROUP-END");
    protected final Marker scenarioGroupErrorMarker = MarkerFactory.getMarker("SCENARIO-GROUP-ERROR");
    protected Account account;
    protected Logger logger;

    public ScenarioGroupBase(Account account) {
        this.account = account;
    }

    protected CosmosDatabaseResponse createDatabaseIfNotExists(CosmosClient cosmosClient, String databaseName) {
        logger.info(scenarioPreparationStep, String.format("Creating database with name = '%s'", databaseName));
        var response = cosmosClient.createDatabaseIfNotExists(databaseName);
        logger.info(scenarioPreparationStep, String.format("Database with name = '%s' is created", databaseName));
        return response;
    }

    protected CosmosContainerResponse createContainerIfNotExists(CosmosDatabase database, String containerName, String partitionKeyPath) {
        logger.info(scenarioPreparationStep, String.format("Creating container with name = '%s' in the database with name = '%s'", containerName, database.getId()));
        var response = database.createContainerIfNotExists(new CosmosContainerProperties(containerName, partitionKeyPath));
        logger.info(scenarioPreparationStep, String.format("Container with name = '%s' is created in the database with name = '%s'", containerName, database.getId()));
        return response;
    }

    protected void closeConnection(CosmosClient cosmosClient, String databaseName, Marker logMarker) {
        logger.info(logMarker, String.format("Closing connection to Cosmos Account", databaseName));
        cosmosClient.close();
        logger.info(logMarker, String.format("Connection to Cosmos Account is closed", databaseName));
    }

    protected void closeConnectionAndCleanupResources(CosmosClient cosmosClient, String databaseName, Marker logMarker) {
        logger.info(logMarker, String.format("Deleting the %s database and closing connection to Cosmos Account", databaseName));
        CosmosClientHelper.closeConnectionAndCleanupResources(cosmosClient);
        logger.info(logMarker, String.format("The %s database is deleted, connection to Cosmos Account is closed", databaseName));
    }

    protected static String cosmosItemRequestOptionsToString(CosmosItemRequestOptions options) {

        var stringBuilder = new StringBuilder();

        stringBuilder.append("\n");
        stringBuilder.append(String.format("\tConsistency level = %s\n", options.getConsistencyLevel()));
        stringBuilder.append("\tDedicated gateway request options:\n");
        stringBuilder.append(String.format("\t\tMax integrated cache staleness = %s\n", options.getDedicatedGatewayRequestOptions() != null ? options.getDedicatedGatewayRequestOptions().getMaxIntegratedCacheStaleness() : "null"));
        stringBuilder.append(String.format("\tContent response on write enabled = %b\n", options.isContentResponseOnWriteEnabled()));
        stringBuilder.append(String.format("\tIf-Match (ETag) = %s\n", options.getIfMatchETag()));
        stringBuilder.append(String.format("\tIf-None-Match (ETag) = %s\n", options.getIfNoneMatchETag()));
        stringBuilder.append(String.format("\tIndexingDirective = %s\n", options.getIndexingDirective()));

        stringBuilder.append("\tPostTriggerInclude:");

        if (options.getPostTriggerInclude() == null) {
            stringBuilder.append(" null\n");
        } else if (options.getPostTriggerInclude().size() == 0) {
            stringBuilder.append(" Empty\n");

        } else {
            stringBuilder.append("\n");
            for (int i = 0; i < options.getPostTriggerInclude().size(); i++) {
                var postTrigger = options.getPostTriggerInclude().get(i);
                stringBuilder.append(String.format("\t\tPost-trigger #%d: %s", i, postTrigger));
            }
        }

        stringBuilder.append("\tPreTriggerInclude:");

        if (options.getPreTriggerInclude() == null) {
            stringBuilder.append(" null\n");
        } else if (options.getPreTriggerInclude().size() == 0) {
            stringBuilder.append(" Empty\n");
        } else {
            stringBuilder.append("\n");
            for (int i = 0; i < options.getPreTriggerInclude().size(); i++) {
                var preTrigger = options.getPreTriggerInclude().get(i);
                stringBuilder.append(String.format("\t\tPre-trigger #%d: %s", i, preTrigger));
            }
        }

        stringBuilder.append(String.format("\tSession token = %s\n", options.getSessionToken()));
        stringBuilder.append(String.format("\tThreshold for diagnostics on tracer = %s\n", options.getThresholdForDiagnosticsOnTracer()));
        stringBuilder.append(String.format("\tThroughput control group name = %s", options.getThroughputControlGroupName()));

        return stringBuilder.toString();
    }

    protected static String cosmosItemResponseToString(CosmosItemResponse response) {

        // TODO: Add more fields
        var stringBuilder = new StringBuilder();

        stringBuilder.append("\n");
        stringBuilder.append(String.format("\tStatus code = %d\n", response.getStatusCode()));
        stringBuilder.append(String.format("\tSession token = %s\n", response.getSessionToken()));
        stringBuilder.append(String.format("\tETag = %s\n", response.getETag()));
        stringBuilder.append(String.format("\tActivity id = %s\n", response.getActivityId()));

        if (response.getStatusCode() == 200) {
            var itemString = response.getItem().toString().replace("\n", "\n\t\t");
            stringBuilder.append(String.format("\tItem: \n\t\t%s", itemString));
        }

        return stringBuilder.toString();
    }

    public abstract void runScenarios() throws Exception;
}
