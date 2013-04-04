package com.netoprise.neo4j;

import javax.resource.spi.ManagedConnection;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Interface abstracting some elements of neo4j managed connection for neo4j version dependant code to work correctly
 * @author ndx
 *
 */
public interface Neo4jManagedConnectionInterface extends ManagedConnection {

	boolean isActive();

	void rollback();

	void finish();

	void commit();

	void begin();

	void closeHandle(GraphDatabaseService neo4jConnectionImpl);

}
