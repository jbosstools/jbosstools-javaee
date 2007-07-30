package org.jboss.ide.seam.gen.actions;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.jboss.ide.seam.gen.SeamGenPlugin;
import org.jboss.ide.seam.gen.SeamGenProperty;

public class SetupDeluxeAction extends SeamGenAction implements
		IWorkbenchWindowActionDelegate {

	
	protected static final String DB = "Database";
	protected static final String CODEGEN = "Code generation";
	
	protected String getTarget() {
		return "setup";
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

		properties.put( "project.name.new",
				new SeamGenProperty( "Project name", "project.name" ) {
					public String getDefaultValue(Properties others) {
						String property = others.getProperty(
								"project.name.new", "" );
						return property;
					}
					
					public String getGroup() {
						return GENERAL; 
					}
				}

				
		);

		properties.put( "workspace.home.new", new SeamGenProperty(
				"Seam project workspace", "workspace.home" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "workspace.home",
						SeamGenPlugin.assumeWorkspacePath() );
				return property;
			}
			
			public int getInputType() {
				return DIR;
			};
			
			public String valid(String string) {
				if(string==null) return null;
				File file = new File(string);
				if (file.exists() && file.isDirectory()) {
					return null;
				} else {
					return string + " does not exist or is not a directory";
				}
			}
			
			
			
		} );

		properties.put( "jboss.home.new", new SeamGenProperty(
				"JBoss AS home directory", "jboss.home" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "jboss.home.new",
						SeamGenPlugin.assumeJBossASHome() );
				return property;
			}
			
			public int getInputType() {
				return DIR;
			};

			public String valid(String string) {
				if(string==null) return null;
				File file = new File(string);
				if (file.exists() && file.isDirectory()) {
					return null;
				} else {
					return string + " does not exist or is not a directory";
				}
			}
		} );

		properties.put( "project.type.new", new SeamGenProperty(
				"Deploy as an EAR or a WAR", "project.type" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "project.type", "ear" );
				return property; // ear,war
			}
			
			public String getGroup() {
				return GENERAL; 
			}
		} );

		properties.put( "action.package.new", new SeamGenProperty(
				"Session beans package name", "action.package" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "project.name.new", "" );
				return "com.mydomain." + property;
			}
			
			public String getGroup() {
				return CODEGEN; 
			}
		} );

		properties.put( "model.package.new", new SeamGenProperty(
				"Entity beans package name", "model.package" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "action.package.new", "" );
				return property;
			}
			
			public String getGroup() {
				return CODEGEN; 
			}
		} );

		properties.put( "test.package.new", new SeamGenProperty(
				"Test cases package name", "test.package" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "action.package.new", "" );
				return property + ".test";
			}
			
			public String getGroup() {
				return CODEGEN; 
			}
		} );

		properties.put( "database.type.new", new SeamGenProperty(
				"Database type", "database.type" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "database.type.new",
						"hql" );
				return property; // hsql,mysql,oracle,postgres,mssql,db2,sybase,enterprisedb
			}
			
			public String getGroup() {
				return DB; 
			}
					
		} );

		properties.put( "hibernate.dialect.new", new SeamGenProperty(
				"Hibernate dialect", "hibernate.dialect" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "hibernate.dialect.new",
						"org.hibernate.dialect.HSQLDialect" );
				return property;
			}
			public String getGroup() {
				return DB; 
			}
		} );

		properties.put( "driver.jar.new", new SeamGenProperty(
				"Filesystem path to the JDBC driver jar", "driver.jar" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "driver.jar.new",
						"lib/hsqldb.jar" );
				return property;
			}
			
			public int getInputType() {
				return JAR;
			};

			public String valid(String string) {
				if(string==null) return null;
				File file = new File(string);
				if (file.exists() && file.isFile()) {
					return null;
				} else {
					return string + " does not exist or is not a file";
				}
			}
		} );

		properties.put( "hibernate.connection.driver_class.new",
				new SeamGenProperty( "JDBC driver class for your database", "hibernate.connection.driver_class" ) {
					public String getDefaultValue(Properties others) {
						String property = others.getProperty(
								"hibernate.connection.driver_class",
								"org.hsqldb.jdbcDriver" );
						return property;
					}
					public String getGroup() {
						return DB; 
					}
				} );

		properties.put( "hibernate.connection.url.new", new SeamGenProperty(
				"JDBC URL for your database", "hibernate.connection.url" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty(
						"hibernate.connection.url",
						"jdbc:hsqldb:hsql://localhost:1701" );
				return property;				
			}
			public String getGroup() {
				return DB; 
			}
		} );

		properties.put( "hibernate.connection.username.new",
				new SeamGenProperty( "Database username", "hibernate.connection.username" ) {
					public String getDefaultValue(Properties others) {
						String property = others.getProperty(
								"hibernate.connection.username", "sa" );
						return property;
					}
					public String getGroup() {
						return DB; 
					}
				} );

		properties.put( "hibernate.connection.password.new",
				new SeamGenProperty( "Database password", "hibernate.connection.password" ) {
					public String getDefaultValue(Properties others) {
						String property = others.getProperty(
								"hibernate.connection.password", "" );
						return property;
					}

					public boolean isRequired() {
						return false;
					}
					public String getGroup() {
						return DB; 
					}
				} );

		properties.put( "hibernate.default_schema.new", new SeamGenProperty(
				"Database schema name", "hibernate.default_schema" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty(
						"hibernate.default_schema", "" );
				return property;
			}

			public boolean isRequired() {
				return false;
			}
			
			public String getGroup() {
				return DB; 
			}
		} );

		properties.put( "hibernate.default_catalog.new", new SeamGenProperty(
				"Database catalog name", "hibernate.default_catalog" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty(
						"hibernate.default_catalog", "" );
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

		properties.put( "database.exists.new", new SeamGenProperty(
				"Does tables already exist in the database?", "database.exists" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "database.exists", "y" );
				return property; // yn
			}
			
			public String getGroup() {
				return DB; 
			}
			
			public int getInputType() {
				return YES_NO;
			};
			
			
		} );

		properties.put( "database.drop.new", new SeamGenProperty(
				"Recreate database tables + data on deploy?", "database.drop" ) {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "database.drop", "n" );
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
