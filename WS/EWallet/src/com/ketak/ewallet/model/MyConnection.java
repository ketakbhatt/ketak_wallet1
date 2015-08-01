package com.ketak.ewallet.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.sql.DataSource;
import javax.ws.rs.POST;

import com.sun.accessibility.internal.resources.accessibility;

/**
 *
 * @author KETAK_BHATT
 */
public class MyConnection {

	Statement st;
	Connection con;
	ResultSet rs = null;
	DataSource ds;
	CallableStatement cs;
	
	/**
	 * Method to create DB Connection
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Connection createConnection() throws Exception {
	    Connection con = null;
	    try {
			System.out.println(Constants.dbClass);
	        Class.forName(Constants.dbClass);
			System.out.println(Constants.dbUrl);
			System.out.println(Constants.dbUser);
			System.out.println(Constants.dbPwd);
	        con = DriverManager.getConnection(Constants.dbUrl, Constants.dbUser, Constants.dbPwd);
			System.out.println("Connection is fine..");
	    } catch (Exception e) {
	    	System.out.println("Exception while creating "+new Object(){}.getClass().getEnclosingMethod().getName()+":"+e.getMessage());
	    	throw e;
	    }
	   
	    return con;	    
	}
	
	public int RegisterMe(String Username,String Pass, 
			String Mobile,String Mobile_VerificationCode,
			String Email,String Email_VerificationCode,
			String ReferralCode)
	{
		int regID = -99;
		
		try {
			con = createConnection();
			//getDBUSERByUserId is a stored procedure
			if(con == null)
			{
				System.out.println("Connection is null..");
			}
			else
			{
			
				String RegisterMe = "{call EWallet.RegisterMe(?,?,?,?,?,?,?,?,?,?,?)}";
				cs = con.prepareCall(RegisterMe);
				if(cs == null)
				{
					System.out.println("CS is null..");
				}
				else
				{
					System.out.println("CS is not null..");
					cs.setString("USERNAME1", Username);
					System.out.println(Username);
					cs.setString("PASSWORD1", Pass);
					System.out.println(Pass);
					cs.setString("EMAIL1", Email);
					System.out.println(Email);
					cs.setString("EMAIL_VERIFICATIONCODE1", Email_VerificationCode);
					System.out.println(Email_VerificationCode);
					cs.setString("MOBILE1",Mobile);
					System.out.println(Mobile);
					cs.setString("MOBILE_VERIFICATIONCODE1", Mobile_VerificationCode);
					System.out.println(Mobile_VerificationCode);
					cs.setString("REFERRALCODE1", ReferralCode);
					cs.registerOutParameter("GENERATED_REGISTRATION_ID", java.sql.Types.INTEGER);
					cs.registerOutParameter("STATUS_CODE", java.sql.Types.INTEGER);
					 
					System.out.println("Everything ready to hit DB..");
					
					// execute getDBUSERByUserId store procedure
					cs.executeUpdate();
					System.out.println("Execution Successful..");
					 
					regID = cs.getInt("GENERATED_REGISTRATION_ID");
					System.out.println("GENERATED_REGISTRATION_ID:"+cs.getInt("GENERATED_REGISTRATION_ID"));
					int Statuscode = cs.getInt("STATUS_CODE");;
					System.out.println("Statuscode:"+Statuscode);
					
				}
				
				con.close();
			}

		} catch (Exception e) {
	    	System.out.println("Exception in "+new Object(){}.getClass().getEnclosingMethod().getName()+":"+e.getMessage());
	    }
		
        return regID;
	}
	
	public int LoginMeIn(String EmailORMobile, String Pass) {
		int regId = -99;
		
		System.out.println("Username:"+EmailORMobile);
		System.out.println("password:"+Pass);
		try
		{
			con = createConnection();
			if(con == null)
			{
				System.out.println("Connection is null..");
			}
			else
			{
				System.out.println("Connection is not null..");
				String LogMeIn = "{call My.LogMeIn(?,?,?,?)}";
				
				cs = null;
				cs = con.prepareCall(LogMeIn);

				if(cs == null)
				{
					System.out.println("CS is null..");
				}
				else
				{
					System.out.println("CS is not null..");
					cs.setString("USERNAME1", EmailORMobile);
					System.out.println(EmailORMobile);
					cs.setString("PASSWORD1", Pass);
					System.out.println(Pass);
					cs.registerOutParameter("MY_REG_ID", java.sql.Types.INTEGER);
					cs.registerOutParameter("STATUS_CODE", java.sql.Types.INTEGER);
					 
					System.out.println("Everything ready to hit DB..");
					
					// execute getDBUSERByUserId store procedure
					cs.executeUpdate();
					System.out.println("Execution Successful..");
					 
					regId = cs.getInt("MY_REG_ID");;
					System.out.println("MY_REG_ID for login cust:"+regId);
					int Statuscode = cs.getInt("STATUS_CODE");;
					System.out.println("Statuscode for login cust:"+Statuscode);
					
				}
				
				con.close();
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception Occured in "+new Object(){}.getClass().getEnclosingMethod().getName()+": "+ex.toString());
			ex.printStackTrace();
		}

		return regId;
	}

	public String GetVerificationCode(String contact) {
		String VerificationCode = null;
		try
		{
			con = createConnection();
			
			if(con == null)
			{
				System.out.println("Connection is null..");
			}
			else
			{
				System.out.println("Connection is not null..");
				String GetVerificationCode = "{call Ewallet.GetVerificationCode(?,?,?)}";
				cs = con.prepareCall(GetVerificationCode);
				if(cs == null)
				{
					System.out.println("CS is null..");
				}
				else
				{
					System.out.println("CS is not null..");
					cs.setString("CONTACT1", contact);
					System.out.println(contact);
					cs.registerOutParameter("VERIFICATIONDETAILS1", java.sql.Types.VARCHAR,200);
					cs.registerOutParameter("STATUS_CODE", java.sql.Types.INTEGER);
					 
					System.out.println("Everything ready to hit DB..");
					
					cs.executeUpdate();
					System.out.println("Execution Successful..");
					 
					VerificationCode = cs.getString("VERIFICATIONDETAILS1");
					System.out.println("VERIFICATIONDETAILS1:"+cs.getString("VERIFICATIONDETAILS1"));
					int Statuscode = cs.getInt("STATUS_CODE");;
					System.out.println("Statuscode for GetVerificationCode:"+Statuscode);
					
					if(Statuscode == 0)
					{
						VerificationCode = "ALREADY_VERIFIED";
					}
					
				}
				
				con.close();
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception Occured in "+new Object(){}.getClass().getEnclosingMethod().getName()+": "+ex.toString());
			ex.printStackTrace();
		}
		finally
		{
			
		}
		return VerificationCode;
	}

	public int SetVerifiedContact(String contact) {
		int Statuscode = -99;
		try
		{
			con = createConnection();
			if(con == null)
			{
				System.out.println("Connection is null..");
			}
			else
			{
				System.out.println("Connection is not null..");
				String SetVerifiedContactDetails = "{call EWallet.SetVerifiedContactDetails(?,?)}";
				cs = con.prepareCall(SetVerifiedContactDetails);
				if(cs == null)
				{
					System.out.println("CS is null..");
				}
				else
				{
					System.out.println("CS is not null..");
					cs.setString("CONTACT1", contact);
					System.out.println(contact);
					cs.registerOutParameter("STATUS_CODE", java.sql.Types.INTEGER);
					 
					System.out.println("Everything ready to hit DB..");
					
					// execute getDBUSERByUserId store procedure
					cs.executeUpdate();
					System.out.println("Execution Successful..");
					 
					Statuscode = cs.getInt("STATUS_CODE");;
					System.out.println("Statuscode for Set verified contact:"+Statuscode);
					
				}
				
				con.close();
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception Occured in "+new Object(){}.getClass().getEnclosingMethod().getName()+": "+ex.toString());
			ex.printStackTrace();
		}
		finally
		{
			
		}
		return Statuscode;
	}

	public boolean CreditMyWallet(int MyRegID, double Amount)
	{
	 boolean IsSuccessful = false;
		
		System.out.println("regid:"+MyRegID);
		System.out.println("amount:"+Amount);
		try
		{
			con = createConnection();
			if(con == null)
			{
				System.out.println("Connection is null..");
			}
			else
			{
				System.out.println("Connection is not null..");
				String CreditWallet = "{call EWallet.CreditWallet(?,?,?)}";
				
				cs = null;
				cs = con.prepareCall(CreditWallet);

				if(cs == null)
				{
					System.out.println("CS is null..");
				}
				else
				{
					cs.setInt("MYREGID1", MyRegID);
					System.out.println(MyRegID);
					cs.setDouble("AMOUNT1", Amount);
					System.out.println(Amount);
					cs.registerOutParameter("STATUS_CODE", java.sql.Types.INTEGER);
					 
					System.out.println("Everything ready to hit DB..");
					
					// execute getDBUSERByUserId store procedure
					cs.executeUpdate();
					System.out.println("Execution Successful..");
					 
					int Statuscode = cs.getInt("STATUS_CODE");;
					System.out.println("Statuscode for login cust:"+Statuscode);
					
					if(Statuscode > 0 )
					{
						IsSuccessful = true;
					}
				}
				
				con.close();
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception Occured in "+new Object(){}.getClass().getEnclosingMethod().getName()+": "+ex.toString());
			ex.printStackTrace();
		}

		return IsSuccessful;
		
	}

	public int CreateNewPocket(int MyRegId,double MaxLimit,String Refilltype,String PocketPIN)
	{
		int PocketId = -99;
		try
		{
			con = createConnection();
			if(con == null)
			{
				System.out.println("Connection is null..");
			}
			else
			{
				System.out.println("Connection is not null..");
				String CreatePocket = "{call My.CreatePocket(?,?,?,?,?,?)}";
				
				cs = null;
				cs = con.prepareCall(CreatePocket);

				if(cs == null)
				{
					System.out.println("CS is null..");
				}
				else
				{
					cs.setInt("MYREGID1", MyRegId);
					System.out.println(MyRegId);
					cs.setDouble("MAXLIMIT1", MaxLimit);
					System.out.println(MaxLimit);
					cs.setString("REFILLTYPE1", Refilltype);
					System.out.println(MaxLimit);
					cs.setString("POCKETPIN1", PocketPIN);
					System.out.println(MaxLimit);
					cs.registerOutParameter("GENERATED_POCKET_ID", java.sql.Types.INTEGER);
					cs.registerOutParameter("STATUS_CODE", java.sql.Types.INTEGER);
					 
					System.out.println("Everything ready to hit DB..");
					
					// execute getDBUSERByUserId store procedure
					cs.executeUpdate();
					System.out.println("Execution Successful..");
					 
					PocketId= cs.getInt("GENERATED_POCKET_ID");;
					System.out.println("GENERATED_POCKET_ID:"+PocketId);
					int Statuscode = cs.getInt("STATUS_CODE");;
					System.out.println("Statuscode for login cust:"+Statuscode);
					
				}
				
				con.close();
			
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception Occured in "+new Object(){}.getClass().getEnclosingMethod().getName()+": "+ex.toString());
			ex.printStackTrace();
		}

		return PocketId;
	}
	
	public boolean SpendFromPocket(int MyPocketId, double Amount,int MerchantId,String PIN)
	{
		boolean IsSuccessful = false;
		
		System.out.println("MyPocketId:"+MyPocketId);
		System.out.println("amount:"+Amount);
		try
		{
			con = createConnection();
			if(con == null)
			{
				System.out.println("Connection is null..");
			}
			else
			{
				System.out.println("Connection is not null..");
				String UsePocket = "{call EWallet.UsePocket(?,?,?,?,?)}";
				
				cs = null;
				cs = con.prepareCall(UsePocket);

				if(cs == null)
				{
					System.out.println("CS is null..");
				}
				else
				{
					cs.setInt("MYPOCKETID1", MyPocketId);
					System.out.println(MyPocketId);
					cs.setInt("MERCHANTID1", MerchantId);
					System.out.println(MerchantId);
					cs.setString("PIN1", PIN);
					System.out.println(PIN);
					cs.setDouble("AMOUNT1", Amount);
					System.out.println(Amount);
					cs.registerOutParameter("STATUS_CODE", java.sql.Types.INTEGER);
					 
					System.out.println("Everything ready to hit DB..");
					
					// execute getDBUSERByUserId store procedure
					cs.executeUpdate();
					System.out.println("Execution Successful..");
					 
					int Statuscode = cs.getInt("STATUS_CODE");;
					System.out.println("Statuscode:"+Statuscode);
					
					if(Statuscode > 0 )
					{
						IsSuccessful = true;
					}
				}
				
				con.close();
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception Occured in "+new Object(){}.getClass().getEnclosingMethod().getName()+": "+ex.toString());
			ex.printStackTrace();
		}

		return IsSuccessful;
		
	}
	
	public boolean TransferToFriend(int MyRegId,String EmailORMobileOfFriend,double Amount)
	{
		boolean IsSuccessful = false;

		try
		{
			con = createConnection();
			if(con == null)
			{
				System.out.println("Connection is null..");
			}
			else
			{
				System.out.println("Connection is not null..");
				String TransferToFriend = "{call EWallet.TransferToFriend(?,?,?,?,?)}";
				
				cs = null;
				cs = con.prepareCall(TransferToFriend);

				if(cs == null)
				{
					System.out.println("CS is null..");
				}
				else
				{
					cs.setInt("MYREGID1", MyRegId);
					System.out.println(MyRegId);
					cs.setString("EMAILORMOBILEOFFRIEND1", EmailORMobileOfFriend);
					System.out.println(EmailORMobileOfFriend);
					cs.setDouble("AMOUNT1", Amount);
					System.out.println(Amount);
					cs.registerOutParameter("STATUS_CODE", java.sql.Types.INTEGER);
					 
					System.out.println("Everything ready to hit DB..");
					
					// execute getDBUSERByUserId store procedure
					cs.executeUpdate();
					System.out.println("Execution Successful..");
					 
					int Statuscode = cs.getInt("STATUS_CODE");;
					System.out.println("Statuscode:"+Statuscode);
					
					if(Statuscode > 0 )
					{
						IsSuccessful = true;
					}
				}
				
				con.close();
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception Occured in "+new Object(){}.getClass().getEnclosingMethod().getName()+": "+ex.toString());
			ex.printStackTrace();
		}

		return IsSuccessful;
	
	}

	public int CreateMerchantProfile(int MyRegId,String Fname,String Lname,String ProfileType,String BusinessNum,String Mobile_VerificationCode)
	{
		int merId = -99;
		
		try {
			con = createConnection();
			//getDBUSERByUserId is a stored procedure
			if(con == null)
			{
				System.out.println("Connection is null..");
			}
			else
			{
			
				String CreateMerchantAccount = "{call EWallet.CreateMerchantAccount(?,?,?,?,?,?,?,?,?,?,?)}";
				cs = con.prepareCall(CreateMerchantAccount);
				if(cs == null)
				{
					System.out.println("CS is null..");
				}
				else
				{
					System.out.println("CS is not null..");
					cs.setInt("MYREGID1", MyRegId);
					cs.setString("FNAME1", Fname);
					cs.setString("LNAME1", Lname);
					cs.setString("BUSINESSNUM1",BusinessNum);
					cs.setString("MOBILE_VERIFICATIONCODE1", Mobile_VerificationCode);
					cs.registerOutParameter("GENERATED_MERCHANT_ID", java.sql.Types.INTEGER);
					cs.registerOutParameter("STATUS_CODE", java.sql.Types.INTEGER);
					 
					System.out.println("Everything ready to hit DB..");
					
					// execute getDBUSERByUserId store procedure
					cs.executeUpdate();
					System.out.println("Execution Successful..");
					 
					merId = cs.getInt("GENERATED_MERCHANT_ID");
					System.out.println("GENERATED_MERCHANT_ID:"+cs.getInt("GENERATED_MERCHANT_ID"));
					int Statuscode = cs.getInt("STATUS_CODE");;
					System.out.println("Statuscode:"+Statuscode);
					
				}
				
				con.close();
			}

		} catch (Exception e) {
	    	System.out.println("Exception in "+new Object(){}.getClass().getEnclosingMethod().getName()+":"+e.getMessage());
	    }
		
        return merId;
	}

	public int CreateDriverProfile(int MyMerchantId,String DLNum)
	{
		int DPId = -99;
		
		try {
			con = createConnection();
			//getDBUSERByUserId is a stored procedure
			if(con == null)
			{
				System.out.println("Connection is null..");
			}
			else
			{
			
				String CreateDriverProfile = "{call EWallet.CreateDriverProfile(?,?,?,?)}";
				cs = con.prepareCall(CreateDriverProfile);
				if(cs == null)
				{
					System.out.println("CS is null..");
				}
				else
				{
					System.out.println("CS is not null..");
					cs.setInt("MERCHANTID1", MyMerchantId);
					cs.setString("DRIVINGLICENCENUMBER1", DLNum);
					cs.registerOutParameter("GENERATED_DRIVER_ID", java.sql.Types.INTEGER);
					cs.registerOutParameter("STATUS_CODE", java.sql.Types.INTEGER);
					 
					System.out.println("Everything ready to hit DB..");
					
					// execute getDBUSERByUserId store procedure
					cs.executeUpdate();
					System.out.println("Execution Successful..");
					 
					DPId = cs.getInt("GENERATED_DRIVER_ID");
					System.out.println("GENERATED_DRIVER_ID:"+DPId);
					int Statuscode = cs.getInt("STATUS_CODE");;
					System.out.println("Statuscode:"+Statuscode);
					
				}
				
				con.close();
			}

		} catch (Exception e) {
	    	System.out.println("Exception in "+new Object(){}.getClass().getEnclosingMethod().getName()+":"+e.getMessage());
	    }
		
        return DPId;
	}

	public int CreateVehicleProfile(String RegistrationNum,String Name,String Type,String Make,String Desc,int MyDPId,boolean IsOwned)
	{
		int vehicleId = -99;
		
		try {
			con = createConnection();
			//getDBUSERByUserId is a stored procedure
			if(con == null)
			{
				System.out.println("Connection is null..");
			}
			else
			{
			
				String CreateVehicleProfile = "{call EWallet.CreateVehicleProfile(?,?,?,?,?,?,?,?,?,?,?)}";
				cs = con.prepareCall(CreateVehicleProfile);
				if(cs == null)
				{
					System.out.println("CS is null..");
				}
				else
				{
					System.out.println("CS is not null..");
					cs.setString("REGNUM1", RegistrationNum);
					cs.setString("NAME1", Name);
					cs.setString("TYPE1", Type);
					cs.setString("MAKE1",Make);
					cs.setString("DESCRIPTION1", Desc);
					cs.setInt("MYDPID1", MyDPId);
					cs.setBoolean("ISOWNED1", IsOwned);
					cs.registerOutParameter("GENERATED_VEHICLE_ID", java.sql.Types.INTEGER);
					cs.registerOutParameter("STATUS_CODE", java.sql.Types.INTEGER);
					 
					System.out.println("Everything ready to hit DB..");
					
					// execute getDBUSERByUserId store procedure
					cs.executeUpdate();
					System.out.println("Execution Successful..");
					 
					vehicleId = cs.getInt("GENERATED_VEHICLE_ID");
					System.out.println("GENERATED_VEHICLE_ID:"+vehicleId);
					int Statuscode = cs.getInt("STATUS_CODE");;
					System.out.println("Statuscode:"+Statuscode);
					
				}
				
				con.close();
			}

		} catch (Exception e) {
	    	System.out.println("Exception in "+new Object(){}.getClass().getEnclosingMethod().getName()+":"+e.getMessage());
	    }
		
        return vehicleId;
	}

}


