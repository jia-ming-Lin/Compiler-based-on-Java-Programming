 /**
 * JUunit tests for the Parser for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Spring 2018.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Spring 2018 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2018
 */

package cop5556sp18;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Parser;
import cop5556sp18.Scanner;
import cop5556sp18.Parser.SyntaxException;
import cop5556sp18.Scanner.LexicalException;

public class SimpleParserTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}
 

	//creates and returns a parser for the given input.
	private Parser makeParser(String input) throws LexicalException {
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		return parser;
	}
	
	

	/**
	 * Simple test case with an empty program.  This throws an exception 
	 * because it lacks an identifier and a block. The test case passes because
	 * it expects an exception
	 *  
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	
	
	/**
	 * Smallest legal program.
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testSmallest() throws LexicalException, SyntaxException {
		String input = "b{}";  
		Parser parser = makeParser(input);
		parser.parse();
	}	
	
	@Test
	public void testprimary() throws LexicalException, SyntaxException {
		String input = "b{hi := +-!+-!+-!+-!+-!+-!1>a  ;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void testinputandprimary2() throws LexicalException, SyntaxException {
		String input = "b{input test from @ INTEGER_LITERAL;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void testpixelselector2() throws LexicalException, SyntaxException {
		String input = "b{test1 [interger,integer] :=  INTEGER_LITERAL;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void testconstructor() throws LexicalException, SyntaxException {
		String input = "b{input test from @ <<FLOAT_LITERAL,FLOAT_LITERAL,BOOLEAN_LITERAL,BOOLEAN_LITERAL>>;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void testwhile() throws LexicalException, SyntaxException {
		String input = "b{while(t==10){};}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void testfunctionalapplication() throws LexicalException, SyntaxException {
		String input = "b{show int (sin(10));sleep polar_r[cos(10),log(20)];}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	
	@Test 
	public void testexpression() throws LexicalException, SyntaxException {
		String input = "b{show hi ? QQ : integer ;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	
	@Test 
	public void testandexpression() throws LexicalException, SyntaxException {
		String input = "b{show hi | thanks & No==30!=50 ;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void testpower() throws LexicalException, SyntaxException {
		String input = "b{show (10)**(+10-8-50%20);}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void testshow() throws LexicalException, SyntaxException {
		String input = "b{show BOOLEAN_LITERAL;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void testimage() throws LexicalException, SyntaxException {
		String input = "b{image test1;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void testimage2() throws LexicalException, SyntaxException {
		String input = "b{image test1 [help,test1];}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void testsleep() throws LexicalException, SyntaxException {
		String input = "b{sleep BOOLEAN_LITERAL;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void testdefalut() throws LexicalException, SyntaxException {
		String input = "abcd_a{sleep default_width;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void testpixelselector() throws LexicalException, SyntaxException {
		String input = "b{red(a[10,50]):=10 ;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	
	
	@Test
	public void multidec() throws LexicalException, SyntaxException {
		String input = "b{int a;int b; float c;}";  
		Parser parser = makeParser(input);
		parser.parse();
	}
	@Test
	public void expressiontest() throws LexicalException, SyntaxException {
		String input = "b{while(n==10){};}";  
		Parser parser = makeParser(input);
		parser.parse();
	}
	@Test
	public void Lexicaltest() throws LexicalException, SyntaxException {
		String input = "=";
		thrown.expect(LexicalException.class);
		Parser parser = makeParser(input);
		parser.parse();
	}
	@Test
	public void decltest() throws LexicalException, SyntaxException {
		String input = "b{int a;int b float c;}";  //The input is the empty string.  
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	@Test
	public void multidec1wrong() throws LexicalException, SyntaxException {
		String input = "b{int a;int b float c;}";  //The input is the empty string.  
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	@Test
	public void test_decl_KW_image() throws LexicalException, SyntaxException {
		String input = "b{image test [int(10), int(10)];}";  
		Parser parser = makeParser(input);
		parser.parse();
	}
	@Test
	public void testfloat() throws LexicalException, SyntaxException {
		String input = "b{float fuck;}";  
		Parser parser = makeParser(input);
		parser.parse();
	}
	
	
	//This test should pass in your complete parser.  It will fail in the starter code.
	//Of course, you would want a better error message. 
	@Test
	public void testDec0() throws LexicalException, SyntaxException {
		String input = "a{if(a*b){int c;};}";
		Parser parser = makeParser(input);
		parser.parse();
	}
	@Test
	public void testwrong() throws LexicalException, SyntaxException {
		String input = "b{int c}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		try{
		parser.parse();
		}
		catch(SyntaxException e){
			show(e);
			throw e;
		}
	}
	
@Test
public void assign() throws LexicalException, SyntaxException {
	String input = "b{a[a+a,k+a]:=a+a;}";  
	Parser parser = makeParser(input);
	parser.parse();
}	
@Test
public void assigncolor() throws LexicalException, SyntaxException {
	String input = "b{red(a[w+e,t**2]):=a-b;}";  
	Parser parser = makeParser(input);
	parser.parse();
}	

@Test
public void show() throws LexicalException, SyntaxException {
	String input = "b{show a+b;}";  
	Parser parser = makeParser(input);
	parser.parse();
}	
@Test
public void statementcolor() throws LexicalException, SyntaxException {
	String input = "a{if(a==b){int c}}";  //The input is the empty string.  
	Parser parser = makeParser(input);
	thrown.expect(SyntaxException.class);
	try {
		parser.parse();
	} catch (SyntaxException e) {  //Catch the exception
		show(e);                    //Display it

		throw e;                    //Rethrow exception so JUnit will see it
	}
	parser.parse();
}
@Test
public void statementnothing() throws LexicalException, SyntaxException {
	String input = "";  //The input is the empty string.  
	Parser parser = makeParser(input);
	thrown.expect(SyntaxException.class);
	try {
		parser.parse();
	} catch (SyntaxException e) {  //Catch the exception
		show(e);                    //Display it

		throw e;                    //Rethrow exception so JUnit will see it
	}
	parser.parse();
}
	@Test
	public void teacher() throws LexicalException, SyntaxException {
		String input = "b{ image a[a+sin(a+Z),m**m];image a[a+sin(a+Z),m**m];}";  
		Parser parser = makeParser(input);
		parser.parse();
	}	

	@Test
	public void pixelconstructor() throws LexicalException, SyntaxException {
		String input = "a{sleep <<Z*default_height,1.255+3.444,j**g,d+p>>;}";  
		Parser parser = makeParser(input);
		parser.parse();
	}

	@Test 
	public void testandexpression20() throws LexicalException, SyntaxException {
		String input = "b{show i | yuo & abc0!=50 ;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}

	@Test 
	public void a() throws LexicalException, SyntaxException {
		String input = "b{show a|b ;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		parser.parse();
	}
	@Test
	public void writetest() throws LexicalException, SyntaxException {
		String input = "=b{write ident to ident;}";
		thrown.expect(LexicalException.class);
		Parser parser = makeParser(input);
		parser.parse();
	}

	@Test
        public void testSamples() throws LexicalException, SyntaxException {
                String input = "samples{image bird; input bird from @0;show bird;sleep(4000);image bird2[width(bird),height(bird)];int x;x:=0;while(x<width(bird2)) {int y;y:=0;while(y<height(bird2)) {blue(bird2[x,y]):=red(bird[x,y]);green(bird2[x,y]):=blue(bird[x,y]);red(bird2[x,y]):=green(bird[x,y]);alpha(bird2[x,y]):=Z;y:=y+1;};x:=x+1;};show bird2;sleep(4000);}";
                Parser parser = makeParser(input);
                parser.parse();
        }

@Test
        public void testDemo1() throws LexicalException, SyntaxException {
                String input = "demo1{image h;input h from @0;show h; sleep(4000); image g[width(h),height(h)];int x;x:=0;"
                                + "while(x<width(g)){int y;y:=0;while(y<height(g)){g[x,y]:=h[y,x];y:=y+1;};x:=x+1;};show g;sleep(4000);}";
                Parser parser = makeParser(input);
                parser.parse();
        }
}
	

