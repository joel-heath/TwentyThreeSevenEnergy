package uk.ac.soton.comp2300.group42.energyclient.services;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static final Map<String, String> users = new HashMap<>();

    static {
        users.put("admin", "admin123");
    }

    //register new user
    public static boolean register(String username, String password){
        if (users.containsKey(username)){
            return false;
        }
        users.put(username, password);
        return true;
    }

    //Authenticate an existing user
    public static boolean login(String username, String password){
        return users.containsKey(username) && users.get(username).equals(password);
    }
}