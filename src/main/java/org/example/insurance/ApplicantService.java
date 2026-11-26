package org.example.insurance;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.inject.Named;

@Named("applicantService")
@ApplicationScoped
public class ApplicantService {

    @Inject
    DataSource dataSource;

    public void saveApplicant(String name) throws Exception {
        String sql = "INSERT INTO applicant_test (name) VALUES (?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }
}