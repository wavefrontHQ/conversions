import java.util.Arrays;

public class Test {

	@org.junit.Test
	public void doTest() {

		//String str = "this is a,good test;to see;if,this will work";
		String SEP = " \"\"\" ";
		String str = "this is a" + SEP + "test to see if it" + SEP + " works";

		System.out.println(Arrays.toString(str.split(" \\\"\\\"\\\" ")));
		

		
	}
}
