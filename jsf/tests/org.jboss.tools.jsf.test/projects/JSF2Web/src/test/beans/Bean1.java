package test.beans;

import javax.faces.bean.ManagedBean;

@ManagedBean(name="mybean1")
public class Bean1 {
	public String getName() {
		return "#{}";
	}

}
