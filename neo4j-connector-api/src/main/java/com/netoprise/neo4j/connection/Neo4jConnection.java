package com.netoprise.neo4j.connection;

import org.neo4j.graphdb.GraphDatabaseService;

public interface Neo4jConnection extends GraphDatabaseService {
	/**
	 * Provides access to neo4j raw graph. Indeed, there are some code elemnts in Tinkerpop Blueprints Neo4jGraph that require access to graph node manager.	 * @return
	 */
	GraphDatabaseService getRawGraph();
	
	void close();

}
