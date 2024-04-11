package com.studio.sa.controllers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.studio.sa.entities.Cart;
import com.studio.sa.entities.Orders;
import com.studio.sa.entities.Users;
import com.studio.sa.repo.OrderRepo;
import com.studio.sa.repo.UsersRepo;
import com.studio.sa.util.JwtUtil;

@RestController
@CrossOrigin(origins = "*")
public class OrderController {
	
	@Autowired
	private OrderRepo orderRepo;
	
	@Autowired
	private UsersRepo usersRepo;
	
	@GetMapping("/orders")
	public ResponseEntity<?> getAllOrders(){
		String authToken = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
		System.out.println("Token+" + authToken);
		JSONObject response = new JSONObject();
		if(authToken != null) {
			authToken = authToken.replace("Bearer ", "");
			String phone = JwtUtil.extractUsername(authToken);
			System.out.println("phone" + phone);
			List<Users> users = usersRepo.findByPhone(phone);
			if(users.size() != 0) {
				Users user = users.get(0);
				
				
				List<Orders> orders = orderRepo.findByUser(user.get_id());
				
				Collections.sort(orders, new Comparator<Orders>() {

					@Override
					public int compare(Orders o1, Orders o2) {
						// TODO Auto-generated method stub
						return o2.getModifiedDate().compareTo(o1.getModifiedDate());
					}
				});
				
				if(orders.size() == 0) {
					
					response.put("data", new JSONObject());
					response.put("error", "");
				}else {
					response.put("data", new JSONArray(orders));
				}
				return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response.toString());	
			}	
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
}
