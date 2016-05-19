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

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.wizards.TableFilterView;
import org.hibernate.mapping.Table;
import org.hibernate.util.StringHelper;
import org.jboss.tools.seam.ui.views.DBTablesViewer.Catalog;
import org.jboss.tools.seam.ui.views.DBTablesViewer.Schema;

/**
 * @author Dmitry Geraskov
 *
 */
public abstract class DBTableFilterView extends TableFilterView {
	
	private DBTablesViewer viewer;

	public DBTableFilterView(Composite parent, int style) {
		super( parent, style );
	}
	
	@Override
	protected TreeViewer createTreeViewer() {
		if (viewer == null){
			viewer = new DBTablesViewer( tree );
		}		
		return viewer;
	}
	
	@Override
	protected void doRefreshTree() {
		ConsoleConfiguration configuration = KnownConfigurations.getInstance()
				.find( getConsoleConfigurationName() );

		if(configuration!=null) {
			viewer.setInput( configuration );
		}
	}

	@Override
	protected void toggle(boolean exclude) {
		ISelection selection = viewer.getSelection();
		ConsoleConfiguration configuration = KnownConfigurations.getInstance()
				.find( getConsoleConfigurationName() );

		if ( !selection.isEmpty() ) {
			StructuredSelection ss = (StructuredSelection) selection;
			Iterator iterator = ss.iterator();
			while ( iterator.hasNext() ) {
				Object sel = iterator.next();
				ITableFilter filter = null;

				if ( sel instanceof Table ) {
					Table table = (Table) sel;
					filter = revEngDef.createTableFilter(configuration);
					if ( StringHelper.isNotEmpty( table.getName() ) ) {
						filter.setMatchName( table.getName() );
					}
					if ( StringHelper.isNotEmpty( table.getCatalog() ) ) {
						filter.setMatchCatalog( table.getCatalog() );
					}
					if ( StringHelper.isNotEmpty( table.getSchema() ) ) {
						filter.setMatchSchema( table.getSchema() );
					}
					filter.setExclude( Boolean.valueOf( exclude ) );
				} else if ( sel instanceof Schema ) { // assume its a schema!
					Schema tc = (Schema) sel;
					filter = revEngDef.createTableFilter(configuration);
					String schema = tc.getName();
					String catalog = tc.getParent().getName();
					if(StringHelper.isNotEmpty(schema)) {
						filter.setMatchSchema(schema);
					}
					if(StringHelper.isNotEmpty(catalog)) {
						filter.setMatchCatalog(catalog);
					}
					filter.setExclude( Boolean.valueOf( exclude ) );
				} else if ( sel instanceof Catalog ) { // assume its a catalog!
					Catalog tc = (Catalog) sel;
					filter = revEngDef.createTableFilter(configuration);
					if(StringHelper.isNotEmpty(tc.getName())) {
						filter.setMatchCatalog(tc.getName());
					}
					filter.setExclude( Boolean.valueOf( exclude ) );
				}
				if ( filter != null )
					revEngDef.addTableFilter( filter );
			}
		} else {
			ITableFilter filter = revEngDef.createTableFilter(configuration);
			filter.setExclude( Boolean.valueOf( exclude ) );
			revEngDef.addTableFilter( filter );
		}
	}

}
