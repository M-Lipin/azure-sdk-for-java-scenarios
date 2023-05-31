package com.azure.cosmos.sdk.scenarios.groups;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.sdk.scenarios.dbitems.Document;
import com.azure.cosmos.sdk.scenarios.dbitems.Employee;
import com.azure.cosmos.sdk.scenarios.helpers.Account;
import com.azure.cosmos.sdk.scenarios.helpers.CosmosClientHelper;
import com.azure.cosmos.sdk.scenarios.helpers.CreateItemRequest;
import com.azure.cosmos.sdk.scenarios.helpers.ReadItemRequest;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

public class ContainerReadItemScenarioGroup extends ScenarioGroupBase {

    private final String databaseName = "Organization";
    private final String documentsContainerName = "Documents";
    private final String documentPartitionKeyPath = "/documentType";
    private final String employeesContainerName = "Employees";
    private final String employeePartitionKeyPath = "/department";

    public ContainerReadItemScenarioGroup(Account account) {
        super(account);
        logger = LoggerFactory.getLogger(ContainerReadItemScenarioGroup.class);
    }

    private <T> CosmosItemResponse<T> createItem(CosmosContainer container, CreateItemRequest<T> createItemRequest, String databaseName) {
        logger.info(scenarioPreparationStep, String.format("Creating an item of type '%s' with id = '%s' and partitionKey = %s in the '%s' database with options: %s", createItemRequest.getItem().getClass(), createItemRequest.getItemId(), createItemRequest.getPartitionKey(), databaseName, cosmosItemRequestOptionsToString(createItemRequest.getRequestOptions())));
        var response = container.createItem(createItemRequest.getItem(), createItemRequest.getPartitionKey(), createItemRequest.getRequestOptions());
        logger.info(scenarioPreparationStep, String.format("Item creation resulted in response %s", cosmosItemResponseToString(response)));
        logger.info(scenarioPreparationStep, String.format("Item of type '%s' with id = '%s' and partitionKey = %s has been created in the '%s' database", createItemRequest.getItem().getClass(), createItemRequest.getItemId(), createItemRequest.getPartitionKey(), databaseName));
        return response;
    }

    private <T> CosmosItemResponse<T> readItem(CosmosContainer container, ReadItemRequest readItemRequest, String databaseName) {
        try {
            logger.info(scenarioExecutionStep, String.format("Reading an item of type '%s' with id = '%s', partitionKey = %s from the '%s' database with options: %s", readItemRequest.getType(), readItemRequest.getItemId(), readItemRequest.getPartitionKey(), databaseName, cosmosItemRequestOptionsToString(readItemRequest.getRequestOptions())));
            var response = container.readItem(readItemRequest.getItemId(), readItemRequest.getPartitionKey(), readItemRequest.getRequestOptions(), readItemRequest.getType());
            logger.info(scenarioEnd, String.format("Item reading resulted in response %s", cosmosItemResponseToString(response)));
            return response;
        } catch (Exception ex) {
            logger.info(scenarioEnd, String.format("Reading an item of type '%s' with id = '%s', partitionKey = %s from the '%s' database resulted in an exception of type: %s", readItemRequest.getType(), readItemRequest.getItemId(), readItemRequest.getPartitionKey(), databaseName, ex.getClass()));
            return null;
        }
    }

    private CosmosContainer buildContainerIfNotExists(CosmosClient cosmosClient, String containerName, String partitionKeyPath) {
        createDatabaseIfNotExists(cosmosClient, databaseName);
        var database = cosmosClient.getDatabase(databaseName);
        createContainerIfNotExists(database, containerName, partitionKeyPath);
        var container = database.getContainer(containerName);
        return container;
    }

    @Override
    public void runScenarios() {

        try {
            logger.info(scenarioGroupStartMarker, "Container.readItem() scenario group");

            runOneClientScenarioWithCleanup("Simple item read", this::readExistingItem);
            runOneClientScenarioWithCleanup("Read item non-existent in the database", this::readNonExistingItem);
            runOneClientScenarioWithCleanup("Read an item from non-existing partition", this::readItemCorrectIdNonExistingPartition);
            runOneClientScenarioWithCleanup("Read an item from other partition", this::readItemCorrectIdWrongPartition);

            runOneClientScenarioWithCleanup("Read an item of non-existent type (String)", this::readItemNonExistentTypeString);
            runOneClientScenarioWithCleanup("Read an item of type non-existent type (Employee) ", this::readItemNonExistentTypeEmployee);
            runOneClientScenarioWithCleanup("Read an item as different type", this::readItemAsDifferentType);

            runOneClientScenarioWithCleanup("Read an item with replaced session token", this::readItemReplaceSessionToken);
            runOneClientScenarioWithCleanup("Read an item with bogus session token", this::readItemReplaceSessionTokenWithBogus);

            runOneClientScenario("Read item after client is closed", this::readItemAfterClientIsClosed);
            runOneClientScenario("Read item after in a new client", this::readItemInReopenedClient);

            runTwoClientScenarioWithCleanup("Read item created by different client", this::readItemCreatedByAnotherClient);

            logger.info(scenarioGroupEndMarker, "Container.readItem() scenario group has finished without errors");

        } catch (Exception exception) {
            logger.error(scenarioGroupErrorMarker, String.format("Container.readItem() scenario group has finished with an error %s", Arrays.toString(exception.getStackTrace())));
        }
    }

    private void runOneClientScenario(String scenarioName, OneClientScenario oneClientScenario) {

        logger.info(scenarioStart, scenarioName);

        var cosmosClient = CosmosClientHelper.buildClient(account);

        try {
            oneClientScenario.run(cosmosClient);
        } catch (Exception exception) {
            logger.info(scenarioEnd, String.format("Scenario '%s' has been finished with the exception " + "'%s'", scenarioName, exception.getClass().toString()));
            closeConnectionAndCleanupResources(cosmosClient, databaseName, scenarioEnd);
        }
    }

    private void runOneClientScenarioWithCleanup(String scenarioName, OneClientScenario oneClientScenario) {

        logger.info(scenarioStart, scenarioName);

        var cosmosClient = CosmosClientHelper.buildClient(account);

        try {
            oneClientScenario.run(cosmosClient);
        } catch (Exception exception) {
            logger.info(scenarioEnd, String.format("Scenario '%s' has been finished with the exception " + "'%s'", scenarioName, exception.getClass().toString()));
        } finally {
            closeConnectionAndCleanupResources(cosmosClient, databaseName, scenarioEnd);
        }
    }

    private void runTwoClientScenarioWithCleanup(String scenarioName, TwoClientScenario twoClientScenario) {

        logger.info(scenarioStart, scenarioName);

        var cosmosClient1 = CosmosClientHelper.buildClient(account);
        var cosmosClient2 = CosmosClientHelper.buildClient(account);

        try {
            twoClientScenario.run(cosmosClient1, cosmosClient2);
        } catch (Exception exception) {
            logger.info(scenarioEnd, String.format("Scenario '%s' has been finished with the exception " + "'%s'", scenarioName, exception.getClass().toString()));
        } finally {
            closeConnectionAndCleanupResources(cosmosClient1, databaseName, scenarioEnd);
            closeConnectionAndCleanupResources(cosmosClient2, databaseName, scenarioEnd);
        }
    }

    private void readExistingItem(CosmosClient cosmosClient) {
        var document = new Document();

        var documentContainer = buildContainerIfNotExists(cosmosClient, documentsContainerName, documentPartitionKeyPath);

        var createItemRequest = new CreateItemRequest(document, document.getId(), document.getDocumentType());
        createItem(documentContainer, createItemRequest, databaseName);

        var readItemRequest = new ReadItemRequest(document.getClass(), document.getId(), document.getDocumentType());
        readItem(documentContainer, readItemRequest, databaseName);
    }

    private void readItemAfterClientIsClosed(CosmosClient cosmosClient) {
        var document = new Document();
        var documentContainer = buildContainerIfNotExists(cosmosClient, documentsContainerName, documentPartitionKeyPath);

        var createItemRequest = new CreateItemRequest(document, document.getId(), document.getDocumentType());
        createItem(documentContainer, createItemRequest, databaseName);

        closeConnectionAndCleanupResources(cosmosClient, databaseName, scenarioPreparationStep);

        var readItemRequest = new ReadItemRequest(document.getClass(), document.getId(), document.getDocumentType());
        readItem(documentContainer, readItemRequest, databaseName);
    }

    private void readItemInReopenedClient(CosmosClient cosmosClient) {
        var document = new Document();
        var documentContainer = buildContainerIfNotExists(cosmosClient, documentsContainerName, documentPartitionKeyPath);

        var createItemOptions = new CosmosItemRequestOptions();
        createItemOptions.setConsistencyLevel(ConsistencyLevel.SESSION);

        var createItemRequest = new CreateItemRequest(document, document.getId(), document.getDocumentType());
        createItem(documentContainer, createItemRequest, databaseName);

        closeConnection(cosmosClient, databaseName, scenarioPreparationStep);

        cosmosClient = CosmosClientHelper.buildClient(account);
        documentContainer = buildContainerIfNotExists(cosmosClient, documentsContainerName, documentPartitionKeyPath);

        var readItemOptions = new CosmosItemRequestOptions();
        readItemOptions.setConsistencyLevel(ConsistencyLevel.SESSION);

        var readItemRequest = new ReadItemRequest(document.getClass(), document.getId(), document.getDocumentType());
        readItem(documentContainer, readItemRequest, databaseName);

        closeConnection(cosmosClient, databaseName, scenarioEnd);
    }

    private void readNonExistingItem(CosmosClient cosmosClient) {
        var document = new Document();

        var documentContainer = buildContainerIfNotExists(cosmosClient, documentsContainerName, documentPartitionKeyPath);

        var createItemRequest = new CreateItemRequest(document, document.getId(), document.getDocumentType());
        createItem(documentContainer, createItemRequest, databaseName);

        var readItemRequest = new ReadItemRequest(document.getClass(), UUID.randomUUID().toString(), document.getDocumentType());
        readItem(documentContainer, readItemRequest, databaseName);
    }

    private void readItemCorrectIdNonExistingPartition(CosmosClient cosmosClient) {
        var document = new Document();

        var documentContainer = buildContainerIfNotExists(cosmosClient, documentsContainerName, documentPartitionKeyPath);

        var createItemRequest = new CreateItemRequest(document, document.getId(), document.getDocumentType());
        createItem(documentContainer, createItemRequest, databaseName);

        var readItemRequest = new ReadItemRequest(document.getClass(), document.getId(), UUID.randomUUID().toString());
        readItem(documentContainer, readItemRequest, databaseName);
    }

    private void readItemCorrectIdWrongPartition(CosmosClient cosmosClient) {
        var document1 = new Document();
        var document2 = new Document();

        var documentContainer = buildContainerIfNotExists(cosmosClient, documentsContainerName, documentPartitionKeyPath);

        var createItemRequest = new CreateItemRequest(document1, document1.getId(), document1.getDocumentType());
        createItem(documentContainer, createItemRequest, databaseName);

        createItemRequest = new CreateItemRequest(document2, document2.getId(), document2.getDocumentType());
        createItem(documentContainer, createItemRequest, databaseName);

        var readItemRequest = new ReadItemRequest(document1.getClass(), document1.getId(), document2.getDocumentType());
        readItem(documentContainer, readItemRequest, databaseName);
    }

    private void readItemNonExistentTypeString(CosmosClient cosmosClient) {
        var document = new Document();

        var documentContainer = buildContainerIfNotExists(cosmosClient, documentsContainerName, documentPartitionKeyPath);

        var createItemRequest = new CreateItemRequest(document, document.getId(), document.getDocumentType());
        createItem(documentContainer, createItemRequest, databaseName);

        var readItemRequest = new ReadItemRequest(String.class, document.getId(), document.getDocumentType());
        readItem(documentContainer, readItemRequest, databaseName);
    }

    private void readItemNonExistentTypeEmployee(CosmosClient cosmosClient) {
        var document = new Document();
        var employee = new Employee();

        var documentContainer = buildContainerIfNotExists(cosmosClient, documentsContainerName, documentPartitionKeyPath);

        var createItemRequest = new CreateItemRequest(document, document.getId(), document.getDocumentType());
        createItem(documentContainer, createItemRequest, databaseName);

        var readItemRequest = new ReadItemRequest(employee.getClass(), document.getId(), document.getDocumentType());
        readItem(documentContainer, readItemRequest, databaseName);
    }

    private void readItemAsDifferentType(CosmosClient cosmosClient) {
        var document = new Document();
        var employee = new Employee();

        var documentContainer = buildContainerIfNotExists(cosmosClient, documentsContainerName, documentPartitionKeyPath);
        var employeeContainer = buildContainerIfNotExists(cosmosClient, employeesContainerName, employeePartitionKeyPath);

        var createItemRequest = new CreateItemRequest(document, document.getId(), document.getDocumentType());
        createItem(documentContainer, createItemRequest, databaseName);

        createItemRequest = new CreateItemRequest(employee, employee.getId(), employee.getDepartment());
        createItem(employeeContainer, createItemRequest, databaseName);

        var readItemRequest = new ReadItemRequest(employee.getClass(), document.getId(), document.getDocumentType());
        readItem(documentContainer, readItemRequest, databaseName);
    }

    private void readItemReplaceSessionTokenWithBogus(CosmosClient cosmosClient) {
        var document = new Document();
        var documentContainer = buildContainerIfNotExists(cosmosClient, documentsContainerName, documentPartitionKeyPath);

        var createItemRequest = new CreateItemRequest(document, document.getId(), document.getDocumentType());
        createItem(documentContainer, createItemRequest, databaseName);

        var readItemOptions = new CosmosItemRequestOptions();
        readItemOptions.setSessionToken(UUID.randomUUID().toString());

        var readItemRequest = new ReadItemRequest(document.getClass(), document.getId(), document.getDocumentType(), readItemOptions);
        readItem(documentContainer, readItemRequest, databaseName);
    }

    private void readItemReplaceSessionToken(CosmosClient cosmosClient) {
        var document = new Document();
        var documentContainer = buildContainerIfNotExists(cosmosClient, documentsContainerName, documentPartitionKeyPath);

        var createItemRequest = new CreateItemRequest(document, document.getId(), document.getDocumentType());
        var createdItemResponse = createItem(documentContainer, createItemRequest, databaseName);

        var sessionToken = createdItemResponse.getSessionToken();
        var newSessionToken = new StringBuilder(sessionToken.substring(0, sessionToken.indexOf('#') + 1));

        var splitIndex = sessionToken.indexOf("#") + 1;
        var sessionTokenLastNumber = Integer.valueOf(sessionToken.substring(splitIndex));
        var sessionTokenLastNumberIncremented = sessionTokenLastNumber + 1;

        newSessionToken.append(sessionTokenLastNumberIncremented);

        var readItemOptions = new CosmosItemRequestOptions();
        readItemOptions.setSessionToken(newSessionToken.toString());

        var readItemRequest = new ReadItemRequest(document.getClass(), document.getId(), document.getDocumentType(), readItemOptions);
        readItem(documentContainer, readItemRequest, databaseName);
    }

    private void readItemCreatedByAnotherClient(CosmosClient cosmosClient1, CosmosClient cosmosClient2) {
        var document1 = new Document();
        var document2 = new Document();

        var documentContainer1 = buildContainerIfNotExists(cosmosClient1, documentsContainerName, documentPartitionKeyPath);
        var documentContainer2 = buildContainerIfNotExists(cosmosClient2, documentsContainerName, documentPartitionKeyPath);

        var createItemRequest1 = new CreateItemRequest(document1, document1.getId(), document1.getDocumentType());
        createItem(documentContainer1, createItemRequest1, databaseName);

        var createItemRequest2 = new CreateItemRequest(document2, document2.getId(), document2.getDocumentType());
        var createdItemResponse2 = createItem(documentContainer2, createItemRequest2, databaseName);

        var readItemOptions = new CosmosItemRequestOptions();
        readItemOptions.setSessionToken(createdItemResponse2.getSessionToken());

        var readItemRequest = new ReadItemRequest(document1.getClass(), document1.getId(), document1.getDocumentType(), readItemOptions);
        readItem(documentContainer1, readItemRequest, databaseName);
    }

    private interface OneClientScenario {
        void run(CosmosClient cosmosClient);
    }

    private interface TwoClientScenario {
        void run(CosmosClient cosmosClient1, CosmosClient cosmosClient2);
    }
}
