package io.getstream.example.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationFeedItem {

    private ArrayList<String> usernames;
    private String verb;
    private String first_user_email;
    private String first_user_name;
    private String photo_url;

    /*
        {
          "feed": [
            {
              "payload": {
                "photo_url": "https://android-demo.s3.amazonaws.com/photos/3a98b339-7e81-46f0-9692-68ca9ac19ac6.png",
                "actors": [
                  { "author_email": "josh@getstream.io", "author_name": "josh" },
                  { "author_email": "ian@getstream.io", "author_name": "ian" }
                ]
              },
              "verb": "like"
            },
            {
              "payload": [ { "author_email": "ian@getstream.io", "author_name": "ian" } ],
              "verb": "follow"
            }
          ],
          "newest_activity_id": ""
        }
    */
    public NotificationFeedItem(JSONObject object) {
        this.usernames = new ArrayList<String>();
        try {
            this.verb = object.getString("verb");

            if (this.verb.equals("like")) {
                JSONObject payload = object.getJSONObject("payload");
                this.photo_url = payload.getString("photo_url");
                JSONArray actors = payload.getJSONArray("actors");
                for (int i=0; i < actors.length(); i++) {
                    String actor_email = actors.getJSONObject(i).getString("author_email");
                    String actor_username = actors.getJSONObject(i).getString("author_name");
                    if (i == 0) {
                        this.first_user_email = actor_email;
                        this.first_user_name = actor_username;
                    }
                    this.usernames.add(actor_username);
                }
            }

            if (this.verb.equals("follow")) {
                JSONArray actors = object.getJSONArray("payload");
                for (int i=0; i < actors.length(); i++) {
                    String actor_email = actors.getJSONObject(i).getString("author_email");
                    String actor_username = actors.getJSONObject(i).getString("author_name");
                    if (i == 0) {
                        this.first_user_email = actor_email;
                        this.first_user_name = actor_username;
                    }
                    this.usernames.add(actor_username);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getFirstEmail() {
        return this.first_user_email;
    }
    public String getFirstUsername() {
        return this.first_user_name;
    }
    public ArrayList<String> getUsernames() {
        return this.usernames;
    }
    public String getPhotoUrl() {
        return this.photo_url;
    }
    public String getVerb() {
        return this.verb;
    }
}
