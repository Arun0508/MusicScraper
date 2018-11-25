package com.dakshin.musicdownloader;

import org.json.JSONObject;

public interface NetworkCallCompleteListener {
    public void networkCallComplete(String code,JSONObject object);
}
