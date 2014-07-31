package com.itaas.ankit.snippetviewer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.itaas.ankit.snippetviewer.PojoDataList.PojoSnippet;

public class HttpDataListProvider implements DataListProvider {
	private static final String TAG = HttpDataListProvider.class.getName();
	//public static boolean isOdd = false;

	public static final String DATA_URI_STRING = "https://www.dropbox.com/s/g41ldl6t0afw9dv/facts.json?dl=1";

	public URI getURI() throws URISyntaxException{
		return new URI(DATA_URI_STRING);
	}

	@Override
	public DataList getDataList() {
		String content = fetchDataString();
		DataList dataList = parseJson(content);
		
		/*if(isOdd && dataList.getRows().length > 1){
			
			Snippet[] snippetArr = Arrays.copyOfRange(
					dataList.getRows(), 1, dataList.getRows().length);
			
			dataList.setRows(snippetArr);
			Log.e(TAG, "mischief: " + dataList.getRows().length);
		}
		isOdd = !isOdd;*/
		
		return dataList;
	}

	private String fetchDataString() {
		String content = null;
		try {
			Log.d(TAG, "URI:" + getURI());
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(getURI());
			BasicResponseHandler response = new BasicResponseHandler();

			content = client.execute(get, response);
			Log.d(TAG, "Got Content" + content.substring(0,100));

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d(TAG, "After fetch execution");
		return content;
	}
	
	public DataList parseJson(String jsonString) {
		Log.d(TAG, "parseJson");

		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(DataList.Snippet.class, new SnippetDeserializer());
		
		DataList dataList = gson.create().fromJson(jsonString, PojoDataList.class);
		
		Log.d(TAG, dataList.toString());
		return dataList;
	}

	private static class SnippetDeserializer implements JsonDeserializer<DataList.Snippet> {
		public DataList.Snippet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			PojoSnippet snippet = context.deserialize(json, PojoSnippet.class);
			return snippet;
		}
	}

}
