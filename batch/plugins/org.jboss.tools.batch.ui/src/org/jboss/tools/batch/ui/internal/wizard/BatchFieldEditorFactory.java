/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.ui.internal.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.common.ui.widget.editor.BaseListFieldEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.SwtFieldEditorFactory;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchFieldEditorFactory {
	public static final String ARTIFACT_EDITOR = "artifact";
	public static final String DERIVE_FROM_EDITOR = "derive-from";
	public static final String LOADER_OPTIONS_EDITOR = "loader-options";
	public static final String NAME_EDITOR = "name";
	public static final String PROPERTIES_EDITOR = "properties";
	
	static Map<String, BatchArtifactType> ARTIFACTS = new TreeMap<String, BatchArtifactType>();
	static List<String> ARTIFACT_LIST = new ArrayList<String>();
	
	static {
		bindArtifact(WizardMessages.batchletTypeLabel, BatchArtifactType.BATCHLET);
		bindArtifact(WizardMessages.deciderTypeLabel, BatchArtifactType.DECIDER);
		bindArtifact(WizardMessages.itemReaderTypeLabel, BatchArtifactType.ITEM_READER);
		bindArtifact(WizardMessages.itemWriterTypeLabel, BatchArtifactType.ITEM_WRITER);
		bindArtifact(WizardMessages.itemProcessorTypeLabel, BatchArtifactType.ITEM_PROCESSOR);
		bindArtifact(WizardMessages.checkpointAlgorithmTypeLabel, BatchArtifactType.CHECKPOINT_ALGORITHM);
		bindArtifact(WizardMessages.partitionMapperTypeLabel, BatchArtifactType.PARTITION_MAPPER);
		bindArtifact(WizardMessages.partitionReducerTypeLabel, BatchArtifactType.PARTITION_REDUCER);
		bindArtifact(WizardMessages.partitionCollectorTypeLabel, BatchArtifactType.PARTITION_COLLECTOR);
		bindArtifact(WizardMessages.partitionAnalyzerTypeLabel, BatchArtifactType.PARTITION_ANALYZER);
		bindArtifact(WizardMessages.jobListenerTypeLabel, BatchArtifactType.JOB_LISTENER);
		bindArtifact(WizardMessages.stepListenerTypeLabel, BatchArtifactType.STEP_LISTENER);
		bindArtifact(WizardMessages.chunkListenerTypeLabel, BatchArtifactType.CHUNK_LISTENER);
		bindArtifact(WizardMessages.itemReadListenerTypeLabel, BatchArtifactType.ITEM_READ_LISTENER);
		bindArtifact(WizardMessages.itemProcessListenerTypeLabel, BatchArtifactType.ITEM_PROCESS_LISTENER);
		bindArtifact(WizardMessages.itemWriteListenerTypeLabel, BatchArtifactType.ITEM_WRITE_LISTENER);
		bindArtifact(WizardMessages.skipReadListenerTypeLabel, BatchArtifactType.SKIP_READ_LISTENER);
		bindArtifact(WizardMessages.skipProcessListenerTypeLabel, BatchArtifactType.SKIP_PROCESS_LISTENER);
		bindArtifact(WizardMessages.skipWriteListenerTypeLabel, BatchArtifactType.SKIP_WRITE_LISTENER);
		bindArtifact(WizardMessages.retryReadListenerTypeLabel, BatchArtifactType.RETRY_READ_LISTENER);
		bindArtifact(WizardMessages.retryProcessListenerTypeLabel, BatchArtifactType.RETRY_PROCESS_LISTENER);
		bindArtifact(WizardMessages.retryWriteListenerTypeLabel, BatchArtifactType.RETRY_WRITE_LISTENER);
	}

	static void bindArtifact(String label, BatchArtifactType type) {
		ARTIFACT_LIST.add(label);
		ARTIFACTS.put(label,  type);
	}

	public static String getArtifactLabel(BatchArtifactType type) {
		for (String label: BatchFieldEditorFactory.ARTIFACT_LIST) {
			if(type.equals(BatchFieldEditorFactory.ARTIFACTS.get(label))) {
				return label;
			}
		}
		return null;
	}

	public static IFieldEditor createArtifactEditor(BatchArtifactType artifact) {
		return SwtFieldEditorFactory.INSTANCE.createComboEditor(ARTIFACT_EDITOR, 
				WizardMessages.artifactLabel, ARTIFACT_LIST, getArtifactLabel(artifact));
	}

	public static IFieldEditor createArtifactEditor(BatchArtifactType artifact, List<BatchArtifactType> types) {
		List<String> list = new ArrayList<String>();
		for (String t: ARTIFACT_LIST) {
			if(types.contains(ARTIFACTS.get(t))) {
				list.add(t);
			}
		}
		return SwtFieldEditorFactory.INSTANCE.createComboEditor(ARTIFACT_EDITOR, 
				WizardMessages.artifactLabel, list, getArtifactLabel(artifact));
	}

	public static final String DERIVE_FROM_CLASS = "class";
	public static final String DERIVE_FROM_INTERFACE = "interface";
	
	static List<String> DERIVE_FROM_OPTIONS = Arrays.asList(DERIVE_FROM_INTERFACE, DERIVE_FROM_CLASS);
	static List<String> DERIVE_FROM_OPTION_LABELS = Arrays.asList(WizardMessages.artifactImplementInterfaceLabel, WizardMessages.artifactExtendAbstractClassLabel);

	public static IFieldEditor createDerivedFromEditor() {
		return SwtFieldEditorFactory.INSTANCE.createRadioEditor(DERIVE_FROM_EDITOR, 
				""/*WizardMessages.deriveFromLabel*/, DERIVE_FROM_OPTION_LABELS, DERIVE_FROM_OPTIONS, DERIVE_FROM_CLASS);
	}

	public static final String LOADER_OPTION_ANNOTATION = "annotation";
	public static final String LOADER_OPTION_XML = "xml";
	public static final String LOADER_OPTION_QUALIFIED = "qualified";
	
	static List<String> LOADER_OPTIONS = Arrays.asList(LOADER_OPTION_ANNOTATION, LOADER_OPTION_XML, LOADER_OPTION_QUALIFIED);
	static List<String> LOADER_OPTIONS_LABELS = Arrays.asList(
			WizardMessages.artifactLoaderAnnotationLabel, WizardMessages.artifactLoaderXMLLabel, WizardMessages.artifactLoaderQualifiedLabel);

	public static IFieldEditor createNameOptionsEditor() {
		return SwtFieldEditorFactory.INSTANCE.createRadioEditor(LOADER_OPTIONS_EDITOR, 
				WizardMessages.artifactLoaderLabel, LOADER_OPTIONS_LABELS, LOADER_OPTIONS, LOADER_OPTION_ANNOTATION);
	}

	public static IFieldEditor createArtifactNameEditor() {
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(NAME_EDITOR, WizardMessages.artifactNameLabel, "");
	}

	public static IFieldEditor createPropertiesEditor() {
		return new PropertiesListEditor(PROPERTIES_EDITOR, WizardMessages.artifactPropertiesLabel, new ArrayList<Object>());
	}

	static class PropertiesListEditor extends BaseListFieldEditor {

		public PropertiesListEditor(String name, String label,
				Object defaultValue) {
			super(name, label, defaultValue);
		}

		@SuppressWarnings("unchecked")
		public List<Object> getValues() {
			return (List<Object>)getValue();
		}

		@Override
		protected List<Object> runAddAction() {
			List<Object> added = new ArrayList<Object>();
			NewPropertyDialog dialog = new NewPropertyDialog(this.getLabelControl().getShell());
			dialog.usedValues = getValues();
			if(dialog.open() == Dialog.OK) {
				added.add(dialog.getName());
				setValue(new ArrayList<Object>());
			}
			return added;
		}
	}

	static class NewPropertyDialog extends Dialog {
		private String fName;

		private Label fNameLabel;
		private Text fNameText;

		List<Object> usedValues = new ArrayList<Object>();

		public NewPropertyDialog(Shell shell) {
			super(shell);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite comp = (Composite) super.createDialogArea(parent);
			((GridLayout) comp.getLayout()).numColumns = 2;

			fNameLabel = new Label(comp, SWT.NONE);
			fNameLabel.setText("Field name:");
			fNameLabel.setFont(comp.getFont());

			fNameText = new Text(comp, SWT.BORDER | SWT.SINGLE);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.widthHint = 300;
			fNameText.setLayoutData(gd);
			fNameText.setFont(comp.getFont());
			fNameText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					updateButtons();
				}
			});

			return comp;
		}

		public String getName() {
			return fName;
		}

		@Override
		protected void buttonPressed(int buttonId) {
			if (buttonId == IDialogConstants.OK_ID) {
				fName = fNameText.getText().trim();
			} else {
				fName = null;
			}
			super.buttonPressed(buttonId);
		}

		@Override
		protected void configureShell(Shell shell) {
			super.configureShell(shell);
			shell.setText(WizardMessages.addPropertyDialogTitle);
		}

		/**
		 * Enable the OK button if valid input
		 */
		protected void updateButtons() {
			String name = fNameText.getText().trim();
			getButton(IDialogConstants.OK_ID).setEnabled((name.length() > 0) && !usedValues.contains(name));
		}

		@Override
		public void create() {
			super.create();
			updateButtons();
		}

	}
}
