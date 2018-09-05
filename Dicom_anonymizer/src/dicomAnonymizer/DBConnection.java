package dicomAnonymizer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database
 * 
 * @author Krzysztof Szewczyk
 *
 */

public class DBConnection {
	/**
	 * nazwa bazy danych
	 */
	private static final String JDBC = "jdbc:derby:dataBase;create=true";
	/**
	 * tags
	 */
	private int[] tags = DicomTags.tags;
	/**
	 * Connection
	 */
	private Connection conn;

	/**
	 * Constructor creates a connection to the database
	 */
	public DBConnection() {
		try {
			this.conn = DriverManager.getConnection(JDBC);
			if (this.conn != null) {
				System.out.println("Uzyskano po³¹czenie z baz¹ danych");
			}
		} catch (SQLException e) {
			System.out.println("Nie uzyskano po³¹czenia z baz¹ danych");
			e.printStackTrace();
		}
	}

	/**
	 * Create array with columns [ID, NAME (new), oldName, xtag0, xtag1, ... ,
	 * xtagN], N=41. Each cell has size varchar(150).
	 */
	public void createTable() {
		try {
			System.out.println("Rozpoczêto tworzenie tablicy danych");
			String tmp;
			conn.createStatement()
					.execute("Create TABLE Mytable(Index varchar(5000), Name varchar(50), oldName varchar(50))");
			for (int i = 0; i < tags.length; i++) {
				tmp = "x" + tags[i];
				conn.createStatement().executeUpdate("ALTER TABLE Mytable ADD COLUMN " + tmp + " varchar(150)");
			}
		} catch (SQLException e) {
			System.out.println("Tablica ju¿ zosta³a stworzona wczesniej lub b³ad przy tworzeniu");
			e.printStackTrace();
		}
	}

	/**
	 * Save dicom tagss in array
	 * 
	 * @param deletedTags
	 *            All tags from DICOM file
	 * @param index
	 *            id
	 * @param dicomName
	 *            The current file name
	 * @param dicomOldName
	 *            Old file name
	 */
	public void insetIntoTable(String[] deletedTags, String index, String dicomName, String dicomOldName) {
		// Connecting tags to the appropriate form, to one String
		String joined = "'" + index + "', '" + dicomName + ".dcm" + "', '" + dicomOldName + "',";
		for (int i = 0; i < (deletedTags.length - 1); i++) {
			joined = joined + "'" + deletedTags[i] + "',";
		}
		joined = joined + "'" + deletedTags[deletedTags.length - 1] + "'";
		System.out.println(joined);
		// Sending the String to MyTable
		try {
			conn.createStatement().execute("INSERT INTO MyTable Values (" + joined + ")");
		} catch (SQLException e) {
			System.out.println("Nie dzia³a zapisywanie w Mytabel (String joined)");
			e.printStackTrace();
		}
	}

	// /**
	// * Printing with "||" separator.
	// */
	// public void printAll() {
	// Statement statement;
	// try {
	// statement = this.conn.createStatement();
	// ResultSet res = statement.executeQuery("Select * FROM MyTable");
	// while (res.next()) {
	// System.out
	// .print(res.getString("Index") + "||" + res.getString("Name") + "||" +
	// res.getString("oldName"));
	// String tmp;
	// for (int i = 0; i < tags.length; i++) {
	// tmp = "x" + tags[i];
	// System.out.print("||" + res.getString(tmp));
	// }
	// System.out.println();
	// }
	// } catch (SQLException e) {
	// System.out.println("Nie dzia³a printAll");
	// e.printStackTrace();
	// }
	//
	// }

	/**
	 * @return rows number
	 */
	int rowsNumbers() {
		Statement statement;
		try {
			int x = 0;
			statement = this.conn.createStatement();
			ResultSet res = statement.executeQuery("SELECT COUNT(*) AS total FROM Mytable");
			res.next();
			x = res.getInt("total");
			return x;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Number of rows error: return 0");
			return 0;
		}
	}

	/**
	 * Validation of the identifier and file name
	 * 
	 * @param idDicom
	 *            DICOM id
	 * @return Returns the data of the selected dicom file
	 */
	String[] findDicomData(String nameDicom, String index) {
		String tmp;
		String[] dicomData = new String[tags.length + 1];
		Statement statement;
		try {
			statement = this.conn.createStatement();
			ResultSet res = statement.executeQuery("Select * FROM MyTable");
			while (res.next()) {
				// validation of the index and name
				if (index.equals(res.getString("index")) && nameDicom.equals(res.getString("Name"))) {
					dicomData[0] = res.getString("oldName");
					for (int i = 1; i < tags.length; i++) {
						tmp = "x" + tags[i];
						dicomData[i] = res.getString(tmp);
					}
					return dicomData;
				}
			}
			return null;
		} catch (SQLException e) {
			System.out.println("b³¹d w findDicomData");
			return null;
		}
	}

	/**
	 * @return Returns an array with the names of the anonymised files
	 */
	String[] getDicomNames() {
		Statement statement;
		String[] dicomNames = new String[rowsNumbers()];
		int i = 0;
		try {
			statement = this.conn.createStatement();
			ResultSet res = statement.executeQuery("Select * FROM MyTable");
			while (res.next()) {
				System.out.println(res.getString("Name"));
				dicomNames[i] = res.getString("Name");
				i++;
			}
			return dicomNames;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("b³¹d w szukaniu nazwy pliku");
			return null;
		}
	}

}
