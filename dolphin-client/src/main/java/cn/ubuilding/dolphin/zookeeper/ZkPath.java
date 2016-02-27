package cn.ubuilding.dolphin.zookeeper;


/**
 * @author Wu Jianfeng
 * @since 16/2/21 15:22
 */

public final class ZkPath {

    private static final String RootPath = "/apps";

    private static final String PropertiesPath = "/properties";

    /**
     * 根据app name获取 zookeeper上对应的存放该app属性的path
     *
     * @param appName app name,如: app01
     * @return path, 如: /apps/app01/properties
     */
    public static String forAppProperties(String appName) {
        if (appName == null || appName.length() == 0) throw new IllegalArgumentException("appName must not be null");
        return RootPath + "/" + appName + PropertiesPath;
    }

    /**
     * 获取zookeeper节点路径的最后一个子节点路径,如: /apps/app01/properties/xx 返回 xx
     */
    public static String getLastSubPath(String path) {
        if (null == path || path.length() == 0 || path.trim().length() == 0) return "";
        String[] paths = path.split("/");
        return paths[paths.length - 1];
    }

}
