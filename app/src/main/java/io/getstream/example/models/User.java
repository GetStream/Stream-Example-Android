package io.getstream.example.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by id on 10/25/16.
 */

public class User {

    private String uuid;
    private String username;
    private String email;
    private String created_date;

    /* [
    {
        "ID": 1,
            "CreatedAt": "2016-10-25T18:33:16Z",
            "UpdatedAt": "2016-10-25T18:33:16Z",
            "DeletedAt": null,
            "uuid": "a98b9a61-0a39-4cdc-ae32-f02e07c5f30d",
            "username": "ian",
            "email": "ian@getstream.io"
    },
    */
    public User(JSONObject object) {
        try {
            this.uuid = object.getString("uuid");
            this.username = object.getString("username");
            Log.i("user-model-email", object.getString("email"));
            this.email = object.getString("email");
            this.created_date = object.getString("created_date");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public User(String uuid, String username, String email) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
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
    public String getCreatedDate() {
        return this.created_date;
    }

    public void setUUID(String val) {
        this.uuid = val;
    }
    public void setUsername(String val) {
        this.username = val;
    }
    public void setEmail(String val) {
        this.email = val;
    }
    public void setCreatedDate(String val) {
        this.created_date = val;
    }

}
