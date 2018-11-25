package com.dakshin.musicdownloader;

import java.io.File;

interface DownloadCompleteListener {
    public void onDownloadComplete(File file);
}
