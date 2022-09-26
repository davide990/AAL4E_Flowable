package aal4e;

import aal4e.dto.ProcessInstanceRequest;
import aal4e.dto.TaskDetails;
import aal4e.persistence.TaskHistory;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.io.mxCodec;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.DeploymentEntityImpl;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import java.awt.Graphics2D;
import java.io.FileOutputStream;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.view.mxGraph;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AAL4EService {
    private final static Boolean EXPORT_DIAGRAM_IMAGE_TO_FILE = true;

    RuntimeService runtimeService;
    TaskService taskService;
    ProcessEngine processEngine;
    RepositoryService repositoryService;
    TaskHistory history;

    @Autowired
    private Environment env;

    private static Logger logger = LoggerFactory.getLogger(AAL4EService.class);

    @PostConstruct
    void postConstruct() {
        processEngine.getProcessEngineConfiguration().setCreateDiagramOnDeploy(EXPORT_DIAGRAM_IMAGE_TO_FILE);
    }

    /**
     * This method is invoked once the AAL4E WF is shut down.
     */
    @PreDestroy
    private void preDestroy() {
        Boolean deleteUnfinishedInstances =
                Boolean.parseBoolean(env.getProperty("AAL4E.remove_unfinished_processes_on_terminate"));
        if (!deleteUnfinishedInstances) {
            return;
        }
        logger.info("~~~~~~SHUTTING DOWN~~~~~~\nAll unfinished process will be deleted");
        history.getUnfinishedProcessIDs()
                .forEach(pID -> runtimeService.deleteProcessInstance(pID, "[AAL4E] Unfinished process"));
    }

    //********************************************************** deployment service methods ****************************
    public void deployProcessDefinition(String processID, String processDef) {
        //Note: the deployment fails if the resource name does not finish with either ".bpmn" or ".bpmn20.xml"
        Deployment deployment =
                repositoryService
                        .createDeployment()
                        .addString(processID + ".bpmn", processDef)
                        .deploy();

        int numDeployedArtifact = ((DeploymentEntityImpl) deployment).getDeployedArtifacts(ProcessDefinitionEntityImpl.class).size();
        if (numDeployedArtifact > 0) {
            logger.info("SUCCESS: process deployment with id " + processID);
        } else {
            logger.error("FAIL: process deployment with id " + processID);
        }


        saveDiagram(processID);
    }

    /**
     * Save the diagram definition (must be deployed) to image. Note that this doesn't work if the xml diagram does not
     * have the bpmni:Diagram tags.
     *
     * @param processID
     */
    public void saveDiagram(String processID) {
        ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
        ProcessDefinition process = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processID).latestVersion().singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(process.getId());

        BpmnAutoLayout layout = new BpmnAutoLayout(bpmnModel);
        layout.execute();
        mxGraph graph = layout.getGraph();

        mxCodec encoder = new mxCodec();
        Node result = encoder.encode(graph.getModel()); //where graph is the object you are using

        // you can see the diagram by putting the xml code into
        // https://jgraph.github.io/mxgraph/javascript/examples/editors/diagrameditor.html
        //String xml = mxUtils.getXml(result); //now the global variable 'xml' is assigned with the xml value of the graph

        //mxImageCanvas lmm = new mxImageCanvas(mxGraphics2DCanvas())
        try {
            mxGraphToPdfFile(graph);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        //graph.getView().getGraph().drawGraph();
//        mxUtils.writeFile();


//        processDiagramGenerator.generatePngImage(bpmnModel, 1.);
 //       File targetFile = new File("processImage.png");


    }


    void mxGraphToPdfFile(mxGraph graph) throws FileNotFoundException, DocumentException {

        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_OPACITY, 50);
        style.put(mxConstants.STYLE_FONTCOLOR, "#774400");
        stylesheet.putCellStyle("ROUNDED", style);

        graph.setStylesheet(stylesheet);

        mxRectangle bounds = graph.getGraphBounds();
        Document document = new Document(new Rectangle((float) (bounds
                .getWidth()), (float) (bounds.getHeight())));
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream("example.pdf"));
        document.open();
        final PdfContentByte cb = writer.getDirectContent();

        mxGraphics2DCanvas canvas = (mxGraphics2DCanvas) mxCellRenderer
                .drawCells(graph, null, 1, null, new CanvasFactory() {
                    public mxICanvas createCanvas(int width, int height) {
                        Graphics2D g2 = cb.createGraphics(width, height);
                        return new mxGraphics2DCanvas(g2);
                    }
                });

        canvas.getGraphics().dispose();
        document.close();
    }


    public void clearAllExistingDeployments() {
        List<String> previousDeploymentID = repositoryService.createDeploymentQuery().orderByDeploymentId().asc()
                .list().stream().map(x -> x.getId()).collect(Collectors.toList());
        previousDeploymentID.forEach(id -> repositoryService.deleteDeployment(id));
    }


    public void deployProcessDefinition() throws IOException {
        Deployment deployment =
                repositoryService
                        .createDeployment()
                        .addClasspathResource("process.bpmn.xml")
                        .deploy();

        logger.info("process deployed");
    }

    /**
     * Return the list of process definitions loaded into the current flowable instance
     *
     * @return
     */
    public List<ProcessDefinition> getProcessDefinitionsList() {
        return repositoryService.createProcessDefinitionQuery().list();
    }

    public List<ProcessInstance> getActiveProcessInstances(String processDefinitionID) {
        return runtimeService.createProcessInstanceQuery().active().list();
    }

    public List<ProcessDefinition> getActiveProcesses() {
        return repositoryService.createProcessDefinitionQuery().active().list();
    }

    /**
     * Entry point for the emergency plan. This operation creates a new instance of the emegency plan.
     * <p>
     * This is invoked when a new intervention request is inserted manually. This is for development purpose
     * only, as we suppose that plans are activated only when a new jixel event is received
     *
     * @param processInstanceRequest
     * @return
     */
    public void applyInterventionRequest(ProcessInstanceRequest processInstanceRequest) {
        logger.info("~~~~~~~~~~~~~CREATING NEW PLAN INSTANCE~~~~~~~~~~~~~");
        Map<String, Object> variables = new HashMap<>();
        variables.put("description", processInstanceRequest.getRequestDescription());

        // note that for AAL4E is mandatory
        // I replaced the Tee symbol in sequence flow condition with the expression ${myVariable}.
        // If myVariable is unknown, the execution of the plan will fail.
        variables.put("myVariable", true);

        //Start the process
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(processInstanceRequest.getProcessID(), variables);
    }


    /**
     * Return the list of tasks that can be pursued (currently) for the specified process
     *
     * @param processID
     * @return
     */
    public List<TaskDetails> getTasks(String processID) {
        List<TaskDetails> taskDetails = new ArrayList<>();
        for (Task task : taskService.createTaskQuery().processInstanceId(processID).list()) {
            Map<String, Object> processVariables = taskService.getVariables(task.getId());
            taskDetails.add(new TaskDetails(task.getId(), task.getName(), processID, processVariables));
        }
        return taskDetails;
    }


    private List<TaskDetails> getTaskDetails(List<Task> tasks) {
        List<TaskDetails> taskDetails = new ArrayList<>();
        for (Task task : tasks) {
            Map<String, Object> processVariables = taskService.getVariables(task.getId());
            taskDetails.add(new TaskDetails(task.getId(), task.getName(), task.getProcessInstanceId(), processVariables));
        }
        return taskDetails;
    }
}
