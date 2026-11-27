
package org.example.insurance.server;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PolicyRepository {

    @Inject
    DataSource dataSource;

    public Policy save(Policy policy) throws SQLException {
        String sql = """
                INSERT INTO policy (
                    policy_id, applicant_id, insurance_type_id,
                    policy_number, underwriting_result,
                    contract_text, created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (applicant_id, insurance_type_id)
                DO UPDATE SET
                    underwriting_result = EXCLUDED.underwriting_result,
                    contract_text = REPLACE(
                        EXCLUDED.contract_text,
                        EXCLUDED.policy_number,
                        policy.policy_number
                    )
                RETURNING policy_id, policy_number, contract_text, created_at
                """;

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, policy.id);
            stmt.setObject(2, policy.applicantId);
            stmt.setObject(3, policy.insuranceTypeId);
            stmt.setString(4, policy.policyNumber);
            stmt.setString(5, policy.underwritingResult);
            stmt.setString(6, policy.contractText);
            stmt.setTimestamp(7, Timestamp.valueOf(policy.createdAt));

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                policy.id = rs.getObject("policy_id", UUID.class);
                policy.policyNumber = rs.getString("policy_number");
                policy.contractText = rs.getString("contract_text");
                policy.createdAt = rs.getTimestamp("created_at").toLocalDateTime();
            }
        }
        return policy;
    }

    public List<Policy> findByApplicantId(UUID applicantId)
            throws SQLException {

        String sql = """
                SELECT policy_id,
                       applicant_id,
                       policy_number,
                       underwriting_result,
                       contract_text,
                       created_at
                FROM policy
                WHERE applicant_id = ?
                ORDER BY created_at DESC
                """;

        List<Policy> policies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, applicantId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Policy policy = new Policy();

                    policy.id = rs.getObject("policy_id", UUID.class);
                    policy.applicantId = rs.getObject("applicant_id", UUID.class);
                    policy.policyNumber = rs.getString("policy_number");
                    policy.underwritingResult = rs.getString("underwriting_result");
                    policy.contractText = rs.getString("contract_text");
                    policy.createdAt = rs.getTimestamp("created_at")
                            .toLocalDateTime();

                    policies.add(policy);
                }
            }
        }

        return policies;
    }
}
