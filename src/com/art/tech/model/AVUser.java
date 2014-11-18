package com.art.tech.model;

import com.art.tech.callback.LogInCallback;


public class AVUser {
	
	private int uid;
	private String name;
	private String headImage;
	private String signature;
	private String description;



	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static AVUser getCurrentUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getObjectId() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void logInInBackground(String username, String password,
			LogInCallback logInCallback) {
		// TODO Auto-generated method stub
		
	}


}
