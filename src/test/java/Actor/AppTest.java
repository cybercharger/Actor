package Actor;

import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AppTest {
    static class MockActor extends Actor {
        @Override
        public void onReceive(Object message) throws Exception {
            if(message instanceof String) {
                LogFactory.getLog(MockActor.class).info("Received message: " + message.toString());
                Thread.sleep(1000);
            } else {
                unhandledMessage(message);
            }
        }

        @Override
        protected void reply(UUID messageId) {
            LogFactory.getLog(this.getClass()).info("Message handled: " + messageId);
        }

        @Override
        public UUID tell(Object message, Actor sender) {
            UUID id = super.tell(message, sender);
            LogFactory.getLog(this.getClass()).info("Message sent: " + id);
            return id;
        }
    }    static class MockActor2 extends Actor {
        @Override
        public void onReceive(Object message) throws Exception {
            if(message instanceof String) {
                LogFactory.getLog(MockActor.class).info("Received message: " + message.toString());
                Thread.sleep(1000);
            } else {
                unhandledMessage(message);
            }
        }

        @Override
        protected void reply(UUID messageId) {
            LogFactory.getLog(this.getClass()).info("Message handled: " + messageId);
        }

        @Override
        public UUID tell(Object message, Actor sender) {
            UUID id = super.tell(message, sender);
            LogFactory.getLog(this.getClass()).info("Message sent: " + id);
            return id;
        }
    }
    @Test
    public void testApp() throws InterruptedException, ExecutionException, TimeoutException {
        MockActor a1 = new MockActor();
        MockActor2 a2 = new MockActor2();
        a1.start();
        a2.start();
        a1.tell("Hello a1", a2);
        a2.tell("Hello a2", a1);
        a1.tryStopAndWait(5, TimeUnit.SECONDS);
        a2.tryStopAndWait(5, TimeUnit.SECONDS);
    }
}
