package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;
import uk.ac.soton.comp2300.group42.preferences.PreferencesResponse;
import uk.ac.soton.comp2300.group42.user.UserResponse;

@Singleton
public class UserClient extends BaseApiClient {

    @Inject
    public UserClient(AuthenticatedHttpClient httpClient, @BackendMapper JsonMapper mapper) {
        super(httpClient, mapper);
    }

    public UserResponse fetchMe() {
        return get("users/me", new TypeReference<>() {});
    }

    public UserResponse putMe(UserResponse request) {
        return put("users/me", request, new TypeReference<>() {});
    }

    public void deleteMe() {
        delete("users/me");
    }

    public PreferencesResponse fetchMyPreferences() {
        return get("users/me/preferences", new TypeReference<>() {});
    }

    public PreferencesResponse putMyPreferences(PreferencesResponse request) {
        return put("users/me/preferences", request, new TypeReference<>() {});
    }
}