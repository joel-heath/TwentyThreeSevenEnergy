package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;
import uk.ac.soton.comp2300.group42.preferences.PreferencesResponse;
import uk.ac.soton.comp2300.group42.user.UserResponse;

@Singleton
public class UserClient extends BaseApiClient {

    @Inject
    public UserClient(AuthenticatedHttpClient httpClient, @BackendMapper ObjectMapper mapper) {
        super(httpClient, mapper);
    }

    public UserResponse fetchMe() {
        return get("users/me", new TypeReference<>() {});
    }

    public PreferencesResponse fetchMyPreferences() {
        return get("users/me/preferences", new TypeReference<>() {});
    }
}