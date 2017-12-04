package cn.com.zkapi.client.watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * Created by lenovo on 2017/11/30.
 */
public class WatcherExample implements Watcher {
    private String  zkPath = "localhost:2181";
    public void process(WatchedEvent event) {
        System.out.println("watcher="+this.getClass().getName());
        System.out.println("path="+event.getPath());
        System.out.println("eventType="+event.getType().name());
    }
    public void  setZkPath(String zkPath){
        this.zkPath  = zkPath;
    }
    public String getZkPath(){
        return this.zkPath;
    }
    public static void main(String[] args) {
        WatcherExample wx = new WatcherExample();
        try {
            ZooKeeper zooKeeper = new ZooKeeper(wx.getZkPath(),6000,wx);
            //第一次会打印相关信息
            zooKeeper.getChildren("/node1",true);

            //不会打印 wathcer观察失效
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
