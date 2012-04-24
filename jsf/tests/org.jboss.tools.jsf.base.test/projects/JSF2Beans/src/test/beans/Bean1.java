package test.beans;

import javax.faces.bean.ManagedBean;

@ManagedBean(name="mybean1")
public class Bean1 {
	public String getName() {
		return "#{}";
	}

	String s1 = "#{ mybean1}";
	String s2 = "#{ mybean1.  }";
	String s3 = "#{ mybean1  }";
	String s4 = "#{mybean2['100'].ch}";

}
