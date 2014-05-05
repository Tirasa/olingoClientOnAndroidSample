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
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v4.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataPubFormat;

public class Main extends Activity implements OnClickListener {

    private static final String SERVICE_ROOT =
            "http://services.odata.org/V4/OData/(S(obimzejxivyhzdmpnlc1a4fq))/OData.svc";

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

            // ------------------ CRUD  ------------------
            text.append('\n').append("[CRUD]").append('\n');
            final ODataEntity newProduct = client.getObjectFactory().
                    newEntity(new FullQualifiedName("ODataDemo.Product"));
            newProduct.getProperties().add(client.getObjectFactory().newPrimitiveProperty("ID",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(111)));
            newProduct.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Name",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildString("OlingoDemoProduct")));
            newProduct.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Description",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildString("Olingo Demo Product")));
            newProduct.getProperties().add(client.getObjectFactory().newPrimitiveProperty("ReleaseDate",
                    client.getObjectFactory().newPrimitiveValueBuilder().
                    setType(EdmPrimitiveTypeKind.DateTimeOffset).setText("2014-05-04T00:00:00Z").build()));
            newProduct.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Rating",
                    client.getObjectFactory().newPrimitiveValueBuilder().
                    setType(EdmPrimitiveTypeKind.Int16).setValue(1).build()));
            newProduct.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Price",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildSingle(1.0F)));

            try {
                // create
                final ODataEntityCreateRequest<ODataEntity> createReq = client.getCUDRequestFactory().
                        getEntityCreateRequest(
                                client.getURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").build(),
                                newProduct);
                final ODataEntityCreateResponse<ODataEntity> createRes = createReq.execute();
                text.append("Product created: ").append(createRes.getStatusCode()).append('\n');

                // update
                final ODataEntity created = createRes.getBody();

                final ODataEntity changes = client.getObjectFactory().newEntity(created.getTypeName());
                changes.getProperties().add(client.getObjectFactory().newPrimitiveProperty("DiscontinuedDate",
                        client.getObjectFactory().newPrimitiveValueBuilder().
                        setType(EdmPrimitiveTypeKind.DateTimeOffset).setText("2014-05-04T00:00:00Z").build()));
                final ODataEntityUpdateRequest<ODataEntity> updateReq = client.getCUDRequestFactory().
                        getEntityUpdateRequest(created.getEditLink(), UpdateType.PATCH, changes);
                final ODataEntityUpdateResponse<ODataEntity> updateRes = updateReq.execute();
                text.append("Product updated: ").append(updateRes.getStatusCode()).append('\n');

                // delete
                final ODataDeleteResponse deleteRes =
                        client.getCUDRequestFactory().getDeleteRequest(created.getEditLink()).execute();
                text.append("Product deleted: ").append(deleteRes.getStatusCode()).append('\n');
            } catch (Exception e) {
                Log.e("ERROR", "CRUD", e);
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
                Log.e("ERROR", "JSON - READ", e);
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
                Log.e("ERROR", "Atom - READ", e);
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
