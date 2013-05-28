package Actor;

/**
 * User: Chris
 * Date: 5/28/13
 * Time: 11:21 PM
 */
public interface MessageReceiver {
    void onReceive(Object message) throws Exception;
}
