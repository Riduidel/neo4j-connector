package com.netoprise.neo4j;

import javax.resource.ResourceException;
import javax.resource.spi.LocalTransaction;

/**
 * TODO: delegate transactions to the platform JTA, see
 * http://static.springsource.org/spring/docs/2.5.x/api/org/springframework/transaction/jta/JtaTransactionManager.html
 * 
 * @author asmirnov
 * 
 */
public class Neo4jLocalTransaction implements LocalTransaction {
	private Neo4jManagedConnectionInterface managedConnection;

	public Neo4jLocalTransaction(Neo4jManagedConnectionInterface managedConnection) {
		super();
		this.managedConnection = managedConnection;
	}

	public boolean isActive() {
		return managedConnection.isActive();
	}

	@Override
	public void rollback() throws ResourceException {
		managedConnection.rollback();
	}

	public void finish() {
		managedConnection.finish();
	}

	@Override
	public void commit() throws ResourceException {
		managedConnection.commit();
	}

	@Override
	public void begin() throws ResourceException {
		managedConnection.begin();
	}
}