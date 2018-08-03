package application;

import java.util.UUID;

public interface AudienceService {

  UUID createAudience(Audience audience);

  Audience getAudience(UUID uuid);

  void replaceAudience(Audience audience);

  void deleteAudience(UUID uuid);

  void associateMemberToAudience(UUID audienceUUID, UUID memberUUID);

  void disassociateMemberFromAudience(UUID audienceUUID, UUID memberUUID);
}
