package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.preferences.PreferencesResponse;
import uk.ac.soton.comp2300.group42.user.UserResponse;

@Singleton
public class UserClient extends BaseApiClient {

    @Inject
    public UserClient(AuthenticatedHttpClient httpClient, ObjectMapper mapper) {
        super(httpClient, mapper);
    }

    public UserResponse findCurrentUser() {
        return get("users/me", new TypeReference<>() {});
    }

    public PreferencesResponse findPreferences() {
        return get("users/me/preferences", new TypeReference<>() {});
    }
}