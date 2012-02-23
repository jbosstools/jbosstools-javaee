/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRadio;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.condition.NonEmptyTableCondition;
import org.jboss.tools.ui.bot.ext.condition.TaskDuration;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

public class CDIWizardBase extends Wizard {

	private final CDIWizardType type;

	public CDIWizardBase(CDIWizardType type) {
		super(new SWTBot().activeShell().widget);
		assert ("New " + type).equals(getText());
		this.type = type;
	}

	public CDIWizardBase setName(String name) {
		if (CDIWizardType.BEANS_XML == type) {
			setText("File name:", name);
		} else {
			setText("Name:", name);
		}
		return this;
	}

	public CDIWizardBase setPackage(String pkg) {
		setText("Package:", pkg);
		return this;
	}

	public CDIWizardBase setSourceFolder(String src) {
		if (CDIWizardType.BEANS_XML == type) {
			setText("Enter or select the parent folder:", src);
		} else {
			setText("Source folder:", src);
		}
		return this;
	}

	public CDIWizardBase setInherited(boolean set) {
		setCheckbox("Add @Inherited", set);
		return this;
	}

	public boolean isInherited() {
		return isCheckboxSet("Add @Inherited");
	}

	public CDIWizardBase setGenerateComments(boolean set) {
		setCheckbox("Generate comments", set);
		return this;
	}

	public boolean isGenerateComments() {
		return isCheckboxSet("Generate comments");
	}

	public CDIWizardBase setNormalScope(boolean set) {
		switch (type) {
		case SCOPE:
			setCheckbox("is normal scope", set);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public boolean isNormalScope() {
		switch (type) {
		case SCOPE:
			return isCheckboxSet("is normal scope");
		default:
			return true;
		}
	}

	public CDIWizardBase setPassivating(boolean set) {
		switch (type) {
		case SCOPE:
			setCheckbox("is passivating", set);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public boolean isPassivating() {
		switch (type) {
		case SCOPE:
			return isCheckboxSet("is passivating");
		default:
			return false;
		}
	}

	public CDIWizardBase setAlternative(boolean set) {
		switch (type) {
		case STEREOTYPE:
		case BEAN:
			setCheckbox("Add @Alternative", set);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public boolean isAlternative() {
		switch (type) {
		case STEREOTYPE:
		case BEAN:
			return isCheckboxSet("Add @Alternative");
		default:
			return false;
		}
	}
	
	public CDIWizardBase setRegisterInBeansXml(boolean set) {
		switch (type) {
		case STEREOTYPE:
		case BEAN:
			setCheckbox("Register in beans.xml", set);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}
	
	public boolean isRegisteredInBeansXML() {
		switch (type) {
		case STEREOTYPE:
		case BEAN:
			return isCheckboxSet("Register in beans.xml");
		default:
			return false;
		}
	}

	public CDIWizardBase setNamed(boolean set) {
		switch (type) {
		case STEREOTYPE:
		case BEAN:
			setCheckbox("Add @Named", set);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public CDIWizardBase setNamedName(String name) {
		if (CDIWizardType.BEAN != type) {
			throw new UnsupportedOperationException();
		}
		setText("Bean Name:", name);
		return this;
	}

	public boolean isNamed() {
		switch (type) {
		case STEREOTYPE:
			return isCheckboxSet("Add @Named");
		default:
			return false;
		}
	}

	public CDIWizardBase setTarget(String target) {
		switch (type) {
		case STEREOTYPE:
		case INTERCEPTOR_BINDING:
			setCombo("Target:", target);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public List<String> getTargets() {
		setFocus();
		return Arrays.asList(bot().comboBoxWithLabel("Target:").items());
	}

	public CDIWizardBase setScope(String scope) {
		switch (type) {
		case STEREOTYPE:
		case BEAN:
			setCombo("Scope:", scope);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public List<String> getScopes() {
		setFocus();
		return Arrays.asList(bot().comboBoxWithLabel("Scope:").items());
	}

	public CDIWizardBase addIBinding(String ib) {
		switch (type) {
		case INTERCEPTOR_BINDING:
		case STEREOTYPE:
		case INTERCEPTOR:
			setFocus();
			bot().button(IDELabel.Button.ADD_WITHOUT_DOTS, 0).click();
			SWTBotShell sh = bot().activeShell();
			sh.bot().text().typeText(ib);
			sh.bot().waitUntil(new NonEmptyTableCondition(sh.bot().table()), TaskDuration.LONG.getTimeout());
			sh.bot().button(IDELabel.Button.OK).click();
			setFocus();
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public List<String> getIBindings() {
		setFocus();
		return Arrays.asList(bot().listWithLabel("Interceptor Bindings:")
				.getItems());
	}

	public CDIWizardBase addStereotype(String stereotype) {
		switch (type) {
		case STEREOTYPE:
			setFocus();
			bot().button("Add", 1).click();
			SWTBotShell sh = bot().activeShell();
			sh.bot().text().setText(stereotype);
			sh.bot().button("OK").click();
			setFocus();
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public List<String> getStereotypes() {
		setFocus();
		return Arrays.asList(bot().listWithLabel("Stereotypes:").getItems());
	}

	public CDIWizardBase setPublic(boolean isPublic) {
		switch (type) {
		case DECORATOR:
		case BEAN:
		case ANNOTATION_LITERAL:
			setFocus();
			if (isPublic) {
				bot().radio("public").click();
			} else {
				class Radio2 extends SWTBotRadio {
					Radio2(Button b) {
						super(b);
					}

					@Override
					public SWTBotRadio click() {
						return (SWTBotRadio) click(true);
					}
				}
				final Button b = bot().radio("default").widget;
				new Radio2(b).click();
			}
			setFocus();
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public CDIWizardBase setFieldName(String name) {
		switch (type) {
		case DECORATOR:
			setText("Delegate Field Name:", name);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public CDIWizardBase addInterface(String intf) {
		switch (type) {
		case DECORATOR:
		case BEAN:
			setFocus();
			bot().button(IDELabel.Button.ADD, 0).click();
			SWTBotShell sh = bot().activeShell();
			sh.bot().text().typeText(intf);
			sh.bot().waitUntil(new NonEmptyTableCondition(sh.bot().table()), TaskDuration.LONG.getTimeout());
			sh.bot().table().getTableItem(0).select();
			sh.bot().button(IDELabel.Button.OK).click();
			setFocus();
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public CDIWizardBase addQualifier(String qualifier) {
		setFocus();
		switch (type) {
		case BEAN:
			bot().button("Add", 0).click();
			break;
		case ANNOTATION_LITERAL:
			bot().button("Browse", 0).click();
			break;
		default:
			throw new UnsupportedOperationException();
		}
		SWTBotShell sh = bot().activeShell();
		sh.bot().text().setText(qualifier);
		sh.bot().waitUntil(new NonEmptyTableCondition(sh.bot().table()), TaskDuration.SHORT.getTimeout());
		sh.bot().table().getTableItem(0).select();
		sh.bot().button("OK").click();
		setFocus();
		return this;
	}

	public CDIWizardBase setAbstract(boolean isAbstract) {
		switch (type) {
		case DECORATOR:
		case BEAN:
		case ANNOTATION_LITERAL:
			setCheckbox("abstract", isAbstract);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public CDIWizardBase setFinal(boolean isFinal) {
		switch (type) {
		case DECORATOR:
		case BEAN:
		case ANNOTATION_LITERAL:
			setCheckbox("final", isFinal);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public CDIWizardBase setSuperclass(String name) {
		switch (type) {
		case INTERCEPTOR:
			setFocus();
			bot().button("Browse...", 2).click();
			SWTBotShell sh = bot().activeShell();
			sh.bot().text().setText(name);
			sh.bot().table().getTableItem(0).select();
			sh.bot().button("OK").click();
			setFocus();
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public CDIWizardBase setMethodName(String name) {
		switch (type) {
		case INTERCEPTOR:
			setText("Around Invoke Method Name:", name);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return this;
	}

	
	private void setCheckbox(String label, boolean set) {
		setFocus();
		SWTBotCheckBox c = bot().checkBox(label);
		if (c.isChecked() != set) {
			if (set) {
				c.select();
			} else {
				c.deselect();
			}
		}
	}

	private boolean isCheckboxSet(String label) {
		setFocus();
		SWTBotCheckBox c = bot().checkBox(label);
		return c.isChecked();
	}

	private void setCombo(String label, String value) {
		setFocus();
		SWTBotCombo c = bot().comboBoxWithLabel(label);
		c.setSelection(value);
	}

}
