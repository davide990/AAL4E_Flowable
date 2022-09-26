package com.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class myAdaptationClass implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("ADAPTATION TASK");
    }
}
