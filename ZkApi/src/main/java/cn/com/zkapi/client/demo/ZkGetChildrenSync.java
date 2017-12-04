package cn.com.zkapi.client.demo;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lenovo on 2017/12/1.
 */
public class ZkGetChildrenSync implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;

    @Override
    public void process(WatchedEvent event) {
        System.out.println("获取子元素");
        if (event.getType() == Event.EventType.NodeDataChanged){
            List<String> childList = null;
            try {
                childList = zk.getChildren(event.getPath(),true);
                connectedSemaphore.countDown();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        zk = new ZooKeeper("localhost:2181",6000,new ZkGetChildrenSync());
        List<String> childList = zk.getChildren("/",true);
        System.out.println(childList.toString());
        connectedSemaphore.await();
    }
}
