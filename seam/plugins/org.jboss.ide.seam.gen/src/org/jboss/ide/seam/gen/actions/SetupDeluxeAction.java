package org.jboss.ide.seam.gen.actions;

import java.io.File;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.jboss.ide.seam.gen.Messages;
import org.jboss.ide.seam.gen.SeamGenPlugin;
import org.jboss.ide.seam.gen.SeamGenProperty;

public class SetupDeluxeAction extends SeamGenAction implements
		IWorkbenchWindowActionDelegate {

	
	protected static final String DB = Messages.SetupDeluxeAction_DatabaseGroup;
	protected static final String CODEGEN = Messages.SetupDeluxeAction_CodeGenerationGroup;
	
	protected String getTarget() {
		return "setup"; //$NON-NLS-1$
	}

	public Set getGroups() {
		LinkedHashSet set = new LinkedHashSet();
		set.add(SeamGenProperty.GENERAL);
		set.add(DB);
		set.add(CODEGEN);
		return set;
	}
	protected Map getQuestions() {
		Map properties = new LinkedHashMap();

		properties.put( "project.name.new", //$NON-NLS-1$
				new SeamGenProperty( Messages.SetupDeluxeAction_ProjectNameDesc, "project.name" ) { //$NON-NLS-1$
					public String getDefaultValue(Properties others) {
						String property = others.getProperty(
								"project.name.new", "" ); //$NON-NLS-1$ //$NON-NLS-2$
						return property;
					}
					
					public String getGroup() {
						return GENERAL; 
					}
				}
		);

		properties.put( "workspace.home.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_WorkspaceHomeDesc, "workspace.home" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "workspace.home", //$NON-NLS-1$
						SeamGenPlugin.assumeWorkspacePath() );
				return property;
			}
			
			public int getInputType() {
				return DIR;
			};
			
			public String valid(String filename) {
				if(filename==null) return null;
				File file = new File(filename);
				if (file.exists() && file.isDirectory()) {
					return null;
				} else {
					return MessageFormat
							.format(Messages.SetupDeluxeAction_BadDirectory,
									filename);
				}
			}
			
			
			
		} );

		properties.put( "jboss.home.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_JBossHomeDesc, "jboss.home" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "jboss.home.new", //$NON-NLS-1$
						SeamGenPlugin.assumeJBossASHome() );
				return property;
			}
			
			public int getInputType() {
				return DIR;
			};

			public String valid(String filename) {
				if(filename==null) return null;
				File file = new File(filename);
				if (file.exists() && file.isDirectory()) {
					return null;
				} else {
					return MessageFormat
							.format(Messages.SetupDeluxeAction_BadDirectory,
									filename);
				}
			}
		} );

		properties.put( "project.type.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_ProjectTypeDesc, "project.type" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "project.type", "ear" ); //$NON-NLS-1$ //$NON-NLS-2$
				return property; // ear,war
			}
			
			public String getGroup() {
				return GENERAL; 
			}
		} );

		properties.put( "action.package.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_ActionPackageDesc, "action.package" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "project.name.new", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return "com.mydomain." + property; //$NON-NLS-1$
			}
			
			public String getGroup() {
				return CODEGEN; 
			}
		} );

		properties.put( "model.package.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_ModelPackageDesc, "model.package" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "action.package.new", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return property;
			}
			
			public String getGroup() {
				return CODEGEN; 
			}
		} );

		properties.put( "test.package.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_TestPackageDesc, "test.package" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "action.package.new", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return property + ".test"; //$NON-NLS-1$
			}
			
			public String getGroup() {
				return CODEGEN; 
			}
		} );

		properties.put( "database.type.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_DatabaseTypeDesc, "database.type" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "database.type.new", //$NON-NLS-1$
						"hql" ); //$NON-NLS-1$
				return property; // hsql,mysql,oracle,postgres,mssql,db2,sybase,enterprisedb
			}
			
			public String getGroup() {
				return DB; 
			}
					
		} );

		properties.put( "hibernate.dialect.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_HibernateDialectDesc, "hibernate.dialect" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "hibernate.dialect.new", //$NON-NLS-1$
						"org.hibernate.dialect.HSQLDialect" ); //$NON-NLS-1$
				return property;
			}
			public String getGroup() {
				return DB; 
			}
		} );

		properties.put( "driver.jar.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_DriverJarDesc, "driver.jar" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "driver.jar.new", //$NON-NLS-1$
						"lib/hsqldb.jar" ); //$NON-NLS-1$
				return property;
			}
			
			public int getInputType() {
				return JAR;
			};

			public String valid(String filename) {
				if(filename==null) return null;
				File file = new File(filename);
				if (file.exists() && file.isFile()) {
					return null;
				} else {
					return MessageFormat.format(Messages.SetupDeluxeAction_BadFile,
							filename);
				}
			}
		} );

		properties.put( "hibernate.connection.driver_class.new", //$NON-NLS-1$
				new SeamGenProperty( Messages.SetupDeluxeAction_HibernateConnectionDriverClassDesc, "hibernate.connection.driver_class" ) { //$NON-NLS-1$
					public String getDefaultValue(Properties others) {
						String property = others.getProperty(
								"hibernate.connection.driver_class", //$NON-NLS-1$
								"org.hsqldb.jdbcDriver" ); //$NON-NLS-1$
						return property;
					}
					public String getGroup() {
						return DB; 
					}
				} );

		properties.put( "hibernate.connection.url.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_HibernateConnectionUrlDesc, "hibernate.connection.url" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty(
						"hibernate.connection.url", //$NON-NLS-1$
						"jdbc:hsqldb:hsql://localhost:1701" ); //$NON-NLS-1$
				return property;				
			}
			public String getGroup() {
				return DB; 
			}
		} );

		properties.put( "hibernate.connection.username.new", //$NON-NLS-1$
				new SeamGenProperty( Messages.SetupDeluxeAction_HibernateConnectionUsernameDesc, "hibernate.connection.username" ) { //$NON-NLS-1$
					public String getDefaultValue(Properties others) {
						String property = others.getProperty(
								"hibernate.connection.username", "sa" ); //$NON-NLS-1$ //$NON-NLS-2$
						return property;
					}
					public String getGroup() {
						return DB; 
					}
				} );

		properties.put( "hibernate.connection.password.new", //$NON-NLS-1$
				new SeamGenProperty( Messages.SetupDeluxeAction_DatabasePasswordDesc, "hibernate.connection.password" ) { //$NON-NLS-1$
					public String getDefaultValue(Properties others) {
						String property = others.getProperty(
								"hibernate.connection.password", "" ); //$NON-NLS-1$ //$NON-NLS-2$
						return property;
					}

					public boolean isRequired() {
						return false;
					}
					public String getGroup() {
						return DB; 
					}
				} );

		properties.put( "hibernate.default_schema.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_HibernateDefaultSchemaDesc, "hibernate.default_schema" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty(
						"hibernate.default_schema", "" );  //$NON-NLS-1$//$NON-NLS-2$
				return property;
			}

			public boolean isRequired() {
				return false;
			}
			
			public String getGroup() {
				return DB; 
			}
		} );

		properties.put( "hibernate.default_catalog.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_HibernateDefaultCatalogDesc, "hibernate.default_catalog" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty(
						"hibernate.default_catalog", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return property;
			}

			public boolean isRequired() {
				return false;
			}
			
			public String getGroup() {
				return DB; 
			}
		}

		);

		properties.put( "database.exists.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_DatabaseExistsDesc, "database.exists" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "database.exists", "y" ); //$NON-NLS-1$ //$NON-NLS-2$
				return property; // yn
			}
			
			public String getGroup() {
				return DB; 
			}
			
			public int getInputType() {
				return YES_NO;
			};
			
			
		} );

		properties.put( "database.drop.new", new SeamGenProperty( //$NON-NLS-1$
				Messages.SetupDeluxeAction_DatabaseDropDesc, "database.drop" ) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "database.drop", "n" ); //$NON-NLS-1$ //$NON-NLS-2$
				return property; // yn
			}
			
			public String getGroup() {
				return DB; 
			}
			
			public int getInputType() {
				return YES_NO;
			};
		} );

		return properties;
	}
}
