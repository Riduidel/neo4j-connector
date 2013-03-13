package com.netoprise.neo4j.transaction.neo4j_1_8;

import java.util.logging.Logger;

import org.neo4j.helpers.Service;
import org.neo4j.kernel.impl.core.KernelPanicEventGenerator;
import org.neo4j.kernel.impl.nioneo.store.FileSystemAbstraction;
import org.neo4j.kernel.impl.transaction.AbstractTransactionManager;
import org.neo4j.kernel.impl.transaction.TransactionManagerProvider;
import org.neo4j.kernel.impl.transaction.TxHook;
import org.neo4j.kernel.impl.transaction.XaDataSourceManager;
import org.neo4j.kernel.impl.util.StringLogger;

import com.netoprise.neo4j.connection.Constants;

/**
 * TransactionManager provider that delegates transaction management to the application server.
 * @author asmirnov
 *
 */
@Service.Implementation(TransactionManagerProvider.class)
public class PlatformTransactionProvider extends TransactionManagerProvider {
	
	private static Logger log = Logger
	.getLogger("Neo4jTransactionManagerProvider");

	public PlatformTransactionProvider() {
		super(Constants.JEE_JTA);
		log.info("Load PlatformTransactionManagerProvider");
	}

	@Override
	public AbstractTransactionManager loadTransactionManager(String txLogDir, XaDataSourceManager xaDataSourceManager, KernelPanicEventGenerator kpe,
					TxHook rollbackHook, StringLogger msgLog, FileSystemAbstraction fileSystem) {
		log.info("Create Platform TransactionManager wrapper");
		return new PlatformTransactionManager();
	}

}
