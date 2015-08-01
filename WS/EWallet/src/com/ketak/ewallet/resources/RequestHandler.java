package com.ketak.ewallet.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import com.ketak.ewallet.model.MyConnection;

@Path("/Customers")
public class RequestHandler {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	@POST
    @Produces(MediaType.TEXT_HTML)
	@Path("newCustomer")
	public void newCustomer(@FormParam("Password") String password,
			  @FormParam("Email") String email,
			  @FormParam("Mobile") String mobile,
			  @FormParam("ReferralCode") String referralCode,
			  @Context HttpServletResponse servletResponse) throws IOException {
		
		MyConnection con1 = new MyConnection();

		String EmailCode = getUniqueCode(6);
		sendVerificationEmail(email,EmailCode);
		
		String MobileCode = getUniqueCode(4);
		sendVerificationSMS(mobile,MobileCode);

		
		int regId = con1.RegisterMe(email,password,mobile,MobileCode,email,EmailCode,referralCode);
		
		System.out.println("Customer ID :"+regId);
		
		if(regId > 0)
			servletResponse.sendRedirect("../../SuccessFul.html");
		else
			servletResponse.sendRedirect("../../Failed.html");
	  
	}
	
	@POST
    @Produces(MediaType.TEXT_HTML)
	@Path("verifyContact")
	public void verifyContact(@FormParam("Contact") String contact,
			  @FormParam("VerificationCode") String verificationcode,
			  @Context HttpServletResponse servletResponse) throws IOException {
		
		MyConnection con1 = new MyConnection();

		String code = con1.GetVerificationCode(contact);
		
		int Status_Code = -99;
		
		if (code.equalsIgnoreCase(verificationcode))
		{
			Status_Code = con1.SetVerifiedContact(contact);
			System.out.println("Status Code SetVerifiedContact:"+Status_Code);

			if(Status_Code > 0)
			{
				System.out.println("sendRedirect 3 :"+Status_Code);

				servletResponse.sendRedirect("../../Successful.html");
			}
			else
			{
				System.out.println("sendRedirect 2 :"+Status_Code);
				servletResponse.sendRedirect("../../Failed.html");
			}
			
		}
		else if(code.equalsIgnoreCase("ALREADY_VERIFIED"))
		{
			System.out.println("sendRedirect 1 :"+Status_Code);
		    servletResponse.sendRedirect("../../Successful.html");
		}
		

	  }
	
	@POST
    @Produces(MediaType.TEXT_HTML)
	@Path("loginCustomer")
	public void loginCustomer(@FormParam("username") String username,
			  @FormParam("password") String password,
			  @Context HttpServletResponse servletResponse) throws IOException {
		
		MyConnection con1 = new MyConnection();

		if(username != null && password != null)
		{
			int REG_ID = con1.LoginMeIn(username,password);
			System.out.println("REG_ID LoginMeIn:"+REG_ID);
	
			if(REG_ID > 0)
			{
				System.out.println("sendRedirect 3");
	
				servletResponse.sendRedirect("../../Successful.html");
			}
			else
			{
				System.out.println("sendRedirect 2");
				servletResponse.sendRedirect("../../Failed.html");
			}
		}
		else
		{
			System.out.println("Username or password null..");		
			
		}
		
	  }
		
	private boolean sendVerificationSMS(String mobile, String mobileCode) {
		
		boolean IsSuccessful = false;
		
		System.out.println("SMS to be sent to : "+mobile);
		String MSG = "Your+EWallet+Verification+Code%3A"+mobileCode;
		String url = "https://rest.nexmo.com/sms/json?api_key=a68cba61&api_secret=1d516d89&from=EWALLET&to=918600938885&text="+MSG;
		String USER_AGENT = "Mozilla/5.0";

		try
		{
			URL obj = new URL(url);
			//HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	 
			// optional default is GET
			//con.setRequestMethod("GET");
	 
			//add request header
			//con.setRequestProperty("User-Agent", USER_AGENT);
	 
			//int responseCode = con.getResponseCode();
			
			System.out.println("\nSending 'GET' request to URL : " + url);
			//System.out.println("Response Code : " + responseCode);
			IsSuccessful = true;
		}
		catch(Exception ex)
		{
			
		}
		
		return IsSuccessful;
	}

	private boolean sendVerificationEmail(String email, String emailCode) {
		System.out.println("Email to be sent to : "+email);
		boolean IsSuccessful = false;
		String MSG = "Your EWallet Verification Code:"+emailCode;
		
		final String username = "ketak.bhatt@gmail.com";
																																								final String password = "999244666666";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("from-email@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("to-email@gmail.com"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler,"
				+ "\n\n\t"+MSG+"\n\n"+"Thanks,\nEWalletTeam."); 
			
			Transport.send(message);
 
			System.out.println("Done");
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return true;
		
	}

	private String getUniqueCode(int len) {
		StringBuilder sb = new StringBuilder();
		
		// change this logic to generate random code
		for(int i = 0 ;i<len;i++)
		{
			sb.append(i);
		}

		return sb.toString();
	}
	
}
