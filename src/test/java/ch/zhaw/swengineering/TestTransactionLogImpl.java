package ch.zhaw.swengineering;

import junit.framework.Assert;

import org.junit.Test;

import java.sql.Time;
import java.util.Date;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:spring-config.xml")

@SuppressWarnings("deprecation")
public class TestTransactionLogImpl {

//    @Autowired
    private TransactionLogImpl transactionLogImpl;
	//private Date date = new Date(2014, 10, 3);
	//private Time time = new Time(18, 00, 00);

    @Test
    public void testTransactionLogImpl() {
        Assert.assertEquals("Log message", transactionLogImpl.setMessage("Log message")); // welches Format für vollständige Assert-Meldung?
    }	// welche Methode ist aufzurufen?
}
