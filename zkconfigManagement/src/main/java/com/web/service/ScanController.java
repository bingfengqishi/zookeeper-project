package com.web.service;

import jdk.nashorn.internal.objects.annotations.Constructor;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by lenovo on 2017/12/1.
 */
public class ScanController {
    //被扫描的包名
    private String scanPath = "";
    private CuratorFramework client = null;
    private String connectString = "";
    //应用名
    private String bizCode = "sampleweb";

    public ScanController(String scanPath, String connectString, String bizcode) {
        this.scanPath = scanPath;
        this.connectString = connectString;
        this.bizCode = bizcode;
        System.out.println(scanPath + "--" + connectString + "---" + bizCode);
    }

    /**
     * 构建zookeeper链接
     */
    private void buildZKclient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder().connectString(connectString)
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy)
                .namespace("webServiceCenter").build();
        client.start();
    }
    /**
     * 创建注册的根目录
     */
    private void registBiz(){
        try {
            if(client.checkExists().forPath("/"+bizCode)==null){
                client.create().creatingParentsIfNeeded().
                        withMode(CreateMode.PERSISTENT).
                        withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).
                        forPath("/"+bizCode,(bizCode+"提供的服务列表").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 取指定包下面所有被添加服务注解的类
     * 服务注解为controll和方法上的requestMapping
     * @param scanPath
     * @return
     */
    public Set<Class<?>> getClass(String scanPath){
     // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageName = scanPath;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        System.out.println("packageDirName=" + packageDirName);
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            //循环迭代
            while (dirs.hasMoreElements()){
                //获取下一个元素
                URL url = dirs.nextElement();
                System.out.println("path="+url.getPath());
                //获取协议
                String protocol = url.getProtocol();
                //以文件的形式存在
                if("file".equals(protocol)){
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath,recursive, classes);
                }else if ("jar".equals(protocol)){
                    // 如果是jar包文件
                    // 定义一个JarFile
                    System.err.println("jar类型的扫描");
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection())
                                .getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx)
                                            .replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class")
                                            && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  classes;
    }
    public void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory())|| (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(
                        packageName + "." + file.getName(),
                        file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader()
                            .loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * @param classes
     * @return
     */
    public List<String> getServicePath(Set<Class> classes) {
        List<String> services = new ArrayList<String>();
        StringBuffer sb = null;
        Annotation ann = null;
        for (Class cls : classes) {
            ann = cls.getAnnotation(Controller.class);
            if (ann == null){
                continue;
            }
            ann = cls.getAnnotation(RequestMapping.class);
            String basePath = getRequestMappingPath(ann);
            Method ms[] = cls.getMethods();
            if (ms == null || ms.length == 0)
                continue;
            for (Method m : ms) {
                ann = m.getAnnotation(RequestMapping.class);
                String path = getRequestMappingPath(ann);
                if (path != null) {
                    sb = new StringBuffer();
                    if (basePath != null)
                        sb.append(basePath);
                    if (path.startsWith("/"))
                        sb.append(path);
                    else
                        sb.append("/" + path);
                } else
                    continue;
                if (sb != null) {
                    services.add(sb.toString());
                }
            }
        }
        return  services;
    }
    private String getRequestMappingPath(Annotation ann) {
        if (ann == null)
            return null;
        else {
            RequestMapping rma = (RequestMapping) ann;
            String[] paths = rma.value();
            if (paths != null && paths.length > 0){
                return paths[0];
            }else{
                return null;
            }
        }
    }
    /**
     * 将服务注册到zk上
     * @param services
     */
    private void registBizServices(List<String> services) {
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress().toString();
            for (String s : services) {
                String temp = s.replace("/", ".");
                if(temp.startsWith(".")){
                    temp = temp.substring(1);
                }
                if (client.checkExists().forPath("/" + bizCode +"/"+ temp) == null) {
                    client.create().creatingParentsIfNeeded()
                            .withMode(CreateMode.PERSISTENT)
                            .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                            .forPath("/" + bizCode +"/"+ temp, ("1").getBytes());
                }
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath("/" + bizCode +"/"+ temp + "/" + ip, ("1").getBytes());

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 初始化方法
     */
    public void init() {
        System.out.println("扫描初始化------");
        //初始化zk客户端
        buildZKclient();
        //注册根目录
        registBiz();
        //扫描所有的action和方法
        Set classes = getClass(scanPath);
        if (classes.size() <= 0) {
            return;
        }
        //通过服务注解获得服务地址
        List<String> services = getServicePath(classes);
        for (String s : services){
            System.out.println("service=" + s);
        }
        System.out.println("------------------size="+services.size());
        //把服务注册到zk

        registBizServices(services);
    }
}
