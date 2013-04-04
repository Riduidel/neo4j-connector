package com.netoprise.neo4j;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

final class Neo4jXAResource implements XAResource {

	private int timeout;

	@Override
	public void commit(Xid xid, boolean onePhase) throws XAException {
	}

	@Override
	public void end(Xid xid, int arg1) throws XAException {
	}

	@Override
	public void forget(Xid xid) throws XAException {
	}

	@Override
	public int getTransactionTimeout() throws XAException {
		return this.timeout;
	}

	@Override
	public boolean isSameRM(XAResource arg0) throws XAException {
		return this == arg0;
	}

	@Override
	public int prepare(Xid xid) throws XAException {
		return XA_OK;
	}

	@Override
	public Xid[] recover(int arg0) throws XAException {
		// two-phase commits not supported
		return new Xid[0];
	}

	@Override
	public void rollback(Xid xid) throws XAException {
	}

	@Override
	public boolean setTransactionTimeout(int arg0) throws XAException {
		this.timeout = arg0;
		return true;
	}

	@Override
	public void start(Xid xid, int flags) throws XAException {
	}

}