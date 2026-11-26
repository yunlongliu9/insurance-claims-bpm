
package org.example.insurance;

import java.sql.ResultSet;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.sql.ResultSet;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;

@Named("applicantService")
@ApplicationScoped
public class ApplicantService {

    @Inject
    DataSource dataSource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean applicantExists(String name, String dateOfBirth)
            throws Exception {

        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM applicant
                    WHERE name = ?
                      AND date_of_birth = ?
                )
                """;

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setDate(2, Date.valueOf(dateOfBirth));

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getBoolean(1);
            }
        }
    }

    public void renewApplicant(String body) throws Exception {
        JsonNode applicant = objectMapper.readTree(body);

        String sql = """
                UPDATE applicant
                SET name = ?, gender = ?, phone_number = ?, date_of_birth = ?,
                    residence_germany = ?, employment_status = ?,
                    gross_annual_income = ?, dental_treatment_flag = ?,
                    chronic_condition_flag = ?, inpatient_treatment_flag = ?,
                    psychotherapy_flag = ?, spinal_history = ?, allergy_flag = ?,
                    medication_ongoing = ?, bmi = ?, tariff_benefit_level = ?,
                    prior_adverse_decision = ?
                WHERE name = ? AND date_of_birth = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, applicant.path("name").asText());
            stmt.setString(2, applicant.path("gender").asText());
            stmt.setString(3, applicant.path("phoneNumber").asText());
            stmt.setDate(4, Date.valueOf(applicant.path("dateOfBirth").asText()));
            stmt.setBoolean(5, applicant.path("residenceGermany").asBoolean());
            stmt.setString(6, applicant.path("employmentStatus").asText());
            stmt.setInt(7, applicant.path("grossAnnualIncome").asInt());
            stmt.setBoolean(8, applicant.path("dentalTreatmentFlag").asBoolean());
            stmt.setBoolean(9, applicant.path("chronicConditionFlag").asBoolean());
            stmt.setBoolean(10, applicant.path("inpatientTreatmentFlag").asBoolean());
            stmt.setBoolean(11, applicant.path("psychotherapyFlag").asBoolean());
            stmt.setBoolean(12, applicant.path("spinalHistory").asBoolean());
            stmt.setBoolean(13, applicant.path("allergyFlag").asBoolean());
            stmt.setBoolean(14, applicant.path("medicationOngoing").asBoolean());
            stmt.setDouble(15, applicant.path("bmi").asDouble());
            stmt.setString(16, applicant.path("tariffBenefitLevel").asText());
            stmt.setBoolean(17, applicant.path("priorAdverseDecision").asBoolean());
            stmt.setString(18, applicant.path("name").asText());
            stmt.setDate(19, Date.valueOf(applicant.path("dateOfBirth").asText()));

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new Exception("No rows updated. Applicant not found.");
            }
        }
    }

    public void saveApplicant(String body) throws Exception {

        JsonNode applicant = objectMapper.readTree(body);

        String sql = """
                INSERT INTO applicant (
                    name, gender, phone_number, date_of_birth,
                    residence_germany, employment_status,
                    gross_annual_income, dental_treatment_flag,
                    chronic_condition_flag, inpatient_treatment_flag,
                    psychotherapy_flag, spinal_history, allergy_flag,
                    medication_ongoing, bmi, tariff_benefit_level,
                    prior_adverse_decision, identity
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'applicant')
                """;

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, applicant.path("name").asText());
            stmt.setString(2, applicant.path("gender").asText());
            stmt.setString(3, applicant.path("phoneNumber").asText());
            stmt.setDate(4, Date.valueOf(applicant.path("dateOfBirth").asText()));
            stmt.setBoolean(5, applicant.path("residenceGermany").asBoolean());
            stmt.setString(6, applicant.path("employmentStatus").asText());
            stmt.setInt(7, applicant.path("grossAnnualIncome").asInt());
            stmt.setBoolean(8, applicant.path("dentalTreatmentFlag").asBoolean());
            stmt.setBoolean(9, applicant.path("chronicConditionFlag").asBoolean());
            stmt.setBoolean(10, applicant.path("inpatientTreatmentFlag").asBoolean());
            stmt.setBoolean(11, applicant.path("psychotherapyFlag").asBoolean());
            stmt.setBoolean(12, applicant.path("spinalHistory").asBoolean());
            stmt.setBoolean(13, applicant.path("allergyFlag").asBoolean());
            stmt.setBoolean(14, applicant.path("medicationOngoing").asBoolean());
            stmt.setDouble(15, applicant.path("bmi").asDouble());
            stmt.setString(16, applicant.path("tariffBenefitLevel").asText());
            stmt.setBoolean(17, applicant.path("priorAdverseDecision").asBoolean());

            stmt.executeUpdate();
        }
    }
}
