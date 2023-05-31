package com.azure.cosmos.sdk.scenarios;

import com.azure.cosmos.sdk.scenarios.helpers.Account;
import com.azure.cosmos.sdk.scenarios.groups.ContainerReadItemScenarioGroup;

public class Main {

    public static void main(String[] args) throws Exception {
        var accountHost = System.getenv("ACCOUNT_HOST");
        var accountKey = System.getenv("ACCOUNT_KEY");

        if(accountHost == null || accountKey == null){
            throw new Exception("ACCOUNT_HOST and/or ACCOUNT_KEY is/are not specified!!!");
        }

        var account = new Account(accountHost, accountKey);

        // TODO: Add functional for choosing which tests to run or not
        var containerReadItemScenarioGroup = new ContainerReadItemScenarioGroup(account);
        containerReadItemScenarioGroup.runScenarios();
    }

}
