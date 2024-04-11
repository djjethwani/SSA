package com.studio.sa.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.studio.sa.bodies.AddToCart;
import com.studio.sa.bodies.RemoveFromCart;
import com.studio.sa.entities.Cart;
import com.studio.sa.entities.Items;
import com.studio.sa.entities.Products;
import com.studio.sa.entities.Users;
import com.studio.sa.repo.CartJpaRepo;
import com.studio.sa.repo.ItemsRepo;
import com.studio.sa.repo.ProductsJpaRepo;
import com.studio.sa.repo.UsersRepo;
import com.studio.sa.util.JwtUtil;

@RestController
@CrossOrigin(origins = "*")
public class CartController {
	
	@Autowired
	private UsersRepo usersRepo;
	
	@Autowired
	private CartJpaRepo cartJpaRepo;
	
	@Autowired
	private ProductsJpaRepo productsJpaRepo;
	
	@Autowired
	private ItemsRepo itemsRepo;
	
	@GetMapping("/cart")
	public ResponseEntity<Object> getCart(){
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
				List<Cart> cart = cartJpaRepo.findByUser(user.get_id());
				
				if(cart.size() == 0) {
					
					response.put("data", new JSONObject());
					response.put("error", "");
				}else {
					response.put("data", new JSONObject(cart.get(0)));
				}
				
				return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response.toString());
				
			}
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
	
	@PostMapping("/cart")
	public ResponseEntity<?> addToCart(@RequestBody AddToCart addToCart){
		String authToken = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
		System.out.println("Token+" + authToken);
		JSONObject response = new JSONObject();
		if(authToken != null) {
			authToken = authToken.replace("Bearer ", "");
			String phone = JwtUtil.extractUsername(authToken);
			System.out.println("email" + phone);
			List<Users> users = usersRepo.findByPhone(phone);
			if(users.size() != 0) {
				Users user = users.get(0);
				List<Cart> cart = cartJpaRepo.findByUser(user.get_id());
				
				if(cart.size() == 0) {
					System.out.println("KKKK");
					List<Products> products =  productsJpaRepo.findById(addToCart.getProductId()).stream().collect(Collectors.toList());
					Products product = products.get(0);
					
					if(product != null) {
						Cart newcart = new Cart();
						newcart.setUser(user.get_id());
						List<Products> iproducts = new ArrayList<>();
						iproducts.add(product);
						Items items = new Items();
						items.setQuantity(addToCart.getQuantity());
						items.setProduct(product);
						
						items = itemsRepo.save(items);
						
						List<Items> ii = new ArrayList<>();
						ii.add(items);
						newcart.setItems(ii);
						
						cartJpaRepo.save(newcart);
						
						response.put("data", items);
						
						return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response.toString());
					}
				}else {
					Cart userCart = cart.get(0);
					
					boolean found = false;
					long pid = addToCart.getProductId();
					for(Items item : userCart.getItems()) {
						if(item.getProduct().get_id() == pid) {
							found = true;
							item.setQuantity(item.getQuantity() + addToCart.getQuantity());
							itemsRepo.save(item);
							response.put("data", item);
							break;
						}
					}
					
					if(!found) {
						Items newItem = new Items();
						newItem.setQuantity(addToCart.getQuantity());
						List<Products> products =  productsJpaRepo.findById(addToCart.getProductId()).stream().collect(Collectors.toList());
						Products product = products.get(0);
						newItem.setProduct(product);
						itemsRepo.save(newItem);
						List<Items> existing = userCart.getItems();
						existing.add(newItem);
						userCart.setItems(existing);
						cartJpaRepo.save(userCart);
						response.put("data", newItem);
					}
					return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response.toString());
				}
				
			}else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			
			
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
	
	@PutMapping("/cart")
	public ResponseEntity<?> updateCart(@RequestBody AddToCart addToCart){
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
				List<Cart> cart = cartJpaRepo.findByUser(user.get_id());
				if(cart.size() != 0) {
					Cart userCart = cart.get(0);
					long pid = addToCart.getProductId();
					for(Items item : userCart.getItems()) {
						if(item.getProduct().get_id() == pid) {
							item.setQuantity(addToCart.getQuantity());
							itemsRepo.save(item);
							response.put("data", item);
							break;
						}
					}
					return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response.toString());
				}
			}else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			
			
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
	
	@DeleteMapping("/cart")
	public ResponseEntity<?> removeFromCart(@RequestBody RemoveFromCart removeFromCart){
		String authToken = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
		System.out.println("Token+" + authToken);
		JSONObject response = new JSONObject();
		if(authToken != null) {
			authToken = authToken.replace("Bearer ", "");
			String phone = JwtUtil.extractUsername(authToken);
			System.out.println("phone" + phone);
			List<Users> users = usersRepo.findByEmail(phone);
			if(users.size() != 0) {
				Users user = users.get(0);
				List<Cart> cart = cartJpaRepo.findByUser(user.get_id());
				if(cart.size() != 0) {
					Cart userCart = cart.get(0);
					long pid = removeFromCart.getProductId();
					Items delOne = null;
					List<Items> userItems = userCart.getItems();
					for(Items item : userItems) {
						if(item.getProduct().get_id() == pid) {
							delOne = item;
							break;
						}
					}
					if(delOne != null) {
						userItems.remove(delOne);
						userCart.setItems(userItems);
						cartJpaRepo.save(userCart);
						itemsRepo.delete(delOne);	
					}
					
					response.put("data", cartJpaRepo.getById(userCart.get_id()).getItems());
					
					return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response.toString());
				}
			}else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}	
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
}
