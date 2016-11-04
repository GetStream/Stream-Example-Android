package io.getstream.example.models;

import org.json.JSONException;
import org.json.JSONObject;


public class User {

    private String uuid;
    private String username;
    private String email;
    private String created_date;
    private boolean do_i_follow;

    /* [
    "users": [
    {
      "uuid": "9cf34d34-a042-4231-babc-eee6ba67bd18",
      "username": "ian",
      "email": "ian@getstream.io",
      "doifollow": false
    },
    */
    public User(JSONObject object) {
        try {
            this.uuid = object.getString("uuid");
            this.username = object.getString("username");
            this.email = object.getString("email");
            this.do_i_follow = object.getBoolean("doifollow");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public User(String uuid, String username, String email, boolean do_i_follow) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.do_i_follow = do_i_follow;
    }

    public String getUUID() {
        return this.uuid;
    }
    public String getUsername() {
        return this.username;
    }
    public String getEmail() {
        return this.email;
    }
    public boolean getDoIFollow() {
        return this.do_i_follow;
    }

//    public void setUUID(String val) {
//        this.uuid = val;
//    }
//    public void setUsername(String val) {
//        this.username = val;
//    }
//    public void setEmail(String val) {
//        this.email = val;
//    }
//    public void setDoIFollow(boolean val) {
//        this.do_i_follow = val;
//    }
}
