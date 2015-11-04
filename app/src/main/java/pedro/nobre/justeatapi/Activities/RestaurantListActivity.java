package pedro.nobre.justeatapi.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pedro.nobre.justeatapi.R;
import pedro.nobre.justeatapi.Util.Restaurants;

public class RestaurantListActivity extends AppCompatActivity {


    List<Restaurants> restaurantList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    TextView searchNoResults;
    String postalCode = null;
    int responseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        recyclerView = (RecyclerView) findViewById(R.id.listView_restaurants);
        recyclerView.setHasFixedSize(true);


        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        searchNoResults = (TextView) findViewById(R.id.searchNoResults);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            postalCode = extras.getString("POSTALCODE");

        }

        RestaurantsTask getRestaurants = new RestaurantsTask();
        getRestaurants.execute("http://api-interview.just-eat.com/restaurants?q=" + postalCode.replaceAll("\\s", ""));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_restaurant_list, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder> {

        private List<Restaurants> restaurants;

        public RestaurantListAdapter(List<Restaurants> restaurants) {
            this.restaurants = restaurants;
        }

        @Override
        public RestaurantListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);

            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Restaurants restaurants = this.restaurants.get(position);
            holder.textViewRestaurantName.setText(restaurants.getName());
            holder.textViewRestaurantRating.setText(restaurants.getRatingStars());

            String cusineType = "";
            for (String s : this.restaurants.get(position).getCuisineTypes()) {
                cusineType += s + ", ";
            }
            holder.textViewRestaurantCusineType.setText(cusineType);

            new DownloadImageTask(holder.imageview).execute(this.restaurants.get(position).getImage());


        }

        @Override
        public int getItemCount() {
            return restaurants.size();
        }



        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textViewRestaurantName;
            public TextView textViewRestaurantRating;
            public TextView textViewRestaurantCusineType;
            public ImageView imageview;


            public ViewHolder(View view) {
                super(view);

                textViewRestaurantName = (TextView) view.findViewById(R.id.list_restaurant_name);
                textViewRestaurantRating = (TextView) view.findViewById(R.id.list_restaurant_stars);
                textViewRestaurantCusineType = (TextView) view.findViewById(R.id.list_restaurant_cusineType);
                imageview = (ImageView) view.findViewById(R.id.ivImage);

            }
        }


    }

        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView bmImage;

            public DownloadImageTask(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon = null;
                try {
                    InputStream in = new URL(urldisplay).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return mIcon;
            }

            protected void onPostExecute(Bitmap result) {
                bmImage.setImageBitmap(result);
            }
        }


        public class RestaurantsTask extends AsyncTask<String, String, JSONObject> {

            @Override
            protected JSONObject doInBackground(String... params) {
                URL url;
                HttpURLConnection urlConnection = null;
                JSONObject response = new JSONObject();

                try {
                    url = new URL(params[0]);

                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");

                    //add request header
                    urlConnection.setRequestProperty("Accept-Tenant", "uk");
                    urlConnection.setRequestProperty("Accept-Language", "en-GB");
                    urlConnection.setRequestProperty("Authorization", "Basic a2luZ3MtaGFjazpqNHlrN3ljb3Q1MHRmMng=");
                    urlConnection.setRequestProperty("User-Agent", "hackkings");
                    urlConnection.setRequestProperty("Host", "api-interview.just-eat.com");
                    urlConnection.setRequestProperty("Accept-Version", "2");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept-Charset", "utf-8");

                    responseCode = urlConnection.getResponseCode();

                    if (responseCode == 200) {
                        String responseString = readStream(urlConnection.getInputStream());
                        //Log.v("CatalogClient", responseString);
                        response = new JSONObject(responseString);
                    } else {
                        Log.v("CatalogClient", "Response code:" + responseCode);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }

                return response;
            }

            protected void onPostExecute(JSONObject result) {

                restaurantList = new ArrayList<Restaurants>();


                try {
                    JSONArray rest = result.getJSONArray("Restaurants");


                    if (rest.length() == 0 || responseCode != 200) {
                        searchNoResults.setVisibility(View.VISIBLE);
                        searchNoResults.setText(getResources().getText(R.string.noResults)  + " "+ postalCode);

                    } else {
                        for (int i = 0; i < rest.length(); i++) {

                            Restaurants restaurant = new Restaurants();
                            List<String> cusineTypesList = new ArrayList<String>();
                            JSONObject r = rest.getJSONObject(i);
                            restaurant.setName(r.getString("Name"));
                            restaurant.setAddress(r.getString("Address"));
                            restaurant.setCity(r.getString("City"));
                            restaurant.setPostcode(r.getString("Postcode"));
                            restaurant.setRatingStars(r.getString("RatingStars"));


                            JSONArray cusine = r.getJSONArray("CuisineTypes");
                            for (int ii = 0; ii < cusine.length(); ii++) {

                                JSONObject cusineObject = cusine.getJSONObject(ii);
                                cusineTypesList.add(cusineObject.getString("Name"));
                            }
                            restaurant.setCuisineTypes(cusineTypesList);


                            JSONArray logo = r.getJSONArray("Logo");
                            for (int iii = 0; iii < logo.length(); iii++) {

                                JSONObject logoItem = logo.getJSONObject(iii);
                                restaurant.setImage(logoItem.getString("StandardResolutionURL"));
                            }


                            restaurantList.add(restaurant);

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                RestaurantListAdapter adapter = new RestaurantListAdapter(restaurantList);
                recyclerView.setAdapter(adapter);


            }


            private String readStream(InputStream in) {
                BufferedReader reader = null;
                StringBuffer response = new StringBuffer();
                try {
                    reader = new BufferedReader(new InputStreamReader(in));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return response.toString();
            }
        }



}
