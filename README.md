# Cosmos Scenarios API
Runs different scenarios against cosmos-java-sdk and logs what happens. 

## Prerequisites
- An active Azure account or Cosmos DB emulator (tested only with Azure account).
- openjdk 17.0.7
- Maven 3.9.2

## Running
Setup environment variables:
```
export ACCOUNT_HOST = YOUR_COSMOS_DB_HOSTNAME
export ACCOUNT_KEY = YOUR_COSMOS_DB_MASTER_KEY
```

Build and run:
```
mvn clean package
mvn exec:java -Dexec.mainClass="com.azure.cosmos.sdk.scenarios.Main"
```

## Evaluating logs
Check the scenarios.log file in the project folder.