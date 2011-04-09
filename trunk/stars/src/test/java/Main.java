import org.springframework.beans.factory.annotation.Autowire;


public class Main {

	private Autowire getAw(){
		return Autowire.BY_NAME;
	}
	
	public static void main(String[] args) {
		new Main().getAw();
	}
}
