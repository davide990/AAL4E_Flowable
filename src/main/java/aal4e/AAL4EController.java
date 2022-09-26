package aal4e;

import aal4e.dto.ProcessInstanceRequest;
import aal4e.dto.TaskDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * The REST controller for AAL4E http requests
 */
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AAL4EController {

    AAL4EService AAL4EService;

    @PostMapping("/AAL4E/clearDeployments")
    public void clearDeployments() {
        AAL4EService.clearAllExistingDeployments();
    }


    @PostMapping("/AAL4E/deployProcess/{processID}")
    public void deployWorkflow(@PathVariable("processID") String processID, @RequestBody String processDef) {
        AAL4EService.deployProcessDefinition(processID, processDef);
    }

    @PostMapping("/AAL4E/deployFromFile")
    public void deployWorkflow() throws IOException {
        AAL4EService.deployProcessDefinition();
    }

    @GetMapping("/AAL4E/list/")
    public List<ProcessDefinition> getActiveProcesses() {
        return AAL4EService.getActiveProcesses();
    }

    @GetMapping("/AAL4E/{processID}/tasks")
    public List<TaskDetails> getProcessDefinitions(@PathVariable("processID") String processID) {
        return AAL4EService.getTasks(processID);
    }

    @PostMapping("/AAL4E/apply")
    public void newPlanInstanceRequest(@RequestBody ProcessInstanceRequest processInstanceRequest) {
        AAL4EService.applyInterventionRequest(processInstanceRequest);
    }


}
