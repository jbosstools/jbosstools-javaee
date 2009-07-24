/**
 * 
 */
package test;

import java.io.IOException;

import javax.faces.component.UIComponent;

import org.apache.taglibs.standard.lang.jstl.ELException;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
 * @author mareshkau
 * 
 */
public class IfHandler extends TagHandler {
	private final TagAttribute test;

	private final TagAttribute var;

	/**
	 * @param config
	 */
	public IfHandler(TagConfig config) {
		super(config);
		this.test = this.getRequiredAttribute("test");
		this.var = this.getAttribute("var");
	}

	@Override
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, ELException {
		boolean b = this.test.getBoolean(ctx);
		if (this.var != null) {
			ctx.setAttribute(var.getValue(ctx), new Boolean(b));
		}
		if (b) {
			this.nextHandler.apply(ctx, parent);
		}
	}

}
