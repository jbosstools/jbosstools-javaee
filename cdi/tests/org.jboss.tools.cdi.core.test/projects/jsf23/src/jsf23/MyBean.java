package jsf23;

import java.util.Map;

import javax.faces.annotation.ApplicationMap;
import javax.faces.annotation.FlowMap;
import javax.faces.annotation.HeaderMap;
import javax.faces.annotation.HeaderValuesMap;
import javax.faces.annotation.InitParameterMap;
import javax.faces.annotation.RequestCookieMap;
import javax.faces.annotation.RequestMap;
import javax.faces.annotation.RequestParameterMap;
import javax.faces.annotation.RequestParameterValuesMap;
import javax.faces.annotation.SessionMap;
import javax.faces.annotation.ViewMap;
import javax.inject.Inject;

public class MyBean {
	@Inject
	private ResourceHandler resourceHandler;
	
	@Inject
	private ExternalContext externalContext;
	
	@Inject
	private FacesContext facesContext;
	
	@Inject
	private Flash flash;
	
	@Inject
	@ApplicationMap
	private Map<String, Object> applicationMap;
	
	@Inject
	@RequestCookieMap
	private Map<String, Object> cookieMap;

	@Inject
	@FlowMap
	private Map<Object, Object> flowMap;

	@Inject
	@HeaderMap
	private Map<String, String> headerMap;
	
	@Inject
	@HeaderValuesMap
	private Map<String, String[]> headerValuesMap;
	
	@Inject
	@InitParameterMap
	private Map<String, String> initParameterMap;

	@Inject
	@RequestParameterMap
	private Map<String, String> requestParameterMap;

	@Inject
	@RequestParameterValuesMap
	private Map<String, String[]> requestParametersValuesMap;

	@Inject
	@RequestMap
	private Map<String, Object> requestMap;

	@Inject
	@SessionMap
	private Map<String, Object> sessionMap;

	@Inject
	@ViewMap
	private Map<String, Object> viewMap;
	
}