package com.itaas.ankit.snippetviewer;


import java.net.URI;

/**
 * A Pojo implementation of DataList
 * @author Ankit Sinha
 */
public class PojoDataList implements DataList {
	
	private String title;
	private DataList.Snippet[] rows;

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void setRows(Snippet[] rows) {
		this.rows = rows;
		
	}

	@Override
	public Snippet[] getRows() {
		return this.rows;
	}
	
	
	
	@Override
	public String toString() {
		String result = "PojoDataList [title=" + title + ", rows=" + rows + "]\n"+
				"rows.length = " + rows.length;
		
		if(rows.length > 0) {
			result += "\nlast row: " + rows[rows.length-1];
			
		}
		return result;
	}


	public static class PojoSnippet implements Snippet {
		private String title;
		private String description;
		private URI imageHref;
		
		@Override
		public String getTitle() {
			return this.title;
		}

		@Override
		public void setTitle(String title) {
			this.title = title;
		}
		
		public String getDescription(){
			return this.description;
		}
		public void setDescription(String description){
			this.description = description;
		}
		
		public URI getImageHref() {
			return this.imageHref;
		}
		public void setImageHref(URI imageHref) {
			this.imageHref = imageHref;
		}

		@Override
		public String toString() {
			return "PojoSnippet [title=" + title + ", description="
					+ description + ", imageHref=" + imageHref + "]";
		}
	}

}
