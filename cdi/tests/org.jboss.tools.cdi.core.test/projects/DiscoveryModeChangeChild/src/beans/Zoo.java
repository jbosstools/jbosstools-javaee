package beans;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Zoo {

	@Inject IBear bear;

}
