Neo4j Java EE connector 
=======================

The next question was: how to add Neo4j to our application and how to coordinate transactions between JPA and Neo4j ? Because we run inside Glassfish 3.1 container, we put graph database outside of application and created Java EE connector neo4j-connector that deployed to server as resource adaptor and manages Neo4j database. Application get instance of GraphDatabaseService from JNDI and doesn’t have to care about database startup/shutdown, configuration, and transactions.

Connector features
------------------

* Standard JCA 1.6 connector, that can be installed on any Java EE 6 compatible server. For the oldest servers, It’s pretty easy to convert connector to JCA 1.5 API.
* ResourceAdapter starts Neoj4 database at first request and shutdown it with server.
* Supports both LocalTransaction and XA transaction. XA support may be not quite correct – instead of using proper XAResource from adapter, it provides access to platform TransactionManager for Neo4j server. Diving into Neo4j internals, I’ve found that it creates couple of XAResource objects and enlists them in the Transaction, while ResourceAdaptor lets to create only one XAResource. Finally, I creater Provider for JEE Server TransactionManager, similar to the Spring Data project.

Usage
-----

Check out source code from the Github neo4j-connector project and build it with Maven. Project uses some artifacts from the Jboss Maven repository, so you have to configure it before build.

Notice that, if you want to have it installed to your repository, you'll have to do a 

    mvn clean deploy -DaltDeploymentRepository=The_URL_To_Your_Repository

Deploy connector to your application server. For Glassfish 3, there is shell script that deploys resource adapter and configures connector. All what you need is having asadmin application in the path or GLASSFISH_HOME environment variable. The connector supports some configuration options :

* `dir` for the Neo4j database location 
* boolean `xa` property that switches adapter from Local to XA transactions. Notice that, as Glassfish doesn't support boolean properties in connectors, it has to be replaced by the `string` `xaMode`.
* All neo4j config properties can be given through a single `string`named `neo4jConfig` and for which values are given using this syntax `key1->value1;key2->vaue2`. **Beware** : using "=" in that configuration string would lead to unsupported behaviour, as application servers may interpret it strangely.

Add `neo4j-connector-api` library to compile your application. JCA classes loaded in the parent Classloader on the server, you don’t need neo4j classes at runtime. For maven, just add dependency to ejb or war project:

	<dependency>
		<groupId>com.netoprise</groupId>
		<artifactId>neo4j-connector-api</artifactId>
		<version>${neo4j-connector.version}</version>
		<scope>provided</scope>
	</dependency>



There you go – Neo4j GraphDatabaseService now available as JNDI resource!

	@Resource(mappedName="/eis/Neo4j") Neo4JConnectionFactory neo4jConnectionFactory;
	 
	@Produces GraphDatabaseService createDatabaseService() throws ResourceException{
		return neo4jConnectionFactory.getConnection();
	}



In the EJB services, you don’t have to start/stop transactions by hand, it will be done by container if you set apporpriate `@TransactionAttribute` for EJB/business method.
In the XA mode, you can use JPA/SQL and Neo4j together, and container will take care for data consistency ( of course, database connection also have to use XADataSource ).

Adding neo4j-connector-impl to your build environment
-----------------------------------------------------

Contrary to impl, the connector impl (and the rar, by the way) depend upon the neo4j version, starting with connector v. 0.4-SNAPSHOT and upper.

Through a crafty (don't look at my poms, U mad !) use of maven-shade-plugin, these artifacts are available through some commons artifacts names :

	<dependency>
		<groupId>com.netoprise</groupId>
		<artifactId>neo4j-connector-impl</artifactId>
		<version>${neo4j-connector.version}</version>
		<classifier>${neo4j_version_with_underscores}</classifier>
	</dependency>

Is the connector implementation jar, shaded with all that's needed to have it working. As a consequence, no other dependency should be pulled from maven. if it's not the case, you can fill a bug.

	<dependency>
		<groupId>com.netoprise</groupId>
		<artifactId>neo4j-connector</artifactId>
		<version>${neo4j-connector.version}</version>
		<type>rar</type>
		<classifier>${neo4j_version_with_underscores}</classifier>
	</dependency>

Contains the full working resource adapter for a given neo4j version.

Both these artifacts are currently available for neo4j versions 1.5 to 1.8. Notice that, due to weird restrictions in maven repositories (and especially in Nexus), classifier can't include the "." character. As a consequence, I had to replace those version numbers with underscored one, transforming 1.5 into 1_5, and so on ...

Future plans
------------

* Support Neo4j High Availability cluster in the adaptor. I plan to start and configure all necessary services directly in the adapter, using Application Server cluster service where possible. Therefore, JEE application can be scaled without any changes in the code, as it supposed for JEE.
* Provide JPA style Object to Graph mapping, most likely as the CDI extension.