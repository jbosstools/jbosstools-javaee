/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.extensions;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.LoggingService;
import org.eclipse.sapphire.Sapphire;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionException;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.jboss.tools.batch.ui.editor.internal.model.Flow;
import org.jboss.tools.batch.ui.editor.internal.model.Job;
import org.jboss.tools.batch.ui.editor.internal.model.Split;

/**
 * This EL extension function returns a String representation of a path from
 * root to a Flow in a valid Batch model. The path consist of Job/Flow/Split ids
 * separated by '/'. If an element on the path does not have an id, its path
 * segment is a &lt;classname&gt; instead.
 * 
 * @author Tomas Milata
 */
public class BatchPathFunction extends Function {

	private static final char SEPARATOR = '/';

	@Override
	public String name() {
		return "BatchPath";
	}

	@Override
	public FunctionResult evaluate(FunctionContext context) {

		return new FunctionResult(this, context) {

			@Override
			protected Object evaluate() throws FunctionException {
				StringBuilder path = new StringBuilder();

				try {
					Element element = cast(operand(0), Element.class);
					do {
						path.insert(0, label(element));
						path.insert(0, SEPARATOR);

						if (element.parent() != null) {
							element = element.parent().element();
						} else {
							break;
						}

					} while (element != null);

				} catch (ClassCastException e) {
					Sapphire.service(LoggingService.class).log(e);
				}

				return path.toString();
			}
		};
	}

	/**
	 * @param element
	 *            Its type should be {@link Job}, a {@link Flow} or a
	 *            {@link Split}.
	 * @return its id or &lt;classname&gt; if id is null or element is of an
	 *         incorrect type.
	 */
	private static String label(Element element) {
		String id = null;
		if (element instanceof Job) {
			id = ((Job) element).getId().content();
		} else if (element instanceof Flow) {
			id = ((Flow) element).getId().content();
		} else if (element instanceof Split) {
			id = ((Split) element).getId().content();
		}

		if (id != null) {
			return id;
		}

		StringBuilder label = new StringBuilder("<");
		label.append(element.type().getSimpleName().toLowerCase());
		label.append(">");
		return label.toString();
	}

}
