package com.itaas.ankit.snippetviewer.data;

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
import com.itaas.ankit.snippetviewer.data.PojoDataList.PojoSnippet;

/**
 * A DataListProvider that fetches data over HTTP
 * (eg. https://www.dropbox.com/s/g41ldl6t0afw9dv/facts.json)
 * @author Ankit Sinha
 */
public class HttpDataListProvider implements DataListProvider {
	private static final String TAG = HttpDataListProvider.class.getName();
	
	private URI uri;

	public HttpDataListProvider(URI uri) {
		this.uri = uri;
	}
	
	public URI getURI() throws URISyntaxException{
		return uri;
	}
	
	

	@Override
	public DataList getDataList() {
		String content = fetchDataString();
		DataList dataList = parseJson(content);
		
		return dataList;
	}

	private String fetchDataString() {
		String content = null;
		try {
			Log.d(TAG, "URI:" + getURI());
			
			if(getURI() == null){
				return null;
			}
			
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
		
		if(jsonString == null){
			return null;
		}

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
