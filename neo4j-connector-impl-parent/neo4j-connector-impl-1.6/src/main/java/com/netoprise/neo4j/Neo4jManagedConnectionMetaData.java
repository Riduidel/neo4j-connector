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

import java.util.logging.Logger;

import javax.resource.ResourceException;

import javax.resource.spi.ManagedConnectionMetaData;

/**
 * Neo4jManagedConnectionMetaData
 *
 * @version $Revision: $
 */
public class Neo4jManagedConnectionMetaData implements ManagedConnectionMetaData
{
   /** The logger */
   private static Logger log = Logger.getLogger("Neo4jManagedConnectionMetaData");

   /**
    * Default constructor
    */
   public Neo4jManagedConnectionMetaData()
   {

   }

   /**
    * Returns Product name of the underlying EIS instance connected through the ManagedConnection.
    *
    * @return Product name of the EIS instance
    * @throws ResourceException Thrown if an error occurs
    */
   public String getEISProductName() throws ResourceException
   {
      log.info("getEISProductName()");
      return "Neo4J";
   }

   /**
    * Returns Product version of the underlying EIS instance connected through the ManagedConnection.
    *
    * @return Product version of the EIS instance
    * @throws ResourceException Thrown if an error occurs
    */
   public String getEISProductVersion() throws ResourceException
   {
      log.info("getEISProductVersion()");
      return "1.4.M01";
   }

   /**
    * Returns maximum limit on number of active concurrent connections 
    *
    * @return Maximum limit for number of active concurrent connections
    * @throws ResourceException Thrown if an error occurs
    */
   public int getMaxConnections() throws ResourceException
   {
      log.info("getMaxConnections()");
      return 0;
   }

   /**
    * Returns name of the user associated with the ManagedConnection instance
    *
    * @return Name of the user
    * @throws ResourceException Thrown if an error occurs
    */
   public String getUserName() throws ResourceException
   {
      log.info("getUserName()");
      return null; //TODO
   }


}
