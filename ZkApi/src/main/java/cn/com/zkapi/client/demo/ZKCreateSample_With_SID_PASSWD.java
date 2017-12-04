package cn.com.zkapi.client.demo;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lenovo on 2017/11/30.
 */
public class ZKCreateSample_With_SID_PASSWD implements Watcher {
    private static  CountDownLatch connectedSemaphore = null;
    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper  = new ZooKeeper("localhost:2181",6000,new ZKCreateSample_With_SID_PASSWD());
            long sessionId = zooKeeper.getSessionId();
            byte[] sessionPasswd = zooKeeper.getSessionPasswd();
            connectedSemaphore = new CountDownLatch(1);
            connectedSemaphore.await();
            System.out.println(zooKeeper.getState());
            System.out.println("休眠结束");
            //用不合法的sid和passWd创建连接
            ZooKeeper zooKeeper1 = new ZooKeeper("localhost:2181",6000,new ZKCreateSample_With_SID_PASSWD(),111,"test".getBytes());
            System.out.println(zooKeeper1.getState());
            //用合法的sid和passWd创建连接
            ZooKeeper zooKeeper2 = new ZooKeeper("localhost:2181",6000,new ZKCreateSample_With_SID_PASSWD(),sessionId,sessionPasswd);
            if(connectedSemaphore.getCount()==0){
                connectedSemaphore = new CountDownLatch(1);
            }
            connectedSemaphore.await();
            System.out.println(zooKeeper2.getState());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void process(WatchedEvent event) {
        System.out.println("recived watched event=" + event);
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
