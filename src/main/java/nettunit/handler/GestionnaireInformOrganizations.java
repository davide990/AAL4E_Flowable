package nettunit.handler;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class GestionnaireInformOrganizations  implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("TACHE GESTIONNAIRE INFORMER ORG SECOURS");
    }
}
