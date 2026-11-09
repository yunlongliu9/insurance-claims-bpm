package org.example.insurance.server;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.Map;

@Path("/coverage/check")
public class CoverageResource {
    @Inject
    CoverageService service;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Boolean> check(Map<String, String> data) {
        boolean result = service.isWithinCoveragePeriod(
                LocalDate.parse(data.get("policyStart")),
                LocalDate.parse(data.get("policyEnd")),
                LocalDate.parse(data.get("treatmentDate")));
        return Map.of("withinCoverage", result);
    }
}