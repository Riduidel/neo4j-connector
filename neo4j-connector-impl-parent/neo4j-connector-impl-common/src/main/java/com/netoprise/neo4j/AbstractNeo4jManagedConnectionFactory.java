package com.netoprise.neo4j;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.ResourceAdapter;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import com.netoprise.neo4j.connection.Constants;

public abstract class AbstractNeo4jManagedConnectionFactory implements Neo4jManagedConnectionFactoryInterface {
	private static final Logger logger = Logger.getLogger(AbstractNeo4jManagedConnectionFactory.class.getName());
	
	
	/** The resource adapter */
	private Neo4jResourceAdapter ra;
	/** The logwriter */
	protected PrintWriter logwriter;
	protected GraphDatabaseService database;
	protected int connectionsCreated = 0;
	@ConfigProperty
	protected String neo4jConfig;
	@ConfigProperty
	private String dir;
	
	public AbstractNeo4jManagedConnectionFactory() {
		this.logwriter = new PrintWriter(System.out);
	}
	/**
	 * Like for {@link Neo4jResourceAdapter#xaMode}, we have here to declare a string containing the boolean value, as glassfish is not able to use boolean config properties
	 */
	@ConfigProperty
	private String xaMode;
	private boolean xa;

	public String getDir() {
		if (null == dir) {
			if(null==ra)
				return dir;
			return ra.getDir();
		}
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getXaMode() {
		return xaMode;
	}

	public void setXaMode(String xaMode) {
		this.xaMode = xaMode;
		setXa(Boolean.parseBoolean(xaMode));
	}

	public boolean isXa() {
		return xa || (ra!=null && ra.isXa());
	}

	public void setXa(boolean xa) {
		this.xa = xa;
	}

	/**
	 * Get the log writer for this ManagedConnectionFactory instance.
	 * 
	 * @return PrintWriter
	 * @throws ResourceException
	 *             generic exception
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		logwriter.append("getLogWriter()");
		return logwriter;
	}

	/**
	 * Set the log writer for this ManagedConnectionFactory instance.
	 * 
	 * @param out
	 *            PrintWriter - an out stream for error logging and tracing
	 * @throws ResourceException
	 *             generic exception
	 */
	public void setLogWriter(PrintWriter out) throws ResourceException {
		logwriter.append("setLogWriter()");
		logwriter = out;
	}

	/**
	 * Get the resource adapter
	 * 
	 * @return The handle
	 */
	public Neo4jResourceAdapter getResourceAdapter() {
		logwriter.append("getResourceAdapter()");
		return ra;
	}

	/**
	 * Set the resource adapter
	 * 
	 * @param ra
	 *            The handle
	 */
	public void setResourceAdapter(ResourceAdapter ra) {
		logwriter.append("setResourceAdapter()");
		this.ra = (Neo4jResourceAdapter) ra;
		this.ra.addFactory(this);
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return A hash code value for this object.
	 */
	@Override
	public int hashCode() {
		int result = 17;
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
		if (other == null) {
			return false;
		} else if (other == this) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the neo4jConfig
	 * @category getter
	 * @category neo4jConfig
	 */
	public String getNeo4jConfig() {
		return neo4jConfig;
	}

	/**
	 * @param neo4jConfig the neo4jConfig to set
	 * @category setter
	 * @category neo4jConfig
	 */
	public void setNeo4jConfig(String neo4jConfig) {
		this.neo4jConfig = neo4jConfig;
	}

	public void start() {
		createDatabase();
	}

	public void stop() {
		shutdownDatabase();
	}

	protected void shutdownDatabase() {
		if (null != database) {
			database.shutdown();
			database = null;
		}
	}

	protected void createDatabase() {
		if (null == database) {
			Map<String, String> config = createNeo4jConfigFromParameters();
			database = createDatabase(config);
		}
	}

	/**
	 * Create a map containing neo4j config from given parameters
	 * @return
	 */
	private Map<String, String> createNeo4jConfigFromParameters() {
		Map<String, String> config = new HashMap<String, String>();
		// Do some double split
		if(neo4jConfig!=null) {
			String[] parameterPairs = neo4jConfig.split(Neo4jManagedConnectionFactoryInterface.NEO4J_CONFIG_SEPARATOR);
			for(String pair : parameterPairs) {
				int equalsPos = pair.indexOf(Neo4jManagedConnectionFactoryInterface.NEO4J_CONFIG_EQUALS);
				String key = pair.substring(0, equalsPos);
				String value = pair.substring(equalsPos+Neo4jManagedConnectionFactoryInterface.NEO4J_CONFIG_EQUALS.length());
				config.put(key, value);
			}
		}
		// XA config is always done after manual parameter passing to override it
		if (isXa()) {
			config.put(PropertiesUtils.getNeo4jTransactionManagerImplementationKey(),
					Constants.JEE_JTA);
		}
		return config;
	}

	/**
	 * Overridable method allowing graph database construction to be delegated to the used managed connection factory
	 * @param config
	 * @return
	 */
	protected EmbeddedGraphDatabase createDatabase(Map<String, String> config) {
		return new EmbeddedGraphDatabase(getDir(), config);
	}

	/**
	 * @return the database
	 */
	public GraphDatabaseService getDatabase() {
		return database;
	}
}
