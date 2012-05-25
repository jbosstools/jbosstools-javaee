package deltaspike.handler;

import org.apache.deltaspike.core.api.exception.control.annotation.ExceptionHandler;
import org.apache.deltaspike.core.api.exception.control.annotation.Handles;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent;

@ExceptionHandler
public class MyHandlers {

	void printExceptions(@Handles ExceptionEvent<Throwable> evt) {
	}
}
