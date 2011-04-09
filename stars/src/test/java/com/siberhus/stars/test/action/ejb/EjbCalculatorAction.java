package com.siberhus.stars.test.action.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.exception.StripesRuntimeException;

import com.siberhus.stars.test.action.BaseAction;
import com.siberhus.stars.test.service.ejb.EjbCalculatorLocal;


public class EjbCalculatorAction extends BaseAction {
	
	private Double numberOne = new Double(0);
	private Double numberTwo = new Double(0);
	private Double result = new Double(0);
	
	@EJB
	private EjbCalculatorLocal calcService;
	
	private String message;
	
	@PostConstruct
	public void sayHello() throws StripesRuntimeException{
		message = "Hello";
	}
	
	@PreDestroy
	public void sayBye(){
		message = "Goodbye";
	}
	
	@DefaultHandler
	public Resolution add(){
		result = calcService.add(numberOne, numberTwo);
		return new ForwardResolution("");
	}
	
	public Double getResult() {
		return result;
	}

	public String getMessage(){
		return message;
	}
	
	public Double getNumberOne() {
		return numberOne;
	}

	public void setNumberOne(Double numberOne) {
		this.numberOne = numberOne;
	}

	public Double getNumberTwo() {
		return numberTwo;
	}

	public void setNumberTwo(Double numberTwo) {
		this.numberTwo = numberTwo;
	}
	
	
}
