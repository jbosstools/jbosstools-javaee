package beans;

import java.util.List;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public abstract class DecoratorAnnotatedBean extends BeanNotAnnotated implements List<String> {
	@Inject @Delegate List<String> delegate;
}
