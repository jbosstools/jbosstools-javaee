package cdi.seam;

public class Application {

	private String value = "#{}";

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
