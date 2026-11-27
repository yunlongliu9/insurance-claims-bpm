
package org.example.insurance.server;

import java.time.LocalDateTime;
import java.util.UUID;

public class Policy {

    public UUID id;
    public UUID applicantId;
    public String policyNumber;
    public String underwritingResult;
    public String contractText;
    public LocalDateTime createdAt;
}
