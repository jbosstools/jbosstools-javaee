package deltaspike.handler;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.deltaspike.core.api.exception.control.ExceptionHandler;
import org.apache.deltaspike.core.api.exception.control.Handles;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent;
import javax.enterprise.context.ApplicationScoped;

@ExceptionHandler
@ApplicationScoped
public class MyHandlers {

	void printExceptions(@Handles ExceptionEvent<Throwable> evt, @Named("handlerParam") String s) {
	}

	void brokenHandler(@Handles Throwable evt) {
	}

	@Produces
	@Named("handlerParam")
	String s = "";
}
