package infrastructure;

import domain.Message;

import org.jvnet.hk2.annotations.Contract;

/**
 *	Defines the contract for all classes that wish to represent a service for sending text messages.
 */
@Contract
public interface ShortMessageService {

	/**
	 *	Sends the provided message from the phone number provided to the phone number provided.
	 *	@param from		The phone number that the message is sent from.
	 *	@param to		The phone number that the message is sent to.
	 *	@param message	The message content that is sent.
	 */
	Message send(final Message message);

	/** Sends the provided message from the phone number provided to the phone numbers provided.
	 *	@param from		The phone number that the message is sent from.
	 *	@param to		The collection of phone numbers that the message is sent to.
	 *	@param message	The message content that is sent.
	 *
	Message send(Set<Message> messages);
	*/
}
