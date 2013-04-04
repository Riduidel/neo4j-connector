package org.netoprise.neo4j;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.resource.ResourceException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import com.netoprise.neo4j.connection.Neo4JConnectionFactory;
import com.netoprise.neo4j.connection.Neo4jConnection;

/**
 * Test bean used to make sure usage of two parallel connections to the same neo4j graph are possible
 * @author ndx
 *
 */
@Stateless
@LocalBean
@Resource(mappedName=Neo4jStatelessClient.NEO4J_NAME,name="Neo4j",type=Neo4JConnectionFactory.class)
public class Neo4jStatefulClient {
	
	public static final String MESSAGE = "I made links, therefore I am";

	public static final String NEO4J_NAME = "java:/eis/Neo4j";
	
	@Resource(mappedName=NEO4J_NAME)
	private Neo4JConnectionFactory connectionFactory;

	private Neo4jConnection connection;

	
	public Neo4JConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void imGonnaTalk() throws ResourceException {
		connection = connectionFactory.getConnection();
		Node referenceNode = connection.getReferenceNode();
		referenceNode.setProperty(getClass().getName(), MESSAGE);
	}

	public String talk() {
		Node referenceNode = connection.getReferenceNode();
		String message = (String) referenceNode.getProperty(getClass().getName());
		connection.close();
		return message;
	}

}
