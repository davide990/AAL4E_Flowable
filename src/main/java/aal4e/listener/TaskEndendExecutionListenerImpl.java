package aal4e.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskEndendExecutionListenerImpl implements ExecutionListener {
    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static Logger logger = LoggerFactory.getLogger(TaskEndendExecutionListenerImpl.class);

    @Override
    public void notify(DelegateExecution execution) {
        String myName = execution.getCurrentFlowElement().getName();
        String myID = execution.getId();
        logger.info("[" + DATE_FORMATTER.format(new Date()) + "]  started/ended task: \"" + myName + "\" with ID: " + myID);
    }
}

