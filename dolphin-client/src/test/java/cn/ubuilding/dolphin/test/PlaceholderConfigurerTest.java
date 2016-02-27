package cn.ubuilding.dolphin.test;

import cn.ubuilding.dolphin.test.bean.TestZookeeperPropertyPlaceholderConfigurerBean;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Wu Jianfeng
 * @since 16/1/31 21:22
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-*.xml")
public class PlaceholderConfigurerTest {

    @Autowired
    private TestZookeeperPropertyPlaceholderConfigurerBean testBean;


    @Test
    public void testPlaceholder(){
        Assert.assertEquals("1",testBean.getX());
        Assert.assertEquals("2",testBean.getY());
        Assert.assertEquals(3,testBean.getZ());

    }

}
