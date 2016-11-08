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
    private Integer created_date;
    private Boolean iLikePhoto;
    private Boolean iFollowAuthor;
    private Boolean supressGravatar;
    private Integer photoLikes;

    /*
    {
      "id": "64f3c633-dd3c-404d-ab81-34cf00e89917",
      "author_email": "ian@getstream.io",
      "author_name": "ian",
      "author_id": "9cf34d34-a042-4231-babc-eee6ba67bd18",
      "photo_url": "https://android-demo.s3.amazonaws.com/photos/d326223c-5a01-40a1-a419-dd68292be6c1.png",
      "photo_uuid": "64f3c633-dd3c-404d-ab81-34cf00e89917",
      "doifollow": false,
      "likes": 0,
      "ilikethis": false,
      "created_date": "2016-11-01T11:06:50.5275"
    }
    */
    public FeedItem(JSONObject object) {
        try {
            this.id = object.getString("id");
            this.author_name = object.getString("author_name");
            this.author_email = object.getString("author_email");
            this.author_id = object.getString("author_id");
            this.photo_url = object.getString("photo_url");
            this.photo_uuid = object.getString("photo_uuid");
            this.created_date = object.getInt("created_date");
            this.iLikePhoto = object.getBoolean("ilikethis");
            this.photoLikes = object.getInt("likes");
            this.iFollowAuthor = object.getBoolean("doifollow");
            this.supressGravatar = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public FeedItem(JSONObject object, Boolean supressGravatar) {
        try {
            this.id = object.getString("id");
            this.author_name = object.getString("author_name");
            this.author_email = object.getString("author_email");
            this.author_id = object.getString("author_id");
            this.photo_url = object.getString("photo_url");
            this.photo_uuid = object.getString("photo_uuid");
            this.created_date = object.getInt("created_date");
            this.iLikePhoto = object.getBoolean("ilikethis");
            this.photoLikes = object.getInt("likes");
            this.iFollowAuthor = object.getBoolean("doifollow");
            this.supressGravatar = supressGravatar;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public FeedItem(
            String author_id,
            String author_name,
            String author_email,
            Integer created_date,
            Boolean iLikePhoto,
            Integer photoLikes,
            Boolean iFollowAuthor,
            String photoUrl,
            String photoUUID,
            Boolean suppressGravatar
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
    public Integer getCreatedDate() {
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
    public Boolean getSupressGravatar() {
        return this.supressGravatar;
    }

    public void setPhotoLikes(Integer val) {
        this.photoLikes = val;
    }
    public void setILikePhoto(Boolean val) {
        this.iLikePhoto = val;
    }
}
