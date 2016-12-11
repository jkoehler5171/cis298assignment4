//Jordan Koehler
//MW 2:30 - 4:45
//December 11th, 2016

package edu.kvcc.cis298.cis298assignment4;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomato on 12/11/2016.
 */

public class BeverageFetcher {


    //Alrighty, so this method opens up a connection to your webserver using the address passed in, stores the information it recieves, and then closes the connection.
    private byte[] getURLBytes (String urlSpec) throws IOException
    {
        URL url = new URL(urlSpec);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            InputStream input = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with" + urlSpec);
            }

            int bytesRead = 0;

            byte[] buffer = new byte[1024];

            while ((bytesRead = input.read(buffer)) > 0) {

                output.write(buffer, 0, bytesRead);
            }

            output.close();
            input.close();

            return output.toByteArray();

        }
        finally {
            connection.disconnect();
        }

    }

    //This method creates a string that holds all of the data pulled off the webserver by calling the above method.
    private String getURLString(String urlSpec) throws IOException {
        return new String(getURLBytes(urlSpec));
    }


    //This method creates a new list of Beverages by calling the above method and passing in the URL to the webserver. After, it calls a method to parse the
    //URL string into a list we can use.
    public List<Beverage> fetchBeverages() {

        List<Beverage> bevs = new ArrayList<>();

        try{
            String url = Uri.parse("http://barnesbrothers.homeserver.com/beverageapi").buildUpon().build().toString();

            String jsonString = getURLString(url);

            JSONArray jsonArray = new JSONArray(jsonString);

            parseBevs(bevs, jsonArray);
        }
        catch (JSONException jse) {

        }
        catch (IOException ioe) {

        }

        return bevs;
    }


    //This method takes a JSON Array, and list of Beverages, parses the JSON Array into something that we can load into a Beverage object, which we then add to the Beverage Array.
    private void parseBevs(List<Beverage> bevs, JSONArray jsonArray) throws IOException, JSONException{

        for (int counter = 0; counter<jsonArray.length(); counter++){

            JSONObject bevJSONObject = jsonArray.getJSONObject(counter);

            String IDString = bevJSONObject.getString("id");

            String bevName =  bevJSONObject.getString("name");

            String bevPack = bevJSONObject.getString("pack");

            String bevPrice = bevJSONObject.getString("price");

            Double parsedPrice = Double.parseDouble(bevPrice);

            boolean isAvailable = (bevJSONObject.getInt("isActive") == 1);

            Beverage newBev = new Beverage(IDString, bevName, bevPack, parsedPrice, isAvailable);

            bevs.add(newBev);

        }
    }


}
