package com.wormoscorp.ieaapp;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender  {

    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Activity mActivity;
    int number;
    String togo;
    String ownerKey;
    String chatKey;
    String eventType;

    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey ="AAAA9WJ23gM:APA91bEsqJBW9F1MQ6-GwZOQmi8G460AMTaVLYQx2hlvVZJjfuxzwIGT2Abcq00HZiq3Eqjspb9x5DfCnZlHTDvU7lNJL5f3ESsooNm6X3or01f5GSx07CDE-xFvt0-vXDI-haIo4RsJ";



    public FcmNotificationsSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity, String togo, String ownerKey, String chatKey) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.togo = togo;
        this.ownerKey = ownerKey;
        this.chatKey = chatKey;
    }

    public FcmNotificationsSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity, String togo, String ownerKey, String chatKey, String eventType) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.togo = togo;
        this.ownerKey = ownerKey;
        this.chatKey = chatKey;
        this.eventType = eventType;
    }

    public void SendNotifications() {

        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            notiObject.put("icon", R.drawable.iea_logo); // enter icon that exists in drawable only
            notiObject.put("number", number);

            mainObj.put("notification", notiObject);

            JSONObject extraData = new JSONObject();
            extraData.put("activity",togo);
            extraData.put("ownerKey",ownerKey);
            extraData.put("chatKey",chatKey);
            extraData.put("eventType",eventType);
            mainObj.put("data",extraData);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;

                }
            };
            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
