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
import java.util.Arrays;
import net.tirasa.olingo4android.net.azurewebsites.odatae2etest.microsoft.test.odata.services.odatawcfservice.InMemoryEntities;
import net.tirasa.olingo4android.net.azurewebsites.odatae2etest.microsoft.test.odata.services.odatawcfservice.types.AccessLevel;
import net.tirasa.olingo4android.net.azurewebsites.odatae2etest.microsoft.test.odata.services.odatawcfservice.types.Color;
import net.tirasa.olingo4android.net.azurewebsites.odatae2etest.microsoft.test.odata.services.odatawcfservice.types.GiftCard;
import net.tirasa.olingo4android.net.azurewebsites.odatae2etest.microsoft.test.odata.services.odatawcfservice.types.Product;
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
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.ext.proxy.EntityContainerFactory;

public class Main extends Activity implements OnClickListener {

    private static final String SERVICE_ROOT = "http://odatae2etest.azurewebsites.net/javatest/DefaultService";

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
            text.append("ProductID: ").
                    append(product.getProperty("ProductID").getValue().asPrimitive().toString()).
                    append('\n');

            if (product.getProperty("Name") != null) {
                text.append("Name: ").
                        append(product.getProperty("Name").getValue().asPrimitive().toString()).
                        append('\n');
            }
            if (product.getProperty("QuantityPerUnit") != null) {
                text.append("QuantityPerUnit: ").
                        append(product.getProperty("QuantityPerUnit").getValue().asPrimitive().toString()).
                        append('\n');
            }
            if (product.getProperty("UnitPrice") != null) {
                text.append("UnitPrice: ").
                        append(product.getProperty("UnitPrice").getValue().asPrimitive().toString()).
                        append('\n');
            }
            if (product.getProperty("QuantityInStock") != null) {
                text.append("QuantityInStock: ").
                        append(product.getProperty("QuantityInStock").getValue().asPrimitive().toString()).
                        append('\n');
            }
            if (product.getProperty("UserAccess") != null) {
                text.append("UserAccess: ").
                        append(product.getProperty("UserAccess").getValue().asEnum().toString()).
                        append('\n');
            }
        }

        private void output(final StringBuilder text, final Product product) {
            text.append("ProductID: ").
                    append(product.getProductID()).
                    append('\n');

            if (product.getName() != null) {
                text.append("Name: ").
                        append(product.getName()).
                        append('\n');
            }
            if (product.getQuantityPerUnit() != null) {
                text.append("QuantityPerUnit: ").
                        append(product.getQuantityPerUnit()).
                        append('\n');
            }
            if (product.getUnitPrice() != null) {
                text.append("UnitPrice: ").
                        append(product.getUnitPrice()).
                        append('\n');
            }
            if (product.getQuantityInStock() != null) {
                text.append("QuantityInStock: ").
                        append(product.getQuantityInStock()).
                        append('\n');
            }
            if (product.getUserAccess() != null) {
                text.append("UserAccess: ").
                        append(product.getUserAccess()).
                        append('\n');
            }
        }

        private void output(final StringBuilder text, final GiftCard giftcard) {
            text.append("GiftCardID: ").
                    append(giftcard.getGiftCardID()).
                    append('\n');

            if (giftcard.getGiftCardNO()!= null) {
                text.append("GiftCardNO: ").
                        append(giftcard.getGiftCardNO()).
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

            // ------------------ CORE ------------------
            text.append('\n').append("[CUD (core)]").append('\n');

            final ODataEntity newProduct = client.getObjectFactory().
                    newEntity(new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Product"));
            newProduct.getProperties().add(client.getObjectFactory().newPrimitiveProperty("ProductID",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(111)));
            newProduct.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Name",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildString("Latte")));
            newProduct.getProperties().add(client.getObjectFactory().newPrimitiveProperty("QuantityPerUnit",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildString("100g Bag")));
            newProduct.getProperties().add(client.getObjectFactory().newPrimitiveProperty("UnitPrice",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildSingle(3.24f)));
            newProduct.getProperties().add(client.getObjectFactory().newPrimitiveProperty("QuantityInStock",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(100)));
            newProduct.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Discontinued",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildBoolean(false)));
            newProduct.getProperties().add(client.getObjectFactory().newEnumProperty("UserAccess",
                    client.getObjectFactory().
                    newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", "Execute")));
            newProduct.getProperties().add(client.getObjectFactory().newEnumProperty("SkinColor",
                    client.getObjectFactory().
                    newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.Color", "Blue")));
            newProduct.getProperties().add(client.getObjectFactory().newCollectionProperty("CoverColors",
                    client.getObjectFactory().
                    newCollectionValue("Microsoft.Test.OData.Services.ODataWCFService.Color")));
            newProduct.getProperty("CoverColors").getCollectionValue().add(client.getObjectFactory().
                    newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.Color", "Green"));
            newProduct.getProperty("CoverColors").getCollectionValue().add(client.getObjectFactory().
                    newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.Color", "Red"));

            try {
                // create
                final ODataEntityCreateRequest<ODataEntity> createReq = client.getCUDRequestFactory().
                        getEntityCreateRequest(
                                client.newURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").build(),
                                newProduct);
                final ODataEntityCreateResponse<ODataEntity> createRes = createReq.execute();
                text.append("Product created: ").append(createRes.getStatusCode()).append('\n');

                // update
                final ODataEntity created = createRes.getBody();

                final ODataEntity changes = client.getObjectFactory().newEntity(created.getTypeName());
                changes.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Discontinued",
                        client.getObjectFactory().newPrimitiveValueBuilder().buildBoolean(true)));
                final ODataEntityUpdateRequest<ODataEntity> updateReq = client.getCUDRequestFactory().
                        getEntityUpdateRequest(created.getEditLink(), UpdateType.PATCH, changes);
                final ODataEntityUpdateResponse<ODataEntity> updateRes = updateReq.execute();
                text.append("Product updated: ").append(updateRes.getStatusCode()).append('\n');

                // delete
                final ODataDeleteResponse deleteRes =
                        client.getCUDRequestFactory().getDeleteRequest(created.getEditLink()).execute();
                text.append("Product deleted: ").append(deleteRes.getStatusCode()).append('\n');
            } catch (Exception e) {
                Log.e("ERROR", "CUD (core)", e);
                text.append(e.getLocalizedMessage()).append('\n');
            }

            text.append('\n').append("[READ (core)]").append('\n');
            try {
                final ODataEntityRequest<ODataEntity> jsonReq = client.getRetrieveRequestFactory().
                        getEntityRequest(client.newURIBuilder(SERVICE_ROOT).
                                appendEntitySetSegment("Products").appendKeySegment(5).build());

                final ODataEntity jsonProduct = jsonReq.execute().getBody();

                output(text, jsonProduct);
            } catch (Exception e) {
                Log.e("ERROR", "JSON READ (core)", e);
                text.append(e.getLocalizedMessage()).append('\n');
            }

            // ------------------ PROXY ------------------
            final InMemoryEntities service =
                    EntityContainerFactory.getV4(SERVICE_ROOT).getEntityContainer(InMemoryEntities.class);

            text.append('\n').append("[CUD (proxy)]").append('\n');
            try {
                Product product = service.getProducts().newProduct();
                product.setProductID(112);
                product.setName("Latte");
                product.setQuantityPerUnit("100g Bag");
                product.setUnitPrice(3.24f);
                product.setQuantityInStock(100);
                product.setDiscontinued(false);
                product.setUserAccess(AccessLevel.Execute);
                product.setSkinColor(Color.Blue);
                product.setCoverColors(Arrays.asList(new Color[] { Color.Green, Color.Red }));

                service.flush();
                text.append("Product created").append('\n');

                product = service.getProducts().get(112);
                product.setDiscontinued(true);

                service.flush();
                text.append("Product updated").append('\n');

                service.getProducts().delete(112);

                service.flush();
                text.append("Product deleted").append('\n');
            } catch (Exception e) {
                Log.e("ERROR", "CUD (proxy)", e);
                text.append(e.getLocalizedMessage()).append('\n');
            }

            text.append('\n').append("[READ (proxy)]").append('\n');
            try {
                final Product product = service.getProducts().get(7);
                output(text, product);

                // output a complex value
                final GiftCard giftCard = service.getAccounts().get(101).getMyGiftCard();
                output(text, giftCard);
            } catch (Exception e) {
                Log.e("ERROR", "JSON READ (proxy)", e);
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
