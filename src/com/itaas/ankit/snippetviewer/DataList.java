package com.itaas.ankit.snippetviewer;


import java.net.URI;

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
