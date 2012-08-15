package test.beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;

@ManagedBean(name="myBean")
public class MapBean {
	Map<String, Collection> myMap = new HashMap<String, Collection>();
	
	public Map<String, Collection> getMyMap() {
		return myMap;
	}
}
