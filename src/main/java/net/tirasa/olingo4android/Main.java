/*
 * Copyright (C) 2014 Tirasa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tirasa.olingo4android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ODataPubFormat;

public class Main extends Activity implements OnClickListener {

    private static final String SERVICE_ROOT = "http://services.odata.org/V4/OData/OData.svc";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.my_button).setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        final Button button = (Button) findViewById(R.id.my_button);
        button.setClickable(false);
        new LongRunningGetIO(ODataClientFactory.getV4()).execute();
    }

    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

        private final ODataClient client;

        public LongRunningGetIO(final ODataClient client) {
            this.client = client;
        }

        private void output(final StringBuilder text, final ODataEntity product) {
            text.append("ID: ").
                    append(product.getProperty("ID").getValue().asPrimitive().toString()).
                    append('\n');

            if (product.getProperty("Name") != null) {
                text.append("Name: ").
                        append(product.getProperty("Name").getValue().asPrimitive().toString()).
                        append('\n');
            }
            if (product.getProperty("Description") != null) {
                text.append("Description: ").
                        append(product.getProperty("Description").getValue().asPrimitive().toString()).
                        append('\n');
            }
            if (product.getProperty("ReleaseDate") != null) {
                text.append("Release date: ").
                        append(product.getProperty("ReleaseDate").getValue().asPrimitive().toString()).
                        append('\n');
            }
            if (product.getProperty("Rating") != null) {
                text.append("Rating: ").
                        append(product.getProperty("Rating").getValue().asPrimitive().toString()).
                        append('\n');
            }
            if (product.getProperty("Price") != null) {
                text.append("Price: ").
                        append(product.getProperty("Price").getValue().asPrimitive().toString()).
                        append('\n');
            }
        }

        @Override
        protected String doInBackground(final Void... params) {
            final StringBuilder text = new StringBuilder();

            text.append("[METADATA]").append('\n');
            // ------------------ Metadata ------------------
            try {
                final Edm metadata = client.getRetrieveRequestFactory().
                        getMetadataRequest(SERVICE_ROOT).execute().getBody();

                text.append("Schema namespace: ").append(metadata.getSchemas().get(0).getNamespace()).append('\n');
            } catch (Exception e) {
                Log.e("ERROR", "METADATA", e);
                text.append(e.getLocalizedMessage()).append('\n');
            }

            // ------------------ JSON ------------------
            text.append('\n').append("[JSON]").append('\n');
            try {
                final ODataEntityRequest<ODataEntity> jsonReq = client.getRetrieveRequestFactory().
                        getEntityRequest(client.getURIBuilder(SERVICE_ROOT).
                                appendEntitySetSegment("Products").appendKeySegment(0).build());

                final ODataEntity jsonProduct = jsonReq.execute().getBody();

                output(text, jsonProduct);
            } catch (Exception e) {
                Log.e("ERROR", "JSON - Engine", e);
                text.append(e.getLocalizedMessage()).append('\n');
            }

            // ------------------ ATOM ------------------
            text.append('\n').append("[Atom]").append('\n');
            try {
                final ODataEntityRequest<ODataEntity> atomReq = client.getRetrieveRequestFactory().
                        getEntityRequest(client.getURIBuilder(SERVICE_ROOT).
                                appendEntitySetSegment("Products").appendKeySegment(1).build());
                atomReq.setFormat(ODataPubFormat.ATOM);
                final ODataEntity atomProduct = atomReq.execute().getBody();

                output(text, atomProduct);
            } catch (Exception e) {
                Log.e("ERROR", "Atom - Engine", e);
                text.append(e.getLocalizedMessage()).append('\n');
            }

            // ------------------
            return text.toString();
        }

        @Override
        protected void onPostExecute(final String results) {
            if (results != null) {
                final EditText edit = (EditText) findViewById(R.id.my_edit);
                edit.setText(results);
            }
            final Button button = (Button) findViewById(R.id.my_button);
            button.setClickable(true);
        }
    }
}
