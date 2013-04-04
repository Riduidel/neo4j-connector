package com.netoprise.neo4j;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.kernel.Config;

public class PropertiesUtils {
	private static final Logger logger = Logger.getLogger(PropertiesUtils.class.getName());
	
	private static final String PROPERTIES_FILE_NAME = "/neo4j-connector.properties";


	static String transactionManagerImplementation;


	private static Properties properties;

	/**
	 * Load a constant properties file that should be in 
	 * @return
	 */
	static Properties getProperties() {
		if(properties==null) {
			Properties used = new Properties();
			try {
				used.load(PropertiesUtils.class.getResourceAsStream(PROPERTIES_FILE_NAME));
				properties = used;
			} catch (IOException e) {
				logger.log(Level.SEVERE, "unable to load properties from "
								+PROPERTIES_FILE_NAME+" using resource path "
								+PropertiesUtils.class.getResource(PROPERTIES_FILE_NAME),
								e);
			}
		}
		return properties;
	}

	/**
	 * A method allowing loading the key from a property file, instead of referring directly to one of neo4j class.
	 * This method exists solely for malking sure there is no dependency between this class and a code that depends upon a neo4j version.
	 * @return the value of {@link Config#TXMANAGER_IMPLEMENTATION} ... which package unfortunatly depends upon neo4j version. 
	 */
	static String getNeo4jTransactionManagerImplementationKey() {
		if(transactionManagerImplementation==null) {
			transactionManagerImplementation = getProperties().getProperty("Config.TXMANAGER_IMPLEMENTATION", 
							"tx_manager_impl" /* default value has been extracted from neo4j 1.5, and is unchanged in neo4j 1.8 */);
		}
		return transactionManagerImplementation;
	}

}
