package deltaspike.exclude;

import javax.inject.Named;

import org.apache.deltaspike.core.api.exclude.Exclude;
import javax.enterprise.context.ApplicationScoped;

@Exclude
@Named
@ApplicationScoped
public class ExcludedBean {
}
