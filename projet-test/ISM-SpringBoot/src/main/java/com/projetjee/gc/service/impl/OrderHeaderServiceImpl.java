package com.projetjee.gc.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.projetjee.gc.dao.CustomerDAO;
import com.projetjee.gc.dao.OrderHeaderDAO;
import com.projetjee.gc.dao.OrderLineDAO;
import com.projetjee.gc.dao.ProductDAO;
import com.projetjee.gc.dto.OrderHeaderDTO;
import com.projetjee.gc.dto.OrderLineDTO;
import com.projetjee.gc.entity.Customer;
import com.projetjee.gc.entity.OrderHeader;
import com.projetjee.gc.entity.OrderLine;
import com.projetjee.gc.entity.Product;
import com.projetjee.gc.service.mapper.OrderHeaderMapper;
import com.projetjee.gc.service.mapper.OrderLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projetjee.gc.service.OrderHeaderService;

@Service
@Transactional
public class OrderHeaderServiceImpl implements OrderHeaderService {

	@Autowired
	private OrderHeaderDAO orderHeaderDAO;

	@Autowired
	private OrderLineDAO orderLineDAO;

	@Autowired
	private OrderHeaderMapper orderHeaderMapper;

	@Autowired
	private OrderLineMapper orderLineMapper;

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private ProductDAO productDAO;

	public List<OrderHeaderDTO> getOrderHeaderID() {
		List<OrderHeader> orderHeaders = orderHeaderDAO.getOrderHeader();
		List<OrderHeaderDTO> orderHeaderDTOList = new ArrayList<OrderHeaderDTO>();

		for (OrderHeader oh : orderHeaders) {
			orderHeaderDTOList.add(orderHeaderMapper.mapToDTO(oh));
		}
		return orderHeaderDTOList;
	}

	public String getNextOrderHeaderNumber() {
		OrderHeader oh = orderHeaderDAO.getLastOrderHeader();
		int nextOrderNumber = 1;
		if (oh != null && oh.getNumber() != null && !oh.getNumber().equals("")) {
			int lastOrderNumber = Integer.parseInt(oh.getNumber());
			nextOrderNumber = ++lastOrderNumber;
		}

		return String.valueOf(nextOrderNumber);
	}

	@Override
	public void saveOrderHeader(OrderHeaderDTO theOrderHeader) {
		OrderHeader orderHeader = null;
		if(theOrderHeader.getOrderHeaderId() <= 0) { // order doesn't exist - create a new order
			// save all the orderLineItems
			// orderlinerepository
			Customer customer = customerDAO.getCustomer(theOrderHeader.getCustomer().getEmail());

			orderHeader = orderHeaderMapper.mapToEntity(theOrderHeader);
			orderHeader.setCustomer(customer);
			orderHeaderDAO.saveOrderHeader(orderHeader);
		}else { // order exists. so use the existing order
			orderHeader = orderHeaderDAO.getOrderHeaderById(theOrderHeader.getOrderHeaderId());
		}
		
		OrderLineDTO newOrderLine = theOrderHeader.getNewOrderLine();
		OrderLine orderLine = orderLineMapper.mapToEntity(newOrderLine);
		Product product = productDAO.getProduct(newOrderLine.getProduct().getName());
		// fetch product by name from the DB and set it to orderLine
		orderLine.setOrderHeader(orderHeader);
		orderLine.setProduct(product);
		orderLine.setPrice(product.getPrice());
		orderLine.setTotal(orderLine.getPrice()*orderLine.getQuantity());

		orderLineDAO.saveOrderLine(orderLine);


	}

	@Override
	public OrderHeaderDTO getOrderHeaderById(Integer orderHeaderId) {

		OrderHeader orderHeader = orderHeaderDAO.getOrderHeaderById(orderHeaderId);

		return orderHeaderMapper.mapToDTO(orderHeader);

	}

	@Override
	public List<OrderHeaderDTO> getAllOrders() {

		List<OrderHeader> orderHeaders = orderHeaderDAO.getAllOrders();

		List<OrderHeaderDTO> orderHeaderDTOList = new ArrayList<OrderHeaderDTO>();

		for (OrderHeader oh : orderHeaders) {
			orderHeaderDTOList.add(orderHeaderMapper.mapToDTO(oh));
		}

		return orderHeaderDTOList;
	}

	@Override
	public List<OrderHeaderDTO> getAllOrders(String type) {
		List<OrderHeader> orderHeaders = orderHeaderDAO.getAllOrders(type);

		List<OrderHeaderDTO> orderHeaderDTOList = new ArrayList<OrderHeaderDTO>();

		for (OrderHeader oh : orderHeaders) {
			orderHeaderDTOList.add(orderHeaderMapper.mapToDTO(oh));
		}
		return orderHeaderDTOList;

	}

	@Override
	public OrderHeaderDTO getOrderHeaderByNumber(String orderHeaderNumber) {
		OrderHeader orderHeader = orderHeaderDAO.getOrderHeaderByNumber(orderHeaderNumber);

		
		return orderHeaderMapper.mapToDTO(orderHeader);
	}

	@Override
	public void checkout(String orderHeaderNumber) {
		OrderHeader orderHeader = orderHeaderDAO.getOrderHeaderByNumber(orderHeaderNumber);
		List<OrderLine> orderLineByOrderHeaderID = orderLineDAO.getOrderLineByOrderHeaderID(orderHeader.getOrderHeaderId());
		int finalTotal = 0;
		for(OrderLine line : orderLineByOrderHeaderID) {
			finalTotal += line.getTotal();
		}
		orderHeader.setFinalTotal(finalTotal);
		orderHeader.setOrderStatus("Complete");
		orderHeaderDAO.saveOrderHeader(orderHeader);
	}
}
