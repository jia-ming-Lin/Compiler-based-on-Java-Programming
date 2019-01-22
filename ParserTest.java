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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Parser;
import cop5556sp18.Scanner;
import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
import cop5556sp18.AST.Expression;
import cop5556sp18.AST.ExpressionBinary;
import cop5556sp18.AST.ExpressionFloatLiteral;
import cop5556sp18.AST.ExpressionFunctionAppWithExpressionArg;
import cop5556sp18.AST.ExpressionIdent;
import cop5556sp18.AST.ExpressionIntegerLiteral;
import cop5556sp18.AST.ExpressionPredefinedName;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.LHS;
import cop5556sp18.AST.LHSIdent;
import cop5556sp18.AST.LHSPixel;
import cop5556sp18.AST.LHSSample;
import cop5556sp18.AST.PixelSelector;
import cop5556sp18.AST.Program;
import cop5556sp18.AST.Statement;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementWrite;
import cop5556sp18.Parser.SyntaxException;
import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.Scanner.Token;
import static cop5556sp18.Scanner.Kind.*;

public class ParserTest {

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
	 * because it lacks an identifier and a block
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		@SuppressWarnings("unused")
		Program p = parser.parse();
	}
	
	/**
	 * Smallest legal program.
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testSmallest() throws LexicalException, SyntaxException {
		String input = "prog{ image im[256,256]; \nfilename f; \ninput f from @0; \nint x;\n int y; \nx := 0; \ny := 0; \nwhile (x < width(im)){ \n y := 0; while (y < height(im)){\nim[x,y] := <<15,255,0,0>>; \nint z; z := im[x,y];y := y + 1; \n};\nx := x + 1;};\nwrite im to f;}";  
		Parser parser = makeParser(input);
		Program p = parser.parse();
		show(p);
	}	
	
	@Test
	public void testcase() throws LexicalException, SyntaxException {
		String input = "prog{filename b;}";  
		Parser parser = makeParser(input);
		Program p = parser.parse();
		show(p);
		Declaration kent=(Declaration)p.block.decsOrStatements.get(0);
		show(kent.type);
	}	
	
	@Test
	public void testcase2() throws LexicalException, SyntaxException {
		String input = "prog{int b; int a10;p{};} ";  
		Parser parser = makeParser(input);
		Program p = parser.parse();
		show(p);
	}	
	
	
	/**
	 * Checks that an element in a block is a declaration with the given type and name.
	 * The element to check is indicated by the value of index.
	 * 
	 * @param block
	 * @param index
	 * @param type
	 * @param name
	 * @return
	 */
	Declaration checkDec(Block block, int index, Kind type,
			String name) {
		ASTNode node = block.decOrStatement(index);
		assertEquals(Declaration.class, node.getClass());
		Declaration dec = (Declaration) node;
		assertEquals(type, dec.type);
		assertEquals(name, dec.name);
		return dec;
	}	
	
	@Test
	public void testDec0() throws LexicalException, SyntaxException {
		String input = "prog{ int var2; var2 := var1[0,0];}";
		Parser parser = makeParser(input);
		Program p = parser.parse();
		show(p);	
		
	}
	
	
	/** This test illustrates how you can test specific grammar elements by themselves by
	 * calling the corresponding parser method directly, instead of calling parse.
	 * This requires that the methods are visible (not private). 
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */
	
	@Test
	public void testExpression() throws LexicalException, SyntaxException {
		String input = "x+2";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);
		
		assertEquals(ExpressionBinary.class, e.getClass());
		ExpressionBinary b = (ExpressionBinary)e;
		assertEquals(ExpressionIdent.class, b.leftExpression.getClass());
		ExpressionIdent left = (ExpressionIdent)b.leftExpression;
		assertEquals("x", left.name);
		assertEquals(ExpressionIntegerLiteral.class, b.rightExpression.getClass());
		ExpressionIntegerLiteral right = (ExpressionIntegerLiteral)b.rightExpression;
		assertEquals(2, right.value);
		assertEquals(OP_PLUS, b.op);
	
	}
	

	@Test
	public void teststate() throws LexicalException, SyntaxException {
		String input = "image foo bar";
		Parser parser = makeParser(input);
		Declaration e = parser.declaration();  //call expression here instead of parse
		show(e);
		/*
		assertEquals(ExpressionBinary.class, e.getClass());
		ExpressionBinary b = (ExpressionBinary)e;
		assertEquals(ExpressionIdent.class, b.leftExpression.getClass());
		ExpressionIdent left = (ExpressionIdent)b.leftExpression;
		assertEquals("x", left.name);
		assertEquals(ExpressionIntegerLiteral.class, b.rightExpression.getClass());
		ExpressionIntegerLiteral right = (ExpressionIntegerLiteral)b.rightExpression;
		assertEquals(2, right.value);
		assertEquals(OP_PLUS, b.op);
	*/
	}
	@Test
	public void testExpression10() throws LexicalException, SyntaxException {
		String input = "(x + 2)";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(ExpressionBinary.class, e.getClass());
		ExpressionBinary b = (ExpressionBinary)e;
		assertEquals(ExpressionIdent.class, b.leftExpression.getClass());
		ExpressionIdent left = (ExpressionIdent)b.leftExpression;
		assertEquals("x", left.name);
		assertEquals(ExpressionIntegerLiteral.class, b.rightExpression.getClass());
		ExpressionIntegerLiteral right = (ExpressionIntegerLiteral)b.rightExpression;
		assertEquals(2, right.value);
		assertEquals(OP_PLUS, b.op);
	}
	@Test
	public void testwrite() throws LexicalException, SyntaxException {
		String input = "write IDENTIFIER to IDENTIFIER2";
		Parser parser = makeParser(input);
		Statement e = parser.statement(); 
		show(e);
		assertEquals(StatementWrite.class, e.getClass());
		StatementWrite b = (StatementWrite)e;
		assertEquals("IDENTIFIER", b.sourceName);
		assertEquals("IDENTIFIER2",b.destName);
	}
	@Test
	public void testwritewrong() throws LexicalException, SyntaxException {
		String input = "write IDENTIFIER to";
		thrown.expect(SyntaxException.class);
		try{
		Parser parser = makeParser(input);
		Statement e = parser.statement(); 
		show(e);
		assertEquals(StatementWrite.class, e.getClass());
		StatementWrite b = (StatementWrite)e;
		assertEquals("IDENTIFIER", b.sourceName);
		assertEquals("IDENTIFIER2",b.destName);
		}
		catch(SyntaxException e){
			show(e);
			throw e;
		}
	}
	@Test
	public void testassignment() throws LexicalException, SyntaxException {
		String input = "case [a**+2-2**5+20,b**2]:=a";
		
		try{
		Parser parser = makeParser(input);
		Statement e = parser.statement(); 
		show(e);
		assertEquals(StatementAssign.class, e.getClass());
		StatementAssign b=(StatementAssign) e;
		assertEquals(LHSPixel.class, b.lhs.getClass());
		LHSPixel k=(LHSPixel) b.lhs;
		assertEquals("case", k.name);
		assertEquals(PixelSelector.class, k.pixelSelector.getClass());
		}
		catch(SyntaxException e){
			show(e);
			throw e;
		}
	}
	@Test
	public void testexpre() throws LexicalException, SyntaxException {
		String input = "x + 2";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(ExpressionBinary.class, e.getClass());
		ExpressionBinary b = (ExpressionBinary)e;
		assertEquals(ExpressionIdent.class, b.leftExpression.getClass());
		ExpressionIdent left = (ExpressionIdent)b.leftExpression;
		assertEquals("x", left.name);
		assertEquals(ExpressionIntegerLiteral.class, b.rightExpression.getClass());
		ExpressionIntegerLiteral right = (ExpressionIntegerLiteral)b.rightExpression;
		assertEquals(2, right.value);
		assertEquals(OP_PLUS, b.op);
	}
	@Test
	public void testexpression2() throws LexicalException, SyntaxException {
		String input = "12 + a * 2";
		Parser parser = makeParser(input);
		Expression e = parser.expression(); 
		show(e);
	}
	@Test
	public void testfunctionapplication() throws LexicalException, SyntaxException {
		String input = "sin(a)";
		Parser parser = makeParser(input);
		Expression e = parser.functionapplication(); 
		show(e);
		assertEquals(ExpressionFunctionAppWithExpressionArg.class,e.getClass());
		ExpressionFunctionAppWithExpressionArg t=(ExpressionFunctionAppWithExpressionArg) e;
		assertEquals(KW_sin,t.function);
		assertEquals(ExpressionIdent.class,t.e.getClass());
		ExpressionIdent a=(ExpressionIdent) t.e;
		assertEquals("a",a.name);
		/*
		assertEquals(ExpressionBinary.class, e.getClass());
		ExpressionBinary b = (ExpressionBinary)e;
		assertEquals(ExpressionIdent.class, b.leftExpression.getClass());
		ExpressionIdent left = (ExpressionIdent)b.leftExpression;
		assertEquals("x", left.name);
		assertEquals(ExpressionIntegerLiteral.class, b.rightExpression.getClass());
		ExpressionIntegerLiteral right = (ExpressionIntegerLiteral)b.rightExpression;
		assertEquals(2, right.value);
		assertEquals(OP_PLUS, b.op);
		*/
	}
	@Test
	public void testfunctionapplication2() throws LexicalException, SyntaxException {
		String input = "polar_r(a)";
		Parser parser = makeParser(input);
		Expression e = parser.functionapplication(); 
		show(e);
		assertEquals(ExpressionFunctionAppWithExpressionArg.class,e.getClass());
		ExpressionFunctionAppWithExpressionArg t=(ExpressionFunctionAppWithExpressionArg) e;
		assertEquals(KW_polar_r,t.function);
		assertEquals(ExpressionIdent.class,t.e.getClass());
		ExpressionIdent a=(ExpressionIdent) t.e;
		assertEquals("a",a.name);
	}
	@Test
	public void testfunctionapplication3() throws LexicalException, SyntaxException {
		String input = "polar_r[a,c]";
		Parser parser = makeParser(input);
		Expression e = parser.functionapplication(); 
		show(e);
		
	}
	@Test
	public void testpred1() throws LexicalException, SyntaxException {
		String input = "Z";
		Parser parser = makeParser(input);
		Expression e = parser.expression(); 
		show(e);
		assertEquals(ExpressionPredefinedName.class,e.getClass());
	}
	@Test
	public void testpred2() throws LexicalException, SyntaxException {
		String input = "default_width";
		Parser parser = makeParser(input);
		Expression e = parser.expression(); 
		show(e);
		assertEquals(ExpressionPredefinedName.class,e.getClass());
		ExpressionPredefinedName k=(ExpressionPredefinedName)e;
		assertEquals(KW_default_width,k.name);
	}
	@Test
	public void teststatement() throws LexicalException, SyntaxException {
		String input = "write IDENTIFIER to IDENTIFIER";
		Parser parser = makeParser(input);
		Statement e = parser.statement(); 
		show(e);
		/*
		assertEquals(ExpressionBinary.class, e.getClass());
		ExpressionBinary b = (ExpressionBinary)e;
		assertEquals(ExpressionIdent.class, b.leftExpression.getClass());
		ExpressionIdent left = (ExpressionIdent)b.leftExpression;
		assertEquals("x", left.name);
		assertEquals(ExpressionIntegerLiteral.class, b.rightExpression.getClass());
		ExpressionIntegerLiteral right = (ExpressionIntegerLiteral)b.rightExpression;
		assertEquals(2, right.value);
		assertEquals(OP_PLUS, b.op);
		*/
	}
	
	
	
	
	/**
	 * Smallest legal program.
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	
	
	@Test
	public void testprimary() throws LexicalException, SyntaxException {
		String input = "b{hi := +-!+-!+-!+-!+-!+-!1>a  ;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		
		Program p=parser.parse();
		show(p);
		assertEquals(1, p.block.decsOrStatements.size());
		
	}
	@Test
	public void testinputandprimary2() throws LexicalException, SyntaxException {
		String input = "b{input test from @ INTEGER_LITERAL;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		Program p=parser.parse();
		show(p);
		Block b=p.block;
		List Lt= b.decsOrStatements;
		StatementInput sinput=(StatementInput)Lt.get(0);
		assertEquals("b", p.progName);
		assertEquals("LBRACE",p.block.firstToken.kind.toString());
		assertEquals(StatementInput.class,Lt.get(0).getClass());
		assertEquals("test",sinput.destName);
		assertEquals("INTEGER_LITERAL",sinput.e.firstToken.getText());
		System.out.println(sinput.e.toString());
	}
	@Test
	public void testpixelselector2() throws LexicalException, SyntaxException {
		String input = "ind{test1 [10,1035.12] :=  int (x);}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		Program p=parser.parse();
		Block b=p.block;
		StatementAssign test=(StatementAssign)b.decsOrStatements.get(0);
		LHSPixel lhs=(LHSPixel) test.lhs;
		
		assertEquals("ind", p.progName);
		assertEquals("test1", test.firstToken.getText());
		assertEquals("test1", lhs.name);
		assertEquals(ExpressionIntegerLiteral.class, lhs.pixelSelector.ex.getClass());
		assertEquals(ExpressionFloatLiteral.class, lhs.pixelSelector.ey.getClass());
		ExpressionFunctionAppWithExpressionArg func=(ExpressionFunctionAppWithExpressionArg)test.e;
		assertEquals(func.function, KW_int);
		System.out.println(func.function);
	}
	@Test//finish
	public void testconstructor() throws LexicalException, SyntaxException {
		String input = "b{input test from @ <<FLOAT_LITERAL,FLOAT_LITERAL,BOOLEAN_LITERAL,BOOLEAN_LITERAL>>;}";  
		Parser parser = makeParser(input);
		Program p=parser.parse();
		show(p);
	}
	@Test
	public void testwhile() throws LexicalException, SyntaxException {
		String input = "b{while(t==10){};}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		Program p=parser.parse();
		show(p);
	}
	@Test
	public void testfunctionalapplication4506() throws LexicalException, SyntaxException {
		String input ="samples{image bird;filename abc;write bird to abc;show bird;sleep(4000);image bird2[width(bird),height(bird)];int x;x:=0;while(x<width(bird2)) {int y;y:=0;while(y<height(bird2)) {blue(bird2[x,y]):=red(bird[x,y]);green(bird2[x,y]):=blue(bird[x,y]);red(bird2[x,y]):=green(bird[x,y]);alpha(bird2[x,y]):=Z;y:=y+1;};x:=x+1;};show bird2;sleep(4000);}";
		Parser parser = makeParser(input);
		Program p=parser.parse();
		show(p);
	}
	
	@Test
	public void testfunctionalapplication() throws LexicalException, SyntaxException {
		String input = "X{ float x; int y; while (log(x) == y) {show x;};}";  
		Parser parser = makeParser(input);
		Program p=parser.parse();
		show(p);
	}
	
	@Test 
	public void testexpression() throws LexicalException, SyntaxException {
		String input = "b{show hi ? QQ : +10.30302 ;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		
		Program p=parser.parse();
		show(p);
	}
	
	@Test 
	public void testandexpression() throws LexicalException, SyntaxException {
		String input = "b{show hi | thanks & No==30!=50 ;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		
		Program p=parser.parse();
		show(p);
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
		Program p=parser.parse();
		show(p);
	}
	@Test
	public void testimage() throws LexicalException, SyntaxException {
		String input = "b{image1 :=1;}";  
		Parser parser = makeParser(input);
		//UnsupportedOperationException
		Program p=parser.parse();
		show(p);
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
		String input = "b{image test [int(10),int(10)];}";  
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
public void testif() throws LexicalException, SyntaxException {
	String input = "b{if(++10){};}";  
	Parser parser = makeParser(input);
	//UnsupportedOperationException
	
	Program p=parser.parse();
	show(p);
}
@Test
public void predefined() throws LexicalException, SyntaxException {
	String input = "b{show true;}";  
	Parser parser = makeParser(input);
	Program p=parser.parse();
	show(p);
}	

@Test
public void show() throws LexicalException, SyntaxException {
	String input = "b{show a+b;sleep(10);}";  
	Parser parser = makeParser(input);
	Program p=parser.parse();
	show(p);
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
		Program p=parser.parse();
		System.out.println(p.toString());
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
        		Program p = parser.parse();
        		show(p);	
        		assertEquals(p.toString(),"Program [progName=demo1, block=Block [decsOrStatements=[Declaration [type=KW_image, name=h, width=null, height=null], StatementInput [destName=h, e=ExpressionIntegerLiteral [value=0]], ShowStatement [e=ExpressionIdent [name=h]], StatementSleep [duration=ExpressionIntegerLiteral [value=4000]], Declaration [type=KW_image, name=g, width=ExpressionFunctionApp [function=KW_width, e=ExpressionIdent [name=h]], height=ExpressionFunctionApp [function=KW_height, e=ExpressionIdent [name=h]]], Declaration [type=KW_int, name=x, width=null, height=null], StatementAssign [lhs=LHSIdent [name=x], e=ExpressionIntegerLiteral [value=0]], StatementWhile [guard=ExpressionBinary [leftExpression=ExpressionIdent [name=x], op=OP_LT, rightExpression=ExpressionFunctionApp [function=KW_width, e=ExpressionIdent [name=g]]], b=Block [decsOrStatements=[Declaration [type=KW_int, name=y, width=null, height=null], StatementAssign [lhs=LHSIdent [name=y], e=ExpressionIntegerLiteral [value=0]], StatementWhile [guard=ExpressionBinary [leftExpression=ExpressionIdent [name=y], op=OP_LT, rightExpression=ExpressionFunctionApp [function=KW_height, e=ExpressionIdent [name=g]]], b=Block [decsOrStatements=[StatementAssign [lhs=LHSPixel [name=g, pixelSelector=PixelSelector [ex=ExpressionIdent [name=x], ey=ExpressionIdent [name=y]]], e=ExpressionPixel [name=h, pixelSelector=PixelSelector [ex=ExpressionIdent [name=y], ey=ExpressionIdent [name=x]]]], StatementAssign [lhs=LHSIdent [name=y], e=ExpressionBinary [leftExpression=ExpressionIdent [name=y], op=OP_PLUS, rightExpression=ExpressionIntegerLiteral [value=1]]]]]], StatementAssign [lhs=LHSIdent [name=x], e=ExpressionBinary [leftExpression=ExpressionIdent [name=x], op=OP_PLUS, rightExpression=ExpressionIntegerLiteral [value=1]]]]]], ShowStatement [e=ExpressionIdent [name=g]], StatementSleep [duration=ExpressionIntegerLiteral [value=4000]]]]]");
        }
@Test
public void testdec() throws LexicalException, SyntaxException {
        String input ="image hi [a,a]";
         
        Parser parser = makeParser(input);
		Declaration p = parser.declaration();
		show(p);	

}

@Test
        public void makeRedImage() throws LexicalException, SyntaxException {
                String input = "makeRedImage{image im[256,256];int x;int y;x:=0;y:=0;while(x<width(im)) {y:=0;while(y<height(im)) {im[x,y]:=<<255,255,0,0>>;y:=y+1;};x:=x+1;};show im;}";
                Parser parser = makeParser(input);
                Program p = parser.parse();
                show(p);
                assertEquals(p.toString(),"Program [progName=makeRedImage, block=Block [decsOrStatements=[Declaration [type=KW_image, name=im, width=ExpressionIntegerLiteral [value=256], height=ExpressionIntegerLiteral [value=256]], Declaration [type=KW_int, name=x, width=null, height=null], Declaration [type=KW_int, name=y, width=null, height=null], StatementAssign [lhs=LHSIdent [name=x], e=ExpressionIntegerLiteral [value=0]], StatementAssign [lhs=LHSIdent [name=y], e=ExpressionIntegerLiteral [value=0]], StatementWhile [guard=ExpressionBinary [leftExpression=ExpressionIdent [name=x], op=OP_LT, rightExpression=ExpressionFunctionApp [function=KW_width, e=ExpressionIdent [name=im]]], b=Block [decsOrStatements=[StatementAssign [lhs=LHSIdent [name=y], e=ExpressionIntegerLiteral [value=0]], StatementWhile [guard=ExpressionBinary [leftExpression=ExpressionIdent [name=y], op=OP_LT, rightExpression=ExpressionFunctionApp [function=KW_height, e=ExpressionIdent [name=im]]], b=Block [decsOrStatements=[StatementAssign [lhs=LHSPixel [name=im, pixelSelector=PixelSelector [ex=ExpressionIdent [name=x], ey=ExpressionIdent [name=y]]], e=ExpressionPixelConstructor [alpha=ExpressionIntegerLiteral [value=255], red=ExpressionIntegerLiteral [value=255], green=ExpressionIntegerLiteral [value=0], blue=ExpressionIntegerLiteral [value=0]]], StatementAssign [lhs=LHSIdent [name=y], e=ExpressionBinary [leftExpression=ExpressionIdent [name=y], op=OP_PLUS, rightExpression=ExpressionIntegerLiteral [value=1]]]]]], StatementAssign [lhs=LHSIdent [name=x], e=ExpressionBinary [leftExpression=ExpressionIdent [name=x], op=OP_PLUS, rightExpression=ExpressionIntegerLiteral [value=1]]]]]], ShowStatement [e=ExpressionIdent [name=im]]]]]");
        }

@Test
        public void testPolarR2() throws LexicalException, SyntaxException {
                String input = "PolarR2{image im[1024,1024];int x;x:=0;while(x<width(im)) {int y;y:=0;while(y<height(im)) {float p;p:=polar_r[x,y];int r;r:=int(p)%Z;im[x,y]:=<<Z,0,0,r>>;y:=y+1;};x:=x+1;};show im;}";
                Parser parser = makeParser(input);
                Program p = parser.parse();
                show(p);
                assertEquals(p.toString(),"Program [progName=PolarR2, block=Block [decsOrStatements=[Declaration [type=KW_image, name=im, width=ExpressionIntegerLiteral [value=1024], height=ExpressionIntegerLiteral [value=1024]], Declaration [type=KW_int, name=x, width=null, height=null], StatementAssign [lhs=LHSIdent [name=x], e=ExpressionIntegerLiteral [value=0]], StatementWhile [guard=ExpressionBinary [leftExpression=ExpressionIdent [name=x], op=OP_LT, rightExpression=ExpressionFunctionApp [function=KW_width, e=ExpressionIdent [name=im]]], b=Block [decsOrStatements=[Declaration [type=KW_int, name=y, width=null, height=null], StatementAssign [lhs=LHSIdent [name=y], e=ExpressionIntegerLiteral [value=0]], StatementWhile [guard=ExpressionBinary [leftExpression=ExpressionIdent [name=y], op=OP_LT, rightExpression=ExpressionFunctionApp [function=KW_height, e=ExpressionIdent [name=im]]], b=Block [decsOrStatements=[Declaration [type=KW_float, name=p, width=null, height=null], StatementAssign [lhs=LHSIdent [name=p], e=ExpressionFunctionAppWithPixel [name=KW_polar_r, e0=ExpressionIdent [name=x], e1=ExpressionIdent [name=y]]], Declaration [type=KW_int, name=r, width=null, height=null], StatementAssign [lhs=LHSIdent [name=r], e=ExpressionBinary [leftExpression=ExpressionFunctionApp [function=KW_int, e=ExpressionIdent [name=p]], op=OP_MOD, rightExpression=ExpressionPredefinedName [name=KW_Z]]], StatementAssign [lhs=LHSPixel [name=im, pixelSelector=PixelSelector [ex=ExpressionIdent [name=x], ey=ExpressionIdent [name=y]]], e=ExpressionPixelConstructor [alpha=ExpressionPredefinedName [name=KW_Z], red=ExpressionIntegerLiteral [value=0], green=ExpressionIntegerLiteral [value=0], blue=ExpressionIdent [name=r]]], StatementAssign [lhs=LHSIdent [name=y], e=ExpressionBinary [leftExpression=ExpressionIdent [name=y], op=OP_PLUS, rightExpression=ExpressionIntegerLiteral [value=1]]]]]], StatementAssign [lhs=LHSIdent [name=x], e=ExpressionBinary [leftExpression=ExpressionIdent [name=x], op=OP_PLUS, rightExpression=ExpressionIntegerLiteral [value=1]]]]]], ShowStatement [e=ExpressionIdent [name=im]]]]]");
        }

@Test
        public void testSamasdples() throws LexicalException, SyntaxException {
                String input = "samples{image bird; input bird from @0;show bird;sleep(4000);image bird2[width(bird),height(bird)];int x;x:=0;while(x<width(bird2)) {int y;y:=0;while(y<height(bird2)) {blue(bird2[x,y]):=red(bird[x,y]);green(bird2[x,y]):=blue(bird[x,y]);red(bird2[x,y]):=green(bird[x,y]);alpha(bird2[x,y]):=Z;y:=y+1;};x:=x+1;};show bird2;sleep(4000);}";
                Parser parser = makeParser(input);
                Program p = parser.parse();
                show(p);
                assertEquals(p.toString(),"Program [progName=samples, block=Block [decsOrStatements=[Declaration [type=KW_image, name=bird, width=null, height=null], StatementInput [destName=bird, e=ExpressionIntegerLiteral [value=0]], ShowStatement [e=ExpressionIdent [name=bird]], StatementSleep [duration=ExpressionIntegerLiteral [value=4000]], Declaration [type=KW_image, name=bird2, width=ExpressionFunctionApp [function=KW_width, e=ExpressionIdent [name=bird]], height=ExpressionFunctionApp [function=KW_height, e=ExpressionIdent [name=bird]]], Declaration [type=KW_int, name=x, width=null, height=null], StatementAssign [lhs=LHSIdent [name=x], e=ExpressionIntegerLiteral [value=0]], StatementWhile [guard=ExpressionBinary [leftExpression=ExpressionIdent [name=x], op=OP_LT, rightExpression=ExpressionFunctionApp [function=KW_width, e=ExpressionIdent [name=bird2]]], b=Block [decsOrStatements=[Declaration [type=KW_int, name=y, width=null, height=null], StatementAssign [lhs=LHSIdent [name=y], e=ExpressionIntegerLiteral [value=0]], StatementWhile [guard=ExpressionBinary [leftExpression=ExpressionIdent [name=y], op=OP_LT, rightExpression=ExpressionFunctionApp [function=KW_height, e=ExpressionIdent [name=bird2]]], b=Block [decsOrStatements=[StatementAssign [lhs=LHSSample [name=bird2, pixelSelector=PixelSelector [ex=ExpressionIdent [name=x], ey=ExpressionIdent [name=y]], color=KW_blue], e=ExpressionFunctionApp [function=KW_red, e=ExpressionPixel [name=bird, pixelSelector=PixelSelector [ex=ExpressionIdent [name=x], ey=ExpressionIdent [name=y]]]]], StatementAssign [lhs=LHSSample [name=bird2, pixelSelector=PixelSelector [ex=ExpressionIdent [name=x], ey=ExpressionIdent [name=y]], color=KW_green], e=ExpressionFunctionApp [function=KW_blue, e=ExpressionPixel [name=bird, pixelSelector=PixelSelector [ex=ExpressionIdent [name=x], ey=ExpressionIdent [name=y]]]]], StatementAssign [lhs=LHSSample [name=bird2, pixelSelector=PixelSelector [ex=ExpressionIdent [name=x], ey=ExpressionIdent [name=y]], color=KW_red], e=ExpressionFunctionApp [function=KW_green, e=ExpressionPixel [name=bird, pixelSelector=PixelSelector [ex=ExpressionIdent [name=x], ey=ExpressionIdent [name=y]]]]], StatementAssign [lhs=LHSSample [name=bird2, pixelSelector=PixelSelector [ex=ExpressionIdent [name=x], ey=ExpressionIdent [name=y]], color=KW_alpha], e=ExpressionPredefinedName [name=KW_Z]], StatementAssign [lhs=LHSIdent [name=y], e=ExpressionBinary [leftExpression=ExpressionIdent [name=y], op=OP_PLUS, rightExpression=ExpressionIntegerLiteral [value=1]]]]]], StatementAssign [lhs=LHSIdent [name=x], e=ExpressionBinary [leftExpression=ExpressionIdent [name=x], op=OP_PLUS, rightExpression=ExpressionIntegerLiteral [value=1]]]]]], ShowStatement [e=ExpressionIdent [name=bird2]], StatementSleep [duration=ExpressionIntegerLiteral [value=4000]]]]]");
        }
@Test
public void testassign() throws LexicalException, SyntaxException {
	String input="assign{int a;a:=3;}" ;
	Parser parser = makeParser(input);
    Program p = parser.parse();
    show(p);
}
}

	

