/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.netoprise.neo4j;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionDefinition;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.security.auth.Subject;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import com.netoprise.neo4j.connection.Constants;
import com.netoprise.neo4j.connection.Neo4JConnectionFactory;
import com.netoprise.neo4j.connection.Neo4JConnectionFactoryImpl;
import com.netoprise.neo4j.connection.Neo4JConnectionImpl;

/**
 * Neo4jManagedConnectionFactory
 * 
 * @version $Revision: $
 */
@ConnectionDefinition(connectionFactory = Neo4JConnectionFactory.class, connectionFactoryImpl = Neo4JConnectionFactoryImpl.class, connection = GraphDatabaseService.class, connectionImpl = Neo4JConnectionImpl.class)
public class Neo4jManagedConnectionFactory extends AbstractNeo4jManagedConnectionFactory {
	private static final Logger logger = Logger.getLogger(Neo4jManagedConnectionFactory.class.getName());

	/** The serial version UID */
	private static final long serialVersionUID = 1L;


	/**
	 * Creates a Connection Factory instance.
	 * 
	 * @param cxManager
	 *            ConnectionManager to be associated with created EIS connection
	 *            factory instance
	 * @return EIS-specific Connection Factory instance or
	 *         javax.resource.cci.ConnectionFactory instance
	 * @throws ResourceException
	 *             Generic exception
	 */
	public Object createConnectionFactory(ConnectionManager cxManager)
			throws ResourceException {
		logwriter.append("createConnectionFactory()");
		return new Neo4JConnectionFactoryImpl(this, cxManager);
	}

	/**
	 * Creates a Connection Factory instance.
	 * 
	 * @return EIS-specific Connection Factory instance or
	 *         javax.resource.cci.ConnectionFactory instance
	 * @throws ResourceException
	 *             Generic exception
	 */
	public Object createConnectionFactory() throws ResourceException {
		throw new ResourceException(
				"This resource adapter doesn't support non-managed environments");
	}

	/**
	 * Creates a new physical connection to the underlying EIS resource manager.
	 * 
	 * @param subject
	 *            Caller's security information
	 * @param cxRequestInfo
	 *            Additional resource adapter specific connection request
	 *            information
	 * @throws ResourceException
	 *             generic exception
	 * @return ManagedConnection instance
	 */
	public ManagedConnection createManagedConnection(Subject subject,
			ConnectionRequestInfo cxRequestInfo) throws ResourceException {
		logwriter.append("createManagedConnection()");
		createDatabase();
		connectionsCreated++;
		return new Neo4jManagedConnection(this);
	}

	/**
	 * Returns a matched connection from the candidate set of connections.
	 * 
	 * @param connectionSet
	 *            Candidate connection set
	 * @param subject
	 *            Caller's security information
	 * @param cxRequestInfo
	 *            Additional resource adapter specific connection request
	 *            information
	 * @throws ResourceException
	 *             generic exception
	 * @return ManagedConnection if resource adapter finds an acceptable match
	 *         otherwise null
	 */
	public ManagedConnection matchManagedConnections(Set connectionSet,
			Subject subject, ConnectionRequestInfo cxRequestInfo)
			throws ResourceException {
		logwriter.append("matchManagedConnections()");
		ManagedConnection result = null;
		Iterator it = connectionSet.iterator();
		while (result == null && it.hasNext()) {
			ManagedConnection mc = (ManagedConnection) it.next();
			if (mc instanceof Neo4jManagedConnection) {
				result = mc;
			}

		}
		return result;
	}

	public void destroyManagedConnection(Neo4jManagedConnection connection) {
		connectionsCreated--;
		if (connectionsCreated <= 0) {
			shutdownDatabase();
		}
	}

	@Override
	public TransactionSupportLevel getTransactionSupport() {
		if (isXa()) {
			return TransactionSupportLevel.XATransaction;
		} else {
			return TransactionSupportLevel.LocalTransaction;
		}
	}

}
