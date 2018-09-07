package vidar.server;

import java.sql.ResultSet;

import vidar.server.process_client.*;
import vidar.server.database.*;
import vidar.game.model.*;

public class Account
{
	private SessionHandler session;
	
	public String userName;
	public String userPassword;
	
	public PcInstance activePc = null;
	
	public Account () {
		super () ;
	}
	
	public Account (SessionHandler _session, String account, String password) {
		session = _session;
		userName = account;
		userPassword = password;
	}
	
	/*
	 * 載入帳號
	 */
	public int load () {
		ResultSet rs = null;
		ResultSet online = null;
	
		int loginResult = 0;
		try {
			rs = DatabaseCmds.loadAccount (userName) ;
			
			if (!rs.next () ) {
				System.out.println ("Account:" + userName + " NOT EXISTS.") ;
				loginResult = AccountOperation.ACCOUNT_ALREADY_EXISTS;
				
			} else {
				String pw = rs.getString ("password") ;
				if (userPassword.equals (pw) ) {
					loginResult = AccountOperation.LOGIN_OK;
				} else {
					loginResult = AccountOperation.ACCOUNT_PASSWORD_ERROR;
				}
				
				online  = DatabaseCmds.checkCharacterOnline (userName) ;
				if (online.next () ) {
					loginResult = AccountOperation.ACCOUNT_IN_USE;
				}
			}
		} catch (Exception e) {
			e.printStackTrace () ;
			
		} finally {
			DatabaseUtil.close (rs) ;
			DatabaseUtil.close (online) ;
			
		}
		return loginResult;
	}
	
	public void updateLastLogin () {
		DatabaseCmds.updateAccountLoginTime (userName, session.getIP (), session.getHostName () ) ;
	}
}
