package ch.zhaw.swengineering;

import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ParkingMeterRunner.class, loader=AnnotationConfigContextLoader.class)
public class TestTransactionLogImpl {

    @Autowired
    public TransactionLog transactionLog;
	//private Date date = new Date(2014, 10, 3);
	//private Time time = new Time(18, 00, 00);

    @Test
    public void testTransactionLogImpl() {
        assertEquals("Log message", transactionLog.setMessage("Log message")); // welches Format für vollständige Assert-Meldung?
    }	// welche Methode ist aufzurufen?
}
