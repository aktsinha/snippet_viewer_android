package com.itaas.ankit.snippetviewer;


import java.net.URI;

/**
 * Represents the data structure that will be displayed by the app 
 * (eg. https://www.dropbox.com/s/g41ldl6t0afw9dv/facts.json)
 * @author Ankit Sinha
 */
public interface DataList {
	public String getTitle();
	public void setTitle(String title);
	
	public Snippet[] getRows();
	public void setRows(Snippet[] rows);
	
	public static interface Snippet {
		public String getTitle();
		public void setTitle(String title);
		
		public String getDescription();
		public void setDescription(String description);
		
		public URI getImageHref();
		public void setImageHref(URI imageHref);
	}
}
