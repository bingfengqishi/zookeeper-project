package cn.com.zkapi.client.demo;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/11/30.
 */
public class ZkOperateSample {
    private ZooKeeper zk;

    public void setZk(ZooKeeper zk){
        this.zk = zk;
    }
    public ZooKeeper getZk(){
        return  this.zk;
    }

    public ZkOperateSample(String path){
        try {
            zk  = new ZooKeeper(path,1000,null);
        } catch (IOException e) {
            if (zk != null){
                try {
                    zk.close();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    /**
     * 创建节点
     * @param path
     * @param data
     * @param acl
     */
    public void testCreateNode(String path,byte[] data, List<ACL> acl){
        String res = "";
        try {
            res = zk.create(path,data,acl, CreateMode.PERSISTENT);
            System.out.println("创建节点"+res+"成功!");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<ACL> getIpAcl(){
        List<ACL> acls= new ArrayList<ACL>();
        Id id = new Id("ip","127.0.0.1");
        ACL acl = new ACL(ZooDefs.Perms.ALL,id);
        acls.add(acl);
        return  acls;
    }

    /**
     * 异步删除节点
     *
     * @param path
     * @return
     */
    public boolean deleteNodeWithAsync(String path,int version) {
        String context ="上下文对象测试";
        System.out.println("删除");
        zk.delete(path, version, new DeleteCallBack(), context);
        return true;
    }

    public static void main(String[] args) {
        ZkOperateSample zkOperateSample = new ZkOperateSample("localhost:2181");
        //创建所有人可以访问的节点
        //zkOperateSample.testCreateNode("/node2/test","testcreat".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE);

        //只有通过认证的用户才可以访问
        //zkOperateSample.getZk().addAuthInfo("digest","libo:123456".getBytes());
        //zkOperateSample.testCreateNode("/node2/test/node2","testcreat".getBytes(),ZooDefs.Ids.CREATOR_ALL_ACL);

        zkOperateSample.testCreateNode("/node2/test/node3","testcreat".getBytes(),zkOperateSample.getIpAcl());

        //异步删除节点，有回调
        zkOperateSample.deleteNodeWithAsync("/node2/test/node3",0);
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
