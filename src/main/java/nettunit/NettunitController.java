package nettunit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import nettunit.dto.InterventionRequest;
import nettunit.dto.ProcessInstanceResponse;
import nettunit.dto.ProcessInstancesRegister;
import nettunit.dto.TaskDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class NettunitController {

    NettunitService nettunitService;

    //********************************************************** deployment endpoints **********************************************************
    @PostMapping("/deploy")
    public void deployWorkflow() {
        nettunitService.deployProcessDefinition();
    }

    @PostMapping("/incident/complete_task/{taskId}")
    public void completeTask(@PathVariable("taskId") String taskId) {
        /*nettunitService.completeTask(taskId);*/
    }

    @GetMapping("/incident/list")
    public List<ProcessInstanceResponse> getProcessDefinitions() {
        return ProcessInstancesRegister.get().processes();
    }

    @GetMapping("/incident/{processID}/tasks")
    public List<TaskDetails> getProcessDefinitions(@PathVariable("processID") String processID) {
        return nettunitService.getTasks(processID);
    }

    //********************************************************** process endpoints **********************************************************

    @PostMapping("/incident/apply")
    public ProcessInstanceResponse applyInterventionRequest(@RequestBody InterventionRequest interventionRequest) {
        return nettunitService.applyInterventionRequest(interventionRequest);
    }

    @GetMapping("/equipe_interne/tasks/")
    public List<TaskDetails> getInternalEquipeTasks() {
        return nettunitService.getInternalEquipeTasks();
    }

    @GetMapping("/gestionnaire/tasks")
    public List<TaskDetails> getGestionnaireTasks() {
        return nettunitService.getGestionnaireTasks();
    }

    @GetMapping("/prefecture/tasks")
    public List<TaskDetails> getPrefectureTasks() {
        return nettunitService.getPrefectureTasks();
    }

    @GetMapping("/pompiers/tasks")
    public List<TaskDetails> getPompiersTasks() {
        return nettunitService.getPompiersTasks();
    }

    //********************************************************** GESTIONNAIRE **********************************************************

    @PostMapping("/gestionnaire/confirmer_notification/{taskID}")
    public void gestionnaire_confirmReceivedNotification(@PathVariable("taskID") String taskID) {
        nettunitService.gestionnaire_confirmReceivedNotification(taskID);
    }

    @PostMapping("/gestionnaire/activer_plan_securite_interne/{taskID}")
    public void gestionnaire_activateInternalSecurityPlan(@PathVariable("taskID") String taskID) {
        nettunitService.gestionnaire_activateInternalSecurityPlan(taskID);
    }

    @PostMapping("/gestionnaire/selection_plan_du_repertoire/{taskID}")
    public void gestionnaire_selectPlanFromRepository(@PathVariable("taskID") String taskID) {
        nettunitService.gestionnaire_selectPlanFromRepository(taskID);
    }

    //****************************************************** DIRIGEANT PREFECTURE ******************************************************
    @PostMapping("/prefecture/confirmer_notification_evaluation/{taskID}")
    public void prefecture_receiveIncidentEvaluation(@PathVariable("taskID") String taskID) {
        nettunitService.prefecture_receiveIncidentEvaluation(taskID);
    }

    //****************************************************** POMPIERS ******************************************************
    @PostMapping("/pompiers/confirmer_notification_evaluation/{taskID}")
    public void firefighter_receiveReport(@PathVariable("taskID") String taskID) {
        nettunitService.firefighter_receiveReport(taskID);
    }

    @PostMapping("/pompiers/envoie_equipe_secours/{taskID}")
    public void firefighter_sendRescueTeam(@PathVariable("taskID") String taskID) {
        nettunitService.firefighter_sendRescueTeam(taskID);
    }

}
