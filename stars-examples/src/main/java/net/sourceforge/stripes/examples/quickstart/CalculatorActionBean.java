package net.sourceforge.stripes.examples.quickstart;

import javax.ejb.EJB;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontBind;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.examples.service.CalculatorService;
import net.sourceforge.stripes.examples.service.CalculatorServiceImpl;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.siberhus.stars.Service;

/**
 * A very simple calculator action.
 * @author Tim Fennell
 * @author Hussachai Puripunpinyo
 * 
 */
@UrlBinding("/action/quickstart/calc/{$event}/{numberOne}/{numberTwo}")
public class CalculatorActionBean extends AbstractActionBean {
	
	@Service(impl=CalculatorServiceImpl.class) //Inject Stars Service
	@Autowired //Inject Spring Service
	@EJB
	private CalculatorService calculatorService;
	
    @Validate(required=true) private double numberOne;
    @Validate(required=true) private double numberTwo;
    private double result;
    
    public double getNumberOne() { return numberOne; }
    public void setNumberOne(double numberOne) { this.numberOne = numberOne; }

    public double getNumberTwo() { return numberTwo; }
    public void setNumberTwo(double numberTwo) { this.numberTwo = numberTwo; }

    public double getResult() { return result; }
    public void setResult(double result) { this.result = result; }

    @DontBind
    @DefaultHandler
    public Resolution index(){
    	numberOne = numberTwo = 0;
    	return new ForwardResolution("/quickstart/calc.jsp");
    }
    
    /** An event handler method that adds number one to number two. */
    @HandlesEvent("add")
    public Resolution addition() {
        result = calculatorService.add(numberOne, numberTwo);
        return new ForwardResolution("/quickstart/calc.jsp");
    }
    
    /** An event handler method that divides number one by number two. */
    @HandlesEvent("divide")
    @Secured("ROLE_GUEST")
    public Resolution division() {
        result = calculatorService.devide(numberOne, numberTwo);
        return new ForwardResolution("/quickstart/calc.jsp");
    }

    /**
     * An example of a custom validation that checks that division operations
     * are not dividing by zero.
     */
    @ValidationMethod(on="divide")
    public void avoidDivideByZero(ValidationErrors errors) {
        if (this.numberTwo == 0) {
            errors.add("numberTwo", new SimpleError("Dividing by zero is not allowed."));
        }
    }
}
