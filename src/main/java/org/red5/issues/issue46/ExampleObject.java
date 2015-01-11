package org.red5.issues.issue46;

import java.util.ArrayList;

public class ExampleObject extends Object {
	
	public int random1 = 0;

	public int random2 = 0;

	public long ct = 0;

	public Boolean closed = false;

	public String siteId = "";

	public ArrayList<String> clientId = new ArrayList<String>();

	public ExampleObject() {
		ct = System.currentTimeMillis();
	}
	
}
