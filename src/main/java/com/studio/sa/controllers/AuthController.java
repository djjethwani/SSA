package com.studio.sa.controllers;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.studio.sa.bodies.Login;
import com.studio.sa.bodies.SignUp;
import com.studio.sa.entities.Users;
import com.studio.sa.repo.UsersRepo;
import com.studio.sa.util.JwtUtil;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {
	
	@Autowired
	UsersRepo usersRepo;
	
	@PostMapping("/auth/signUp")
	public ResponseEntity<Object> signUp(@RequestBody SignUp signUp){
		JSONObject response = new JSONObject();
		
		if(usersRepo.findByPhone(signUp.getPhone()).size() == 0) {
			Users newUser = new Users();
			newUser.setPhone(signUp.getPhone());
			newUser.setPassword(signUp.getPassword());
			newUser.setName(signUp.getName());
			newUser.setRole("user");
			Users user = usersRepo.save(newUser);
			
			
			
			String token = JwtUtil.generateToken(signUp.getPhone());
			
			JSONObject userObj = new JSONObject();
			
			
			
			response.put("user", new JSONObject(user));
			response.put("token", token);
		}else {
			response.put("message", "Phone number already present, plesae login with same phone number");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response.toString());
		}
		
		
		
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response.toString());
		
	}
	
	@GetMapping("/auth/me")
	public ResponseEntity<Object> getMe(){
		String authToken = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
		System.out.println("Token+" + authToken);
		JSONObject response = new JSONObject();
		if(authToken != null) {
			authToken = authToken.replace("Bearer ", "");
			String phone = JwtUtil.extractUsername(authToken);
			System.out.println("phone" + phone);
			List<Users> users = usersRepo.findByPhone(phone);
			if(users.size() != 0) {
				response.put("user", new JSONObject(users.get(0)));
				return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(new JSONObject().put("data", response).toString());
			}else {
				return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(new JSONObject().toString());
			}
		}else {
			return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(new JSONObject().toString());
		}
		
	}
	
	@PostMapping("/auth/login")
	public ResponseEntity<Object> login(@RequestBody Login login){
		JSONObject response = new JSONObject();
		List<Users> users =usersRepo.findByPhone(login.getPhone()); 
		if(users.size() == 0 || users.size() > 1 ) {
			response.put("message", "Account does not exists");
		}else {
			if(users.get(0).getPassword().equals(login.getPassword())) {
				String token = JwtUtil.generateToken(users.get(0).getPhone());
				Users user = users.get(0);
				
				response.put("user", new JSONObject(user));
				response.put("token", token);
			}else {
				response.put("message", "Password In Correct");
			}
		}
		
		
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response.toString());
	}
}
