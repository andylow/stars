package net.sourceforge.stripes.examples.quickstart;

import java.io.StringReader;
import java.util.List;

import javax.ejb.EJB;

import org.springframework.beans.factory.annotation.Autowired;

import com.siberhus.stars.Service;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontBind;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.examples.service.CalculatorService;
import net.sourceforge.stripes.examples.service.CalculatorServiceImpl;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;

/**
 * A very simple calculator action that is designed to work with an ajax front end.
 * Handles 'add' and 'divide' events just like the non-ajax calculator. Each event
 * calculates the result, and then "streams" it back to the browser. Implements the
 * ValidationErrorHandler interface to intercept any validation errors, convert them
 * to an HTML message and stream the back to the browser for display.
 *
 * @author Tim Fennell
 * @author Hussachai Puripunpinyo
 */
@UrlBinding("/action/quickstart/ajax-calc/{$event}/{numberOne}/{numberTwo}")
public class AjaxCalculatorActionBean extends AbstractActionBean implements ValidationErrorHandler {

	
	@Service(impl=CalculatorServiceImpl.class)
	@Autowired
	@EJB
	private CalculatorService calculatorService;
	
    @Validate(required=true) private double numberOne;
    @Validate(required=true) private double numberTwo;
    
    /** Converts errors to HTML and streams them back to the browser. */
    public Resolution handleValidationErrors(ValidationErrors errors) throws Exception {
        StringBuilder message = new StringBuilder();

        for (List<ValidationError> fieldErrors : errors.values()) {
            for (ValidationError error : fieldErrors) {
                message.append("<div style=\"color: firebrick;\">");
                message.append(error.getMessage(getContext().getLocale()));
                message.append("</div>");
            }
        }

        return new StreamingResolution("text/html", new StringReader(message.toString()));
    }

    @DontBind
    @DefaultHandler
    public Resolution index(){
    	numberOne = numberTwo = 0;
    	return new ForwardResolution("/quickstart/ajax-calc.jsp");
    }
    
    /** Handles the 'add' event, adds the two numbers and returns the result. */
    public Resolution add() {
        String result = String.valueOf(calculatorService.add(numberOne, numberTwo));
        return new StreamingResolution("text", new StringReader(result));
    }

    /** Handles the 'divide' event, divides number two by oneand returns the result. */
    public Resolution divide() {
        String result = String.valueOf(calculatorService.devide(numberOne, numberTwo));
        return new StreamingResolution("text", new StringReader(result));
    }
    
    // Standard getter and setter methods
    public double getNumberOne() { return numberOne; }
    public void setNumberOne(double numberOne) { this.numberOne = numberOne; }

    public double getNumberTwo() { return numberTwo; }
    public void setNumberTwo(double numberTwo) { this.numberTwo = numberTwo; }
}
