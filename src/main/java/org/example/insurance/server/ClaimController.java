
package org.example.insurance.server;

import org.example.insurance.client.CibSevenClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/claims")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClaimController {

        @Inject
        @RestClient
        CibSevenClient client;

        @ConfigProperty(name = "insurance.service.base-url")
        String quarkusBaseUrl;

        @POST
        public Map<String, Object> submit(Map<String, Object> claim) {

                // 1. client input -> CIB seven pricess variables
                Map<String, Object> variables = new HashMap<>(
                                claim.entrySet().stream()
                                                .collect(Collectors.toMap(
                                                                Map.Entry::getKey,
                                                                entry -> Map.of(
                                                                                "value", entry.getValue(),
                                                                                "type", "String"))));

                // 2. adds Quarkus service address
                variables.put("quarkusBaseUrl", Map.of(
                                "value", quarkusBaseUrl,
                                "type", "String"));

                // 3. start CIB seven process
                return client.startProcess(
                                "coverageCheck",
                                Map.of("variables", variables));
        }
}
