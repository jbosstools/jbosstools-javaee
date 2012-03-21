package cdi.seam;

import javax.enterprise.context.SessionScoped;

import org.jboss.seam.solder.core.Requires;
import java.io.Serializable;

@SessionScoped
@Requires("cdi.test.Manager")
public class ManagerProducer implements Serializable {

	private static final long serialVersionUID = 3229728978587622719L;

}
