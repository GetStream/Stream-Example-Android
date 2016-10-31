package io.getstream.example.models;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedItem {

    private String id;
    private String author_name;
    private String author_email;
    private String author_id;
    private String photo_url;
    private String photo_uuid;
    private String created_date;
    private Boolean iLikePhoto;
    private Boolean iFollowAuthor;
    private Integer photoLikes;

    /*
    {
      "id": "cb18ab6b-4b97-4136-b5e8-b7433969da18",
      "author_email": "josh@getstream.io",
      "author_name": "josh",
      "author_id": "03a1cfed-3590-4aa8-a592-f78bc71ccfbd",
      "photo_url": "https://android-demo.s3.amazonaws.com/photos/d25855d1-59ef-43db-b6b6-35a8c01db543.png",
      "photo_uuid": "cb18ab6b-4b97-4136-b5e8-b7433969da18",
      "doifollow": false,
      "likes": 0,
      "ilikethis": false,
      "created_date": "2016-10-30T16:01:51.80732"
    },
    */
    public FeedItem(JSONObject object) {
        try {
            this.id = object.getString("id");
            this.author_name = object.getString("author_name");
            this.author_email = object.getString("author_email");
            this.author_id = object.getString("author_id");
            this.photo_url = object.getString("photo_url");
            this.photo_uuid = object.getString("photo_uuid");
            this.created_date = object.getString("created_date");
            this.iLikePhoto = object.getBoolean("ilikethis");
            this.photoLikes = object.getInt("likes");
            this.iFollowAuthor = object.getBoolean("doifollow");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public FeedItem(
            String author_id,
            String author_name,
            String author_email,
            String created_date,
            Boolean iLikePhoto,
            Integer photoLikes,
            Boolean iFollowAuthor,
            String photoUrl,
            String photoUUID
    ) {
        this.author_id = author_id;
        this.author_name = author_name;
        this.author_email = author_email;
        this.created_date = created_date;
        this.iLikePhoto = iLikePhoto;
        this.photoLikes = photoLikes;
        this.photo_url = photoUrl;
        this.iFollowAuthor = iFollowAuthor;
    }

    public String getId() {
        return this.id;
    }
    public String getAuthorId() {
        return this.author_id;
    }
    public String getAuthorName() {
        return this.author_name;
    }
    public String getAuthorEmail() {
        return this.author_email;
    }
    public String getCreatedDate() {
        return this.created_date;
    }
    public String getPhotoUrl() {
        return this.photo_url;
    }
    public String getPhotoUUID() {
        return this.photo_uuid;
    }
    public Integer getPhotoLikes() {
        return this.photoLikes;
    }
    public Boolean getILikePhoto() {
        return this.iLikePhoto;
    }
    public Boolean getIFollowAuthor() {
        return this.iFollowAuthor;
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
