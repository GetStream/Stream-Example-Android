package io.getstream.example.models;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedItem {

    private String id;
    private String author_name;
    private String author_email;
    private String author_id;
    private String photo_url;
    private String created_date;

    // {
    // "id":"1",
    // "author_email":"ian.douglas@iandouglas.com",
    // "author_name":"ian",
    // "author_id":"67aa3e30-80a9-4410-8836-15d0a1d78993",
    // "photo_url":"https://dvqg2dogggmn6.cloudfront.net/images/stream_logo.svg",
    // "created_date":"2016-10-17"
    // }
    public FeedItem(JSONObject object) {
        try {
            this.id = object.getString("id");
            this.author_name = object.getString("author_name");
            this.author_email = object.getString("author_email");
            this.author_id = object.getString("author_id");
            this.photo_url = object.getString("photo_url");
            this.created_date = object.getString("created_date");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public FeedItem(String author_name, String author_id, String author_email) {
        this.author_name = author_name;
        this.author_email = author_email;
        this.author_id = author_id;
    }

    public String getId() {
        return this.id;
    }

    public String getAuthorName() {
        return this.author_name;
    }
    public String getAuthorEmail() {
        return this.author_email;
    }
    public String getAuthorId() {
        return this.author_id;
    }
    public String getPhotoUrl() {
        return this.photo_url;
    }
    public String getCreatedDate() {
        return this.created_date;
    }

    public void setAuthorName(String val) {
        this.author_name = val;
    }
    public void setAuthorEmail(String val) {
        this.author_email = val;
    }
    public void setAuthorId(String val) {
        this.author_id = val;
    }
    public void setPhotoUrl(String val) {
        this.photo_url = val;
    }
    public void setCreatedDate(String val) {
        this.created_date = val;
    }

}
