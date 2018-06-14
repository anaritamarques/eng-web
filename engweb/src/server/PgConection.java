package server;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import server.utils.AppConfig;
import server.utils.SQLTools;
import object.MTable;


public class PgConection {
	
	public static Connection connectionDB;
	
	public class FldVal {
		private String field;
		private Object value;

		public FldVal(String field, Object value) {
			setField(field);
			setValue(value);
		}

		public String getField() {
			return this.field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public Object getValue() {
			return this.value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
	}

	public static PgConection getDBCon() throws Exception {
		PgConection con = new PgConection(AppConfig.DBSERVER, AppConfig.DBNAME);
		if ((con != null) && (con.connect(AppConfig.DBUSER, AppConfig.DBPASS))) {
			return con;
		}
		throw new Exception("Liga��o � base de dados inv�lida!");
	}
	public static <T> List<T> getDbList(String sql, String classe) throws Exception {
		return getDbList(sql, null, classe);
	}

	public static <T> List<T> getDbList(String sql, Class<T> classe) throws Exception {
		return getDbList(sql, null, classe);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getDbList(String sql, Object[] params, String classe) throws Exception {
		return (List<T>) getDbList(sql, params, Class.forName(classe));
	}

	public static <T> List<T> getDbList(String sql, Object[] params, Class<T> classe) throws Exception {
		List<T> lst = null;
		PgConection con = getDBCon();
		lst = con.Select(sql, params, classe);
		con.destroy();
		return lst;
	}

	public static <T> MTable<String, T> getDbListKeys(String sql, Class<T> classe) throws Exception {
		return getDbListKeys(sql, null, classe);
	}

	@SuppressWarnings("unchecked")
	public static <T> MTable<String, T> getDbListKeys(String sql, Object[] params, String classe) throws Exception {
		return (MTable<String, T>) getDbListKeys(sql, params, Class.forName(classe));
	}

	public static <T> MTable<String, T> getDbListKeys(String sql, Object[] params, Class<T> classe) throws Exception {
		MTable<String, T> lst = null;
		PgConection con = getDBCon();
		lst = con.SelectKeys(sql, params, classe);
		con.destroy();
		return lst;
	}

	private String host = "";
	private String dbName = "";
	private Connection conn = null;

	public PgConection() {
		this(AppConfig.DBSERVER, AppConfig.DBNAME);
	}
	
	public PgConection(String host, String db) {
		this.host = host;
		this.dbName = db;
	}

	public boolean connect(String user, String pass) throws Exception {
		Class.forName("org.postgresql.Driver").newInstance();
		String connString = "jdbc:postgresql://" + this.host + "/" + this.dbName;
		this.conn = DriverManager.getConnection(connString, user, pass);
		
		System.out.println("Connected: " + this.conn);
		return this.conn != null;
	}

	public Connection getConn() {
		return this.conn;
	}
	
	public void destroy() {
		try {
			if (this.conn != null) {
				if (!this.conn.isClosed()) {
					this.conn.close();
				}
				this.conn = null;
			}
		} catch (SQLException ex) {
			this.conn = null;
		}
	}

	private void setParams(PreparedStatement pstmt, Object[] params) throws SQLException {
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				int pType = pstmt.getParameterMetaData().getParameterType(i + 1);
				if (params[i] == null) {
					pstmt.setNull(i + 1, pType);
				} else if ((params[i] instanceof Object[])) {
					String tName = pstmt.getParameterMetaData().getParameterTypeName(i + 1);
					if (tName.startsWith("_")) {
						tName = tName.substring(1);
					}
					pstmt.setArray(i + 1, this.conn.createArrayOf(tName,
							(Object[]) params[i]));
				} else if ((params[i] instanceof byte[])) {
					pstmt.setBytes(i + 1, (byte[]) params[i]);
				} else {
					pstmt.setObject(i + 1, params[i], pType);
				}
			}
		}
	}

	public void beginTransaction() throws SQLException {
		this.conn.setAutoCommit(false);
	}

	public void endTransaction(Boolean commit) throws SQLException {
		if (commit.booleanValue()) {
			this.conn.commit();
		} else {
			this.conn.rollback();
		}
		this.conn.setAutoCommit(true);
	}

	public ArrayList<Object> getObjectsSQL(String campos, String tabela, String condicao) throws SQLException {
		return getObjectsSQL(campos, tabela, condicao, null, null);
	}

	public ArrayList<Object> getObjectsSQL(String campos, String tabela, String condicao, String groupBy, String fieldOrder) throws SQLException {
		return getObjectsSQL("select "
				+ campos + " from " + tabela
				+ (condicao == null ? "" : new StringBuilder(" where ").append(condicao).toString())
				+ (groupBy == null ? "" : new StringBuilder(" group by ").append(groupBy).toString())
				+ (fieldOrder == null ? "" : new StringBuilder(" order by ").append(fieldOrder).toString()));
	}

	public ArrayList<Object> getObjectsSQL(String sql) throws SQLException {
		return getObjectsSQL(sql, null);
	}

	public ArrayList<Object> getObjectsSQL(String sql, Object[] params) throws SQLException {
		ArrayList<Object> result = null;
		ResultSet rs = null;
		try {
			rs = getDataSQL(sql, params);
			if ((rs != null) && (rs.next())) {
				result = new ArrayList<Object>();
				int nc = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= nc; i++) {
					result.add(rs.getObject(i));
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					throw e;
				}
				rs = null;
			}
		}
		return result;
	}

	public String getJsonProp(String sql, Object[] params) {
		String res = "";

		ArrayList<FldVal> rec = getRecord(sql, params);
		for (FldVal f : rec) {
			String sVal = f.getValue() != null ? f.getValue().toString() : "";
			sVal.replace("\"", "\\\"");
			res = res + ",{campo:\"" + f.getValue() + "\",valor:\"" + sVal + "\"}";
		}
		res = "[" + (res.length() > 0 ? res.substring(1) : "") + "]";
		return res;
	}

	public ArrayList<FldVal> getRecord(String sql, Object[] params) {
		ArrayList<FldVal> res = new ArrayList<FldVal>();
		ResultSet rsRes = null;
		try {
			rsRes = getDataSQL(sql, params);
			if ((rsRes != null) && (rsRes.next())) {
				ResultSetMetaData resMetaData = rsRes.getMetaData();
				int nCols = resMetaData.getColumnCount();
				for (int kCol = 1; kCol <= nCols; kCol++) {
					res.add(new FldVal(resMetaData.getColumnName(kCol), rsRes.getObject(kCol)));
				}
			}
		} catch (SQLException localSQLException) {
		} finally {
			if (rsRes != null) {
				try {
					rsRes.close();
				} catch (SQLException localSQLException2) {
				}
				rsRes = null;
			}
		}
		return res;
	}

	public String getJsonSQL(String sql, Object[] params) {
		String res = "";
		ResultSet rsRes = null;
		try {
			rsRes = getDataSQL(sql, params);
			if (rsRes != null) {
				ResultSetMetaData resMetaData = rsRes.getMetaData();
				int nCols = resMetaData.getColumnCount();
				while (rsRes.next()) {
					String cols = "";
					for (int kCol = 1; kCol <= nCols; kCol++) {
						int typ = resMetaData.getColumnType(kCol);
						String cmp = resMetaData.getColumnName(kCol);
						int tipo = 0;
						switch (typ) {
						case -5:
						case 3:
						case 4:
						case 5:
						case 6:
						case 8:
							tipo = 0;
							break;
						case -16:
						case -15:
						case -9:
						case -1:
						case 1:
						case 12:
							tipo = 1;
							break;
						case 91:
							tipo = 2;
						}
						String sVal = "";
						if (tipo == 1) {
							sVal = rsRes.getObject(kCol) != null ? rsRes.getString(kCol) : "";
							sVal = sVal.replaceAll("'", "\\'");
							sVal = "'" + sVal + "'";
							sVal = sVal.replaceAll("\n", "\\\\n");
						} else if (tipo == 0) {
							sVal = rsRes.getObject(kCol) != null ? rsRes.getObject(kCol).toString() : "0";
						} else if (tipo == 2) {
							sVal = "'" + (rsRes.getObject(kCol) != null ? rsRes.getObject(kCol).toString() : "") + "'";
						}
						cols = cols + "," + cmp + ":" + sVal;
					}
					res = res + ",{"
							+ (cols.length() > 0 ? cols.substring(1) : "")
							+ "}";
				}
				res = res.length() > 0 ? res.substring(1) : "";
			}
		} catch (SQLException sqle) {
			res = "";
		} finally {
			if (rsRes != null) {
				try {
					rsRes.close();
				} catch (SQLException localSQLException2) {
				}
				rsRes = null;
			}
		}
		res = "[" + res + "]";
		return res;
	}

	public ResultSet getDataSQL(String sql, Object[] params) throws SQLException {
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		setParams(pstmt, params);
		return pstmt.executeQuery();
	}

	public int executeSQL(String sql) throws SQLException {
		return executeSQLParams(sql, null);
	}

	public int executeSQLParams(String sql, Object[] params) throws SQLException {
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		setParams(pstmt, params);
		return pstmt.executeUpdate();
	}

	public String getPKeyFromTable(String schema, String tName)
			throws SQLException {
		return getPKeyFromTable(schema + "." + tName);
	}

	public String getPKeyFromTable(String tName) throws SQLException {
		String sql = "SELECT pg_attribute.attname, format_type(pg_attribute.atttypid, pg_attribute.atttypmod) FROM pg_index, pg_class, pg_attribute WHERE pg_class.oid = ?::regclass AND indrelid = pg_class.oid AND pg_attribute.attrelid = pg_class.oid AND pg_attribute.attnum = any(pg_index.indkey) AND indisprimary";
		ArrayList<Object> res = getObjectsSQL(sql, new Object[] { tName });
		return res != null ? (String) res.get(0) : null;
	}

	public ArrayList<Object> getFKeyFromTable(String tName_fk, String tName_pk) throws SQLException {
		String sql = "SELECT fk.columns AS fk, fk.foreign_columns AS pk FROM (SELECT conrelid::regclass as tabela,a.attname as columns,confrelid::regclass as foreign_table,af.attname as foreign_columns FROM pg_attribute AS af,pg_attribute AS a, (SELECT conrelid, confrelid,conkey[i] AS conkey,confkey[i] as confkey FROM (SELECT conrelid,confrelid, conkey,confkey,generate_series(1, array_upper(conkey, 1)) AS i FROM pg_constraint WHERE contype = 'f') AS ss) AS ss2 WHERE af.attnum = confkey AND af.attrelid = confrelid AND a.attnum = conkey AND a.attrelid = conrelid) fk WHERE fk.tabela::text=? AND fk.foreign_table::text=?";
		return getObjectsSQL(sql, new Object[] { tName_fk, tName_pk });
	}

	public Integer getNewPKeyValue(String tName) throws SQLException {
		ArrayList<String> t = splitTableName(tName);
		return getNewPKeyValue(t.get(0), t.get(1));
	}

	public Integer getNewPKeyValue(String schema, String tName) throws SQLException {
		String pkey = getPKeyFromTable(schema, tName);
		String sql = "((SELECT 1 AS pkey WHERE NOT EXISTS(SELECT " + pkey + " FROM " + schema + "." + tName + " WHERE " + pkey + "=1))";
		sql = sql + " UNION ";
		sql = sql + "(SELECT l." + pkey + " + 1 AS pkey" + " FROM " + schema + "." + tName + " l LEFT OUTER JOIN " + schema + "." + tName
				+ " r ON l." + pkey + "+1 = r." + pkey + " WHERE r." + pkey + " IS NULL)) t";
		sql = "SELECT pkey FROM " + sql + " ORDER BY pkey LIMIT 1";
		ArrayList<Object> res = getObjectsSQL(sql);
		return Integer.valueOf(res != null ? ((Integer) res.get(0)).intValue() : 1);
	}

	private ArrayList<String> splitTableName(String tName) {
		ArrayList<String> res = new ArrayList<String>();
		res.add("public");
		res.add(tName);
		if (tName.indexOf(".") != -1) {
			String[] strs = tName.split("\\.", 2);
			res.set(0, strs[0]);
			res.set(1, strs[1]);
		}
		return res;
	}

	public <T> List<T> Select(String sql, String className) throws Exception {
		return Select(sql, null, className);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> Select(String sql, Object[] params, String className)
			throws Exception {
		return (List<T>) Select(sql, params, Class.forName(className));
	}


	@SuppressWarnings("unchecked")
	public <T> List<T> Select(String sql, Object[] params, Class<T> classe) throws Exception {
		List<T> list = new ArrayList<T>();
		ResultSet rsRes = null;
		try {
			rsRes = getDataSQL(sql, params);
			if (rsRes != null) {
				ResultSetMetaData resMetaData = rsRes.getMetaData();
				int nCols = resMetaData.getColumnCount();
				Constructor<?> c = null;
				while (rsRes.next()) {
					Object[] initargs = new Object[nCols];
					for (int kCol = 1; kCol <= nCols; kCol++) {
						initargs[(kCol - 1)] = rsRes.getObject(kCol);
					}
					if (c == null) {
						
						Class<?>[] cls = new Class[nCols];
						for (int i = 0; i < initargs.length; i++) {
							cls[i] = (initargs[i] != null ? initargs[i].getClass() : null);
						}
						c = SQLTools.getConstructorByArgTypes(classe, cls);
					}
					list.add((T) c.newInstance(initargs));
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (rsRes != null) {
				try {
					rsRes.close();
				} catch (Exception localSQLException) {
					localSQLException.printStackTrace();
				} 
				rsRes = null;
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T> MTable<String, T> SelectKeys(String sql, Object[] params, Class<T> classe) throws Exception {
		MTable<String, T> list = new MTable<String, T>();

		ResultSet rsRes = null;
		try {
			rsRes = getDataSQL(sql, params);
			if (rsRes != null) {
				ResultSetMetaData resMetaData = rsRes.getMetaData();
				int nCols = resMetaData.getColumnCount();

				Constructor<?> c = null;
				while (rsRes.next()) {
					Object[] initargs = new Object[nCols];
					for (int kCol = 1; kCol <= nCols; kCol++) {
						initargs[(kCol - 1)] = rsRes.getObject(kCol);
					}
					if (c == null) {
						@SuppressWarnings("rawtypes")
						Class[] cls = new Class[nCols];
						for (int i = 0; i < initargs.length; i++) {
							cls[i] = (initargs[i] != null ? initargs[i].getClass() : null);
						}
						c = SQLTools.getConstructorByArgTypes(classe, cls);
					}
					list.put(initargs[0].toString(), (T) c.newInstance(initargs));
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (rsRes != null) {
				try {
					rsRes.close();
				} catch (SQLException localSQLException) {
				}
				rsRes = null;
			}
		}
		return list;
	}
}
