package cn.com.zklock.demo.repository;



import cn.com.zklock.demo.pojo.OrderInfo;

public interface OrderRepository {
	void saveOrder(OrderInfo order);
}
