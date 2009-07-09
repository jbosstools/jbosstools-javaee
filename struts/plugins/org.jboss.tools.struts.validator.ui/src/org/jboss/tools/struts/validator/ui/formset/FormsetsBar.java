package org.jboss.tools.struts.validator.ui.formset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.meta.help.HelpUtil;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.ui.action.CommandBar;
import org.jboss.tools.common.model.ui.action.CommandBarLayout;
import org.jboss.tools.common.model.ui.action.CommandBarListener;
import org.jboss.tools.common.model.undo.XTransactionUndo;
import org.jboss.tools.common.model.undo.XUndoManager;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.struts.validator.ui.Messages;
import org.jboss.tools.struts.validator.ui.XStudioValidatorPlugin;
import org.jboss.tools.struts.validator.ui.formset.model.DependencyModel;
import org.jboss.tools.struts.validator.ui.formset.model.FModel;
import org.jboss.tools.struts.validator.ui.formset.model.FieldModel;
import org.jboss.tools.struts.validator.ui.formset.model.FormModel;
import org.jboss.tools.struts.validator.ui.formset.model.FormsetsModel;
import org.jboss.tools.struts.validators.model.ValidatorConstants;
import org.jboss.tools.struts.validators.model.XModelEntityResolver;

public class FormsetsBar implements CommandBarListener, ActionNames, FSelectionListener {
	static String CREATE = Messages.FormsetsBar_Create;
	static String CREATE_FORMSET = Messages.FormsetsBar_CreateFormset;
	static String DELETE_FORMSET = Messages.FormsetsBar_DeleteFormset;
	static String CREATE_FORM = Messages.FormsetsBar_CreateForm;
	static String CREATE_FIELD = Messages.FormsetsBar_CreateField;
	static String CREATE_DEPEN = Messages.FormsetsBar_AddValidationRule;
	static String[] createActions = {CREATE, CREATE_FORM, CREATE_FIELD, CREATE_DEPEN, CREATE, CREATE};
	static String HELP = Messages.FormsetsBar_Help;

	protected Composite control;
	protected CommandBar formBar = createBar();
	protected FormsetsEditor formsetsEditor = null;
	protected XModelObject root = null;
	protected FModel selected = null;
	protected int selectedStatus = -1;
	protected int inheritanceStatus = -1;
	protected boolean enabled = false;

	public FormsetsBar() {
		setIcons();
	}
	public void dispose() {
		if (formBar!=null) formBar.dispose();
		formBar = null;
		
	}
	
	public Control createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new RowLayout());
		formBar.createControl(control);
		return control;	
	}
	
	public Control getControl() {
		return control;
	}
	
	public void setObject(XModelObject object) {
		root = object;
		setSelected(selected);
	}
	
	public XModelObject getObject() {
		return root;
	}

	private CommandBar createBar() {
		CommandBar c = new CommandBar();
		CommandBarLayout layout = c.getLayout();
		layout.iconsOnly = true;
		layout.asToolBar = true;
		c.setCommands(new String[]{CREATE_FORMSET, DELETE_FORMSET, CommandBar.SEPARATOR, CREATE, EDIT, DELETE});
		c.setEnabled(CREATE_FORMSET, false);
		c.setEnabled(DELETE_FORMSET, false);
		c.setEnabled(CREATE, false);
		c.setEnabled(EDIT, false);
		c.setEnabled(DELETE, false);
		c.addCommandBarListener(this);
		return c;
	}

	public void setSelected(FModel model) {
		boolean enabledChanged = updateFramesetEnabled();
		if(!enabledChanged && selected == model && selectedStatus > -1 && inheritanceStatus == getInheritanceStatus(model)) return;
		selected = model;
		updateInheritance();
		int s = (selected == null) ? 0 : (selected instanceof FormsetsModel) ? 1 :
				(selected instanceof FormModel) ? 2 : (selected instanceof FieldModel) ? 3 :
				(selected instanceof DependencyModel) ? 4 : 5;
		if(enabledChanged || selectedStatus != s) {
			if(selectedStatus < 0) selectedStatus = 0;
			formBar.rename(createActions[selectedStatus], createActions[s]);
			selectedStatus = s;
			boolean e = !CREATE.equals(createActions[s]) && enabled;
			formBar.setEnabled(createActions[s], e);
			formBar.setEnabled(EDIT, e);
			formBar.setEnabled(OVERWRITE, e);
			formBar.setEnabled(DELETE, (s > 0 && s < 5) && enabled);
			formBar.setEnabled(DEFAULT, (s > 0 && s < 5) && enabled);
		}
	}

	private int getInheritanceStatus(FModel model) {
		return (model == null) ? 0 : (model.isInherited()) ? 1 : (model.isOverriding()) ? 2 : 0;
	}

	private void updateInheritance() {
		updateInheritance(updateFramesetEnabled());
	}

	private void updateInheritance(boolean enabledChanged) {
		if(hasNoFormsets()) return;
		int is = getInheritanceStatus(selected);
		if(!enabledChanged && inheritanceStatus == is) return;
		inheritanceStatus = is;
		if(is == 1) {
			formBar.rename(EDIT, OVERWRITE);
		} else {
			formBar.rename(OVERWRITE, EDIT);
		}
		if(is == 2) {
			formBar.rename(DELETE, DEFAULT);
			formBar.setEnabled(DEFAULT, true);
		} else {
			formBar.rename(DEFAULT, DELETE);
			formBar.setEnabled(DELETE, (is != 1));
		}
	}

	public void action(String name) {
		if(name.equals(CREATE_FORMSET)) createFormset();
		else if(isCreateAction(name)) {
			FModel m = selected;
			Set s = getCurrentChildren(m);
			if(name.equals(CREATE_FORM)) createForm();
			else if(name.equals(CREATE_FIELD)) createField();
			else if(name.equals(CREATE_DEPEN)) createDependency();
			select(getNew(s, m));
		} else {
			if(name.equals(DELETE_FORMSET)) deleteFormset();
			else if(name.equals(DELETE)) {
				FModel m = (selected == null) ? null : selected.getParent();
				deleteObject(false);
				if(m != null && m.getChild(selected.getName()) == null) {
					select(m);
				}
			} 
			else if(name.equals(DEFAULT)) deleteObject(true);
			else if(name.equals(EDIT)) editObject();
			else if(name.equals(OVERWRITE)) overwriteObject();
			else if(name.equals(HELP)) help();
		}
	}
	
	private boolean isCreateAction(String name) {
		return (CREATE_FORM.equals(name) || CREATE_FIELD.equals(name) || CREATE_DEPEN.equals(name));
	}
	
	private void select(FModel m) {
		if(m != null) formsetsEditor.formsEditor.select(m);
	}
	
	private Set<String> getCurrentChildren(FModel m) {
		Set<String> s = new HashSet<String>();
		if(m != null) for (int i = 0; i < m.getChildCount(); i++) s.add(m.getChildAt(i).toString());
		return s;
	}

	private FModel getNew(Set s, FModel m) {
		if(m == null || m.getChildCount() != s.size() + 1) return null;
		if(m != null) for (int i = 0; i < m.getChildCount(); i++) {
			FModel c = m.getChildAt(i);
			if(!s.contains(c.toString())) return c;
		}		
		return null;
	}

	private void setIcons() {
		formBar.setImage(CREATE_FORMSET, FEditorConstants.IMAGE_CREATE_FORMSET);
		formBar.setImage(DELETE_FORMSET, FEditorConstants.IMAGE_DELETE);
		formBar.setImage(CREATE, FEditorConstants.IMAGE_CREATE);
		formBar.setImage(EDIT, FEditorConstants.IMAGE_EDIT);
		formBar.setImage(DELETE, FEditorConstants.IMAGE_DELETE);
	}

	private void help() {
		if(selected == null) return;
		try {
			HelpUtil.helpEclipse(selected.getModel(), selected.getKey());
		} catch (Exception e) {
			XStudioValidatorPlugin.getPluginLog().logError(e);			
		}		
	}

	private void createFormset() {
		invoke("CreateActions.AddFormset", root); //$NON-NLS-1$
	}

	private void deleteFormset() {
		XModelObject[] os = formsetsEditor.getFormsetsModel().getCurrentFormsets();
		deleteObject(os, false);
	}

	private void createForm() {
		XModelObject[] os = formsetsEditor.getFormsetsModel().getCurrentFormsets();
		if(os.length > 0) invoke("CreateActions.AddForm", os[0]); //$NON-NLS-1$
	}

	private void createField() {
		XModelObject[] fs = formsetsEditor.getFormsetsModel().getCurrentFormsets();
		if(selected == null) return;
		XModelObject[] os = selected.getModelObjects();
		for (int i = 0; i < os.length; i++) if(isCurrent(fs, os[i])) {
			XModelObject o = getAncestor(os[i], ValidatorConstants.ENT_FORM);
			if(o == null) continue;
			invoke("CreateActions.AddField", o); //$NON-NLS-1$
			return;
		}
	}

	private XModelObject getAncestor(XModelObject o, String entity) {
		// danger FORMSET starts with FORM
		while(o != null && !o.getModelEntity().getName().startsWith(entity)) o = o.getParent();
		return o;
	}

	private void createDependency() {
		XModelObject[] fs = formsetsEditor.getFormsetsModel().getCurrentFormsets();
		if(selected == null) return;
		XModelObject[] os = selected.getModelObjects();
		for (int i = 0; i < os.length; i++) if(isCurrent(fs, os[i])) {
			XModelObject o = getAncestor(os[i], ValidatorConstants.ENT_FIELD);
			if(o == null) continue;
			Properties p = new Properties();
			p.setProperty("help", "Wizard_Validation_Dependency"); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("shell", formBar.getControl().getShell()); //$NON-NLS-1$
			XActionInvoker.invoke("ValidationDependencyHelper", "CreateActions.AddDependency", os[i], p);  //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
	}

	private boolean isCurrent(XModelObject[] fs, XModelObject o) {
		String op = o.getPath();
		if(op == null) return false;
		for (int i = 0; i < fs.length; i++)
		  if(fs[i].getPath() != null && op.startsWith(fs[i].getPath())) return true;
		return false;
	}

	private void deleteObject(boolean isOverriding) {
		XModelObject[] fs = formsetsEditor.getFormsetsModel().getCurrentFormsets();
		if(selected instanceof DependencyModel) {
			deleteDependency(fs, selected);
		} else {
			XModelObject[] os = selected.getModelObjects();
			ArrayList<XModelObject> l = new ArrayList<XModelObject>();
			for (int i = 0; i < os.length; i++) if(isCurrent(fs, os[i])) l.add(os[i]);
			os = l.toArray(new XModelObject[0]);
			deleteObject(os, isOverriding);
		}
		updateInheritance();
	}

	private void deleteObject(XModelObject[] os, boolean isOverriding) {
		if(os.length == 0) return;
		String action = (isOverriding) ? "DeleteActions.ResetDefault" : "DeleteActions.Delete"; //$NON-NLS-1$ //$NON-NLS-2$
		if(os.length == 1) {
			invoke(action, os[0]);
			return;
		}
		XUndoManager undo = os[0].getModel().getUndoManager();
		XTransactionUndo u = new XTransactionUndo(os[0].getPresentationString(), XTransactionUndo.REMOVE);
		undo.addUndoable(u);
		try {
			invoke(action, os[0]);
			if(os[0].isActive()) {
				undo.rollbackTransactionInProgress();
			} else {
				for (int i = 1; i < os.length; i++)
				  if(os[i].isActive()) DefaultRemoveHandler.removeFromParent(os[i]);
			}
		} catch (Exception e) {
			XStudioValidatorPlugin.getPluginLog().logError(e);			
			undo.rollbackTransactionInProgress();
		} finally {
			u.commit();
		}
	}

	private void editObject() {
		XModelObject[] fs = formsetsEditor.getFormsetsModel().getCurrentFormsets();
		XModelObject[] os = selected.getModelObjects();
		for (int i = 0; i < os.length; i++) if(isCurrent(fs, os[i])) {
			invoke("Properties.Properties", os[i]); //$NON-NLS-1$
			return;
		}
	}

	private void overwriteObject() {
		XModelObject[] targets = FieldDataEditor.getTarget(selected);
		if(targets != null && targets[1] != null) {
			try {
				DefaultCreateHandler.addCreatedObject(targets[1], targets[2], FindObjectHelper.IN_EDITOR_ONLY);
			} catch (XModelException e) {
				ModelPlugin.getPluginLog().logError(e);
			}
		}
		updateInheritance();
	}

	private void deleteDependency(XModelObject[] fs, FModel f) {
		XModelObject[] os = f.getParent().getModelObjects();
		if(os.length == 0) return;
		Properties p = new Properties();
		p.setProperty("dependency name", f.getName()); //$NON-NLS-1$
		XActionInvoker.invoke("ValidationDependencyHelper", "DeleteActions.Delete", os[0], p); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void invoke(String actionpath, XModelObject object) {
		XActionInvoker.invoke(actionpath, object, null);
	}

	static String SEPARATOR = "separator"; //$NON-NLS-1$

	public String[] getMenu() {
		ArrayList<String> l = new ArrayList<String>();
		if(selectedStatus >= 0 && !createActions[selectedStatus].equals(CREATE)) {
			if(addToMenu(l, createActions[selectedStatus])) l.add(SEPARATOR);
		}
		String edit = (inheritanceStatus == 1) ? OVERWRITE : EDIT;
		if(addToMenu(l, edit)) l.add(SEPARATOR);
		String delete = (inheritanceStatus == 2) ? DEFAULT : DELETE;
		if(addToMenu(l, delete)) {
			//add separator?
		}
		return l.toArray(new String[0]);
	}

	private boolean addToMenu(ArrayList<String> menu, String command) {
		boolean b = (formBar.isEnabled(command));
		if(b) menu.add(command);
		return b;
	}

	private boolean updateFramesetEnabled() {
		boolean e = root != null && root.isObjectEditable();
		if(hasNoFormsets()) {
			if(!enabled && !e) return false;
			enabled = false;
			formBar.setEnabled(CREATE_FORMSET, e);
			formBar.setEnabled(DELETE_FORMSET, false);
			return true;
		}
		if (e != enabled) {
			enabled = e;
			formBar.setEnabled(CREATE_FORMSET, enabled);
			formBar.setEnabled(DELETE_FORMSET, enabled);
			return true;
		} else {
			return false;
		}
	}

	private boolean hasNoFormsets() {
		if(root!=null) {
			XModelObject[] xmos = XModelEntityResolver.getResolvedChildren(root, ValidatorConstants.ENT_FORMSET);
			if(xmos.length==0) {
				return true;
			}
		}
		return false;
	}
}