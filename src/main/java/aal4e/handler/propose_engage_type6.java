package aal4e.handler;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class propose_engage_type6  implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String className = this.getClass().getSimpleName();
        System.out.println("Executing capability: " + className);

        //execution.getVariable("hello")

        //throw new BpmnError("REQUIRE_ORCHESTRATION");
    }
}