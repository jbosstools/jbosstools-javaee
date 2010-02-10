package org.jboss.tools.seam.xml.ds.model;

public interface DSConstants {
	public String PUBLIC_ID_1_5 = "-//JBoss//DTD JBOSS JCA Config 1.5//EN"; //$NON-NLS-1$
	public String SYSTEM_ID_1_5 = "http://www.jboss.org/j2ee/dtd/jboss-ds_1_5.dtd"; //$NON-NLS-1$
	public String PUBLIC_ID_5_0 = "-//JBoss//DTD JBOSS JCA Config 5.0//EN"; //$NON-NLS-1$
	public String SYSTEM_ID_5_0 = "http://www.jboss.org/j2ee/dtd/jboss-ds_5_0.dtd"; //$NON-NLS-1$
	
	public String ENT_DATASOURCES_FILE = "FileDSDatasources"; //$NON-NLS-1$
	public String ENT_CONNECTION_FACTORIES_FILE = "FileDSConnectionFactories"; //$NON-NLS-1$

	public String SUFF_50_DTD = "50d"; //$NON-NLS-1$
	public String SUFF_50_XSD = "50s"; //$NON-NLS-1$

	public String ENT_DATASOURCES_FILE_50_DTD = ENT_DATASOURCES_FILE + SUFF_50_DTD;
	public String ENT_CONNECTION_FACTORIES_FILE_50_DTD = ENT_CONNECTION_FACTORIES_FILE + SUFF_50_DTD;

	public String ENT_DATASOURCES_FILE_50_XSD = ENT_DATASOURCES_FILE + SUFF_50_XSD;
	public String ENT_CONNECTION_FACTORIES_FILE_50_XSD = ENT_CONNECTION_FACTORIES_FILE + SUFF_50_XSD;

	public String ATTR_TRACK_CONN = "track-connection-by-tx"; //$NON-NLS-1$
	public String ATTR_TRANSACTION = "transaction"; //$NON-NLS-1$
	public String ATTR_SECURITY_TYPE = "security-type"; //$NON-NLS-1$
	public String ATTR_SECURITY_DOMAIN = "security-domain"; //$NON-NLS-1$
	public String ATTR_NO_TX_SEPARATE_POOLS = "no-tx-separate-pools"; //$NON-NLS-1$

}
