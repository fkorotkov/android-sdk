/*
 * Copyright (c) 2013 Kinvey Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.kinvey.sample.contentviewr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.kinvey.android.Client;
import com.kinvey.java.core.DownloaderProgressListener;
import com.kinvey.java.core.MetaDownloadProgressListener;
import com.kinvey.java.core.MediaHttpDownloader;
import com.kinvey.java.model.FileMetaData;
import com.kinvey.sample.contentviewr.file.FileCache;
import com.kinvey.sample.contentviewr.model.ContentItem;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author edwardf
 */
public class SourceFactory {


    public void asyncLoadThumbnail(final Client client, final ContentItem item, final ArrayAdapter adapter){
        switch(item.getThumbnail().getType()){
            case FILE:

                FileCache cache = new FileCache(Contentviewr.cacheLocation);
                FileInputStream in = cache.get(client.getContext(), item.getThumbnail().getReference());
                if (in != null){
                    if (adapter == null || item == null){
                        return;
                    }
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;

                    Bitmap ret = BitmapFactory.decodeStream(in, null, options);
                    Log.i("source", "" + ret.getByteCount());
                    item.setThumbnailImage(ret);
                    adapter.notifyDataSetChanged();
                    return;

                }

                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                FileMetaData meta = new FileMetaData(item.getThumbnail().getReference());
                MetaDownloadProgressListener dpl =  new MetaDownloadProgressListener() {

                    @Override
                    public void progressChanged(MediaHttpDownloader downloader) throws IOException {}

                    @Override
                    public void onSuccess(Void result) {
                        if (adapter == null || item == null){
                            return;
                        }

                        FileCache cache = new FileCache(Contentviewr.cacheLocation);
                        byte[] outarray = out.toByteArray();

                        Log.i("source factory", "cache array size: " + outarray.length);
                        Log.i("source factory", "cache metadata: " + (getMetadata() != null));


                        if (getMetadata() != null){
                            cache.save(client.getContext(), client, getMetadata(), outarray);
                        }

                        Bitmap ret = BitmapFactory.decodeByteArray(outarray, 0, outarray.length);
                        item.setThumbnailImage(ret);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Throwable error) {}

//                    @Override
//                    public void metaDataRetrieved(FileMetaData metadata) {
//                        currentData.setFileName(metadata.getFileName());
//
//                    }
                };

                client.file().download(meta, out, dpl);



                break;
            case WEBSITE:

                new AsyncTask<ArrayAdapter, Void, ArrayAdapter>(){

                    @Override
                    protected ArrayAdapter doInBackground(ArrayAdapter ... adapter) {
                        item.setThumbnailImage(getBitmapFromURL(item.getThumbnail().getReference()));
                        return adapter[0];

                    }

                    @Override
                    protected void onPostExecute(ArrayAdapter adapter){
                        if (adapter != null){
                            adapter.notifyDataSetChanged();
                        }
                    }


                }.execute(adapter);

                break;

        }



    }


    private static void loadFile(Client client, final ContentItem item, final ArrayAdapter adapter){}
    private static void loadWebsite(Client client, final ContentItem item, final ArrayAdapter adapter){}

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.i(Contentviewr.TAG, "got image, for setting image " + src);
            return myBitmap;
        } catch (IOException e) {
            Log.i(Contentviewr.TAG, "cant be setting image!" + e);
            e.printStackTrace();
            return null;
        }
    }



}
