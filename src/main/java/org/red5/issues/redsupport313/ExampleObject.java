package org.red5.issues.redsupport313;

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

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
	    result = prime * result + (int) (ct ^ (ct >>> 32));
	    result = prime * result + random1;
	    result = prime * result + random2;
	    result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    ExampleObject other = (ExampleObject) obj;
	    if (ct != other.ct)
		    return false;
	    if (random1 != other.random1)
		    return false;
	    if (random2 != other.random2)
		    return false;
	    if (siteId == null) {
		    if (other.siteId != null)
			    return false;
	    } else if (!siteId.equals(other.siteId))
		    return false;
	    if (clientId.size() == other.clientId.size()) {
	    	return false;
	    }
	    return true;
    }

	@Override
    public String toString() {
	    return "ExampleObject [random1=" + random1 + ", random2=" + random2 + ", ct=" + ct + ", closed=" + closed + ", siteId=" + siteId + ", clientId=" + clientId + "]";
    }
	
}
