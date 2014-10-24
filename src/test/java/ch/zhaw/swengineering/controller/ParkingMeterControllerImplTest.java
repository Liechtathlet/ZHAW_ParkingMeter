package ch.zhaw.swengineering.controller;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Calendar;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.model.ParkingLot;
import ch.zhaw.swengineering.model.ParkingMeter;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ParkingMeterRunner.class, loader=AnnotationConfigContextLoader.class)
public class ParkingMeterControllerImplTest {

	@Mock(name="parkingMeter")
	private ConfigurationProvider configurationProvider;

	@InjectMocks
	@Resource
	private ParkingMeterControllerImpl controller;

	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void isParkingLotWithInvalidLotNumber() throws IOException {
		ParkingMeter parkingMeter = getParkingMeterMock();
		
		when(configurationProvider.get()).thenReturn(parkingMeter);
		
		// Assert
		Assert.assertFalse(controller.isParkingLot(100));
		Assert.assertFalse(controller.isParkingLot(0));
		Assert.assertFalse(controller.isParkingLot(150));
		Assert.assertFalse(controller.isParkingLot(20));
	}
	
	@Test
	public void isParkingLotWithValidLotNumber() throws IOException {
		ParkingMeter parkingMeter = getParkingMeterMock();
		
		when(configurationProvider.get()).thenReturn(parkingMeter);
		
		// Assert
		Assert.assertFalse(controller.isParkingLot(1));
		Assert.assertFalse(controller.isParkingLot(2));
		Assert.assertFalse(controller.isParkingLot(5));
		Assert.assertFalse(controller.isParkingLot(10));
	}
	

	private ParkingMeter getParkingMeterMock() {
		ParkingMeter parkingMeter = new ParkingMeter();
		
		Calendar parkingTime = Calendar.getInstance();
		
		//Preset values of calendar
		parkingTime.set(Calendar.DAY_OF_MONTH, 5);
		parkingTime.set(Calendar.SECOND,0);
		parkingTime.set(Calendar.MILLISECOND, 0);
		
		//ParkingLot Nr. 1
		parkingTime.set(Calendar.HOUR, 11);
		parkingTime.set(Calendar.MINUTE,0);
		parkingMeter.parkingLots.add(new ParkingLot(1,parkingTime.getTime()));
		
		//ParkingLot Nr. 2
		parkingTime.set(Calendar.HOUR, 10);
		parkingTime.set(Calendar.MINUTE,0);
		parkingMeter.parkingLots.add(new ParkingLot(2,parkingTime.getTime()));
		
		//ParkingLot Nr. 5
		parkingTime.set(Calendar.HOUR, 15);
		parkingTime.set(Calendar.MINUTE,30);
		parkingMeter.parkingLots.add(new ParkingLot(5,parkingTime.getTime()));
		
		//ParkingLot Nr. 10
		parkingTime.set(Calendar.HOUR, 13);
		parkingTime.set(Calendar.MINUTE,20);
		parkingMeter.parkingLots.add(new ParkingLot(10,parkingTime.getTime()));
		return parkingMeter;
	}
}