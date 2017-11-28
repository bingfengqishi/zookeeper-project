package cn.com.zkapi.demo;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lenovo on 2017/11/28.
 */

    /**
     * Created by lenovo on 2017/11/27.
     * 创建会话
     */
public class ZkCreateSample implements Watcher {
    private  static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws IOException {
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181",6000,new ZkCreateSample());
        System.out.println("begin state="+zooKeeper.getState());
        try {
            connectedSemaphore.await();
        }catch (InterruptedException e){
            e.printStackTrace();
            System.out.println("Zookeeper session established!");
        }
        System.out.println("end \\state ="+zooKeeper.getState());
    }
    @Override
    public void process(WatchedEvent event) {
        System.out.println("recived watched event="+event);
        if(Event.KeeperState.SyncConnected==event.getState()){
            connectedSemaphore.countDown();
        }
    }

}
