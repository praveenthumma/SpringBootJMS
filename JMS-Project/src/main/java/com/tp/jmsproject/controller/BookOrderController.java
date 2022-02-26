package com.tp.jmsproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tp.jmsproject.pojo.Book;
import com.tp.jmsproject.pojo.BookOrder;
import com.tp.jmsproject.pojo.Customer;
import com.tp.jmsproject.service.BookOrderService;
import com.tp.jmsproject.service.WarehouseProcessingService;

@RestController
public class BookOrderController {
	
	@Autowired
	BookOrderService bookOrderService;

	@GetMapping("/order")
	public void order() {
		
		Book book = new Book("1","Designing Data intensive applications");
		Customer customer = new Customer("1","Praveen Thumma");
		BookOrder bookOrder = new BookOrder("1",book, customer);
		bookOrderService.send(bookOrder);
		
		
	}
	
		

}
