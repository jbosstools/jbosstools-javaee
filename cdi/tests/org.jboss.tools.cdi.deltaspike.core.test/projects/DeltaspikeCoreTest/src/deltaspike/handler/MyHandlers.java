package deltaspike.handler;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.deltaspike.core.api.exception.control.annotation.ExceptionHandler;
import org.apache.deltaspike.core.api.exception.control.annotation.Handles;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent;

@ExceptionHandler
public class MyHandlers {

	void printExceptions(@Handles ExceptionEvent<Throwable> evt, @Named("handlerParam") String s) {
	}

	void brokenHandler(@Handles Throwable evt) {
	}

	@Produces
	@Named("handlerParam")
	String s = "";
}
