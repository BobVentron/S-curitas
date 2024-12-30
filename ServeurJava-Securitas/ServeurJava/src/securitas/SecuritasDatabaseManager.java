package securitas;

import java.sql.*;

public class SecuritasDatabaseManager {
	private Connection Connection;
	
	public SecuritasDatabaseManager (String driverClassName,String databaseURL, String username, String password) throws ClassNotFoundException, SQLException{
		
		 try {Class.forName(driverClassName);}catch (ClassNotFoundException e) {e.printStackTrace();}
		 
		 Connection ConnectionServ = null;
		 try {
			 ConnectionServ = DriverManager.getConnection(databaseURL, username, password);
			 this.setConnection(ConnectionServ);
		 }
		 catch (SQLException e) {e.printStackTrace();}
	}
	
	public Boolean checkmdp(String mdp) throws UserPasDroitException, MDPErronerException {
		String req = "SELECT * FROM Utilisateurs";
		Statement stat;
		boolean resultMDP = false;

		try {
			stat = this.Connection.createStatement();

			ResultSet rs = null;
			try {rs = stat.executeQuery(req);}catch (SQLException e) {e.printStackTrace();}
			
			while (rs.next())
			{
				String mdp_bdd = rs.getString("mdp");
				if (mdp_bdd.equals(mdp)) {
					resultMDP = true;
					String droitSerrure = rs.getString("droitSerrure");
					if ( droitSerrure.equals("true")) {
						return true;
					}else {
						throw new UserPasDroitException(rs.getString("prenom") + " " + rs.getString("nom"));
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!resultMDP) {
			throw new MDPErronerException(mdp);
		}
		return false;
	}
	
	public boolean checkUUID(String UUID) {
		String req = "SELECT * FROM Utilisateurs";
		Statement stat;
		try {
			stat = this.Connection.createStatement();

			ResultSet rs = null;
			try {rs = stat.executeQuery(req);}catch (SQLException e) {e.printStackTrace();}
			
			while (rs.next())
			{
				String UUID_BDD = rs.getString("UUID");
				if (UUID_BDD.equals(UUID)) {
					return true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public String getUUID(String mdp) {
		String result = "";
		String req = "SELECT * FROM Utilisateurs";
		Statement stat;
		try {
			stat = this.Connection.createStatement();

			ResultSet rs = null;
			try {rs = stat.executeQuery(req);}catch (SQLException e) {e.printStackTrace();}
			
			while (rs.next())
			{
				String UUID_BDD = rs.getString("UUID");
				String mdp_BDD = rs.getString("mdp");
				if (mdp_BDD.equals(mdp)) {
					result = UUID_BDD;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public String getautorisations(String UUID) {
		String result = "";
		String req = "SELECT * FROM Utilisateurs";
		Statement stat;
		try {
			stat = this.Connection.createStatement();

			ResultSet rs = null;
			try {rs = stat.executeQuery(req);}catch (SQLException e) {e.printStackTrace();}
			
			while (rs.next())
			{
				String droitSerrure_BDD = rs.getString("droitSerrure");
				String UUID_BDD = rs.getString("UUID");
				if (UUID_BDD.equals(UUID)) {
					result = droitSerrure_BDD;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public String checkAdmin(String UUID) {
		String result = "";
		String req = "SELECT * FROM Utilisateurs";
		Statement stat;
		try {
			stat = this.Connection.createStatement();

			ResultSet rs = null;
			try {rs = stat.executeQuery(req);}catch (SQLException e) {e.printStackTrace();}
			
			while (rs.next())
			{
				String admin_BDD = rs.getString("admin");
				String UUID_BDD = rs.getString("UUID");
				if (UUID_BDD.equals(UUID)) {
					result = admin_BDD;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public Connection getConnection() {
		return Connection;
	}

	public void setConnection(Connection connection) {
		Connection = connection;
	}
	
	public void finalize() {
		try {this.Connection.close(); } catch (SQLException e) {e.printStackTrace();}
	}
}
