package com.netoprise.neo4j;

import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.resource.spi.TransactionSupport;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Interface allowing us to have one managed connection factory per neo4j
 * version
 * 
 * @author ndx
 * 
 */
public interface Neo4jManagedConnectionFactoryInterface extends ManagedConnectionFactory, ResourceAdapterAssociation, TransactionSupport {

	static final String NEO4J_CONFIG_SEPARATOR = ";";
	/**
	 * String used as equals for {@link #neo4jConfig} content.
	 * This string is NOT the "=" character, as it may be misunderstood by application servers.
	 * As an example, when configuring this rar using Glassfish, all properties are given using the single "--property" 
	 * parameter, which make impossible to use neo4j config options.
	 */
	static final String NEO4J_CONFIG_EQUALS = "->";
	
	/**
	 * Start the connection factory
	 */
	void start();

	/**
	 * Stop the connection factory
	 */
	void stop();

	GraphDatabaseService getDatabase();

	void destroyManagedConnection(Neo4jManagedConnection neo4jManagedConnection);

}
