
package org.example.insurance.server;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PolicyService {

    @Inject
    PolicyRepository policyRepository;

    public Policy issuePolicy(
            UUID applicantId,
            UUID insuranceTypeId,
                    String underwritingResult) throws SQLException {

        if (applicantId == null) {
            throw new IllegalArgumentException(
                    "Applicant ID is required");
        }

        if (underwritingResult == null) {
            throw new IllegalArgumentException(
                    "Underwriting result is required");
        }

        String result = underwritingResult.trim().toUpperCase();

        if (!result.equals("STANDARD")
                && !result.equals("RATED")
                && !result.equals("EXCLUSION")) {

            throw new IllegalArgumentException(
                    "Unsupported underwriting result: " + result);
        }

        Policy policy = new Policy();

        policy.id = UUID.randomUUID();
        policy.applicantId = applicantId;
        policy.policyNumber = "POL-" + policy.id.toString().substring(0, 8).toUpperCase();
        policy.underwritingResult = result;
        policy.createdAt = LocalDateTime.now();
        policy.insuranceTypeId = insuranceTypeId;
        policy.contractText = generateContract(
                policy.policyNumber,
                result);

        policyRepository.save(policy);

        return policy;
    }

    public List<Policy> getPoliciesByApplicantId(UUID applicantId)
            throws SQLException {

        if (applicantId == null) {
            throw new IllegalArgumentException(
                    "Applicant ID is required");
        }

        return policyRepository.findByApplicantId(applicantId);
    }

    private String generateContract(
            String policyNumber,
            String underwritingResult) {

        String header = """
                HEALTH INSURANCE POLICY
                Policy Number: %s

                """.formatted(policyNumber);

        return switch (underwritingResult) {

            case "STANDARD" -> header + """
                    Underwriting Decision: STANDARD ACCEPTANCE

                    The applicant has been accepted under
                    standard insurance conditions.

                    Coverage: Standard health insurance coverage.
                    Premium: Standard premium.
                    Exclusions: None.

                    Policy Status: ISSUED
                    """;

            case "RATED" -> header + """
                    Underwriting Decision: RATED ACCEPTANCE

                    The applicant has been accepted with
                    an additional risk-based premium.

                    Coverage: Standard health insurance coverage.
                    Premium: Standard premium plus risk surcharge.
                    Exclusions: None.

                    Policy Status: ISSUED
                    """;

            case "EXCLUSION" -> header + """
                    Underwriting Decision: ACCEPTANCE WITH EXCLUSION

                    The applicant has been accepted subject
                    to specific coverage exclusions.

                    Coverage: Health insurance with exclusions.
                    Premium: Standard premium.
                    Exclusions: Certain pre-existing conditions
                    are excluded from coverage.

                    Policy Status: ISSUED
                    """;

            default -> throw new IllegalArgumentException(
                    "Unsupported underwriting result");
        };
    }
}
