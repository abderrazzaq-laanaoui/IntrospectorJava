package com.projetjee.gc.dao;

import java.util.List;

import com.projetjee.gc.entity.OrderLine;

public interface OrderLineDAO {
	
	public List<OrderLine> getOrderLines();
	
	public void saveOrderLine(OrderLine theOrderLine);
	
	public List<OrderLine> getOrderLineByOrderHeaderID(int orderHeaderId);

	public void deleteOrderLine(int theId);
}
