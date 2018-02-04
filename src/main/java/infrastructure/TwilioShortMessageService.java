package infrastructure;

import javax.inject.Inject;
import javax.inject.Named;

import org.jvnet.hk2.annotations.Service;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import domain.MessageStatus;

/**
 *	Represents the Twilio SMS implementation. This service is responsible
 *	for directly interfacing with Twilio APIs to send text messages.
 */
@Service
public final class TwilioShortMessageService implements ShortMessageService {

	private final String accountSID;
	private final String authToken;
	private final MetricRegistry metricRegistry;
	private final Meter createTwilioMessageMeter;
	private final Timer createTwilioMessageTimer;

	/**
	 *	Construct a {@link TwilioShortMessageService} instance.
	 *	@param accountSID	Used to exercise the REST API.
	 *	@param authToken	Used by Twilio to authenticate requests.
	 */
	@Inject
	public TwilioShortMessageService(
		final MetricRegistry metricRegistry,
		@Named("SMS_ACCOUNT_SID") final String accountSID, 
		@Named("SMS_AUTH_TOKEN") final String authToken
	) {
		this.metricRegistry = metricRegistry;
		this.createTwilioMessageMeter = 
			this.metricRegistry.meter(
				MetricRegistry.name(
					TwilioShortMessageService.class,
					"createTwilioMessageMeter"
				)
			);
		this.createTwilioMessageTimer = 
			this.metricRegistry.timer(
				MetricRegistry.name(
					TwilioShortMessageService.class,
					"twilio",
					"message",
					"create"
				)
			);
		this.accountSID = accountSID;
		this.authToken = authToken;
	}

	/**
	 *	Sends the provided message from the phone number provided to the phone numbers provided.
	 *	@param from		The phone number that the message is sent from.
	 *	@param to		The  phone number that the message is sent to.
	 *	@param content	The message content that is sent.
	 */
	@Override
	public domain.Message send(final domain.Message message) {
		final Timer.Context timerContext = createTwilioMessageTimer.time();
		Twilio.init(this.accountSID, this.authToken);

		Message twilioMessage = 
			Message.creator(
				new PhoneNumber(message.getTo().toE164()),
				new PhoneNumber(message.getFrom().toE164()),
				message.getContent()
			).create();
		timerContext.stop();
		
		//insert metric here to indicate how many messages are sent to Twilio.
		this.createTwilioMessageMeter.mark();
		System.out.println(twilioMessage.getStatus());
		
		MessageStatus status;
		
		switch (twilioMessage.getStatus()){
		case QUEUED:
		case SENDING:
			status = MessageStatus.PENDING;
			break;
		case SENT:
			status = MessageStatus.SENT;
			break;
		case DELIVERED:
			status = MessageStatus.DELIVERED;
			break;
		default:
			status = MessageStatus.FAILED;
		}
		
		//should be a factory here?
		message.setExternalId(twilioMessage.getSid());
		message.setStatus(status);
		return message;
	}

	/**
	 *	Sends the provided message from the phone number provided to the phone numbers provided.
	 *	@param from		The phone number that the message is sent from.
	 *	@param to		The collection of phone numbers that the message is sent to.
	 *	@param content	The message content that is sent.
	 *
	@Override
	public domain.Message send(Set<domain.Message> messages) {
		for (domain.Message message : messages) {
			this.send(message);
		}
	}*/
}
