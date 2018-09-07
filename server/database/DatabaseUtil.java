package vidar.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtil
{
	public static SQLException close (Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			return e;
		}
		return null;
	}
	
	public static SQLException close(PreparedStatement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			return e;
		}
		return null;
	}
	
	public static SQLException close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			return e;
		}
		return null;
	}
	
	public static void close(ResultSet rs, PreparedStatement pstm,
			Connection con) {
		close(rs);
		close(pstm);
		close(con);
	}
}
