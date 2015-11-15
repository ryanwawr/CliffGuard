package edu.umich.robustopt.postgres;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.relationalcloud.tsqlparser.loader.IntegrityConstraintsExistsException;
import com.relationalcloud.tsqlparser.loader.PrimaryKey;
import com.relationalcloud.tsqlparser.loader.Schema;
import com.relationalcloud.tsqlparser.loader.SchemaLoader;
import com.relationalcloud.tsqlparser.loader.SchemaTable;

import edu.umich.robustopt.dblogin.DatabaseLoginConfiguration;
import edu.umich.robustopt.vertica.VerticaConnection;

public class PostgresDatabaseLoginConfiguration extends
		DatabaseLoginConfiguration {

	String PostgresServerName;
	private String PostgresUsername;
	private String PostgresPassword;
	
	public PostgresDatabaseLoginConfiguration() {
		PostgresServerName = "";
	}

	public PostgresDatabaseLoginConfiguration(boolean empty, String DBalias,
			String DBhost, Integer DBport, String DBname, String DBuser,
			String DBpasswd, String PostgresUsername, String PostgresPassword, String PostgresServerName) {
		super(empty, DBalias, DBhost, DBport, DBname, DBuser, DBpasswd);

		this.PostgresServerName = PostgresServerName;
		this.PostgresUsername = PostgresUsername;
		this.PostgresPassword = PostgresPassword;
	}

	public String getPostgresServerName() {
		return PostgresServerName;
	}
	
	public void setPostgresUsername(String PostgresUsername) {
		this.PostgresUsername = PostgresUsername;
	}
	
	public String getPostgresUsername() {
		return PostgresUsername;
	}
	
	public void setPostgresPassword(String PostgresPassword) {
		this.PostgresPassword = PostgresPassword;
	}
	
	public String getPostgresPassword() {
		return PostgresPassword;
	}

	@Override
	public Connection createConnection() {
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");

			//System.out.print(getDBhost() + "\n");
			//System.out.print(getDBport() + "\n");
			//System.out.print(getDBname() + "\n");
			//System.out.print(getDBuser() + "\n");
			//System.out.print(getDBpasswd() + "\n");
			
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
					//String.format("jdbc:postgresql://%s:%s/%s", 
						//	getDBhost(), getDBport(), getDBname()), getDBuser(), getDBpasswd());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return conn;
	}

	@Override
	//TODO: Probably rewrite this
	public Map<String, Schema> getSchemaMap() throws SQLException {
		
		Connection conn = createConnection();
		
		// list schemas
		List<String> schemas = new ArrayList<String>();
		Statement stmt = conn.createStatement();
		ResultSet res = stmt.executeQuery("select nspname from pg_namespace");
		
		while (res.next()){
			schemas.add(res.getString(1));
			System.out.print(res.getString(1) + "\n");
		}
		res.close();
		stmt.close();
		
		HashMap<String, Schema> schemaMap = new HashMap<String, Schema>();
		for (String s : schemas) {
			//Schema sch = SchemaLoader.loadSchemaFromDB(conn, s);
			Schema sch = loadSchemaFromDB(conn, s);
			schemaMap.put(s, sch);
		}
		
		System.out.print("Get map 4\n");
		
		return schemaMap;
	}
	
	  private Schema loadSchemaFromDB(Connection conn,String schemaName) throws SQLException
	  {
		  String driver=conn.getMetaData().getDriverName();
		  if(driver.contains("PostgreSQL"))
		  {
			  System.out.print("Schema name = " + schemaName + "\n");
			  Statement stmt = conn.createStatement();
			  Statement stmt2 = conn.createStatement();
			  Statement stmt3 = conn.createStatement();
			  String columnPreparedSql;
			  //String schemaSql = "select * from information_schema.SCHEMATA where CATALOG_NAME='"+schemaName + "' AND SCHEMA_NAME='public'";
			  String schemaSql = "select * from information_schema.SCHEMATA where CATALOG_NAME='postgres' AND SCHEMA_NAME='public'";
			  String tablesSql = "select TABLE_NAME from information_schema.TABLES where TABLE_CATALOG='"+schemaName+"' AND TABLE_SCHEMA='public'";
			  columnPreparedSql= "select COLUMN_NAME,DATA_TYPE from information_schema.COLUMNS where TABLE_CATALOG='"+schemaName+"' AND TABLE_SCHEMA='public' AND TABLE_NAME=?";
			  ResultSet schemaRes = stmt.executeQuery(schemaSql);
			  schemaRes.next();
			  String collationName=null;
			  System.out.print("Loading schema\n");
			  Schema schema = new Schema(schemaRes.getString("CATALOG_NAME"),
					  schemaName,
					  schemaRes.getString("DEFAULT_CHARACTER_SET_NAME"),
					  collationName,
					  schemaRes.getString("SQL_PATH"),
					  driver);
			  ResultSet tableList = stmt2.executeQuery(tablesSql);;
			  PreparedStatement ps = conn.prepareStatement(columnPreparedSql);
			  String tableName = null;
			  while(tableList.next()){
				  tableName = tableList.getString("table_name");
				  ps.setString(1, tableName);
				  ResultSet columnList = ps.executeQuery();
				  SchemaTable tt = new SchemaTable(schemaName, tableName);
				  String columnName = null;
				  String columnType = null;
				  while(columnList.next()){
					  columnName = columnList.getString("COLUMN_NAME");
					  columnType = columnList.getString("DATA_TYPE");       
					  tt.addColumn(columnName,columnType);        
				  }
				  schema.addTable(tt);


				  // ---- load primary keys ----
				  String primarykeyquery =" SELECT c.TABLE_NAME,COLUMN_NAME,c.CONSTRAINT_NAME "+
				  " FROM  information_schema.KEY_COLUMN_USAGE k, information_schema.TABLE_CONSTRAINTS c" +
				  " WHERE k.TABLE_NAME = c.TABLE_NAME" +
				  " AND k.TABLE_SCHEMA = c.TABLE_SCHEMA" +
				  " AND k.CONSTRAINT_NAME = c.CONSTRAINT_NAME" +
				  " AND CONSTRAINT_TYPE = 'PRIMARY KEY'" +     // Maybe changing this to an or of primary keys and unique keys would work
				  " AND k.TABLE_CATALOG = '" + schemaName +"' "+
				  " AND k.TABLE_SCHEMA = 'public' "+ 
				  " AND c.TABLE_CATALOG = '" + schemaName +"' "+
				  " AND c.TABLE_SCHEMA = 'public' "+ 
				  " AND c.TABLE_NAME = '" + tableName + "'" +
				  " ORDER BY COLUMN_NAME;"; 


				  //System.out.println(primarykeyquery);
				  ResultSet constraintslist = stmt3.executeQuery(primarykeyquery);       

				  Vector<String> colinkey = new Vector<String>();
				  String consname = null;
				  while(constraintslist.next()){
					  consname = constraintslist.getString("CONSTRAINT_NAME");
					  colinkey.add(constraintslist.getString("COLUMN_NAME"));
				  }
				  if(consname!= null && colinkey.size()>0)
					  try {
						  tt.addConstraint(new PrimaryKey(consname,(Vector<String>)colinkey.clone()));
					  } catch (IntegrityConstraintsExistsException e) {
						  // TODO Auto-generated catch block
						  e.printStackTrace();
					  }

			  }
			  return schema;
		  }
		  throw new SQLException("Driver not supported");
	  }
}
