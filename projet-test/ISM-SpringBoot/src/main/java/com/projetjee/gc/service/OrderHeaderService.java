package com.projetjee.gc.service;

import java.util.List;

import com.projetjee.gc.dto.OrderHeaderDTO;

public interface OrderHeaderService {

	public List<OrderHeaderDTO> getOrderHeaderID();

	public String getNextOrderHeaderNumber();

	public void saveOrderHeader(OrderHeaderDTO theOrderHeader);

	public OrderHeaderDTO getOrderHeaderById(Integer orderHeaderId);

	public List<OrderHeaderDTO> getAllOrders();

	public List<OrderHeaderDTO> getAllOrders(String type);

	public OrderHeaderDTO getOrderHeaderByNumber(String orderHeaderNumber);

	void checkout(String orderHeaderNumber);

}