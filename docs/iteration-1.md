# MVP - contract validation check
业务场景： 客户提交一笔医疗费用报销申请（Leistungsantrag），系统首先检查治疗日期是否处于保险合同有效期内。
Customer submit a Medical Expense Reimbursement Application[Erstattungsantrag];The system will check if the treatment date is within the available period of the insurance contract;(Liegt das Behandlungsdatum innerhalb der Gültigkeitsdauer des Versicherungsvertrags?)
![image.png](https://raw.githubusercontent.com/Viende/fresh-photos/main/photo1/20261025210926.png)
input:
+ `policyStart`
+ `policyEnd`
+ `treatmentDate`


# dev
+ main/java/org/example/insurance/*.java  the codes for each step in process (the step we wanna add,realized here concretely)
+  main/resource/bpmn/coverage-check.bpmn  the control core by BPMN 2.0 (we add step inside)
+ test/java/org/example/insurance/*.java corresponding Unit test for validation

# Add a new  step into process?
in coverage-check.bpmn add <serviceTask>
```xml
<serviceTask
    id="checkCoverage"
    name="Versicherungszeitraum prüfen"   cibseven:class="org.example.insurance.CoverageCheckDelegate" />
```
+ `cibseven:class="org.example.insurance.CoverageCheckDelegate"` tells CIB SEVEN, when we run to this ServiceTask, please call the java class "org.example.insurance.CoverageCheckDelegate.java"

java execuation:
```java

public class CoverageCheckDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // get BPMN variable
        // call CoverageService to judge date
        // write back the judgement result
    }
}
```
so when BPMN engine execuate Service Task ,it will find the  execute()

the sequence flow in Bcoverage-check.bpmn will add specific execuation steps:
```xml
<sequenceFlow id="flow1"
              sourceRef="start"
              targetRef="checkCoverage" />

<sequenceFlow id="flow1b"
              sourceRef="checkCoverage"
              targetRef="coverageGateway" />
```
# SUMMARY
1. BPMN control the flow,CIB seven will exe the nodes according to Sequence Flow one by one in bpmn files
2. java is responsible for concrete implementation

BPMN:
+ when check? after check we do what?
JavaDelegate:
+ How to get variable from BPMN. and call which code?
XXXService: (CoverageService)
+ Concrete Business process: is the date valid in the contract period?

# execuation Flow: So, how does java know that bpmn wanna check? by Delegation.
actually: The BPMN engine invokes the JavaDelegate when the process reaches the corresponding Service Task;

1. Test will transport the variables[Map.of] first;
```java
var invalidInstance = engine.getRuntimeService()
                    .startProcessInstanceByKey(
                        "coverageCheck",
                        java.util.Map.of(
                         "policyStart", "2026-01-01",
                         "policyEnd", "2026-12-31",
                         "treatmentDate", "2027-02-15"
                )
        );
```
2. Then the Delegate will know the variables by execuation:
```java

public class CoverageCheckDelegate implements JavaDelegate {

    private final CoverageService coverageService = new CoverageService();

    @Override
    public void execute(DelegateExecution execution) {
        LocalDate start = LocalDate.parse((String) execution.getVariable("policyStart"));
        LocalDate end = LocalDate.parse((String) execution.getVariable("policyEnd"));
        LocalDate treatment = LocalDate.parse((String) execution.getVariable("treatmentDate"));

        boolean valid = coverageService.isWithinCoveragePeriod(start, end, treatment);
        execution.setVariable("withinCoverage", valid);
    }
}
```
Delegate knows the start,end date and the checkdate too; 
BPMN knows when come to find delegate to exe this step because of the definition in coverage-check.bpmn and find the implementation execuation(CIB SEVEN);
`<serviceTask id="checkCoverage"
             name="Versicherungszeitraum prüfen"
             cibseven:class="org.example.insurance.CoverageCheckDelegate" />`
3. then delegate use the java code(java defined servic - CoverageService) to execuate concrete business process(date valid?)
So after java service return the value we want(defined inside BPMN):
```xml
<sequenceFlow id="flow2"
              sourceRef="coverageGateway"
              targetRef="validEnd">
    <conditionExpression xsi:type="tFormalExpression">
        ${withinCoverage == true}
    </conditionExpression>
    <!--withinCoverage == true，then coverageGateway go to validEnd(targetRef)-->
</sequenceFlow> 
```
cip seven(Sequence Flow in .bpmn) can decide which branch we can enter into next to access the next step in process.

process control and business computing is seperated;[although in delegate we already know it is valid or not; The meaning of the value transferred back to .bpmn Service Flow is to decide which flow it will go next]

+ CoverageService	           judge valid or not;
+ CoverageCheckDelegate	       write the result into variable
+ ExclusiveGateway	           according to condition select one route
+ SequenceFlow	               def the condition and goal of each route

# End event: validEnd/invalidEnd are end event id 
sourceRef：from which ndoe?
targetRef：to which node?
validEnd：ref the predefined id="validEnd"
```xml
<sequenceFlow
    sourceRef="coverageGateway"
    targetRef="validEnd" />

<endEvent id="validEnd"
          name="Innerhalb des Versicherungszeitraums" />

<endEvent id="invalidEnd"
          name="Manuelle Prüfung" />


                  ┌── true  ──→ validEnd（结束）
Service Task → XOR
                  └── false ──→ invalidEnd（结束）
```
If we wanna manuelly inspect the node, we can add one task "usertask" before invalidEnd;

# Use REST API instead of test to drive the process
src/main/java/org/example/insurance/ClaimResource.java is a REST API interface file;
+ `startProcessInstanceByKey("coverageCheck", claim)` will transfer json in http to be process variable;
ClaimResource has only one function: evoke the BPMN process;[not involved!]
Rest API needs an engine(ProcessEngine) so create ProcessEngineProducer.java:
# Quarkus inject ProcessEngine
src/main/java/org/example/insurance/ProcessEngineProducer.java
+ create CIB seven BPMN engine，deploy our BPMN file，give it to Quarkus to begin our service.
@Produces：register CIB seven as the object that Quarkus can inject
createDeployment()：when start engine,deploy the BPMN process

# roles now
![image.png](https://raw.githubusercontent.com/Viende/fresh-photos/main/photo1/20261025225525.png)
Producer：create and configure engine
Resource：receive http req and begin the process
Delegate：connect the logic between BPMN and Java 
# REST API calling sequence:
```tsx
client [cli: curl post request]
  │
  │ HTTP POST /claims
  │ JSON: start,end,clinic date
  ▼
ClaimResource.submit()
  │
  │ startProcessInstanceByKey()
  ▼
CIB seven ProcessEngine
  │
  │ execuate coverageCheck
  ▼
coverage-check.bpmn
  │
  ▼
CoverageCheckDelegate
  │
  ▼
CoverageService
  │
  │ return true / false
  ▼
BPMN Gateway
  │
  ├── true  → validEnd
  └── false → invalidEnd
  │
  ▼
ClaimResource return Process Instance ID
```
# GET the result of the process and response client by using the result(valid:true)
1. Store the variable: The JavaDelegate uses execution.setVariable("withinCoverage", valid) to save the result.
2. Execute the process: CIB seven runs the BPMN process and stores the variable in its history database (if history recording is enabled).
3. Retrieve the variable: After the process finishes, use HistoryService to query the saved variable.
like:
```java
var result = engine.getHistoryService()
    .createHistoricVariableInstanceQuery()
    .processInstanceId(instance.getId())
    .variableName("withinCoverage")
    .singleResult()
    .getValue();
```