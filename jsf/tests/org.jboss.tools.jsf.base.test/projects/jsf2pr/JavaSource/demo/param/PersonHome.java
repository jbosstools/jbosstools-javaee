package demo.param;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class PersonHome extends EntityHome<Person> {

	@Override
	public String getId() {
		return super.getId();
	}
}