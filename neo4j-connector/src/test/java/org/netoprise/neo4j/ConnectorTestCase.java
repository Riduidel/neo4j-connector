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

import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.netoprise.neo4j.Neo4jManagedConnection;
import org.netoprise.neo4j.Neo4jManagedConnectionFactory;
import org.netoprise.neo4j.Neo4jResourceAdapter;
import org.netoprise.neo4j.connection.Neo4JConnectionFactoryImpl;
import org.netoprise.neo4j.connection.Neo4JConnectionImpl;

import com.netoprise.neo4j.connection.Neo4JConnection;
import com.netoprise.neo4j.connection.Neo4JConnectionFactory;

import static org.junit.Assert.*;


/**
 * ConnectorTestCase
 *
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class ConnectorTestCase
{
   private static Logger log = Logger.getLogger("ConnectorTestCase");

   private static String deploymentName = "ConnectorTestCase";

   /**
    * Define the deployment
    *
    * @return The deployment archive
    */
   @Deployment
   public static ResourceAdapterArchive createDeployment()
   {
      ResourceAdapterArchive raa =
         ShrinkWrap.create(ResourceAdapterArchive.class, deploymentName + ".rar");
      JavaArchive ja = ShrinkWrap.create(JavaArchive.class, UUID.randomUUID().toString() + ".jar");
      ja.addClasses(Neo4jResourceAdapter.class, Neo4jManagedConnectionFactory.class, Neo4jManagedConnection.class, Neo4JConnectionFactory.class, Neo4JConnectionFactoryImpl.class, Neo4JConnection.class,Neo4JConnectionImpl.class);
      raa.addLibrary(ja);

      return raa;
   }

   /** Resource */
   @Resource(mappedName = "java:/eis/ConnectorTestCase")
   private Neo4JConnectionFactory connectionFactory;

   /**
    * Test Basic
    *
    * @exception Throwable Thrown if case of an error
    */
   @Test
   public void testBasic() throws Throwable
   {
      assertNotNull(connectionFactory);
      Neo4JConnection connection = connectionFactory.getConnection();
      assertNotNull(connection);
      connection.close();
   }

}