/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.ui.views;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.progress.WorkbenchJob;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.proxy.SettingsProxy;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.seam.ui.internal.reveng.JDBCTablesColumnsReader;
import org.jboss.tools.seam.ui.internal.reveng.TablesColumnsCollector;

/**
 * Database tree viewer with three-level structure (till Tables)
 * @author Dmitry Geraskov
 */
public class DBTablesViewer extends TreeViewer {
	
	private String connectionErrorMessage = "Couldn't connect to Database";							//$NON-NLS-1$
	
	private Image catalog = EclipseImages.getImageDescriptor(ImageConstants.DATABASE).createImage();
	private Image schema = EclipseImages.getImageDescriptor(ImageConstants.SCHEMA).createImage();
	private Image table  = EclipseImages.getImageDescriptor(ImageConstants.TABLE).createImage();
	
	public class Element{
		private String name;
		protected String defaultLabel;
		
		Element(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
		public String getLabel(){
			return name == null ? defaultLabel : name;
		}
	}
	
	public class Catalog extends Element{
		
		public static final String DEF_CATALOG_NAME = "<Default catalog>";			//$NON-NLS-1$
		
		Catalog(String name){
			super(name);
			defaultLabel = DEF_CATALOG_NAME;
		}
	}
	
	public class Schema extends Element{
		
		public static final String DEF_SCHEMA_NAME = "<Default schema>"; 				//$NON-NLS-1$
		
		private Catalog parent;
		
		Schema(String name, Catalog parent){
			super(name);
			this.parent = parent;
			defaultLabel = DEF_SCHEMA_NAME;
		}
		
		public Catalog getParent(){
				return parent;
		}
	}
	
	public DBTablesViewer(Tree tree){		
		super(tree);
		setContentProvider(createContentProvider());
		setLabelProvider(createLabelProvider());
	}

	public DBTablesViewer(Composite parent) {
		super(parent);
		
	}

	/**
	 * @return ITreeContentProvider
	 */
	protected ITreeContentProvider createContentProvider() {
		return new ITreeContentProvider(){
			
			private IConfiguration cfg = null;
			
			private SettingsProxy buildSettings = null;
			
			private String placeHolder = "Pending...";	//$NON-NLS-1$
			
			private String conErrorItem = '<' + connectionErrorMessage + '>';
			
			private final Object[] NO_CHILDREN = new Object[0];
			
			private static final int BEGIN_STATE = 0;
			
			private static final int CHILDREN_FETCHED = 1;
			
			private static final int CONNECTION_ERROR = 2;
			
			private static final int LOADING_IN_PROGRESS = 3;
			
			private int connectionState = BEGIN_STATE;
			
			private TablesColumnsCollector tablesCollector;
			
			private Object[] getCatalogs(){
				if (connectionState == LOADING_IN_PROGRESS){
					return new String[]{placeHolder};
				}
				if (connectionState == CONNECTION_ERROR){
					return new String[]{connectionErrorMessage};
				}
				List catalogNames = tablesCollector.getCatalogNames();
				Element[] catalogs = new Element[catalogNames.size()];
				for (int i = 0; i < catalogs.length; i++) {
					String catalogName =  (String) catalogNames.get(i);
					if (catalogName == null || "".equals(catalogName)) catalogName = null;			//$NON-NLS-1$
					catalogs[i] = new Catalog(catalogName);
					
				}
				return catalogs;
				}
			
			private Object[] getSchemas(Catalog catalog){
				List schemaNames = tablesCollector.getMatchingSchemaNames(catalog.getName(), buildSettings.getDefaultSchemaName());
				Element[] schemas = new Element[schemaNames.size()];
				for (int i = 0; i < schemaNames.size(); i++) {
					String schemaName = (String) schemaNames.get(i);
					if (schemaName == null || "".equals(schemaName)) schemaName = null;	//$NON-NLS-1$
					schemas[i] = new Schema( schemaName, catalog);
				}
				return schemas;
			}
			
			private Object[] getTables(Schema schema){
				List tableNames = tablesCollector.getMatchingTablesNames(schema.getParent().getName(), schema.getName(), "");			//$NON-NLS-1$
				Table[] tables = new Table[tableNames.size()];
				for (int i = 0; i < tables.length; i++) {
					tables[i] = new Table((String) tableNames.get(i));
					tables[i].setCatalog(schema.getParent().getName());
					tables[i].setSchema(schema.getName());
				}
				return tables;
			}

			public Object[] getChildren(Object parent) {
				if (parent instanceof Catalog) {
					return getSchemas((Catalog)parent);
				}
				if (parent instanceof Schema) {
					Schema schema = (Schema)parent;
					return getTables(schema);
				}
				return NO_CHILDREN;
			}

			public Object getParent(Object element) {
				if (element instanceof Schema){
					return ((Schema)element).getParent();
				}
				return null;
			}

			public boolean hasChildren(Object parent) {
				return parent instanceof Element;
			}

			public Object[] getElements(Object inputElement) {
				if (buildSettings == null) return new String[]{conErrorItem};
				
				if (connectionState == BEGIN_STATE){
					tablesCollector = new TablesColumnsCollector(true);
					if (connectionState == BEGIN_STATE)
						connectionState = LOADING_IN_PROGRESS;
					WorkbenchJob job = new WorkbenchJob("Fetching database structure") {			//$NON-NLS-1$
						@Override
						public IStatus runInUIThread(IProgressMonitor monitor) {
							try{
								MetaDataDialect realMetaData = JDBCReaderFactory.newMetaDataDialect(buildSettings.getTarget()
										.getDialect(), cfg.getProperties());
								
								JDBCTablesColumnsReader reader = new JDBCTablesColumnsReader(realMetaData,
										buildSettings.getConnectionProvider(), buildSettings.getTarget().getSQLExceptionConverter());
								reader.readDatabaseTables(tablesCollector, buildSettings.getDefaultCatalogName(), buildSettings.getDefaultSchemaName());
								
								connectionState = CHILDREN_FETCHED;
							} catch (JDBCException e){
								connectionState = CONNECTION_ERROR;
							}
							DBTablesViewer.this.remove(placeHolder);
							DBTablesViewer.this.refresh();
							return Status.OK_STATUS;
						}};
					job.schedule();
				}
				return getCatalogs();
			}

			public void dispose() {}

			/**
			 * Supported input types are:
			 * org.hibernate.console.ConsoleConfiguration,
			 * org.hibernate.cfg.Settings.
			 */
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				if (newInput == oldInput
						&& connectionState != CONNECTION_ERROR) return;
				
				if (newInput instanceof ConsoleConfiguration) {
					ConsoleConfiguration cc = (ConsoleConfiguration) newInput;
					try{
						cc.build();
						cfg = cc.getConfiguration();
						cc.getExecutionContext().execute(new ExecutionContext.Command() {
							public Object execute() {
								SettingsProxy newSettings = (SettingsProxy)cfg.buildSettings();
								if (!newSettings.equals(buildSettings)){
									buildSettings = newSettings;
									connectionState = BEGIN_STATE;
								}
								return null;
							}});
					} catch (HibernateException e){
						connectionState = CONNECTION_ERROR;
					}
				}
			}
		};
	}
	
	protected LabelProvider createLabelProvider(){
		return new LabelProvider(){

			@Override
			public void dispose() {
				catalog.dispose();
				schema.dispose();
				table.dispose();
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof Catalog){
					return catalog;
				}
				if (element instanceof Schema){
					return schema;
				}
				if (element instanceof Table){
					return table;
				}
				return null;
			}
			
			@Override
			public String getText(Object element) {
				if (element instanceof Element){
					return ((Element)element).getLabel();
				}
				if (element instanceof Table){
					return ((Table)element).getName();
				}
				return super.getText(element);
			}
			};
	}

	public void setConnectionErrorMessage(String connectionErrorMessage) {
		this.connectionErrorMessage = connectionErrorMessage;
	}
}
