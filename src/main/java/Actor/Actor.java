package Actor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * User: Chris
 * Date: 5/28/13
 * Time: 11:22 PM
 */
public abstract class Actor implements MessageReceiver {
    private static Log log = LogFactory.getLog(Actor.class);
    private ExecutorService receiverRunner = Executors.newSingleThreadExecutor();
    private Queue<QueueMessage> messageQueue = new ConcurrentLinkedQueue<QueueMessage>();
    private Semaphore queueSemaphore = new Semaphore(0, true);
    private Future future;

    protected Actor() {
    }

    public void start() {
        final MessageReceiver receiver = this;
        future = receiverRunner.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                boolean shutDown = false;
                while (!shutDown) {
                    while (!messageQueue.isEmpty()) {
                        queueSemaphore.acquire();
                        QueueMessage message = messageQueue.poll();
                        Object msg = message.getMessage();
                        if (msg.equals(PredefinedMessage.PoisonPill)) {
                            shutDown = true;
                            break;
                        }

                        receiver.onReceive(message.getMessage());
                        if (message.getSender() != null) {
                            message.getSender().reply(message.getId());
                        }
                    }
                }
                return null;
            }
        });
    }

    public void tryStop() {
        if (isStopped()) return;
        this.tell(PredefinedMessage.PoisonPill, null);
    }

    public void tryStopAndWait(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        tryStop();
        future.get(timeout, timeUnit);
    }

    public boolean isStopped() {
        return (future == null) || (future.isCancelled() || future.isDone());
    }


    public UUID tell(Object message, Actor sender) {
        if (message == null) throw new IllegalArgumentException("message is null");
        QueueMessage qm = new QueueMessage(message, sender);
        messageQueue.add(qm);
        queueSemaphore.release();
        return qm.getId();
    }

    protected void reply(UUID messageId) {
    }

    protected void unhandledMessage(Object message) {
        //TODO: implement unhandled message queue for trouble-shooting
    }
}
