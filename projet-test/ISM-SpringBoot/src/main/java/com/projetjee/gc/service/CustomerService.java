package com.projetjee.gc.service;


import java.util.List;

import com.projetjee.gc.dto.CustomerDTO;

public interface CustomerService {

		
		public List<CustomerDTO> getCustomer();
		
		public void saveCustomer(CustomerDTO theCustomer);

		public CustomerDTO getCustomer(int theId);

		public void deleteCustomer(int theId);
		
	}
