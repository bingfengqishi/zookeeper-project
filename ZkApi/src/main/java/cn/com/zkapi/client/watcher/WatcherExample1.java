package cn.com.zkapi.client.watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * Created by lenovo on 2017/11/30.
 */
public class WatcherExample1 implements Watcher {
    private String  zkPath = "localhost:2181";
    private ZooKeeper zooKeeper = null;
    @Override
    public void process(WatchedEvent event) {
        System.out.println("watcher="+this.getClass().getName());
        System.out.println("path="+event.getPath());
        System.out.println("eventType="+event.getType().name());
        //重新设置watcher
        try {
            WatcherExample1 wx1 = new WatcherExample1();
            getZooKeeper().getData(event.getPath(),wx1,null);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public String getZkPath(){
        return this.zkPath;
    }
    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }
    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }
    public static void main(String[] args) {
        WatcherExample1 wx = new WatcherExample1();
        try {
            ZooKeeper zooKeeper = new ZooKeeper(wx.getZkPath(),6000,wx);
            wx.setZooKeeper(zooKeeper);
            //第一次会打印相关信息
            zooKeeper.getChildren("/node1",true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

}
