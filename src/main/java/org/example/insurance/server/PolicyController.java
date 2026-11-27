
package org.example.insurance.server;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/policies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PolicyController {

    @Inject
    PolicyService policyService;

    public record IssuePolicyRequest(
            UUID applicantId,
            UUID insuranceTypeId,
            String underwritingResult) {
    }


   
    @POST
    @Path("/issue")
    public Response issuePolicy(IssuePolicyRequest request) {

        if (request == null
                || request.applicantId() == null
                || request.underwritingResult() == null
                || request.insuranceTypeId() == null
                || request.underwritingResult().isBlank()) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing applicantId or underwritingResult")
                    .build();
        }

        try {
            Policy policy = policyService.issuePolicy(
                    request.applicantId(),
                    request.insuranceTypeId(),
                    request.underwritingResult());

            return Response.status(Response.Status.CREATED)
                    .entity(java.util.Map.of(
                            "success", true,
                            "policyIssued", true,
                            "policyNumber", policy.policyNumber,
                            "applicantId", policy.applicantId.toString()))
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity("Policy issuance failed")
                    .build();
        }
    }

    @GET
    @Path("/applicant/{applicantId}")
    public Response getPolicies(
            @PathParam("applicantId") UUID applicantId) {

        try {
            return Response.ok(
                    policyService.getPoliciesByApplicantId(applicantId)).build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to retrieve policies")
                    .build();
        }
    }
}
