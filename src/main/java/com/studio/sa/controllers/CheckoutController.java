package com.studio.sa.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
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
    private FirebaseStorageService firebaseStorageService;
	
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
			String phone = JwtUtil.extractUsername(authToken);
			System.out.println("phone" + phone);
			List<Users> users = usersRepo.findByPhone(phone);
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
					Orders order = orderRepo.save(newOrder);
					generateInvoice(order);
					response.put("data", new JSONObject(order));
					
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
	
	private void generateInvoice(Orders order) {
		try {
			File fileObj = null;
			fileObj = File.createTempFile("Order_"+order.get_id(),".pdf");
			fileObj.createNewFile();
			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fileObj.getPath()));
			pdfDoc.addNewPage();
			
			com.itextpdf.layout.Document doc = new com.itextpdf.layout.Document(pdfDoc);
			
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			
			PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
			
			// Creating a table
		    float [] logoColumnWidths = {3f, 3f};
		    Table logoTable = new Table(logoColumnWidths);
		   
			InputStream is = new FileInputStream(ResourceUtils.getFile("classpath:SSA.jpeg"));
			byte[] bytes = is.readAllBytes();
			is.close();
			
			ImageData data = ImageDataFactory.create(bytes); 
			// Creating an Image object 
			Image img = new Image(data); 
			Cell logCell = new Cell();   
			logCell.add(img.setAutoScale(true));
			logCell.setHeight(50f);
			logCell.setWidth(400f);
			logCell.setBorder(null);
			logoTable.addCell(logCell);
			
			doc.add(logoTable);
			Users user = usersRepo.getById(order.getUser());
			float [] titleColumnWidths = {2f, 2f};
			Table titleTable = new Table(titleColumnWidths);
			titleTable.addCell(new Cell().add("Garmenta Fashion PVT Ltd.").setFont(font).setBorder(null).setWidth(400f));
			titleTable.addCell(new Cell().add("").setBorder(null));
			Cell titleCell = addTableHeaderCell("Order : "+order.get_id(),true, font);
			titleCell.setWidth(400f);
			titleTable.addCell(titleCell);
			Cell dateCell = addTableHeaderCell("Order Date : " + df.format(order.getCreatedAt()),true, font);
			dateCell.setWidth(250f);
			titleTable.addCell(dateCell);
			
			Cell name = addTableHeaderCell("Name : " + user.getName(), true, font);
			titleTable.addCell(name);
			
			Cell address = addTableHeaderCell("Address", true, font);
			titleTable.addCell(address);
			
			Cell orderItems = addTableHeaderCell("Order Items", true, font);
			titleTable.addCell(orderItems);
			
			doc.add(titleTable);

			
			Table table = new Table(5);
			
			
			table.addCell(new Cell().add("Sr No").setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)));
			table.addCell(new Cell().add("Description").setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)));
			table.addCell(new Cell().add("Price").setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)));
			table.addCell(new Cell().add("Quantity").setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)));
			table.addCell(new Cell().add("Amount").setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)));
			
			
			
			List<OrderItems> orderI = order.getOrderItems();
			
			for(int i=0; i<orderI.size(); i++) {
				
				table.addCell(new Cell().add(""+(i+1)).setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)));
				
				OrderItems item = orderI.get(i);
				
				table.addCell(new Cell().add(item.getProduct().getName()).setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)));
				table.addCell(new Cell().add(""+item.getProduct().getPrice()).setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)));
				table.addCell(new Cell().add(""+(item.getQuantity()*item.getProduct().getNumberOfPieces())).setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)));
				table.addCell(new Cell().add(""+(item.getQuantity()*item.getProduct().getPrice()*item.getProduct().getNumberOfPieces())).setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)));
				
			}
			
	        
	        doc.add(table);

	        doc.close();
	        
	        String downloadUrl = firebaseStorageService.uploadPDF(fileObj);
	        
	        order.setDownloadUrl(downloadUrl);
	        
	        orderRepo.save(order);
		}catch(Exception e) {
			System.out.println("Exception Occured:" + e.getMessage());
		}
	}
	
	public static Cell addTableHeaderCell(String content, boolean noBorder, PdfFont font) {
		Cell cell = new Cell();
		cell.setFont(font);
		Border border = new SolidBorder(1);
		if (noBorder) {
			cell.setBorder(null);
		}else {
			cell.setBorder(border);
			cell.setBorderBottom(border);
			//cell.setBorderBottom(new Border());
		}
		if (content != null) {
			cell.add(content);
		}else {
			cell.add("N/A");
		} 

		return cell;
	}
	
	public static Cell addTableCell(String content, boolean noBorder) {
		Cell cell = new Cell();
		if (noBorder) {
			cell.setBorder(null);
		}else {
			cell.setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1));
		}
		if (content != null && content.length() > 0) {
			cell.add(content);
		}else {
			cell.add("N/A");
		} 

		return cell;
	}
}
