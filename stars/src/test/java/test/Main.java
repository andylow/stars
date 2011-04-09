package test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import org.springframework.beans.factory.annotation.Autowire;

import com.siberhus.stars.core.ServiceBeanProxy;
import com.siberhus.stars.test.service.Calculator;
import com.siberhus.stars.test.service.stars.StarsCalculatorService;

public class Main {

	
	
	public static void main(String[] args) throws Exception{
		Calculator calc = (Calculator)ServiceBeanProxy.newInstance(new StarsCalculatorService());
		System.out.println(calc.add(new Double(1), new Double(4)));
		Object realObj = ServiceBeanProxy.getRealObject((Proxy)calc);
		Field f = realObj.getClass().getField("message");
		f.set(realObj, "hello2");
		calc.showMessage();
	}
}
