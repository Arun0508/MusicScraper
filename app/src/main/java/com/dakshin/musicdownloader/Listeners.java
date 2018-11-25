package com.dakshin.musicdownloader;

import org.json.JSONObject;

import java.io.File;

interface NetworkCallCompleteListener {
    void networkCallComplete(String code,JSONObject object);
}

interface DownloadCompleteListener {
    void onSongsloverDownloadComplete(File file);
}

interface SongsLoverListener {
    void onURLReady(JSONObject song);
    void invalidURL();
}

interface ByteArrayDownloadListener {
    void onByteArrayDownloadComplete(byte[] arr);
}