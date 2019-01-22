 /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.Scanner.Token;
import static cop5556sp18.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	
	//To make it easy to print objects and turn this output on and off
	static boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(pos, t.pos);
		assertEquals(length, t.length);
		assertEquals(line, t.line());
		assertEquals(pos_in_line, t.posInLine());
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}
	@Test
	public void testDigits5() throws LexicalException {
		String input = "b{image test [int test1;,int test2;];}";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
	}
	@Test
	public void testDigits6() throws LexicalException {
		String input = "a.i";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
	}
	
	@Test
	public void testequal1() throws LexicalException {
		String input = "=";
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			assertEquals(0,e.getPos()); //Check that it occurred in the expected position
			throw e;                    //Rethrow exception so JUnit will see it
		}
	}


	/**
	 * Simple test case with an empty program.  The only Token will be the EOF Token.
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, the end of line 
	 * character would be inserted by the text editor.
	 * Showing the input will let you check your input is 
	 * what you think it is.
	 * 
	 * @throws LexicalException
	 */
	
	@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	

	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it an illegal character '~' in position 2
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void failIllegalChar() throws LexicalException {
		String input = "=== ====";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			assertEquals(2,e.getPos()); //Check that it occurred in the expected position
			throw e;                    //Rethrow exception so JUnit will see it
		}
	}

	@Test
	public void floatexcepetion() throws LexicalException {
		String input = "v 999191991991001001101010101001111";
		thrown.expect(LexicalException.class);//Tell JUnit to expect a LexicalException
		try {
			Scanner scaneer=new Scanner(input).scan();
			show(scaneer);
		}catch(LexicalException e) {
			show(e);
			System.out.println(e.getPos());
			throw e;
		}
	}
	@Test
	public void testequal() throws LexicalException{
		String input ="== === ===== === =";
		show(input);
		thrown.expect(LexicalException.class);
		try{
			new Scanner(input).scan();
		}
		catch(LexicalException e){
			show(e);
			System.out.println(e.getPos());
			assertEquals(5,e.getPos());
			throw e;
		}
	}
	@Test 
	public void commentexcepetion2() throws LexicalException {
		String input = "/* this is not an end";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		}catch(LexicalException e) {
			show(e);
			System.out.println(e.getPos());
			assertEquals(21,e.getPos());
			throw e;
		}
	}
	@Test 
	public void commentexcepetion() throws LexicalException {
		String input = "9. =  /* this is not an end";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		}catch(LexicalException e) {
			show(e);
			System.out.println(e.getPos());
			assertEquals(3,e.getPos());
			throw e;
		}
	}
	@Test
	public void numbertest() throws LexicalException {
		String input = "abc 99919199199100100114650484056406540654065406546001010101001111";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			Scanner scaneer=new Scanner(input).scan();
			show(scaneer);
		}
		catch(LexicalException e){
			show(e);
			System.out.println(e.getPos());
			throw e;
		
		}
	}
	
	/*
	@Test
	public void testDigits1() throws LexicalException {
		String input = "";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 3, 1, 1);
		checkNext(scanner, INTEGER_LITERAL, 4, 5, 1, 5);
		checkNext(scanner, INTEGER_LITERAL, 10, 2, 1, 11);
		checkNext(scanner, INTEGER_LITERAL, 13, 1, 1, 14);
		checkNext(scanner, INTEGER_LITERAL, 14, 1, 1, 15);
		checkNextIsEOF(scanner);
	}*/

	@Test        
	public void testDigits2() throws LexicalException{	
		String input = "3.57 267.4.6";                     
		Scanner scanner = new Scanner(input).scan(); 	
		show(input);                                 
		show(scanner);                               
		checkNext(scanner, FLOAT_LITERAL,0,4, 1, 1); 
		checkNext(scanner, FLOAT_LITERAL,5,5, 1, 6);
		checkNext(scanner, FLOAT_LITERAL,10,2,1, 11); 
		checkNextIsEOF(scanner);                     
	} 
	@Test            
	public void testDigits3() throws LexicalException{	
		String input = "0012.24 0.1 09";                     
		Scanner scanner = new Scanner(input).scan(); 	
		show(input);                                 
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL,0,1,1, 1); 
		checkNext(scanner, INTEGER_LITERAL,1,1,1, 2); 
		checkNext(scanner, FLOAT_LITERAL,2,5, 1, 3); 
		checkNext(scanner, FLOAT_LITERAL,8,3, 1, 9);
		checkNext(scanner, INTEGER_LITERAL,12,1,1, 13); 
		checkNext(scanner, INTEGER_LITERAL,13,1,1, 14); 
		checkNextIsEOF(scanner);                     
	}  
	@Test 
	public void testDigits4() throws LexicalException{	
		String input = ".24 0.1 09. 20 40 ";                     
		Scanner scanner = new Scanner(input).scan(); 	
		show(input);                                 
		show(scanner);
		checkNext(scanner, FLOAT_LITERAL,0,3,1, 1); 
		checkNext(scanner, FLOAT_LITERAL,4,3,1, 5); 
		checkNext(scanner, INTEGER_LITERAL,8,1, 1, 9); 
		checkNext(scanner, FLOAT_LITERAL,9,2, 1, 10); 
		checkNext(scanner, INTEGER_LITERAL,12,2, 1, 13); 
		checkNext(scanner, INTEGER_LITERAL,15,2, 1, 16); 
		checkNextIsEOF(scanner);                     
	}  
	@Test           
	public void testcase1() throws LexicalException{	
		String input = "* == != <= == >= <= >> >> <<<<!:=?:<>|&";    		
		Scanner scanner = new Scanner(input).scan(); 	
		show(input);                                 
		show(scanner);                               
		checkNext(scanner, OP_TIMES,0,1, 1, 1); 
		checkNext(scanner, OP_EQ,1,2, 1, 2);
		checkNext(scanner, OP_NEQ,3,2,1, 4); 
		checkNext(scanner, OP_LE,5,2,1, 6); 
		checkNext(scanner, OP_EQ,7,2,1, 8);
		checkNext(scanner, OP_GE,9,2,1, 10); 
		checkNext(scanner, OP_LE,11,2,1, 12); 
		checkNext(scanner, RPIXEL,13,2,1, 14);
		checkNext(scanner, RPIXEL,15,2,1, 16);
		checkNext(scanner, LPIXEL,17,2,1,18); 
		checkNext(scanner, LPIXEL,19,2,1,20); 
		checkNext(scanner, OP_EXCLAMATION,21,1,1,22); 
		checkNext(scanner, OP_ASSIGN,22,2,1,23); 
		checkNext(scanner, OP_QUESTION,24,1,1,25); 
		checkNext(scanner, OP_COLON,25,1,1,26); 
		checkNext(scanner, OP_LT,26,1,1,27); 
		checkNext(scanner, OP_GT,27,1,1,28);
		checkNext(scanner, OP_OR,28,1,1,29); 
		checkNext(scanner, OP_AND,29,1,1,30); 
		checkNextIsEOF(scanner);                     
	} 
	@Test           
	public void testcase2() throws LexicalException{	
		String input = "+-**/,.";                     
		Scanner scanner = new Scanner(input).scan(); 	
		show(input);                                 
		show(scanner);                               
		checkNext(scanner, OP_PLUS,0,1, 1, 1); 
		checkNext(scanner, OP_MINUS,1,1, 1, 2);
		checkNext(scanner, OP_POWER,2,2,1, 3); 
		checkNext(scanner, OP_DIV,4,1,1, 5); 
		checkNext(scanner, COMMA,5,1,1, 6); 
		checkNext(scanner, DOT,6,1,1, 7); 
		checkNextIsEOF(scanner);                     
	}   
	@Test
	public void test1() throws LexicalException {
		String input = "_i";
		thrown.expect(LexicalException.class);
		try{
		Scanner scanner = new Scanner(input).scan();
		}catch(LexicalException e){
			show(e);
			System.out.println(e.getPos());
			
			throw e;
		}
	}
	@Test
	public void test2() throws LexicalException {
		String input = "$i";
		thrown.expect(LexicalException.class);
		try{
		Scanner scanner = new Scanner(input).scan();
		}catch(LexicalException e){
			show(e);
			System.out.println(e.getPos());
			
			throw e;
		}
	}
	@Test    
	public void testboolean() throws LexicalException {
		String input = "true false";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, BOOLEAN_LITERAL, 0, 4, 1, 1);
		checkNext(scanner, BOOLEAN_LITERAL, 5, 5, 1, 6);
		checkNextIsEOF(scanner);
	}
	
	@Test  
	public void testkeywordshow2abs() throws LexicalException {
		String input = "show write to input from cart_x \rcart_y\rpolar_a polar_r abs filename";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_show, 0, 4, 1, 1);
		checkNext(scanner, KW_write, 5, 5, 1, 6);
		checkNext(scanner, KW_to, 11, 2, 1, 12);
		checkNext(scanner, KW_input, 14, 5, 1, 15);
		checkNext(scanner, KW_from, 20, 4, 1, 21);
		checkNext(scanner, KW_cart_x, 25, 6, 1, 26);
		checkNext(scanner, KW_cart_y, 33, 6, 2, 1);
		checkNext(scanner, KW_polar_a, 40, 7, 3, 1);
		checkNext(scanner, KW_polar_r, 48, 7, 3, 9);
		checkNext(scanner, KW_abs, 56, 3, 3, 17);
		checkNext(scanner, KW_filename, 60, 8, 3, 21);
		checkNextIsEOF(scanner);
	} 
	
	@Test     
	public void testkeyword() throws LexicalException {
		String input = "Z default_width image default_height red alpha green Z ZZ ZZZ height width ZX  blue";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_Z, 0, 1, 1, 1);
		checkNext(scanner, KW_default_width, 2, 13, 1, 3);
		checkNext(scanner, KW_image, 16, 5, 1, 17);
		checkNext(scanner, KW_default_height, 22, 14, 1, 23);
		checkNext(scanner, KW_red, 37, 3, 1, 38);
		checkNext(scanner, KW_alpha, 41, 5, 1, 42);
		checkNext(scanner, KW_green, 47, 5, 1, 48);
		checkNext(scanner, KW_Z, 53, 1, 1, 54);
		checkNext(scanner, IDENTIFIER, 55, 2, 1, 56);
		checkNext(scanner, IDENTIFIER, 58, 3, 1, 59);
		checkNext(scanner, KW_height, 62, 6, 1, 63);
		checkNext(scanner, KW_width, 69, 5, 1, 70);
		checkNext(scanner, IDENTIFIER, 75, 2, 1, 76);
		checkNext(scanner, KW_blue, 79, 4, 1, 80);
		checkNextIsEOF(scanner);
	}
	
	@Test    
	public void testcomment() throws LexicalException{
		String input = "9/*sdfadfdsfasd*f*/ /*this is comments*/ end /*123456789010350*/		/* **/		";                     
		
		Scanner scanner = new Scanner(input).scan(); 	
		show(input);                                 
		show(scanner);  
		checkNext(scanner, INTEGER_LITERAL,0,1,1, 1); 
		checkNext(scanner, IDENTIFIER,41,3,1, 42); 
		checkNextIsEOF(scanner);                     
	}


	
}

