package io.getstream.example.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AggregatedFeedItem {

    private ArrayList<String> photo_url_list;
    private String author_email;
    private String author_name;
    private Integer created_date;

    /*
        {
          "feed": [
            {
              "author_email": "nick@getstream.io",
              "author_name": "nick",
              "author_id": "7eadc152-dea3-44d2-b0f5-d7fbf94e5a15",
              "photos": [
                "https://android-demo.s3.amazonaws.com/photos/3a98b339-7e81-46f0-9692-68ca9ac19ac6.png",
                "https://android-demo.s3.amazonaws.com/photos/82a386ea-c07a-4469-9c09-e015129669d3.png"
              ],
              "created_date": "2016-11-01T13:50:26.620000"
            },
            {
              "author_email": "josh@getstream.io",
              "author_name": "josh",
              "author_id": "03a1cfed-3590-4aa8-a592-f78bc71ccfbd",
              "photos": [
                "https://android-demo.s3.amazonaws.com/photos/1ddf18ce-9ee1-4863-9e24-aa1a30da942e.png",
                "https://android-demo.s3.amazonaws.com/photos/476ac193-8f03-425c-81ca-04d7c55bd9fd.png"
              ],
              "created_date": "2016-11-01T11:06:32.798000"
            }
          ],
          "newest_activity_id": "",
          "uuid": "9cf34d34-a042-4231-babc-eee6ba67bd18"
        }
    */
    public AggregatedFeedItem(JSONObject object) {
        this.photo_url_list = new ArrayList<String>();
        try {
            this.author_email = object.getString("author_email");
            this.author_name = object.getString("author_name");
            this.created_date = object.getInt("created_date");

            JSONArray photos = object.getJSONArray("photos");
            for (int i=0; i < photos.length(); i++) {
                String photo_url = photos.getString(i);
                this.photo_url_list.add(photo_url);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAuthorEmail() {
        return this.author_email;
    }
    public String getAuthorName() {
        return this.author_name;
    }
    public ArrayList<String> getPhotoURLs() {
        return this.photo_url_list;
    }
    public Integer getCreatedDate() {
        return this.created_date;
    }
}
