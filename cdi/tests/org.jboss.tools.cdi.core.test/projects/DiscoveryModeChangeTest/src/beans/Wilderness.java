package beans;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Wilderness {

	@Inject IBear bear;

}
