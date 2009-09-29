/*******************************************************************************
 * Copyright (c) 2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.internal.reveng;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.util.StringHelper;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

/**
 * @author Vitali
 *
 */
public class JDBCTablesColumnsReader {

	private static final Log log = LogFactory.getLog(JDBCTablesColumnsReader.class);

	private MetaDataDialect metadataDialect;
	// current catalog name - if current connection provide it
	private String currentCatalog;

	private final ConnectionProvider provider;
	private final SQLExceptionConverter sec;

	public JDBCTablesColumnsReader(MetaDataDialect dialect, ConnectionProvider provider,
			SQLExceptionConverter sec) {
		this.metadataDialect = dialect;
		this.provider = provider;
		this.sec = sec;
		currentCatalog = null;
	}

	private boolean safeEquals(Object value, Object tf) {
		if (value == tf)
			return true;
		if (null == value)
			return false;
		return value.equals(tf);
	}

	private String quote(String columnName) {
		if (null == columnName) {
			return null;
		}
		if (!metadataDialect.needQuote(columnName)) {
			return columnName;
		}
		if (columnName.length() > 1 && columnName.charAt(0) == '`'
				&& columnName.charAt(columnName.length() - 1) == '`') {
			return columnName; // avoid double quoting
		}
		return "`" + columnName + "`";
	}

	private void collectTables(TablesColumnsCollector tcc, String catalog, String schema) {
		Iterator tableIterator = null;
		try {
			String tablesMask = null;
			tableIterator = metadataDialect.getTables(StringHelper.replace(catalog, ".*", "%"),
					StringHelper.replace(schema, ".*", "%"), StringHelper.replace(tablesMask, ".*",
							"%"));
			while (tableIterator.hasNext()) {
				Map tableRs = (Map) tableIterator.next();
				String catalogName = (String) tableRs.get("TABLE_CAT");
/*				
 * 				commented by Dmitry Geraskov to prevent showing catalog name if
 *				db provider can't found it
 *				otherwise we'll set it and filter out all tables when will run code generation
 *				if (null == catalogName) {
 *					// simple workaround if db provider do not find resources
 *					// to return right catalog name here
 *					catalogName = currentCatalog;
 *				}
 */
				String schemaName = (String) tableRs.get("TABLE_SCHEM");
				String tableName = (String) tableRs.get("TABLE_NAME");
				//String comment = (String) tableRs.get("REMARKS");
				String tableType = (String) tableRs.get("TABLE_TYPE");
				if (("TABLE".equals(tableType) || "VIEW".equals(tableType))) {
					log.debug("Adding table " + tableName + " of type " + tableType);
					tcc.addTableName(catalogName, schemaName, quote(tableName));
				} else {
					log.debug("Ignoring table " + tableName + " of type " + tableType);
				}
			}
		} finally {
			if (null != tableIterator) {
				metadataDialect.close(tableIterator);
			}
		}
	}

	public void readDatabaseTables(TablesColumnsCollector tcc, String catalog, String schema) {
		try {
			ReverseEngineeringRuntimeInfo info = ReverseEngineeringRuntimeInfo.createInstance(provider, sec,
					null);
			metadataDialect.configure(info);
			tcc.init();
			initCurrentCatalog(info);
			collectTables(tcc, catalog, schema);
			tcc.adjust();
		} finally {
			metadataDialect.close();
		}
	}

	private void collectColumns(TablesColumnsCollector tcc, String catalog, String schema) {
		Iterator columnIterator = null;
		try {
			String tablesMask = null;
			String columnsMask = null;
			columnIterator = metadataDialect.getColumns(StringHelper.replace(catalog, ".*", "%"),
					StringHelper.replace(schema, ".*", "%"), StringHelper.replace(tablesMask, ".*",
							"%"), StringHelper.replace(columnsMask, ".*", "%"));
			while (columnIterator.hasNext()) {
				Map columnRs = (Map) columnIterator.next();
				String catalogName = (String) columnRs.get("TABLE_CAT");
				if (null == catalogName) {
					// simple workaround if db provider do not find resources
					// to return right catalog name here
					catalogName = currentCatalog;
				}
				String schemaName = (String) columnRs.get("TABLE_SCHEM");
				String tableName = (String) columnRs.get("TABLE_NAME");
				String columnName = (String) columnRs.get("COLUMN_NAME");
				//String dataType = (String) tableRs.get("DATA_TYPE");
				log.debug("Adding column " + tableName + "." + columnName);
				tcc.addColumnName(catalogName, schemaName, quote(tableName), columnName);
			}
		} finally {
			if (null != columnIterator) {
				metadataDialect.close(columnIterator);
			}
		}
	}

	public void readDatabaseColumns(TablesColumnsCollector tcc, String catalog, String schema) {
		try {
			ReverseEngineeringRuntimeInfo info = ReverseEngineeringRuntimeInfo.createInstance(provider, sec,
					null);
			metadataDialect.configure(info);
			tcc.init();
			initCurrentCatalog(info);
			collectColumns(tcc, catalog, schema);
			tcc.adjust();
		} finally {
			metadataDialect.close();
		}
	}

	public void readDatabaseTablesColumns(TablesColumnsCollector tcc, String catalog, String schema) {
		try {
			ReverseEngineeringRuntimeInfo info = ReverseEngineeringRuntimeInfo.createInstance(provider, sec,
					null);
			metadataDialect.configure(info);
			tcc.init();
			initCurrentCatalog(info);
			collectTables(tcc, catalog, schema);
			collectColumns(tcc, catalog, schema);
			tcc.adjust();
		} finally {
			metadataDialect.close();
		}
	}

	/**
	 * init current catalog name
	**/	
	private void initCurrentCatalog(ReverseEngineeringRuntimeInfo info) {
		currentCatalog = null;
		try {
			currentCatalog = info.getConnectionProvider().getConnection().getCatalog();
		} catch (SQLException ignore) {
			SeamGuiPlugin.getPluginLog().logError(ignore);
		}
	}
}
