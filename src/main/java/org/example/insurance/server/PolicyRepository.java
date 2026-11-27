
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

    public void save(Policy policy) throws SQLException {

        String sql = """
                INSERT INTO policy (
                    policy_id,
                    applicant_id,
                    policy_number,
                    underwriting_result,
                    contract_text,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, policy.id);
            stmt.setObject(2, policy.applicantId);
            stmt.setString(3, policy.policyNumber);
            stmt.setString(4, policy.underwritingResult);
            stmt.setString(5, policy.contractText);
            stmt.setTimestamp(6, Timestamp.valueOf(policy.createdAt));

            stmt.executeUpdate();
        }
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
