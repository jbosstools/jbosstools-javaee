package org.jboss.tools.seam.text.ext.hyperlink;

import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.IOpenableElement;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.text.ext.SeamExtMessages;
import org.jboss.tools.seam.text.ext.SeamExtPlugin;

public class SeamComponentHyperlink implements IHyperlink {

	private IRegion fRegion; 
	private IJavaElement fElement;
	private IOpenableElement fOpenable;
	private String fLabel;
	private String fName;

	/**
	 * Creates a new Seam Component hyperlink.
	 */
	SeamComponentHyperlink(IRegion region, IJavaElement element, String name) {
		Assert.isNotNull(region);
		Assert.isNotNull(element);
		Assert.isNotNull(name);

		fRegion = region;
		fElement = element;
		fOpenable = null;
		fLabel = SeamExtMessages.SeamFactory;
		fName = name;
	}

	/**
	 * Creates a new Seam Component hyperlink.
	 */
	SeamComponentHyperlink(IRegion region, ISeamComponent element, String name) {
		Assert.isNotNull(region);
		Assert.isNotNull(element);
		Assert.isNotNull(name);

		fRegion = region;
		fElement = null;
		fLabel = SeamExtMessages.SeamComponent;
		fName = name;
		fOpenable = null;
		
		ISeamJavaComponentDeclaration javaDeclaration = element.getJavaDeclaration();
		if (javaDeclaration != null && javaDeclaration instanceof IOpenableElement) {
			fOpenable = (IOpenableElement)javaDeclaration;
		} else {
			Set<ISeamComponentDeclaration> declarations = element.getAllDeclarations();
			for (ISeamComponentDeclaration componentDeclaration : declarations) {
				if (componentDeclaration instanceof IOpenableElement) {
					fOpenable = (IOpenableElement)componentDeclaration;
					break;
				}
			}
		}
	}
	
	/**
	 * Creates a new Seam Component hyperlink.
	 */
	SeamComponentHyperlink(IRegion region, IBijectedAttribute element, String name) {
		Assert.isNotNull(region);
		Assert.isNotNull(element);
		Assert.isNotNull(name);

		fRegion = region;
		fElement = element.getSourceMember();
		fLabel = SeamExtMessages.SeamBijected;
		fName = name;
		fOpenable = null;
	}

	/**
	 * Creates a new Seam Component hyperlink.
	 */
	SeamComponentHyperlink(IRegion region, IRole element, String name) {
		Assert.isNotNull(region);
		Assert.isNotNull(element);
		Assert.isTrue(element instanceof IOpenableElement);
		Assert.isNotNull(name);

		fRegion = region;
		fElement = null;
		fOpenable = (IOpenableElement)element;
		fLabel = SeamExtMessages.SeamRole;
		fName = name;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getHyperlinkRegion()
	 * @since 3.1
	 */
	public IRegion getHyperlinkRegion() {
		return fRegion;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#open()
	 * @since 3.1
	 */
	public void open() {

		if (fOpenable != null) {
			fOpenable.open();
			return;
		}
		
		if (fElement != null) {
			try {
				IEditorPart part = null;
				part = JavaUI.openInEditor(fElement);
				if (part != null) {
					JavaUI.revealInEditor(part, fElement);
				}
			} catch (PartInitException e) {
				SeamExtPlugin.getPluginLog().logError(e);  
			} catch (JavaModelException e) {
				// Ignore. It is probably because of Java element is not found 
			}	
		}
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getTypeLabel()
	 * @since 3.1
	 */
	public String getTypeLabel() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		return MessageFormat.format(SeamExtMessages.OpenAs, fName, fLabel);
	}

	
}
