package com.projetjee.gc.service.mapper;

import com.projetjee.gc.dto.CustomerDTO;
import com.projetjee.gc.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper extends Mapper<CustomerDTO, Customer> {

	@Override
	public CustomerDTO mapToDTO(Customer c) {
		CustomerDTO dto = new CustomerDTO();
		dto.setFirstName(c.getFirstName());
		dto.setLastName(c.getLastName());
		dto.setFullName(c.getFirstName() + " " + c.getLastName());
		dto.setEmail(c.getEmail());
		dto.setId(c.getId());
		return dto;
	}

	@Override
	public Customer mapToEntity(CustomerDTO theCustomer) {
		Customer customer = new Customer();
		customer.setEmail(theCustomer.getEmail());
		customer.setFirstName(theCustomer.getFirstName());
		customer.setLastName(theCustomer.getLastName());
		customer.setId(theCustomer.getId());
		return customer;
	}

	
	
	
	///but in product mapper 
	// if =0 make null for sku and size and
	// Product entity returns int
	// DTO would return integer
	
	
	
	// TO DO
	/// make on more DTO that contains order,product,customer 
	// add to cart shoud
}
