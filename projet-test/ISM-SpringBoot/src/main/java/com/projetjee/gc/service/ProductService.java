package com.projetjee.gc.service;

import java.util.List;

import com.projetjee.gc.dto.ProductDTO;

public interface ProductService {
	
	public List<ProductDTO> getProducts();

	public void saveProduct(ProductDTO theProduct);

	public ProductDTO getProducts(int theId);

	public void deleteProduct(int theId);

}
