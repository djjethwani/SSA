package com.studio.sa.controllers;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.studio.sa.entities.OrderItems;
import com.studio.sa.entities.Orders;
import com.studio.sa.entities.Users;
import com.studio.sa.repo.OrderRepo;
import com.studio.sa.repo.UsersRepo;
import com.studio.sa.util.JwtUtil;

@RestController
@CrossOrigin(origins = "*")
public class InvoiceController {
	
	private final ResourceLoader resourceLoader;

    public InvoiceController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @Autowired
    OrderRepo orderrpRepo;
    
    @Autowired
    UsersRepo usersRepo;
	
	@GetMapping("/generate-invoice/{orderId}")
	public ResponseEntity<?> generateInvoice(@PathVariable String orderId){
		
		Long id = Long.parseLong(orderId);
		
		long userId = orderrpRepo.getById(id).getUser();
		
		String authToken = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
		System.out.println("Token+" + authToken);
		
		
		boolean access = false;
		if(authToken != null) {
			authToken = authToken.replace("Bearer ", "");
			String phone = JwtUtil.extractUsername(authToken);
			System.out.print("phone" + phone);
			List<Users> users = usersRepo.findByPhone(phone);
			
			if(users != null && users.size() == 1) {
				if(users.get(0).get_id() == userId) {
					access = true;
				}
			}
		}
		
		if(access) {
			File fileObj = null;
			try {
				fileObj = File.createTempFile("invoice",".pdf");
				fileObj.createNewFile();
				PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fileObj.getPath()));
				pdfDoc.addNewPage();
				
				com.itextpdf.layout.Document doc = new com.itextpdf.layout.Document(pdfDoc);
				
				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
				
				PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
				Orders order = orderrpRepo.getById((long)1);
				// Creating a table
			    float [] logoColumnWidths = {3f, 3f};
			    Table logoTable = new Table(logoColumnWidths);
			   
				InputStream is = resourceLoader.getResource("classpath:SSA.jpeg").getInputStream();
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
				
				float [] titleColumnWidths = {2f, 2f};
				Table titleTable = new Table(titleColumnWidths);
				titleTable.addCell(new Cell().add("Garmenta Fashion PVT Ltd.").setFont(font).setBorder(null).setWidth(400f));
				titleTable.addCell(new Cell().add("").setBorder(null));
				Cell titleCell = addTableHeaderCell("Order",true, font);
				titleCell.setWidth(400f);
				titleTable.addCell(titleCell);
				Cell dateCell = addTableHeaderCell("Order Date : " + df.format(order.getCreatedAt()),true, font);
				dateCell.setWidth(250f);
				titleTable.addCell(dateCell);
				
				Cell name = addTableHeaderCell("Name", true, font);
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
				
				FileSystemResource resource = new FileSystemResource(fileObj);
				
				HttpHeaders headers = new HttpHeaders();
				
				 headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileObj.getName());
				 
				 return ResponseEntity
			                .ok()
			                .headers(headers)
			                .contentType(MediaType.APPLICATION_OCTET_STREAM)
			                .body(resource);
				 
			}catch(Exception e) {
				System.out.println("Here"+e.getMessage());
			}
		}
		

		return ResponseEntity.status(HttpStatus.OK).body("OK");
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
