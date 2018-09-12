package edu.ucsb.cs56.pconrad;

import static spark.Spark.port;
import com.google.firebase.*;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.util.Scanner;
import com.google.firebase.database.*;

import edu.ucsb.cs56.pconrad.User;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import static spark.Spark.get;
import static spark.Spark.post;
import org.apache.log4j.Logger;
/**
 * Hello world!
 *
 */

public class FireBaseGains {
	private String returnStatement;
	public static final String CLASSNAME="FireBaseGains";
	public static final Logger log = Logger.getLogger(CLASSNAME);
    public static void main(String[] args) {
	port(getHerokuAssignedPort());
	FireBaseGains gains = new FireBaseGains();
	gains.initializeFireBase();
	final FirebaseDatabase database = FirebaseDatabase.getInstance();
	Map<String, User> users = new HashMap<>();
	users.put("somelameemail@gmail", new User("somelameemail@gmail.com", "Lameo", "McLameo", "password123"));
	users.put("somecoolemail@gmail", new User("somecoolemail@gmail.com", "Coolio", "McCoolio", "123password"));
	DatabaseReference ref = database.getReference("server/users");
	ref.setValueAsync(users);
/*
	ref.addValueEventListener(new ValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot dataSnapshot) {
			User someUser = dataSnapshot.getValue(User.class);
			System.out.println(someUser);
		}
		@Override
		public void onCancelled(DatabaseError databaseError) {
			System.out.println("The read failed: " + databaseError.getCode());
		}
	});*/
	Map map = new HashMap();
	map.put("name", "Sam");
	get("/", (rq, rs) -> new ModelAndView(map, "signupform.mustache"), new MustacheTemplateEngine());
	post("/check", (rq,rs) -> gains.signUp(rq));
	}
	
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    private void initializeFireBase() {
		try {	
			FileInputStream serviceAccount = new FileInputStream("target/classes/firebase-credentials.json");
			FirebaseOptions options = new FirebaseOptions.Builder()
    				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
    				.setDatabaseUrl("https://gauchogains-f67f0.firebaseio.com")
    				.build();
			FirebaseApp.initializeApp(options);
		}	
		catch (FileNotFoundException e) {
			spark.Spark.get("/",(req,res) -> "FileNotFound");
		}
		catch (IOException e) {
			spark.Spark.get("/",(req,res) -> "serviceAccount invalid");
		}	
	}
    private String signUp(spark.Request rq) {
	String email = rq.queryParams("email");
	String firstName = rq.queryParams("firstname");
	String lastName = rq.queryParams("lastname");
	String password = rq.queryParams("password");
	
	//String checkValid = checkValidUser(email, firstName, lastName, password);
	returnStatement = checkValidUser(email, firstName, lastName, password);
	checkDupeUser(email);

	if (returnStatement.equals("valid")) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
        	Map<String, Object> newUser = new HashMap<>();
		String cutEmail = email.substring(0, email.indexOf(".")); 
        
		newUser.put(cutEmail, new User(email, firstName, lastName, password));

    		DatabaseReference ref = database.getReference("server/users");
        	ref.updateChildrenAsync(newUser);
		return "Sign Up Successful";
	} else {
		return returnStatement;
	}
    }
    private String checkValidUser(String email, String firstName, String lastName, String password) {
	    if(email.length() == 0 || firstName.length() == 0 || lastName.length() == 0 || password.length() == 0)
                    return "Please fill in all forms";
	    if(!email.contains("@") || email.length() < 5 || email.charAt(email.length()-4) != '.' || 
			    email.charAt(email.indexOf("@")+1) == '.')
                    return "Invalid Email";
	    if(password.length() < 6)
		    return "Please enter a password with length greater than 6";
	    
	    return "valid";
    }
    private void checkDupeUser(String email) {
	    final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("server/users");
	    String cutEmail = email.substring(0, email.indexOf("."));
	    System.out.println(cutEmail);
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
			if (((dataSnapshot.getKey()).toString()).equals(cutEmail)) {
				//System.out.println("hello");
				returnStatement = "This email is already in use";
			}
                }
		@Override
  public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

  @Override
  public void onChildRemoved(DataSnapshot dataSnapshot) {}

  @Override
  public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}


                @Override
                public void onCancelled(DatabaseError databaseError) {
		}
	    });
    }
}
