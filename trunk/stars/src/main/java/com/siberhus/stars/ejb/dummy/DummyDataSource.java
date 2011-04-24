package com.siberhus.stars.ejb.dummy;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DummyDataSource implements DataSource{
	
	private SQLException e = new SQLException(
		"It seems like you've configured JndiLocator improperly or " +
		"you're running this app on an unmanaged server."
	);
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw e;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw e;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw e;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		throw e;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw e;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw e;
	}

	@Override
	public Connection getConnection() throws SQLException {
		throw e;
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		throw e;
	}

}
