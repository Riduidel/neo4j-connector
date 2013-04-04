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
package com.netoprise.neo4j.connection;

import java.util.Collection;
import java.util.logging.Logger;

import javax.transaction.TransactionManager;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.kernel.IdGeneratorFactory;
import org.neo4j.kernel.KernelData;
import org.neo4j.kernel.TransactionBuilder;
import org.neo4j.kernel.guard.Guard;
import org.neo4j.kernel.impl.core.KernelPanicEventGenerator;
import org.neo4j.kernel.impl.core.LockReleaser;
import org.neo4j.kernel.impl.core.NodeManager;
import org.neo4j.kernel.impl.core.RelationshipTypeHolder;
import org.neo4j.kernel.impl.nioneo.store.StoreId;
import org.neo4j.kernel.impl.persistence.PersistenceSource;
import org.neo4j.kernel.impl.transaction.LockManager;
import org.neo4j.kernel.impl.transaction.XaDataSourceManager;
import org.neo4j.kernel.impl.transaction.xaframework.TxIdGenerator;
import org.neo4j.kernel.impl.util.StringLogger;
import org.neo4j.kernel.info.DiagnosticsManager;

import com.netoprise.neo4j.Neo4jManagedConnectionFactoryInterface;
import com.netoprise.neo4j.Neo4jManagedConnectionInterface;

/**
 * Implementation of a connection to a neo4j graph handled by this JCA connector. This implementation makes sure the graph database is never closed 
 * directly by application, but rather when application server shuts down.
 * 
 * Notice this class is NOT the one that will be used in final jars and rars : each version of neo4j requires its own.
 * 
 * @version $Revision: $
 */
public class Neo4JConnectionImpl implements Neo4jConnection, GraphDatabaseAPI {
	/** The logger */
	private static Logger log = Logger.getLogger("Neo4JConnectionImpl");

	/** ManagedConnection */
	private final Neo4jManagedConnectionInterface mc;

	/** ManagedConnectionFactory */
	private final Neo4jManagedConnectionFactoryInterface mcf;

	private GraphDatabaseService graphDatabase;

	/**
	 * Default constructor
	 * 
	 * @param mc
	 *            Neo4JManagedConnection
	 * @param mcf
	 *            Neo4JManagedConnectionFactory
	 */
	public Neo4JConnectionImpl(Neo4jManagedConnectionInterface mc,
			Neo4jManagedConnectionFactoryInterface mcf) {
		this.mc = mc;
		this.mcf = mcf;
		this.graphDatabase = mcf.getDatabase();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#createNode()
	 */
	@Override
	public Node createNode() {
		return this.graphDatabase.createNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#getAllNodes()
	 */
	@Override
	public Iterable<Node> getAllNodes() {
		return graphDatabase.getAllNodes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#getNodeById(long)
	 */
	@Override
	public Node getNodeById(long arg0) {
		return graphDatabase.getNodeById(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#getReferenceNode()
	 */
	@Override
	public Node getReferenceNode() {
		return graphDatabase.getReferenceNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#getRelationshipById(long)
	 */
	@Override
	public Relationship getRelationshipById(long arg0) {
		return graphDatabase.getRelationshipById(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#getRelationshipTypes()
	 */
	@Override
	public Iterable<RelationshipType> getRelationshipTypes() {
		return graphDatabase.getRelationshipTypes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#index()
	 */
	@Override
	public IndexManager index() {
		return graphDatabase.index();
	}


	/**
	 * @return
	 * @see org.neo4j.graphdb.GraphDatabaseService#beginTx()
	 */
	public Transaction beginTx() {
		return graphDatabase.beginTx();
	}


	public void shutdown() {
		close();
	}


	public <T> TransactionEventHandler<T> registerTransactionEventHandler(
			TransactionEventHandler<T> handler) {
		return graphDatabase.registerTransactionEventHandler(handler);
	}


	public <T> TransactionEventHandler<T> unregisterTransactionEventHandler(
			TransactionEventHandler<T> handler) {
		return graphDatabase.unregisterTransactionEventHandler(handler);
	}


	public KernelEventHandler registerKernelEventHandler(
			KernelEventHandler handler) {
		return graphDatabase.registerKernelEventHandler(handler);
	}


	public KernelEventHandler unregisterKernelEventHandler(
			KernelEventHandler handler) {
		return graphDatabase.unregisterKernelEventHandler(handler);
	}


	@Override
	public void close() {
		mc.closeHandle(this);
	}


	/* and now, ladies and gentlemen, come the typical integration nightmare */
	
	@Override
	@Deprecated
	public NodeManager getNodeManager() {
		return ((GraphDatabaseAPI) graphDatabase).getNodeManager();
	}


	@Override
	@Deprecated
	public LockReleaser getLockReleaser() {
		return ((GraphDatabaseAPI) graphDatabase).getLockReleaser();
	}


	@Override
	@Deprecated
	public LockManager getLockManager() {
		return ((GraphDatabaseAPI) graphDatabase).getLockManager();
	}


	@Override
	@Deprecated
	public XaDataSourceManager getXaDataSourceManager() {
		return ((GraphDatabaseAPI) graphDatabase).getXaDataSourceManager();

	}


	@Override
	@Deprecated
	public TransactionManager getTxManager() {
		return ((GraphDatabaseAPI) graphDatabase).getTxManager();
	}


	@Override
	@Deprecated
	public DiagnosticsManager getDiagnosticsManager() {
		return ((GraphDatabaseAPI) graphDatabase).getDiagnosticsManager();
	}


	@Override
	@Deprecated
	public StringLogger getMessageLog() {
		return ((GraphDatabaseAPI) graphDatabase).getMessageLog();
	}


	@Override
	@Deprecated
	public RelationshipTypeHolder getRelationshipTypeHolder() {
		return ((GraphDatabaseAPI) graphDatabase).getRelationshipTypeHolder();
 
	}


	@Override
	@Deprecated
	public IdGeneratorFactory getIdGeneratorFactory() {
		return ((GraphDatabaseAPI) graphDatabase).getIdGeneratorFactory();

	}


	@Override
	@Deprecated
	public String getStoreDir() {
		return ((GraphDatabaseAPI) graphDatabase).getStoreDir();

	}


	@Override
	@Deprecated
	public KernelData getKernelData() {
		return ((GraphDatabaseAPI) graphDatabase).getKernelData();

	}


	@Override
	@Deprecated
	public <T> T getSingleManagementBean(Class<T> type) {
		return ((GraphDatabaseAPI) graphDatabase).getSingleManagementBean(type);

	}


	@Override
	@Deprecated
	public TransactionBuilder tx() {
		return ((GraphDatabaseAPI) graphDatabase).tx();
	}


	@Override
	@Deprecated
	public PersistenceSource getPersistenceSource() {
		return ((GraphDatabaseAPI) graphDatabase).getPersistenceSource();
	}


	@Override
	@Deprecated
	public <T> Collection<T> getManagementBeans(Class<T> type) {
		return ((GraphDatabaseAPI) graphDatabase).getManagementBeans(type);
	}


	@Override
	@Deprecated
	public KernelPanicEventGenerator getKernelPanicGenerator() {
		return ((GraphDatabaseAPI) graphDatabase).getKernelPanicGenerator();
	}


	@Override
	public Guard getGuard() {
		return ((GraphDatabaseAPI) graphDatabase).getGuard();
	}


	@Override
	@Deprecated
	public TxIdGenerator getTxIdGenerator() {
		return ((GraphDatabaseAPI) graphDatabase).getTxIdGenerator();
	}


	@Override
	@Deprecated
	public StoreId getStoreId() {
		return ((GraphDatabaseAPI) graphDatabase).getStoreId();

	}

}
