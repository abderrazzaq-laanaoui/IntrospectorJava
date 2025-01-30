package com.projetjee.gc.dao;

import java.util.List;

import com.projetjee.gc.entity.Customer;

public interface CustomerDAO  {

	public List<Customer> getCustomers();

	public void saveCustomer(Customer theCustomer);

	public Customer getCustomer(int theId);
	
	public Customer getCustomer(String email);

	public void deleteCustomer(int theId);

}
