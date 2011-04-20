package net.sourceforge.stripes.examples.service;

import javax.ejb.Stateless;

import org.springframework.stereotype.Service;

import com.siberhus.stars.ServiceBean;

@ServiceBean //Stars Service
//To enable Spring Service Stereotype, you have to add <context:component-scan> to Spring bean configuration file. 
@Service("calculatorService") //Spring Service Stereotype
@Stateless //EJB Service
public class CalculatorServiceImpl implements CalculatorService {
	
	@Override
	public double add(double numberOne, double numberTwo) {
		
		return numberOne+numberTwo;
	}

	@Override
	public double devide(double numberOne, double numberTwo) {
		
		return numberOne/numberTwo;
	}

}
