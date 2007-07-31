package org.jboss.tools.seam.internal.core.scanner;

public class ScannerException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ScannerException() {
	}
	
	public ScannerException(Throwable cause) {
		super(cause);
	}

	public ScannerException(String message, Throwable cause) {
		super(message, cause);
	}

}
