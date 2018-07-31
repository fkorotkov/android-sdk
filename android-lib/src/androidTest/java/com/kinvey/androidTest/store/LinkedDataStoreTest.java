package com.kinvey.androidTest.store;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.common.io.Files;
import com.kinvey.android.AndroidMimeTypeFinder;
import com.kinvey.android.Client;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.store.LinkedDataStore;
import com.kinvey.androidTest.TestManager;
import com.kinvey.androidTest.model.Person;
import com.kinvey.androidTest.network.LinkedPerson;
import com.kinvey.java.LinkedResources.LinkedFile;
import com.kinvey.java.store.LinkedBaseDataStore;
import com.kinvey.java.store.StoreType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.kinvey.androidTest.TestManager.PASSWORD;
import static com.kinvey.androidTest.TestManager.USERNAME;
import static junit.framework.TestCase.assertNotNull;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class LinkedDataStoreTest {

    private Client client;
    private TestManager<Person> testManager;
    private DataStore<Person> store;

    @Before
    public void setUp() throws InterruptedException {
        Context mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        client = new Client.Builder(mMockContext).build();
        client.enableDebugLogging();
        testManager = new TestManager<>();
        testManager.login(USERNAME, PASSWORD, client);
    }

    @After
    public void tearDown() {
        client.performLockDown();
        if (client.getKinveyHandlerThread() != null) {
            try {
                client.stopKinveyHandlerThread();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    @Test
    public void testBasic() throws IOException {

        String fileName = "test_file_name.txt";
        File file = testManager.createFile(client, fileName, 1024);
        LinkedFile linkedFile = new LinkedFile(fileName);
        byte[] bytes = Files.toByteArray(file);
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);


        linkedFile.setInput(arrayInputStream);
        LinkedBaseDataStore<LinkedPerson> linkedDataStore = new LinkedBaseDataStore<>(client, LinkedPerson.COLLECTION, LinkedPerson.class, StoreType.NETWORK, new AndroidMimeTypeFinder());
        LinkedPerson linkedPerson = new LinkedPerson();
        linkedPerson.setUsername("TestLinkedPerson");
        linkedPerson.putFile("attachment", linkedFile);

        LinkedPerson uploadedLinkedPerson = linkedDataStore.saveBlocking(linkedPerson, null);
        LinkedPerson downloadedLinkedPerson = linkedDataStore.findBlocking(uploadedLinkedPerson.getId(), null);

        try {
            FileOutputStream fStream = client.getContext().openFileOutput("uploaded_" + fileName, Context.MODE_PRIVATE);
            ByteArrayOutputStream bos = downloadedLinkedPerson.getFile("attachment").getOutput();
            bos.writeTo(fStream);
            bos.flush();
            fStream.flush();
            bos.close();
            fStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assertNotNull(downloadedLinkedPerson.getFile("attachment"));
        assertNotNull(downloadedLinkedPerson.getFile("attachment").getOutput());


    }


}
