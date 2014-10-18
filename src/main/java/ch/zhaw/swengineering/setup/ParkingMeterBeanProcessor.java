package ch.zhaw.swengineering.setup;

import ch.zhaw.swengineering.view.SimulationView;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class ParkingMeterBeanProcessor implements BeanFactoryPostProcessor {

	@Autowired
	@Qualifier("ConsoleSimulationView")
	public SimulationView simulationView;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
		configurableListableBeanFactory
				.registerResolvableDependency(SimulationView.class, simulationView);
	}
}
