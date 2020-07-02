package edu.skku.map.personal;

import java.util.HashMap;
import java.util.Map;

public class LoginService {
    public String username;
    public String password;

    public LoginService(){}

    public LoginService(String username, String password){
        this.username = username;
        this.password = password;

    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("username",username);
        result.put("password",password);

        return result;
    }
}
