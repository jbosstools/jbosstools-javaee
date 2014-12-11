package org.jboss.tools.batch.ui.editor.internal.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

public class JobXMLEditor extends SapphireEditor {
	private Job jobModel;
	private StructuredTextEditor schemaSourceEditor;
//	private SapphireDiagramEditor schemaDiagram;
	private MasterDetailsEditorPage design;

		@Override
		protected void createSourcePages() throws PartInitException {
			this.schemaSourceEditor = new StructuredTextEditor();
			this.schemaSourceEditor.setEditorPart(this);
			int index = addPage( this.schemaSourceEditor, getEditorInput() );
			setPageText( index, "Source" );
		}

		@Override
		protected Element createModel() {
			this.jobModel = Job.TYPE.instantiate(new RootXmlResource(new XmlEditorResourceStore(this, this.schemaSourceEditor)));
			return this.jobModel;
		}

		@Override
		protected void createDiagramPages() throws PartInitException {	
//			this.schemaDiagram = new SapphireDiagramEditor(
//					this, this.jobModel,
//					DefinitionLoader.sdef( getClass() ).page( "DiagramPage" )
//			);
//			addEditorPage( 0, this.schemaDiagram );
		}

		@Override
		protected void createFormPages() throws PartInitException {
			this.design = new MasterDetailsEditorPage(
					this, this.jobModel,
					DefinitionLoader.sdef( getClass() ).page( "design" )
			);
			addPage( 0, this.design );
		}

		public Job getSchema() {
			return this.jobModel;
		}

		@Override
		public void doSave( final IProgressMonitor monitor ) {
//			this.schemaDiagram.doSave(monitor);
			super.doSave(monitor);
		}

	
		public StructuredTextEditor getSourceEditor() {
			return schemaSourceEditor;
		}

		public MasterDetailsEditorPage getFormEditor() {
			return design;
		}
}
