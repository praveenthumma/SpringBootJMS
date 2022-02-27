package com.tp.jms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tp.jms.pojo.Book;
import com.tp.jms.pojo.BookOrder;
import com.tp.jms.pojo.Customer;
import com.tp.jms.service.BookOrderService;


@RestController
public class BookOrderController {
	
	@Autowired
	BookOrderService bookOrderService;
	@GetMapping("/order")
	public void order() {
		
		Book book = new Book("1","Designing Data intensive applications");
		Customer customer = new Customer("1","Praveen Thumma");
		BookOrder bookOrder = new BookOrder("1",book, customer);
		bookOrderService.send(bookOrder,"Kharadi Store", "NEW");
		bookOrderService.send(bookOrder,"Kharadi Store", "DELETE");
		
		
	}
	
		

}
