package ch.zhaw.swengineering;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:spring-config.xml")
public class TestXMLService {

    @Autowired
    private XmlService xmlService;

    @Test
    public void testLogAndXmlService() {
        Assert.assertEquals("Hello world!", xmlService.run());
    }
}
