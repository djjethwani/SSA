package com.studio.sa.controllers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.studio.sa.entities.Cart;
import com.studio.sa.entities.Items;
import com.studio.sa.entities.OrderItems;
import com.studio.sa.entities.Orders;
import com.studio.sa.entities.Users;
import com.studio.sa.repo.CartJpaRepo;
import com.studio.sa.repo.ItemsRepo;
import com.studio.sa.repo.OrderItemsRepo;
import com.studio.sa.repo.OrderRepo;
import com.studio.sa.repo.UsersRepo;
import com.studio.sa.util.JwtUtil;

@RestController
@CrossOrigin(origins = "*")
public class CheckoutController {
	
	@Autowired
	private UsersRepo usersRepo;
	
	@Autowired
	private CartJpaRepo cartJpaRepo;
	
	@Autowired
	private ItemsRepo itemsRepo;
	
	@Autowired
	private OrderRepo orderRepo;
	
	@Autowired
	private OrderItemsRepo orderItemsRepo;
	
	@PostMapping("/checkout/create-stripe-charge")
	public ResponseEntity<?> createOrder(){
		String authToken = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
		System.out.println("Token+" + authToken);
		JSONObject response = new JSONObject();
		if(authToken != null) {
			authToken = authToken.replace("Bearer ", "");
			String email = JwtUtil.extractUsername(authToken);
			System.out.println("email" + email);
			List<Users> users = usersRepo.findByEmail(email);
			if(users.size() != 0) {
				Users user = users.get(0);
				List<Cart> cart = cartJpaRepo.findByUser(user.get_id());
				
				if(cart.size() != 0) {
					
					Cart userCart = cart.get(0);
					
					int toalAmount = 0;
					List<OrderItems> orderItems = new ArrayList<>();
					List<Long> removeIds = new ArrayList<>();
					for(Items item : userCart.getItems()) {
						toalAmount += item.getQuantity() * item.getProduct().getPrice() * item.getProduct().getNumberOfPieces();
						OrderItems newItem = new OrderItems();
						newItem.setProduct(item.getProduct());
						newItem.setQuantity(item.getQuantity());
						System.out.println("+++");
						orderItemsRepo.save(newItem);
						System.out.println("---");
						orderItems.add(newItem);
						removeIds.add(item.get_id());
					}
					
					Orders newOrder = new Orders();
					
					newOrder.setOrderItems(orderItems);
					newOrder.setPaid(false);
					newOrder.setPaymentMethod("Web Site");
					newOrder.setTotal(toalAmount);
					newOrder.setUser(user.get_id());
					
					response.put("data", new JSONObject(orderRepo.save(newOrder)));
					
					userCart.setItems(new ArrayList<>());
					cartJpaRepo.save(userCart);
					
					itemsRepo.deleteAllById(removeIds);
					return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response.toString());
				}else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
				}
				
				
				
			}
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
}
