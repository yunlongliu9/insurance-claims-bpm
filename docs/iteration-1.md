# MVP - contract validation check
业务场景： 客户提交一笔医疗费用报销申请（Leistungsantrag），系统首先检查治疗日期是否处于保险合同有效期内。
Customer submit a Medical Expense Reimbursement Application[Erstattungsantrag];The system will check if the treatment date is within the available period of the insurance contract;(Liegt das Behandlungsdatum innerhalb der Gültigkeitsdauer des Versicherungsvertrags?)
![image.png](https://raw.githubusercontent.com/Viende/fresh-photos/main/photo1/20261025210926.png)
input:
+ `policyStart`
+ `policyEnd`
+ `treatmentDate`


# dev
src/main/java/org/example/insurance/CoverageService.java is responsible for date validation, it will be called later by BPMN Service Task;
+ `policyStart ≤ treatmentDate ≤ policyEnd`
```java
package org.example.insurance;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

@ApplicationScoped
public class CoverageService {

    public boolean isWithinCoveragePeriod(
            LocalDate policyStart,
            LocalDate policyEnd,
            LocalDate treatmentDate) {

        if (policyStart == null || policyEnd == null
                || treatmentDate == null
                || policyEnd.isBefore(policyStart)) {
            return false;
        }

        return !treatmentDate.isBefore(policyStart)
                && !treatmentDate.isAfter(policyEnd);
    }
}
```
with `@ApplicationScoped`， Quarkus can manage this service by CDI; Later CIB seven - BPMN Service Task will call this;
+ use `./mvnw compile ` to see BUILD SUCCESS

