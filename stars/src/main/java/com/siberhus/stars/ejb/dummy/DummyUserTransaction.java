package com.siberhus.stars.ejb.dummy;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

public class DummyUserTransaction implements UserTransaction {

	private SystemException e = new SystemException(
		"It seem like you've configured JndiLocator improperly or " +
		"you're running this app on an unmanaged server."
	);
	
	@Override
	public void begin() throws NotSupportedException, SystemException {
		throw e;
	}

	@Override
	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {
		throw e;
	}

	@Override
	public void rollback() throws IllegalStateException, SecurityException,
			SystemException {
		throw e;
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		throw e;
	}

	@Override
	public int getStatus() throws SystemException {
		throw e;
	}
	
	@Override
	public void setTransactionTimeout(int seconds) throws SystemException {
		throw e;
	}

	
}
