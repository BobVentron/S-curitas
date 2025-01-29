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
	public String getInfoUser() {
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
				String nom_BDD = rs.getString("nom");
				String prenom_BDD = rs.getString("prenom");
				result = result + UUID_BDD+"/"+nom_BDD+"/"+prenom_BDD+"+";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public void setdroitSerrure(String param) {
		Statement stat;
		try {
			stat = this.Connection.createStatement();
			String addData = "UPDATE serrure SET param = "+param+" WHERE id = 1;";
			stat.executeUpdate(addData);
			Connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void insertEmployer(String UUID, String nom, String prenom, String mdp) {
		Statement stat;
		try {
			stat = this.Connection.createStatement();
			String addData = "INSERT INTO Utilisateurs (UUID, nom, prenom, mdp, admin, droitSerrure) VALUES ( "+ UUID + ", ' " + nom + " ', '" +prenom +" ', '"+ mdp +"', FALSE, FALSE);";
			stat.executeUpdate(addData);
			Connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void paramEmployer(String bool, String uuid) {
		Statement stat;
		try {
			stat = this.Connection.createStatement();
			String addData = "";
			if(bool.equals("True")){
				addData = "UPDATE Utilisateurs SET droitSerrure = true WHERE UUID = '" + uuid + "';";
			} else {
			    addData = "UPDATE Utilisateurs SET droitSerrure = false WHERE UUID = '" + uuid + "';";
			}
			
			stat.executeUpdate(addData);
			Connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
