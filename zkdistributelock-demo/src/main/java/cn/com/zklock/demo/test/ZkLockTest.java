package cn.com.zklock.demo.test;
import cn.com.zklock.demo.pojo.OrderInfo;
import cn.com.zklock.demo.pojo.Product;
import cn.com.zklock.demo.repository.OrderRepository;
import cn.com.zklock.demo.repository.ProductRepository;
import cn.com.zklock.demo.service.OrderService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

public class ZkLockTest {

    private static ApplicationContext ctx;

    static
    {
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    }
    @Test
    public void testpurchase(){
    	OrderService os = (OrderService)ctx.getBean("orderService");
        OrderInfo order = new OrderInfo();
        order.setProductId(1);
        order.setCreateTime(new Date());
        order.setPnum(1);
        os.doOrder(order);
    }

    @Test
    public  void testMapper(){
    	ProductRepository mapper = (ProductRepository)ctx.getBean("productMapper");
        //测试id=1的用户查询，根据数据库中的情况，可以改成你自己的.
        System.out.println("得到用户id=1的用户信息");;
        Product product = mapper.selectProductById(1);
        System.out.println(product.getName());
        
       OrderRepository omapper = (OrderRepository)ctx.getBean("orderMapper");
        OrderInfo order = new OrderInfo();
        order.setProductId(1);
        order.setCreateTime(new Date());
        order.setPnum(1);
        try {
            omapper.saveOrder(order);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

   /* @Test
    public  void testShardLog(int type,String identity){
        System.out.println("---------------开始获取锁"+identity);
    	//LockUtil.init("localhost:2181,localhost:2182");
    	//LockUtil.getShardLock(type, identity);
        System.out.println("---------------获取锁结束"+identity);
    }*/
  /*  public static void main(String[] args)
    {  

    	int type = Integer.parseInt(args[0]);
    	System.out.println("type="+type);
    	testShardLog(type,args[1]);
    	//testShardLog(0,"f6");
//    	try {
//    		LockUtil.init("localhost:2181");
//			//LockUtil.addChildWatcher("/LockService");
//    		LockUtil.getExclusiveLock();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//    	long nowtime = Calendar.getInstance().getTimeInMillis();
//    	System.out.println("begin with "+nowtime);
//    	testpurchase();
//    	System.out.println("end with "+nowtime);
    //	String []names = ctx.getBeanDefinitionNames();
//    	for(String s: names){
//    		System.out.println(s);
//    	}
    	
    	try {
    		Thread.sleep(20000);
            System.out.println("-------------开始--释放锁");
    		//LockUtil.unlockForShardLock();
            System.out.println("-------------成功--释放锁");
			Thread.sleep(30000);
            System.out.println("---------------结束退出");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    } */
}
