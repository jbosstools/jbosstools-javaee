package org.jboss.tools.seam.ui.widget.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author eskimo
 *
 */
public class LabelFieldEditor extends BaseFieldEditor {

	public LabelFieldEditor(String name, String label) {
		super(name, label, "");
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.BaseFieldEditor#doFillIntoGrid(java.lang.Object)
	 */
	@Override
	public void doFillIntoGrid(Object parent) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.BaseFieldEditor#getEditorControls(java.lang.Object)
	 */
	@Override
	public Object[] getEditorControls(Object composite) {
		// TODO Auto-generated method stub
		return new Control[]{createLabelControl((Composite)composite)};
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.BaseFieldEditor#getEditorControls()
	 */
	@Override
	public Object[] getEditorControls() {
		return getEditorControls(null);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.IFieldEditor#isEditable()
	 */
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.IFieldEditor#save(java.lang.Object)
	 */
	public void save(Object object) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.IFieldEditor#setEditable(boolean)
	 */
	public void setEditable(boolean ediatble) {
		// TODO Auto-generated method stub

	}
	
	public void setValue(Object value) {
		// supress parent method
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.BaseFieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		// TODO Auto-generated method stub
		return 1;
	}
}
