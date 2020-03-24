package com.fabrikam.functions;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.*;

import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;

import java.util.*;

/**
 * azure function with HTTP Trigger.
 */
public class Function {
    @FunctionName("CosmosDBCreate")
    public HttpResponseMessage run(
        @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
        @CosmosDBOutput(
            name = "database",
            databaseName = "demo",
            collectionName = "person",
            connectionStringSetting = "CosmosDbConnection")
        OutputBinding<String> outputItem,
        ExecutionContext context) {
        
        String name = "empty";
        String email = "empty";
        final Gson gson = new Gson();
    
        // Parse query parameter
        if (request.getBody().isPresent()) {
            JsonObject jsonObject = new JsonParser().parse(request.getBody().get()).getAsJsonObject();
            name = jsonObject.get("name").getAsString();
            email = jsonObject.get("email").getAsString();
        }
    
        // Generate random ID
        final String id = String.valueOf(Math.abs(new Random().nextInt()));
    
        // Generate document
        Person person = new Person(id, name, email);
        final String database = gson.toJson(person);
    
        context.getLogger().info(String.format("Document to be saved in DB: %s", database));
    
        outputItem.setValue(database);
    
        // return the document to calling client.
        return request.createResponseBuilder(HttpStatus.OK)
                .body(database)
                .build();
    }
}
