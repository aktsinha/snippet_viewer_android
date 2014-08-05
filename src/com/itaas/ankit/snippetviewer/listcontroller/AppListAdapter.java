package com.itaas.ankit.snippetviewer.listcontroller;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itaas.ankit.snippetviewer.R;
import com.itaas.ankit.snippetviewer.data.DataList;
import com.itaas.ankit.snippetviewer.data.DataList.Snippet;

/**
 * A simple adapter for the list view
 */
public class AppListAdapter extends ArrayAdapter<Snippet> {
	private static final String TAG = AppListAdapter.class.getName();
	private final LayoutInflater mInflater;

	public AppListAdapter(Context context) {
		super(context, R.layout.list_row);
		Log.d(TAG, "constructor");
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(DataList data) {
		Log.d(TAG, "setData");
		clear();
		if (data != null) {
			addAll(Arrays.asList(data.getRows()));
		}
	}

	/**
	 * Populate new items in the list.
	 */
	@Override public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d(TAG, "getView");
		View view;

		if (convertView == null) {
			view = mInflater.inflate(R.layout.list_row, parent, false);
		} else {
			view = convertView;
		}

		Snippet item = getItem(position);
		final URI imageURI = item.getImageHref();
		final ImageView imageView = ((ImageView)view.findViewById(R.id.row_image));
		
		imageView.setTag(imageURI);
		
		if(imageURI == null){
			imageView.setImageDrawable(null);	
		} else {
			Drawable loadingIndicatorDrawable = getContext().getResources().getDrawable(
					android.R.drawable.progress_indeterminate_horizontal);
			imageView.setImageDrawable(loadingIndicatorDrawable);
			
			//load the real image in a background thread
			this.loadImage(imageURI, imageView);
			
		}
		((TextView)view.findViewById(R.id.row_title)).setText(item.getTitle());
		((TextView)view.findViewById(R.id.row_description)).setText(item.getDescription());

		return view;
	}

	private void loadImage(final URI imageURI, final ImageView imageView) {
		
		AsyncTask<Object, Void, Bitmap> fetchImageTask = new AsyncTask<Object, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Object... params) {
				URI imageURI = (URI) params[0];
				ImageView imageView = (ImageView) params[1];
				
				Log.d(TAG, "doInBackground: imageURI: "+ imageURI + ", tag: " + imageView.getTag());
				
				if(!imageURI.equals(imageView.getTag())){
					Log.d(TAG, "imageView has been reused. Quit");
					return null;
				}
				
				try {
					Bitmap bitmap = BitmapFactory.decodeStream(
							(InputStream) imageURI.toURL().getContent());
					return bitmap;
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Bitmap bm) {
				if(bm == null){
					Log.d(TAG, "Got null bitmap. Quit");
					return;
				}
				if(!imageURI.equals(imageView.getTag())){
					Log.d(TAG, "imageView has been reused. Quit");
					return;
				}
				
				imageView.setImageBitmap(bm);
			};
			
		};
		fetchImageTask.executeOnExecutor(
				AsyncTask.THREAD_POOL_EXECUTOR, imageURI, imageView);
	}
}