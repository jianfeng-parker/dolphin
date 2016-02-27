package cn.ubuilding.dolphin.zookeeper;

import cn.ubuilding.dolphin.exception.DolphinException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;

/**
 * @author Wu Jianfeng
 * @since 16/2/15 07:59
 */

public class ZkContainer {

    private static final Logger logger = LoggerFactory.getLogger(ZkContainer.class);

    private static volatile ZkContainer instance;

    private static Map<String, String> cache;

    private CuratorFramework client;

    /**
     * zookeeper中存放属性节点的路径,如: /apps/app01/properties
     */
    private String propertiesRootPath;

    private List<PropertyChanger> changers = new CopyOnWriteArrayList<>();

    static {
        cache = new ConcurrentHashMap<>();

    }

    private ZkContainer() {
        this.propertiesRootPath = ZkPath.forAppProperties(Environment.getAppName());
        this.client = CuratorFrameworkFactory.builder().connectString(Environment.getZkServer())
                .retryPolicy(new ExponentialBackoffRetry(2000, 3)).connectionTimeoutMs(3000)//.sessionTimeoutMs()
                .build();
        this.client.start();
        synchronize();
        watch();
    }

    public static ZkContainer getInstance() {
        if (null == instance) {
            synchronized (ZkContainer.class) {
                if (null == instance) {
                    instance = new ZkContainer();
                }
            }
        }
        return instance;
    }

    public void addChanger(PropertyChanger changer) {
        this.changers.add(changer);
    }

    /**
     * get value from zookeeper
     *
     * @param name property name
     * @return value
     */
    public String getProperty(String name) {
        String value = cache.get(name);
        if (null == value) {
            value = getZkValue(completePath(name));
            if (null != value) cache.put(name, value);
        }
        return value;
    }

    /**
     * get value from zookeeper
     *
     * @param path node path
     * @return value
     */
    private String getZkValue(String path) {
        String value = null;
        try {
            byte[] bytes = client.getData().forPath(path);
            if (null != bytes) {
                value = new String(bytes, "UTF-8");
            }
        } catch (KeeperException.NoNodeException e) {
            logger.warn("not exist node(" + path + "):" + e.getMessage());
        } catch (Exception e) {
            logger.error("get node(" + path + ") error:" + e.getMessage());
            throw new DolphinException("get node(" + path + ") error:" + e.getMessage());
        }
        return value;
    }

    /**
     * 获取 property name所指向的zk 节点的完整路径
     *
     * @param propertyName 属性名称,如: "user.name"
     * @return path, 如: "/apps/app01/properties/user.name"
     */
    private String completePath(String propertyName) {
        return propertiesRootPath + ((propertyName.startsWith("/")) ? "" : "/") + propertyName;
    }


    /**
     * 系统启动后从zookeeper同步数据到本地缓存
     */
    private void synchronize() {
        try {
            List<String> childPaths = client.getChildren().forPath(propertiesRootPath);
            if (!(null == childPaths || childPaths.isEmpty())) {
                for (String path : childPaths) {
                    String value = getZkValue(completePath(path));
                    if (null != value) cache.put(ZkPath.getLastSubPath(path), value);
                }
            }
        } catch (Exception e) {
            logger.error("synchronized properties in path(" + propertiesRootPath + ") from zookeeper failure:" + e.getMessage());
        }
    }

    /**
     * 启动监听器，监听当前应用在zookeeper对应目录下的所有子节点(属性名称)的变化:
     * /apps/app01/properties 下的所有属性节点的变化,如: .../x,.../y,.../z
     */
    private void watch() {
        final PathChildrenCache childCache = new PathChildrenCache(client, propertiesRootPath, true);
        try {
            childCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            childCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    String propertyNodeName = (null == event.getData()) ? null : ZkPath.getLastSubPath(event.getData().getPath());
                    switch (event.getType()) {
                        case CHILD_ADDED:
                        case CHILD_UPDATED:
                            String value = (null == event.getData()) ? "" : new String(event.getData().getData());
                            if (null != propertyNodeName && propertyNodeName.length() > 0) {
                                cache.put(propertyNodeName, value);
                                triggerChangers(new PropertyChangeEvent(propertyNodeName, new String(event.getData().getData()), PropertyChangeEvent.ChangeType.PROPERTY_UPDATED));
                            }
                            break;
                        case CHILD_REMOVED:
                            if (null != propertyNodeName && propertyNodeName.length() > 0) {
                                cache.remove(propertyNodeName);
                                triggerChangers(new PropertyChangeEvent(propertyNodeName, PropertyChangeEvent.ChangeType.PROPERTY_REMOVED));
                            }
                            break;
                        case CONNECTION_RECONNECTED:
                            childCache.rebuild();
                            logger.info("rebuild listener after reconnected from zookeeper(" + Environment.getZkServer() + ")");
                            break;
                        case CONNECTION_SUSPENDED:
                        case CONNECTION_LOST:
                            logger.warn("disconnected from zookeeper server(" + Environment.getZkServer() + ")");
                            break;
                        default:
                            break;
                    }
                }
            }, Executors.newFixedThreadPool(3));
        } catch (Exception e) {
            throw new DolphinException("starting watcher to listen zookeeper failure:" + e.getMessage());
        }
    }

    /**
     * 触发变化事件
     */
    private void triggerChangers(PropertyChangeEvent event) {
        if (null == changers || changers.size() == 0) return;

        for (PropertyChanger changer : changers) {
            changer.onChange(event);
        }
    }

}
