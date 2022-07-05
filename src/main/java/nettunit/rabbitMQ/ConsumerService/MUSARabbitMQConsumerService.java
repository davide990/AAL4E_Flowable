package nettunit.rabbitMQ.ConsumerService;

import RabbitMQ.Consumer.MUSARabbitMQConsumer;
import RabbitMQ.JixelEvent;
import RabbitMQ.JixelEventReport;
import RabbitMQ.JixelEventUpdate;
import RabbitMQ.Listener.MUSAConsumerListener;
import RabbitMQ.Recipient;
import nettunit.NettunitService;
import nettunit.rabbitMQ.PendingMessageComponentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Some;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MUSARabbitMQConsumerService {

    private static int MAXIMUM_CONSUMER_MESSAGES_COUNT = 100000;

    public Optional<PendingMessageComponentListener> listener;

    private static MUSARabbitMQConsumer consumer = new MUSARabbitMQConsumer();

    /**
     * This map stores all the MUSA events/updates/reports sent by Jixel until the message has been acknowledged from
     * MUSA.
     */
    private Map<Object, String> pendingMessages;

    private Thread consumerTask;

    @Autowired
    NettunitService nettunitService;

    private static Logger logger = LoggerFactory.getLogger(MUSARabbitMQConsumerService.class);

    @Autowired
    public MUSARabbitMQConsumerService() {
        pendingMessages = new HashMap<>();
        /**
         * Create a new consumer for the events that are consumed by MUSA (produced by Jixel).
         * The consumer is associated to a listener, whose methods are invoked when a message produced by jixel is
         * consumed by musa.
         */
        consumerTask = new Thread(() -> {
            consumer.init();
            consumer.startConsumerAndAwait(MAXIMUM_CONSUMER_MESSAGES_COUNT, new Some<>(new MUSAConsumerListener() {
                @Override
                public void onNotifyEvent(JixelEvent event) {
                    //TODO here, should I create a new process instance?
                    nettunitService.applyInterventionRequest(event);
                }

                @Override
                public void onEventUpdate(JixelEventUpdate update) {
                    //Check for the activity requiring an update
                }

                @Override
                public void onReceiveJixelReport(JixelEventReport report) {
                    //Check for the activity requiring a report
                }

                @Override
                public void onAddRecipient(Recipient r) {

                }
            }));
        });
    }

    @PostConstruct
    public void init() {
        consumerTask.start();
    }

    private void completeTask(Object obj) {

    }

    public void setListener(PendingMessageComponentListener listener) {
        this.listener = Optional.of(listener);
    }

    public boolean remove(Object obj) {
        String pendingTaskID = pendingMessages.getOrDefault(obj, "");
        if (!pendingTaskID.isEmpty()) {
            pendingMessages.remove(obj);
            return true;
        }
        return false;
    }

    public void save(Object obj, String taskID) {
        pendingMessages.put(obj, taskID);
    }
}
