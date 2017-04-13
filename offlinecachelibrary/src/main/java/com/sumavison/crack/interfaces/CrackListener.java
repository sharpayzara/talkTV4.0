package com.sumavison.crack.interfaces;

import java.util.HashMap;

public interface CrackListener {

	public void start();
	
	public HashMap<String, String> end(HashMap<String, String> h);
	public void setIsInitJar(boolean init);
	public boolean getIsInitJar();
	public void downLoading(int process);
	
}
