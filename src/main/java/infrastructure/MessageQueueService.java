package infrastructure;

import domain.Notification;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface MessageQueueService {

  void send(Notification notification, Integer messageID) throws Exception;
}
