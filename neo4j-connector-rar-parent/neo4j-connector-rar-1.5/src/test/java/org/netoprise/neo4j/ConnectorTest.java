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
package org.netoprise.neo4j;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.ServiceLoader;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.jboss.osgi.testing.ManifestBuilder;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;

import com.netoprise.neo4j.connection.Neo4JConnectionFactory;

/**
 * ConnectorTest
 * 
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class ConnectorTest {
	private static final String PROPERTIES_FILE = "connector-config.xml";

	@Inject
	@EJB
	private Neo4jClient client;

	/**
	 * Define the deployment
	 * 
	 * @return The deployment archive
	 */
	@Deployment(name = "neo4j-connector", order = 1, testable = false)
	public static ResourceAdapterArchive createDeployment() {
		File connector = new File("target/" + getDeploymentName());
		ResourceAdapterArchive raa = ShrinkWrap.createFromZipFile(ResourceAdapterArchive.class, connector);
		raa.addAsManifestResource("ironjacamar.xml");
		raa.addAsManifestResource(ManifestBuilder.newInstance().addManifestHeader("Dependencies", "org.osgi.core export services"), "MANIFEST.MF");
		return raa;
	}

	private static String getDeploymentName() {
		Properties p = new Properties();
		try {
			p.loadFromXML(ConnectorTest.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p.getProperty("connector.deployment.name");
	}

	@Deployment(name = "test", order = 2)
	public static WebArchive createTestArchive() throws Throwable {
		try  {
			return ShrinkWrap.create(WebArchive.class, "test.war")
							.addClasses(Neo4jClient.class)
							.addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
							.addAsManifestResource(createManifest(), "MANIFEST.MF");
		} catch(Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	private static Asset createManifest() {
		return ManifestBuilder.newInstance().addManifestHeader("Dependencies", "deployment." + getDeploymentName() + " export services");
	}

	/**
	 * Test Basic
	 * 
	 * @exception Throwable
	 *                Thrown if case of an error
	 */
	@Test
	@OperateOnDeployment("test")
	public void testBasic() throws Throwable {
		Neo4JConnectionFactory connectionFactory = client.getConnectionFactory();
		assertNotNull(connectionFactory);
	}

	@Test
	@OperateOnDeployment("test")
	public void helloClient() throws Exception {
		assertEquals("Hello world", client.sayHello("world"));
	}

	@Test
	@Ignore
	@OperateOnDeployment("test")
	public void listJNDI() {
		try {
			Context context = new InitialContext();
			System.out.println("Context namespace: " + context.getNameInNamespace());
			NamingEnumeration<NameClassPair> content = context.list("comp");
			while (content.hasMoreElements()) {
				NameClassPair nameClassPair = (NameClassPair) content.nextElement();
				System.out.println("Name :" + nameClassPair.getName() + " with type:" + nameClassPair.getClassName());
			}
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
}
