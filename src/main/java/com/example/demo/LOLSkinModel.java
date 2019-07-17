package com.example.demo;

class LOLSkinModel {
	private String version;
	private String url;
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private static LOLSkinModel singletonHungary = new LOLSkinModel();

	// 将构造器设置为private禁止通过new进行实例化
	private LOLSkinModel() {

	}

	public static LOLSkinModel getInstance() {
		return singletonHungary;
	}
}
