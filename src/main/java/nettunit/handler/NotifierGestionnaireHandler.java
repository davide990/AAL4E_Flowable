package nettunit.handler;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class NotifierGestionnaireHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("Gestionnaire notifie");
    }
}

