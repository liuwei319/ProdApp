package com.kevin.prodapp.entity;

import java.io.Serializable;
import java.util.List;

public class TFunctionMenu implements Serializable {
	String title;
	String url;
	List<TFunctionMenu> data;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<TFunctionMenu> getData() {
		return data;
	}

	public void setData(List<TFunctionMenu> data) {
		this.data = data;
	}

}
