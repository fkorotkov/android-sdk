/*
 * Copyright (c) 2014, Kinvey, Inc.
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

import com.kinvey.java.Logger;
import com.kinvey.java.core.DownloaderProgressListener;
import com.kinvey.java.core.MediaHttpDownloader;
import com.kinvey.java.dto.User;
import com.kinvey.java.model.FileMetaData;
import com.kinvey.java.store.StoreType;
import com.kinvey.nativejava.Client;
import com.kinvey.nativejava.store.DataStore;
import com.kinvey.nativejava.store.FileStore;
import com.kinvey.nativejava.store.UserStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author edwardf
 */
public class HelloWorld {

//    public static final String appKey = "kid_WJxJbK9kC";
//    public static final String appSecret = "f16df5c4be864d34aff7d1cc962f20b9";

    public static final String appKey = "kid_H1lH5Dsw";
    public static final String appSecret = "cc5c72d261d8473590a0fa01024fb313";

    static String FILE_ID_TEST = "dc7e6092-1b6e-404a-9ea5-b623088f0f05";

    public static void main(String[] args) {
        System.out.println("Hello World");

        Client client = new Client.Builder(appKey, appSecret)
                //.setBaseUrl("https://v3yk1n-kcs.kinvey.com")
                .build();
        Logger.configBuilder().all();
        client.enableDebugLogging();

        boolean ping = false;
        try {
            ping = client.ping();
        } catch (Exception e) {
        }
        System.out.println("Client ping -> " + ping);

        try {
            User user = UserStore.login("test", "test", client);
            System.out.println("Client login -> " + client.isUserLoggedIn());
            if (user != null && user.getUsername() != null) {
                System.out.println("Username -> " + user.getUsername());
            }
        } catch (IOException e) {
            System.out.println("Couldn't login -> " + e);
            e.printStackTrace();
        }

        DataStore<HelloEntity> dataStore = DataStore.collection(HelloEntity.COLLECTION, HelloEntity.class, StoreType.NETWORK, client);
        HelloEntity entity = new HelloEntity();
        entity.setName("test_name_" + System.currentTimeMillis());
        HelloEntity he = null;
        try {
            he = dataStore.save(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (he != null) {
            System.out.println("Saved entity:" + he.getName());
        }

        try {
            List<HelloEntity> list = dataStore.find();
            if (list != null) {
                for (HelloEntity helloEntity : list) {
                    System.out.println("Got entity: " + helloEntity.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            testFileStore(client);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void testFileStore(Client client) throws IOException {
        // TODO: 03.11.2016 FileStoreTesting
        FileMetaData fm_temp = null;
        FileMetaData fileMetaData = null;
        FileStore fileStore = client.getFileStore(StoreType.NETWORK);
        fm_temp = fileStore.find(FILE_ID_TEST, null);

        if (fm_temp != null) {
            final File outputFile = new File("D:\\Kinvey\\", fm_temp.getFileName());
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            final FileOutputStream fos = new FileOutputStream(outputFile);

            fileMetaData = fileStore.download(fm_temp, fos, null, new DownloaderProgressListener() {
                public void progressChanged(MediaHttpDownloader mediaHttpDownloader) throws IOException {

                }
            });
        }

        System.out.println(fileMetaData != null ? "Success -> File: " + fileMetaData.getFileName() : "Fail -> File was not found");
    }










/*            for (int i = 0; i < 10; i++) {
                entity = appData.getBlocking(new Query().equals("_id", entityId)).execute()[0];
                System.out.println("Found:" + entity);
                GenericJson saved = appData.saveBlocking(entity).execute();
                System.out.println("Saved:" + saved);
            }*/

//        GenericJson first = new GenericJson();
//        first.put("_id", "1");
//        first.put("encode", "=");
//        GenericJson second = new GenericJson();
//        second.put("_id", "2");
//        second.put("encode", "\u00AD");
//
//        try {
//            client.appData("encoder", GenericJson.class).saveBlocking(first).execute();
//            client.appData("encoder", GenericJson.class).saveBlocking(second).execute();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        GenericJson firstUp = new GenericJson();
//        GenericJson secondUp = new GenericJson();
//        try {
//            firstUp = client.appData("encoder", GenericJson.class).getEntityBlocking("1").execute();
//            secondUp = client.appData("encoder", GenericJson.class).getEntityBlocking("2").execute();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        System.out.println("first: " + firstUp.get("encode"));
//        System.out.println("second: " + secondUp.get("encode"));


//        AppData<GenericJson[]> ok = client.appData("00CCUZones", GenericJson[].class);
//        ArrayList<String> fields = new ArrayList<String>();
//        fields.add("room_id");
//        try {
//            GenericJson[] e = ok.countBlocking(fields, null).execute();
//        }catch (Exception e){
//            System.out.println("Couldn't count! -> " + e);
//            e.printStackTrace();
//        }

//
//
//        HelloEntity test = new HelloEntity();
//        test.setSomedata("hello");
//        String id = "";
//        try{
//            HelloEntity saved = client.appData("native", HelloEntity.class).saveBlocking(test).execute();
//            System.out.println("Client appdata saved -> " + saved.getName());
//            id = saved.getName();
//        }catch (IOException e){
//            System.out.println("Couldn't save! -> " + e);
//            e.printStackTrace();
//        }
//
//        try{
//            HelloEntity loaded = client.appData("native", HelloEntity.class).getEntityBlocking(id).execute();
//            System.out.println("Client appdata loaded by id -> " + loaded.getName());
//        }catch (IOException e){
//            System.out.println("Couldn't load! -> " + e);
//            e.printStackTrace();
//        }
//
//        try{
//            HelloEntity[] loaded = client.appData("native", HelloEntity.class).getBlocking(new String[]{id}).execute();
//            System.out.println("Client appdata loaded by query -> " + loaded.length);
//        }catch (IOException e){
//            System.out.println("Couldn't load! -> " + e);
//            e.printStackTrace();
//        }
//
//        try{
//
//        	FileMetaData[] metas = client.file().prepDownloadBlocking(new Query()).execute();
//        	FileMetaData[] metaSort = client.file().prepDownloadBlocking(new Query().addSort("_id", SortOrder.ASC)).execute();
//        	FileMetaData[] metaLimit = client.file().prepDownloadBlocking(new Query().setLimit(10)).execute();
//
////        	System.out.println("plain query count -> " + metas.length);
////        	System.out.println("plain query first -> " + metas[0].getName());
////        	System.out.println("sort query count -> " + metaSort.length);
////        	System.out.println("sort query first -> " + metaSort[0].getName());
////        	System.out.println("limit query count -> " + metaLimit.length);
////        	System.out.println("limit query first -> " + metaLimit[0].getName());
//
//        }catch (IOException e){
//            e.printStackTrace();
//
//        }
////
////        try{
////        	KinveyDeleteResponse delete = client.file().deleteBlocking(new FileMetaData("myFileId")).execute();
////        }catch(IOException e){
////            System.out.println("Couldn't delete! -> " + e);
////            e.printStackTrace();
////        }
//
//
//        try{
//
//            InputStream is = new FileInputStream("/Users/edward/alpha.apk");
//
//            FileMetaData fm = new FileMetaData();
//            fm.setFileName("alpha.apk");
////            fm.setMimetype("image/png");
//            fm.setPublic(true);
//
//            UploaderProgressListener progressListener = new UploaderProgressListener() {
//                @Override
//                public void progressChanged(MediaHttpUploader uploader) throws IOException {
//                    System.out.println("upload progress change!");
//                }
//
//                @Override
//                public void onSuccess(Void result) {
//                    System.out.println("upload success!");
//                }
//
//                @Override
//                public void onFailure(Throwable error) {
//                    System.out.println("upload failed -> " + error);
//                }
//            };
//            client.file().uploadBlocking(fm, is, progressListener);
//
//            System.out.println("uploading Complete!");
//        }catch(IOException e){
//            System.out.println("Couldn't upload! -> " + e);
//            e.printStackTrace();
//        }
//
//        try{
//
//            OutputStream is = new FileOutputStream(new File("/Users/edward/alpha.apk"));
//
//            FileMetaData fm = new FileMetaData();
//            fm.setFileName("2.png");
//            fm.setMimetype("image/png");
//            fm.setPublic(true);
//
//            DownloaderProgressListener progressListener = new DownloaderProgressListener() {
//                @Override
//                public void progressChanged(MediaHttpDownloader uploader) throws IOException {
//                    System.out.println("download progress change!");
//                }
//
//                @Override
//                public void onSuccess(Void result) {
//                    System.out.println("download success!");
//                }
//
//                @Override
//                public void onFailure(Throwable error) {
//                    System.out.println("download failed -> " + error);
//                }
//            };
////            client.file().downloadBlocking(new FileMetaData("asd"), is, progressListener);
//            client.file().downloadBlocking(new Query().equals("_id", "123"), is, progressListener);
//
//            System.out.println("downloading Complete!");
//        }catch(IOException e){
//            System.out.println("Couldn't upload! -> " + e);
//            e.printStackTrace();
//        }


}

