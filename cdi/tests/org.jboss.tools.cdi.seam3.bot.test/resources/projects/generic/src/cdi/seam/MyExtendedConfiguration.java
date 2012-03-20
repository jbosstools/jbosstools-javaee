package cdi.seam;

@MyGenericType("third")
@Qualifier2
public class MyExtendedConfiguration extends MyConfiguration {
	public MyExtendedConfiguration() {
		super("");
	}
}