package cn.com.zklock.demo.repository;


import cn.com.zklock.demo.pojo.Product;

import java.util.HashMap;

public interface ProductRepository {
	Product selectProductById(int id);
	void reduceNum(HashMap<String, Integer> hm);
}
