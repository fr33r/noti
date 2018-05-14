package infrastructure;

import domain.Message;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface MessageQueueService {

	void send(Message message);
}
