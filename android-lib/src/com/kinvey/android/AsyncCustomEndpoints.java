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
package com.kinvey.android;

import com.google.api.client.json.GenericJson;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.AbstractClient;
import com.kinvey.java.CustomEndpoints;
import com.kinvey.java.core.KinveyClientCallback;

import java.io.IOException;

/**
 * Wraps the {@link com.kinvey.java.CustomEndpoints} public methods in asynchronous functionality using native Android AsyncTask.
 *
 *
 * @author edwardf
 * @since 2.0.2
 */
public class AsyncCustomEndpoints extends CustomEndpoints {


    public AsyncCustomEndpoints(AbstractClient client) {
        super(client);
    }

    public void runCommand(String commandName, GenericJson input, KinveyListCallback callback){
        new AsyncCommand(commandName, input, callback).execute(AsyncClientRequest.ExecutorType.KINVEYSERIAL);
    }




    private class AsyncCommand extends AsyncClientRequest<Void> {

        private String commandName;
        private GenericJson input;

        public AsyncCommand(String commandName, GenericJson input, KinveyClientCallback callback) {
            super(callback);
            this.commandName = commandName;
            this.input = input;
        }

        @Override
        protected Void executeAsync() throws IOException {
            AsyncCustomEndpoints.this.runCommandBlocking(commandName, input).execute();
            return null;
        }

    }
}
