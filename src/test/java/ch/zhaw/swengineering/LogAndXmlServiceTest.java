package ch.zhaw.swengineering;

import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ParkingMeterRunner.class, loader=AnnotationConfigContextLoader.class)
public class LogAndXmlServiceTest {

    @Autowired
    private LogAndXmlService logAndXmlService;

    @Test
    public void testLogAndXmlService() {
        Assert.assertEquals("Hello world!", logAndXmlService.run());
    }
}
