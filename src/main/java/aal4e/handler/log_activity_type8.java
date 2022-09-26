package aal4e.handler;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class log_activity_type8  implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String className = this.getClass().getSimpleName();
        System.out.println("Executing capability: " + className);
        //throw new BpmnError("REQUIRE_ORCHESTRATION");
    }
}
