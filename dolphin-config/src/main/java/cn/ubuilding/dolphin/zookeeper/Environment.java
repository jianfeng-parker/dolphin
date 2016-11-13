package cn.ubuilding.dolphin.zookeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Wu Jianfeng
 * @since 16/2/21 09:41
 */

public class Environment {

    private static Logger logger = LoggerFactory.getLogger(Environment.class);

    private static final String envFile = "sysenv";

    private static final String classpathEnv = "classpath:" + envFile;

    private static final String fileSystemEnv = "file:/data/dolphin/" + envFile;

    private static Properties props = null;

    private static String appName;

    private static String appDomain;

    private static String deployEnv;

    private static String zkServer;

    static {
        try {
            DefaultResourceLoader loader = new DefaultResourceLoader();

            Resource classpathResource = loader.getResource(classpathEnv);
            if (classpathResource.exists()) {
                props = PropertiesLoaderUtils.loadProperties(classpathResource);
            } else {
                Resource fileResource = loader.getResource(fileSystemEnv);
                if (fileResource.exists()) {
                    props = PropertiesLoaderUtils.loadProperties(fileResource);
                } else {
                    throw new FileNotFoundException("not found file '" + classpathEnv + "' or '" + fileSystemEnv + "'");
                }
            }

            appDomain = props.getProperty("app.domain");
            if (null == appDomain || appDomain.length() == 0)
                throw new IllegalArgumentException("not found property(app.domain) in " + envFile);


            appName = props.getProperty("app.name");
            if (null == appName || appName.length() == 0)
                throw new IllegalArgumentException("not found property(app.name) in " + envFile);

            zkServer = props.getProperty("zk.server");
            if (null == zkServer || zkServer.length() == 0)
                throw new IllegalArgumentException("not found property(zk.server) in " + envFile);

            deployEnv = props.getProperty("deploy.env");
            if (null == deployEnv || deployEnv.length() == 0) {
                throw new IllegalArgumentException("not found property(deploy.env) in " + envFile + ", is must be one of (dev/test/prod)");
            }

        } catch (IOException e) {
            logger.error("load " + envFile + " error:" + e.getMessage());
        }
    }


    public static Properties getProps() {
        return props;
    }

    public static String getAppDomain() {
        return appDomain;
    }

    public static String getAppName() {
        return appName;
    }

    public static String getDeployEnv() {
        return deployEnv;
    }

    public static String getZkServer() {
        return zkServer;
    }

    public static void main(String[] args) {
        System.out.print(appName);
    }
}
