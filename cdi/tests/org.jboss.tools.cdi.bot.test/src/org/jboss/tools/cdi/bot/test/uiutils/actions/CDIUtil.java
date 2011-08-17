package org.jboss.tools.cdi.bot.test.uiutils.actions;

import java.io.InputStream;
import java.util.Scanner;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizardType;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;

public class CDIUtil {
	

	public static void addCDISupport(final SWTBotTree tree, SWTBotTreeItem item, SWTBotExt bot, SWTUtilExt util) {
		nodeContextMenu(tree, item, 
				"Configure","Add CDI (Context and Dependency Injection) support...").click();
		bot.activeShell().bot().button("OK").click();
		bot.sleep(Timing.time2S());
		util.waitForNonIgnoredJobs();
	}
	
	public static void resolveQuickFix(final SWTBotTree tree, SWTBotTreeItem item, SWTBotExt bot, SWTUtilExt util) {
		nodeContextMenu(bot.tree(), item, "Quick Fix").click();
		bot.activeShell().bot().button("Finish").click();
		bot.sleep(Timing.time2S());
		util.waitForNonIgnoredJobs();
	}
	
	public static void copyResourceToClass(SWTBotEditor classEdit,
			InputStream resource, boolean closeEdit) {
		SWTBotEclipseEditor st = classEdit.toTextEditor();
		st.selectRange(0, 0, st.getText().length());
		String code = readStream(resource);
		st.setText(code);
		classEdit.save();
		if (closeEdit) classEdit.close(); 		
	}
	
	public static SWTBotMenu nodeContextMenu(final SWTBotTree tree,
			SWTBotTreeItem item, final String... menu) {
		assert menu.length > 0;
		ContextMenuHelper.prepareTreeItemForContextMenu(tree, item);
		return UIThreadRunnable.syncExec(new Result<SWTBotMenu>() {

			public SWTBotMenu run() {
				SWTBotMenu m = new SWTBotMenu(ContextMenuHelper.getContextMenu(
						tree, menu[0], false));
				for (int i = 1; i < menu.length; i++) {
					m = m.menu(menu[i]);
				}
				return m;
			}
		});
	}
	
	public static CDIWizard qualifier(String pkg, String name, boolean inherited,
			boolean comments) {
		return create(CDIWizardType.QUALIFIER, pkg, name, inherited, comments);
	}
	

	public static CDIWizard scope(String pkg, String name, boolean inherited,
			boolean comments, boolean normalScope, boolean passivating) {
		CDIWizard w = create(CDIWizardType.SCOPE, pkg, name, inherited,
				comments);
		w = w.setNormalScope(normalScope);
		return normalScope ? w.setPassivating(passivating) : w;
	}
	

	public static CDIWizard binding(String pkg, String name, String target,
			boolean inherited, boolean comments) {
		CDIWizard w = create(CDIWizardType.INTERCEPTOR_BINDING, pkg, name,
				inherited, comments);
		return target != null ? w.setTarget(target) : w;
	}
	

	public static CDIWizard stereotype(String pkg, String name, String scope,
			String target, boolean inherited, boolean named,
			boolean alternative, boolean comments) {
		CDIWizard w = create(CDIWizardType.STEREOTYPE, pkg, name, inherited,
				comments).setAlternative(alternative).setNamed(named);
		if (scope != null) {
			w = w.setScope(scope);
		}
		return target != null ? w.setTarget(target) : w;
	}
	

	public static CDIWizard decorator(String pkg, String name, String intf, String fieldName,
			boolean isPublic, boolean isAbstract, boolean isFinal, boolean comments) {
		CDIWizard w = create(CDIWizardType.DECORATOR, pkg, name, comments);
		w = w.addInterface(intf).setPublic(isPublic).setFinal(isFinal).setAbstract(isAbstract);
		return fieldName != null ? w.setFieldName(fieldName) : w;
	}
	
	
	public static CDIWizard interceptor(String pkg, String name, String ibinding,
			String superclass, String method, boolean comments) {
		CDIWizard w = create(CDIWizardType.INTERCEPTOR, pkg, name, comments);
		if (superclass != null) {
			w = w.setSuperclass(superclass);
		}
		if (method != null) {
			w = w.setMethodName(method);
		}
		return w.addIBinding(ibinding);
	}
	
	
	public static CDIWizard bean(String pkg, String name, boolean isPublic, boolean isAbstract,
			boolean isFinal, boolean comments, String named,
			String interfaces, String scope, String qualifier) {
		CDIWizard w = create(CDIWizardType.BEAN, pkg, name, comments);
		if (named != null) {
			w.setNamed(true);
			if (!"".equals(named.trim())) {
				w.setNamedName(named);
			}
		}
		w = w.setPublic(isPublic).setFinal(isFinal).setAbstract(isAbstract);
		if (interfaces != null && !"".equals(interfaces.trim())) {
			w.addInterface(interfaces);
		}
		if (scope != null && !"".equals(scope.trim())) {
			w.setScope(scope);
		}
		if (qualifier != null && !"".equals(qualifier.trim())) {
			w.addQualifier(qualifier);
		}
		return w;
	}
	
	public static CDIWizard annLiteral(String pkg, String name, boolean isPublic, boolean isAbstract,
			boolean isFinal, boolean comments, String qualifier) {
		assert qualifier != null && !"".equals(qualifier.trim()) : "Qualifier has to be set"; 
		CDIWizard w = create(CDIWizardType.ANNOTATION_LITERAL, pkg, name, comments);
		return w.setPublic(isPublic).setFinal(isFinal).setAbstract(isAbstract).addQualifier(qualifier);
	}
	
	private static CDIWizard create(CDIWizardType type, String pkg, String name,
			boolean inherited, boolean comments) {
		return create(type, pkg, name, comments).setInherited(inherited);
	}

	private  static CDIWizard create(CDIWizardType type, String pkg, String name, boolean comments) {
		CDIWizard p = new NewCDIFileWizard(type).run();
		return p.setPackage(pkg).setName(name).setGenerateComments(comments);
	}
	
	private static String readStream(InputStream is) {
		// we don't care about performance in tests too much, so this should be
		// OK
		return new Scanner(is).useDelimiter("\\A").next();
	}
	
	

}
