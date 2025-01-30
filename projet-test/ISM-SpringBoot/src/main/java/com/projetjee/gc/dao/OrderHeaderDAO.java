package com.projetjee.gc.dao;


import java.util.List;

import com.projetjee.gc.entity.OrderHeader;

public interface OrderHeaderDAO {

	List<OrderHeader> getOrderHeader();

	public OrderHeader getLastOrderHeader();

	public void saveOrderHeader(OrderHeader theOrderHeader);

	public OrderHeader getOrderHeaderByNumber(String orderHeaderId);
	
	public OrderHeader getOrderHeaderById(Integer orderHeaderId);

	public List<OrderHeader> getAllOrders();

	public List<OrderHeader> getAllOrders(String type);
}
