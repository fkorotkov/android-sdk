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
package com.kinvey.sample.contentviewr.file;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.kinvey.java.model.FileMetaData;
import junit.framework.Assert;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author edwardf
 */
public class FileCache {

    private static final String TAG = "kinvey - filecache";
    private static final long CACHE_LIMIT = 5242880L; // 5 mb default


    /**
     * Get a file from the local file cache.  If the file is not present, then this method will return null.
     * </p> the file cache uses a sqlite table internally to maintain metadata about all locally stored files.
     *
     * @param context the current active context of the running application
     * @param id the id of the file to attempt to load
     * @return an `OutputStream` for the file associated with the provided id, or null
     */
    public FileOutputStream get(Context context, String id){
        Assert.assertNotNull("String id cannot be null!", id);
        Assert.assertNotNull("Context context cannot be null!", context);

        FileCacheSqlHelper helper = new FileCacheSqlHelper(context);
        String filename = helper.getFileNameForId(id);

        if (filename == null){
            //file name is not in the metadata table
            return null;
        }

        File cacheDir = context.getCacheDir();
        File cachedFile = new File(cacheDir, filename);

        if (!cachedFile.exists()){
            //file name is in the metadata table, but the file doesn't exist
            //this should never happen, unless the sqlite table has been modified externally.
            return null;
        }

        FileOutputStream ret = null;
        try{
            //ret = new FileOutputStream()


        }catch (Exception e){
            Log.e(TAG, "couldn't load cached file -> " + e.getMessage());
            e.printStackTrace();
        }

        return ret;
    }

    public void save(Context context, FileMetaData meta, byte[] data){
        Assert.assertNotNull("FileMetaData meta cannot be null!", meta);
        Assert.assertNotNull("FileMetaData meta.getId() cannot be null!", meta.getId());
        Assert.assertNotNull("FileMetaData meta.getFileName() cannot be null!", meta.getFileName());
        Assert.assertNotNull("byte[] data cannot be null!", data);

        //insert into database table
        FileCacheSqlHelper helper = new FileCacheSqlHelper(context);
        helper.insertRecord(meta);

        //write to cache dir
        File cacheDir = context.getCacheDir();

        File file = new File(cacheDir, meta.getFileName());
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data);
        }catch (Exception e){
            Log.e(TAG, "couldn't write file to cache -> " + e.getMessage());
            e.printStackTrace();
        }finally {
            try{
                if (os != null){
                    os.flush();
                    os.close();
                }
            }catch(Exception e){}//couldn't clean up, no need to report


        }



        //check size of cachedir
        //delete older files if necessary, until down to threshold size limit
        trimCache(context);









    }

    public void trimCache(Context context){
        new TrimCache().execute(context);
    }

    /**
     * This task will check the size of the cache against a threshold, and remove the oldest files until the threshold is hit.
     *
     *
     */
    private class TrimCache extends AsyncTask<Context, Void, Void> {


        @Override
        protected Void doInBackground(Context ... context) {
            File cachedir = context[0].getCacheDir();

            for (File f : cachedir.listFiles()){

            }




            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }






}