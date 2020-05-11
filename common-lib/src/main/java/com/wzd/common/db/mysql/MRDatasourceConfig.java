package com.wzd.common.db.mysql;

import java.io.Closeable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.wzd.common.db.mysql.annotation.Dao;
import com.wzd.common.db.mysql.annotation.ForceMasterDS;


/**
 * Main/Replication 구조의 DB를 단일 DataSource와  MyBatis로 사용할수 있도록  설정합니다.
 * Main/Replication 선택 기준은 Service 레이어의 Transactional 상태에 따라 자동 선택되어 집니다.
 * 만약 Replication DB 연결정보가 제공되지 않으면, 일반적인 단일 DataSource와 MyBatis 설정을 처리합니다.
 * @author pat
 *
 */
@Configuration
@MapperScan(basePackages = {"com.wzd"}, annotationClass = Dao.class)
public class MRDatasourceConfig {
	
	/**
	 * 내부적으로 사용되는 객체 
	 * Master DataSource와 Replica DataSource를 포함하여 단일 Connection으로 포장하는 객체입니다.
	 * @author pat
	 *
	 */

	@Profile("mysql")
	private class MRConnection implements Connection {

		private DataSource	mds;
		private DataSource	rds;
		private Connection	con;
		private Boolean		isAutoCommit			= true;
		private Boolean		isReadOnly;
		private Integer		transactionIsolation;
		
		private MRConnection(DataSource	mds, DataSource	rds) {
			this.mds	= mds;
			this.rds	= rds;
		}
		
		private void selectConnection()
			throws SQLException {
			if (this.con != null) {
				return;
			}
			boolean	isToMaster;
			
			isToMaster	= ForceMasterMethodAspect.statusForceMaster.get() || TransactionSynchronizationManager.isActualTransactionActive();  
			MRDatasourceConfig.this.logger.debug("[selectConnection] : forceMaster ? " + ForceMasterMethodAspect.statusForceMaster.get() + ", inTr ? " + TransactionSynchronizationManager.isActualTransactionActive() + ", so isMaster ?" + isToMaster);
			this.con	= isToMaster ? this.mds.getConnection() : this.rds.getConnection();
			if (this.isAutoCommit != null) {
				this.con.setAutoCommit(this.isAutoCommit);
			}
			if (this.isReadOnly != null) {
				this.con.setReadOnly(this.isReadOnly);
			}
			if (this.transactionIsolation != null) {
				this.con.setTransactionIsolation(this.transactionIsolation);
			}
			this.mds	= null;
			this.rds	= null;
		}
		
		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return this.con.unwrap(iface);
		}

		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return this.con.isWrapperFor(iface);
		}

		@Override
		public Statement createStatement() throws SQLException {
			this.selectConnection();
			return this.con.createStatement();
		}

		@Override
		public PreparedStatement prepareStatement(String sql) throws SQLException {
			this.selectConnection();
			return this.con.prepareStatement(sql);
		}

		@Override
		public CallableStatement prepareCall(String sql) throws SQLException {
			this.selectConnection();
			return this.con.prepareCall(sql);
		}

		@Override
		public String nativeSQL(String sql) throws SQLException {
			this.selectConnection();
			this.con.nativeSQL(sql);
			return null;
		}

		@Override
		public void setAutoCommit(boolean autoCommit) throws SQLException {
			this.isAutoCommit	= autoCommit;
			if (this.con != null) {
				this.con.setAutoCommit(this.isAutoCommit);
			}
		}

		@Override
		public boolean getAutoCommit() throws SQLException {
			return this.isAutoCommit == null ? this.con.getAutoCommit() : this.isAutoCommit;
		}

		@Override
		public void commit() throws SQLException {
			if (this.con != null) {
				this.con.commit();
			}
		}

		@Override
		public void rollback() throws SQLException {
			if (this.con != null) {
				this.con.rollback();
			}
		}

		@Override
		public void close() throws SQLException {
			if (this.con != null) {
				this.con.close();
			}
		}

		@Override
		public boolean isClosed() throws SQLException {
			return this.con == null || this.con.isClosed();
		}

		@Override
		public DatabaseMetaData getMetaData() throws SQLException {
			this.selectConnection();
			return this.con.getMetaData();
		}

		@Override
		public void setReadOnly(boolean readOnly) throws SQLException {
			this.isReadOnly	= readOnly;
			if (this.con != null) {
				this.con.setReadOnly(this.isReadOnly);
			}
			
		}

		@Override
		public boolean isReadOnly() throws SQLException {
			return this.isReadOnly == null ? this.con.isReadOnly() : this.isReadOnly();
		}

		@Override
		public void setCatalog(String catalog) throws SQLException {
			this.selectConnection();
			this.con.setCatalog(catalog);
		}

		@Override
		public String getCatalog() throws SQLException {
			return this.con.getCatalog();
		}

		@Override
		public void setTransactionIsolation(int level) throws SQLException {
			this.transactionIsolation	= level;
			if (this.con != null) {
				this.con.setTransactionIsolation(this.transactionIsolation);
			}
		}

		@Override
		public int getTransactionIsolation() throws SQLException {
			return this.transactionIsolation == null ? this.con.getTransactionIsolation() : this.transactionIsolation;
		}

		@Override
		public SQLWarning getWarnings() throws SQLException {
			return this.con.getWarnings();
		}

		@Override
		public void clearWarnings() throws SQLException {
			this.con.clearWarnings();
		}

		@Override
		public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
			this.selectConnection();
			return this.con.createStatement(resultSetType, resultSetConcurrency);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
				throws SQLException {
			this.selectConnection();
			return this.con.prepareStatement(sql, resultSetType, resultSetConcurrency);
		}

		@Override
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
				throws SQLException {
			this.selectConnection();
			return this.con.prepareCall(sql, resultSetType, resultSetConcurrency);
		}

		@Override
		public Map<String, Class<?>> getTypeMap() throws SQLException {
			return this.con.getTypeMap();
		}

		@Override
		public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
			this.con.setTypeMap(map);
		}

		@Override
		public void setHoldability(int holdability) throws SQLException {
			this.con.setHoldability(holdability);
		}

		@Override
		public int getHoldability() throws SQLException {
			return this.con.getHoldability();
		}

		@Override
		public Savepoint setSavepoint() throws SQLException {
			return this.con.setSavepoint();
		}

		@Override
		public Savepoint setSavepoint(String name) throws SQLException {
			return this.con.setSavepoint(name);
		}

		@Override
		public void rollback(Savepoint savepoint) throws SQLException {
			this.con.rollback(savepoint);
		}

		@Override
		public void releaseSavepoint(Savepoint savepoint) throws SQLException {
			this.con.releaseSavepoint(savepoint);
		}

		@Override
		public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
				throws SQLException {
			this.selectConnection();
			return this.con.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
				int resultSetHoldability) throws SQLException {
			this.selectConnection();
			return this.con.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		@Override
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
				int resultSetHoldability) throws SQLException {
			this.selectConnection();
			return this.con.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
			this.selectConnection();
			return this.con.prepareStatement(sql, autoGeneratedKeys);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
			this.selectConnection();
			return this.con.prepareStatement(sql, columnIndexes);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
			this.selectConnection();
			return this.con.prepareStatement(sql, columnNames);
		}

		@Override
		public Clob createClob() throws SQLException {
			return this.con.createClob();
		}

		@Override
		public Blob createBlob() throws SQLException {
			return this.con.createBlob();
		}

		@Override
		public NClob createNClob() throws SQLException {
			return this.con.createNClob();
		}

		@Override
		public SQLXML createSQLXML() throws SQLException {
			return this.con.createSQLXML();
		}

		@Override
		public boolean isValid(int timeout) throws SQLException {
			return this.con.isValid(timeout);
		}

		@Override
		public void setClientInfo(String name, String value) throws SQLClientInfoException {
			this.con.setClientInfo(name, value);
		}

		@Override
		public void setClientInfo(Properties properties) throws SQLClientInfoException {
			this.con.setClientInfo(properties);
		}

		@Override
		public String getClientInfo(String name) throws SQLException {
			return this.con.getClientInfo(name);
		}

		@Override
		public Properties getClientInfo() throws SQLException {
			return this.con.getClientInfo();
		}

		@Override
		public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
			return this.con.createArrayOf(typeName, elements);
		}

		@Override
		public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
			return this.con.createStruct(typeName, attributes);
		}

		@Override
		public void setSchema(String schema) throws SQLException {
			this.con.setSchema(schema);
		}

		@Override
		public String getSchema() throws SQLException {
			return this.con.getSchema();
		}

		@Override
		public void abort(Executor executor) throws SQLException {
			this.con.abort(executor);
		}

		@Override
		public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
			this.con.setNetworkTimeout(executor, milliseconds);

		}

		@Override
		public int getNetworkTimeout() throws SQLException {
			return this.con.getNetworkTimeout();
		}
		
	}
	
	
	/**
	 * 내부적으로 사용되는 객체 
	 * Master DataSource와 Replica DataSource를 포함하여 단일 DataSource로 포장하는 객체입니다.
	 * @author pat
	 *
	 */
	@Profile("mysql")
	private class MRDatasource extends AbstractDataSource implements DisposableBean {

		private DataSource	master;
		private DataSource	replica;
		
		/**
		 * 생성자
		 * @param master master datasource 객체
		 * @param replica replica datasource 객체
		 */
		private MRDatasource(DataSource master, DataSource replica) {
			this.master		= master;
			this.replica	= replica;
		}
		
		@Override
		public Connection getConnection() throws SQLException {
			return new MRConnection(this.master, this.replica);
		}

		@Override
		public Connection getConnection(String username, String password) throws SQLException {
			throw new SQLException("this method is not supported");
		}
		
		@Override
		public void destroy() throws Exception {
			this.cloaseDataSource(this.master);
			this.cloaseDataSource(this.replica);
		}
		
		private void cloaseDataSource(DataSource ds) {
			try {
				if (ds instanceof org.apache.tomcat.jdbc.pool.DataSource) {
					((org.apache.tomcat.jdbc.pool.DataSource)ds).close();
				} else if (ds instanceof Closeable) {
					((Closeable)ds).close();
				}
			} catch (Exception e) {
				MRDatasourceConfig.this.logger.warn("DataSource " + ds + " cloase fail", e);
			}
		}
	}
	
	/**
	 * 내부적으로 사용되는 객체
	 * Master / Replica DataSource를 엑세스하기위한 MyBatis SqlSessionTemplate
	 * inTransaction이 아닌 상태에서는 update 오퍼레이션을 허용하지 않도록 합니다.
	 * @author pat
	 *
	 */
	@Profile("mysql")
	private class MRSqlSessionTemplate extends SqlSessionTemplate {
		
		private static final String ILLEGAL_ACCESS_MSG = "this operation must in transaction only.( forget @Transactional(propagation = Propagation.REQUIRED) in service method?)";

		public MRSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
			super(sqlSessionFactory);
		}

		@Override
		public int insert(String statement) {
			boolean isToMaster;
			
			isToMaster	= ForceMasterMethodAspect.statusForceMaster.get() || TransactionSynchronizationManager.isActualTransactionActive();  
			if (!isToMaster) {
				throw new IllegalStateException(ILLEGAL_ACCESS_MSG);
			}
			return super.insert(statement);
		}

		@Override
		public int insert(String statement, Object parameter) {
			boolean isToMaster;
			
			isToMaster	= ForceMasterMethodAspect.statusForceMaster.get() || TransactionSynchronizationManager.isActualTransactionActive();  
			if (!isToMaster) {
				throw new IllegalStateException(ILLEGAL_ACCESS_MSG);
			}
			return super.insert(statement, parameter);
		}

		@Override
		public int update(String statement) {
			boolean isToMaster;
			
			isToMaster	= ForceMasterMethodAspect.statusForceMaster.get() || TransactionSynchronizationManager.isActualTransactionActive();  
			if (!isToMaster) {
				throw new IllegalStateException(ILLEGAL_ACCESS_MSG);
			}
			return super.update(statement);
		}

		@Override
		public int update(String statement, Object parameter) {
			boolean isToMaster;
			
			isToMaster	= ForceMasterMethodAspect.statusForceMaster.get() || TransactionSynchronizationManager.isActualTransactionActive();  
			if (!isToMaster) {
				throw new IllegalStateException("this operation must in transaction only.");
			}
			return super.update(statement, parameter);
		}

		@Override
		public int delete(String statement) {
			boolean isToMaster;
			
			isToMaster	= ForceMasterMethodAspect.statusForceMaster.get() || TransactionSynchronizationManager.isActualTransactionActive();  
			if (!isToMaster) {
				throw new IllegalStateException("this operation must in transaction only.");
			}
			return super.delete(statement);
		}

		@Override
		public int delete(String statement, Object parameter) {
			boolean isToMaster;
			
			isToMaster	= ForceMasterMethodAspect.statusForceMaster.get() || TransactionSynchronizationManager.isActualTransactionActive();  
			if (!isToMaster) {
				throw new IllegalStateException("this operation must in transaction only.");
			}
			return super.delete(statement, parameter);
		}
		
	}
	
    @Aspect
    @Profile("mysql")
    private static class ForceMasterMethodAspect {
    	
    	private static ThreadLocal<Boolean>	statusForceMaster	= ThreadLocal.withInitial(new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return Boolean.FALSE;
			}
    	});
    	
    	@Around("@within(org.springframework.stereotype.Service)")
    	public Object doForceMasterWrappingProcess(ProceedingJoinPoint pjp)
    		throws Throwable {
    		Object			rv;
    		Boolean			asIsForceMaster;
    		MethodSignature	msi;
    		boolean			isForceMasterDS;
    		
    		msi				= (MethodSignature)pjp.getSignature();
    		isForceMasterDS	= msi.getMethod().isAnnotationPresent(ForceMasterDS.class);
    		asIsForceMaster	= statusForceMaster.get();
    		if (isForceMasterDS) {
	    		statusForceMaster.set(Boolean.TRUE);
    		}
    		try {
    			rv		= pjp.proceed(pjp.getArgs());
    		} finally {
    			statusForceMaster.set(asIsForceMaster);
    		}
    		
    		return rv;
    	}
    }    

    //jhjo15, 20191219, SonarQube에서 패스워드변수로 인식하는 문제가 있어 변수명 변경, SonarQube지적 사항처리

	private Logger	logger	= LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ApplicationContext applicationContext;
	
	 /**
     * 기본 Main DB 연결정보
     * @return spring.multi-datasource.readwrite PoolProperties 
     */
    @Bean(name = "mysqlDatabaseConnectionProperties")
    @ConfigurationProperties(prefix = "spring.multi-datasource.readwrite")
    @Profile("mysql")
    public PoolProperties getMainDbConProp(){
        return new PoolProperties();
    }
    
    /**
     * 선택적 Replication DB 연결정보
     * @return spring.multi-datasource.readonly PoolProperties 
     */
    @Bean(name = "mysqlReplicationDatabaseConnectionProperties")
    @ConfigurationProperties(prefix = "spring.multi-datasource.readonly")
    @Profile("mysql")
    public PoolProperties getRepliDbConProp(){
        return new PoolProperties();
    }
    
    /**
     * Main DB 연결정보와 Replication DB 연결정보의 유무에 따라 Main/Replication 자동선택 DataSource를 생성하거나 일반적인 단일 DataSource를 생성합니다.
     * @return MRDatasource.
     */
    @Bean("MRDataSource")
    @Primary
	@Profile("mysql")
	public DataSource getDataSource (
			@Autowired @Qualifier("mysqlDatabaseConnectionProperties") PoolProperties mainDsProp,
			@Autowired(required = false) @Qualifier("mysqlReplicationDatabaseConnectionProperties") PoolProperties repliDsProp) {
		
		String		dbpassword;
		DataSource	mainDataSource;
		DataSource 	replicationDataSource;

		mainDsProp.setPassword(mainDsProp.getPassword());
		mainDataSource	= new org.apache.tomcat.jdbc.pool.DataSource(mainDsProp);

		repliDsProp.setPassword(repliDsProp.getPassword());
		replicationDataSource	= new org.apache.tomcat.jdbc.pool.DataSource(repliDsProp);
		
		this.logger.info("create Main/Replication DataSource");
		return new MRDatasource(mainDataSource, replicationDataSource);
	}

	
	@Value("${mybatis.config-location:classpath:mapper/mybatis-config.xml}")
	private String mybatisConfig;
	
	@Value("${mybatis.mapper-location:classpath:mapper/**/*Mapper.xml}")
	private String mybatisMapper;

	/**
	 * MyBatis SqlSessionFactory 설정.
	 * @return sqlSessionFactory.
	 * @throws Exception 에러처리.
	 */
	@Bean("MRSqlSessionFactory")
    @Primary
	@Profile("mysql")
	public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactory;
		XMLConfigBuilder configBuiler;
		org.apache.ibatis.session.Configuration	mybatisConfiguration;
		TypeAliasRegistry typeAliasRegistry;
		Reflections				reflections;
		ClassLoader				runClassLoader;
		
		sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(dataSource);
		
		configBuiler			= new XMLConfigBuilder(this.applicationContext.getResource(this.mybatisConfig).getInputStream());
		mybatisConfiguration	= configBuiler.parse();
		typeAliasRegistry		= mybatisConfiguration.getTypeAliasRegistry();
		runClassLoader			= Thread.currentThread().getContextClassLoader();
		reflections				= new Reflections(ClasspathHelper.forPackage("com.wzd"), new SubTypesScanner(false));
		reflections.getAllTypes().forEach(type	-> {
			Class<?>	typeClass;
			
			try {
				typeClass	= Class.forName(type, false, runClassLoader);
				if (type.endsWith("Model") || typeClass.isAnnotationPresent(Alias.class)) {
					typeAliasRegistry.registerAlias(typeClass);
				}
			} catch (NoClassDefFoundError ignore) {
				
				
			} catch (ClassNotFoundException ignore) {
			}
		});
		sqlSessionFactory.setConfiguration(mybatisConfiguration);
		sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mybatisMapper));
		
		return sqlSessionFactory;
	}
	
	/**
	 * Main/Replication DataSource 혹은 단일 DataSource를 위한 SqlSessionTemplate 생성.
	 * @return SqlSessionTemplate.
	 * @throws Exception 에러처리.
	 */
	@Bean("MRSqlSessionTemplate")
    @Primary
    @Profile("mysql")
    public SqlSessionTemplate getSessionTemplate(@Autowired DataSource ds, SqlSessionFactory sqlSessionFactory) throws Exception {
    	if (ds instanceof MRDatasource) {
    		this.logger.info("create Main/Replication SqlSessionTemplate");
            return new MRSqlSessionTemplate(sqlSessionFactory);
    	} else {
    		this.logger.info("create regular SqlSessionTemplate");
    		return new SqlSessionTemplate(sqlSessionFactory);
    	}
    }
 
    /**
     * Main/Replication DataSource 혹은 단일 DataSource를 위한 Transaction Manager 생성.
     * @return DataSourceTransactionManager.
     */
    @Bean("MRTransactionManager")
    @Primary
    @Profile("mysql")
    public PlatformTransactionManager getTransactionManager(DataSource ds) {
//    	if (ds instanceof MRDatasource) {
//    		this.logger.info("create Transaction Manager for Main/Replication DataSource");
//        	return new DataSourceTransactionManager(((MRDatasource)ds).getMasterDatasource());
//    	} else {
//    		this.logger.info("create Transaction Manager for SOLID DataSource");
//    		return new DataSourceTransactionManager(ds);
//    	}
		return new DataSourceTransactionManager(ds);
    }
    
    /**
     * Main/Replication DataSource 혹은 단일 DataSource를 위한 Transaction Manager 생성
     * @return ForceMasterMethodAspect 
     */
    @Bean
    @Profile("mysql")
    public ForceMasterMethodAspect getForceMasterMethodAspect() {
    	return new ForceMasterMethodAspect();
    }
}
