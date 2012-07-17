package test;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

public class Al1 extends AnnotationLiteral<Named> implements Named {

	private final String value;

	public Al1(String value) {
		this.value = value;
	}

	@Override
	public String value() {
		// TODO Auto-generated method stub
		return value;
	}

}
