package org.scholarlydata.clodg.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class HyperSQLClodg {

	private Connection conn;
	
	public HyperSQLClodg() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			
			this.conn = DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");
			
			System.out.println("Database connected.");

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void update(String expression) throws SQLException {

        Statement st = null;

        st = conn.createStatement();    // statements

        int i = st.executeUpdate(expression);    // run the query

        if (i == -1) {
            System.out.println("db error : " + expression);
        }

        st.close();
    }
	
	public synchronized void query(String expression) throws SQLException {

        Statement st = null;
        ResultSet rs = null;

        st = conn.createStatement();         // statement objects can be reused with

        // repeated calls to execute but we
        // choose to make a new one each time
        rs = st.executeQuery(expression);    // run the query

        // do something with the result set.
        dump(rs);
        st.close();    // NOTE!! if you close a statement the associated ResultSet is

        // closed too
        // so you should copy the contents to some other object.
        // the result set is invalidated also  if you recycle an Statement
        // and try to execute some other query before the result set has been
        // completely examined.
    }
	
	public static void dump(ResultSet rs) throws SQLException {

        // the order of the rows in a cursor
        // are implementation dependent unless you use the SQL ORDER statement
        ResultSetMetaData meta   = rs.getMetaData();
        int               colmax = meta.getColumnCount();
        int               i;
        Object            o = null;

        // the result set is a cursor into the data.  You can only
        // point to one row at a time
        // assume we are pointing to BEFORE the first row
        // rs.next() points to next row and returns true
        // or false if there is no next row, which breaks the loop
        for (; rs.next(); ) {
            for (i = 0; i < colmax; ++i) {
                o = rs.getObject(i + 1);    // Is SQL the first column is indexed

                // with 1 not 0
                System.out.print(o.toString() + " ");
            }

            System.out.println(" ");
        }
    } 
	
	
	public void shutdown() throws SQLException {

        Statement st = conn.createStatement();

        // db writes out to files and performs clean shuts down
        // otherwise there will be an unclean shutdown
        // when program ends
        st.execute("SHUTDOWN");
        conn.close();    // if there are no other open connection
    }
	
	public static void main(String[] args) {
		HyperSQLClodg hyperSQLClodg = new HyperSQLClodg();
		String expression = "CREATE TABLE sample_table ( id INTEGER IDENTITY, str_col VARCHAR(256), num_col INTEGER)";
		try {
			hyperSQLClodg.update(expression);
			
			hyperSQLClodg.update(
	                "INSERT INTO sample_table(str_col,num_col) VALUES('Ford', 100)");
			hyperSQLClodg.update(
	                "INSERT INTO sample_table(str_col,num_col) VALUES('Toyota', 200)");
			hyperSQLClodg.update(
	                "INSERT INTO sample_table(str_col,num_col) VALUES('Honda', 300)");
			hyperSQLClodg.update(
	                "INSERT INTO sample_table(str_col,num_col) VALUES('GM', 400)");
			
			
			hyperSQLClodg.query("SELECT * FROM sample_table WHERE num_col < 250");

            // at end of program
			hyperSQLClodg.shutdown();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
