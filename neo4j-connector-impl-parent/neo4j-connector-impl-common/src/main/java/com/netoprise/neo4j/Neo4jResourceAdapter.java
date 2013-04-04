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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.TransactionSupport;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

/**
 * Neo4jResourceAdapter
 * 
 * @version $Revision: $
 */
@Connector(reauthenticationSupport = false, transactionSupport = TransactionSupport.TransactionSupportLevel.XATransaction)
public class Neo4jResourceAdapter implements ResourceAdapter {

	/** The logger */
	private static Logger log = Logger.getLogger("Neo4jResourceAdapter");

	/** dir */
	@ConfigProperty(defaultValue = "/tmp/graphdb")
	private String dir;

	/**
	 * Superseedes the initial {@link #xa} boolean property.
	 * Mind you, if JBoss seems to support perfectly boolean connector config properties, that's absolutely not the case of Glassfish.
	 * As a consequence, there is a boolean xa property that is accessed through this string one, which possible values are all the ones 
	 * that {@link Boolean#parseBoolean(String)} can parse. 
	 */
	@ConfigProperty(defaultValue = "false")
	private String xaMode;
	
	/**
	 * Should xa transactions be used
	 */
	private boolean xa;

	private final Set<Neo4jManagedConnectionFactoryInterface> factories = new HashSet<Neo4jManagedConnectionFactoryInterface>();

	/**
	 * Default constructor
	 */
	public Neo4jResourceAdapter() {

	}

	/**
	 * Set dir
	 * 
	 * @param dir
	 *            The value
	 */
	public void setDir(String dir) {
		this.dir = dir;
	}

	/**
	 * Get dir
	 * 
	 * @return The value
	 */
	public String getDir() {
		return dir;
	}

	public boolean isXa() {
		return xa;
	}

	public void setXa(boolean xa) {
		this.xa = xa;
	}

	public void addFactory(Neo4jManagedConnectionFactoryInterface factory) {
		factories.add(factory);
	}

	/**
	 * This is called during the activation of a message endpoint.
	 * 
	 * @param endpointFactory
	 *            A message endpoint factory instance.
	 * @param spec
	 *            An activation spec JavaBean instance.
	 * @throws ResourceException
	 *             generic exception
	 */
	public void endpointActivation(MessageEndpointFactory endpointFactory,
			ActivationSpec spec) throws ResourceException {
		log.info("endpointActivation()");
	}

	/**
	 * This is called when a message endpoint is deactivated.
	 * 
	 * @param endpointFactory
	 *            A message endpoint factory instance.
	 * @param spec
	 *            An activation spec JavaBean instance.
	 */
	public void endpointDeactivation(MessageEndpointFactory endpointFactory,
			ActivationSpec spec) {
		log.info("endpointDeactivation()");
	}

	/**
	 * This is called when a resource adapter instance is bootstrapped.
	 * 
	 * @param ctx
	 *            A bootstrap context containing references
	 * @throws ResourceAdapterInternalException
	 *             indicates bootstrap failure.
	 */
	public void start(BootstrapContext ctx)
			throws ResourceAdapterInternalException {
		log.info("start()");
		for (Neo4jManagedConnectionFactoryInterface factory : factories) {
			factory.start();
		}
	}

	/**
	 * This is called when a resource adapter instance is undeployed or during
	 * application server shutdown.
	 */
	public void stop() {
		log.info("stop()");
		for (Neo4jManagedConnectionFactoryInterface factory : factories) {
			factory.stop();
		}
	}

	/**
	 * This method is called by the application server during crash recovery.
	 * 
	 * @param specs
	 *            An array of ActivationSpec JavaBeans
	 * @throws ResourceException
	 *             generic exception
	 * @return An array of XAResource objects
	 */
	public XAResource[] getXAResources(ActivationSpec[] specs)
			throws ResourceException {
		log.info("getXAResources()");
		return null;
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return A hash code value for this object.
	 */
	@Override
	public int hashCode() {
		int result = 17;
		if (dir != null)
			result += 31 * result + 7 * dir.hashCode();
		else
			result += 31 * result + 7;
		return result;
	}

	/**
	 * Indicates whether some other object is equal to this one.
	 * 
	 * @param other
	 *            The reference object with which to compare.
	 * @return true if this object is the same as the obj argument, false
	 *         otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof Neo4jResourceAdapter))
			return false;
		Neo4jResourceAdapter obj = (Neo4jResourceAdapter) other;
		boolean result = true;
		if (result) {
			if (dir == null)
				result = obj.getDir() == null;
			else
				result = dir.equals(obj.getDir());
		}
		return result;
	}

	/**
	 * @return the xaMode
	 * @category getter
	 * @category xaMode
	 */
	public String getXaMode() {
		return xaMode;
	}

	/**
	 * @param xaMode the xaMode to set
	 * @category setter
	 * @category xaMode
	 */
	public void setXaMode(String xaMode) {
		this.xaMode = xaMode;
		setXa(Boolean.parseBoolean(xaMode));
	}

}
