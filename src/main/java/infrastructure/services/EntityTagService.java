package infrastructure.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.ws.rs.core.EntityTag;

import org.jvnet.hk2.annotations.Service;

import java.nio.charset.Charset;

/**
 * @author jonfreer
 */
@Service
public class EntityTagService implements infrastructure.EntityTagService {

	/**
	 * Calculates an entity tag based on the provided representation.
	 * @param entity The representation that the generated entity tag is based off of.
	 * @return An instance of {@link EntityTag}, representing the entity tag generated.
	 */
	@Override
	public <T> EntityTag generateTag(T entity) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			// need to switch this to not use .toString(); it doesn't do a robust enough job at detecting changes in object state.
			byte[] bytesMD5 = digest.digest(entity.toString().getBytes(Charset.forName("UTF-8")));
			String entityTagStringBase64Encoded = 
				Base64.getEncoder().encodeToString(bytesMD5);
			
			return new EntityTag(entityTagStringBase64Encoded);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
