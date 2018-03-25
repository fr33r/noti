package infrastructure.services;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.inject.Inject;
import javax.ws.rs.core.EntityTag;

import org.jvnet.hk2.annotations.Service;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

/**
 * @author jonfreer
 */
@Service
public class EntityTagService implements infrastructure.EntityTagService {

	private final Tracer tracer;

	@Inject
	public EntityTagService(Tracer tracer) {
		this.tracer = tracer;
	}

	/**
	 * Calculates an entity tag based on the provided representation.
	 *
	 * @param entity The representation that the generated entity tag is based off of.
	 * @return An instance of {@link EntityTag}, representing the entity tag generated.
	 */
	@Override
	public <T> EntityTag generateTag(T entity) {
		Span span =
			this.tracer
				.buildSpan("EntityTagService#generateTag")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			// need to switch this to not use .toString(); it doesn't do a robust enough job at detecting changes in object state.
			byte[] bytesMD5 = digest.digest(entity.toString().getBytes(Charset.forName("UTF-8")));
			String entityTagStringBase64Encoded =
				Base64.getEncoder().encodeToString(bytesMD5);

			return new EntityTag(entityTagStringBase64Encoded);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} finally {
			span.finish();
		}
	}

	@Override
	public EntityTag generateTag(String nodeName, Long revision) {
		Span span =
			this.tracer
				.buildSpan("EntityTagService#generateTag")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(Scope scope = this.tracer.scopeManager().activate(span, false)) {
			String input = nodeName + revision;
			byte[] inputBytes = input.getBytes(Charset.forName("UTF-8"));
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytesMD5 = digest.digest(inputBytes);
			String entityTagStringBase64Encoded =
				Base64.getEncoder().encodeToString(bytesMD5);

			return new EntityTag(entityTagStringBase64Encoded);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} finally {
			span.finish();
		}
	}
}
