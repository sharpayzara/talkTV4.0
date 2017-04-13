package com.sumavison.crack.interfaces;

import java.util.HashMap;

import android.content.Context;

public interface ILoader {

	public HashMap<String,String> crack(String url, String sourceType, CrackListener cl, Context context);
	
	public HashMap<String,String> crack(int type, String url);
}
