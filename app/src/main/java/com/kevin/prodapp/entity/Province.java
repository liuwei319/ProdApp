package com.kevin.prodapp.entity;

import java.util.List;

public class Province {
	private String name;
	private List<City> citys;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<City> getCitys() {
		return citys;
	}

	public void setCitys(List<City> citys) {
		this.citys = citys;
	}

//	private String id;
//	private String pID;
//	private String icon;
//	private String hasChildren;
//	private String subItemsCount;
//	private String linkPage;
//	private String invalidMenus;
//	private String etName;
//	private String text;
//	private List<City> citys;
//
//	public String getText() {
//		return text;
//	}
//
//	public void setText(String text) {
//		this.text = text;
//	}
//
//	public List<City> getCitys() {
//		return citys;
//	}
//
//	public void setCitys(List<City> citys) {
//		this.citys = citys;
//	}
//
//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
//
//	public String getpID() {
//		return pID;
//	}
//
//	public void setpID(String pID) {
//		this.pID = pID;
//	}
//
//	public String getEtName() {
//		return etName;
//	}
//
//	public void setEtName(String etName) {
//		this.etName = etName;
//	}
//
//	public String getIcon() {
//		return icon;
//	}
//
//	public void setIcon(String icon) {
//		this.icon = icon;
//	}
//
//	public String getHasChildren() {
//		return hasChildren;
//	}
//
//	public void setHasChildren(String hasChildren) {
//		this.hasChildren = hasChildren;
//	}
//
//	public String getSubItemsCount() {
//		return subItemsCount;
//	}
//
//	public void setSubItemsCount(String subItemsCount) {
//		this.subItemsCount = subItemsCount;
//	}
//
//	public String getLinkPage() {
//		return linkPage;
//	}
//
//	public void setLinkPage(String linkPage) {
//		this.linkPage = linkPage;
//	}
//
//	public String getInvalidMenus() {
//		return invalidMenus;
//	}
//
//	public void setInvalidMenus(String invalidMenus) {
//		this.invalidMenus = invalidMenus;
//	}
}
