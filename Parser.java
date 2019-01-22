package cop5556sp18;
/* *
 * Initial code for SimpleParser for the class project in COP5556 Programming Language Principles 
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


import cop5556sp18.Scanner.Token;
import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.LexicalException;
import static cop5556sp18.Scanner.Kind.*;
import java.util.ArrayList;
import java.util.Arrays;
import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
import cop5556sp18.AST.Expression;
import cop5556sp18.AST.ExpressionBinary;
import cop5556sp18.AST.ExpressionBooleanLiteral;
import cop5556sp18.AST.ExpressionConditional;
import cop5556sp18.AST.ExpressionFloatLiteral;
import cop5556sp18.AST.ExpressionFunctionAppWithExpressionArg;
import cop5556sp18.AST.ExpressionFunctionAppWithPixel;
import cop5556sp18.AST.ExpressionIdent;
import cop5556sp18.AST.ExpressionIntegerLiteral;
import cop5556sp18.AST.ExpressionPixelConstructor;
import cop5556sp18.AST.ExpressionPixel;
import cop5556sp18.AST.ExpressionPredefinedName;
import cop5556sp18.AST.ExpressionUnary;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.StatementWhile;
import cop5556sp18.AST.LHS;
import cop5556sp18.AST.LHSIdent;
import cop5556sp18.AST.LHSPixel;
import cop5556sp18.AST.LHSSample;
import cop5556sp18.AST.PixelSelector;
import cop5556sp18.AST.Program;
import cop5556sp18.AST.Statement;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementIf;
import cop5556sp18.AST.StatementWrite;
import cop5556sp18.Parser.SyntaxException;


public class Parser {
	Scanner scanner;
	Token t;
	
	// Initialization
	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		Token t;
		public SyntaxException(String message) {
			super(message);
		}
		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}
		
	}//(throw exception)
	
	void error(Token t, String m) throws SyntaxException {
		String message = m + " is at " + t.line() + ":" + t.posInLine();
		throw new SyntaxException(t, message);
	}
	void error(Kind... kind) throws SyntaxException {
		String kinds = Arrays.toString(kind);	
		throw new SyntaxException(t,  "Wrong!");
	}

	Program parse() throws SyntaxException {
		Program e1=program();
		matchEOF();
		return e1;
	}

	/*
	 * Program ::= Identifier Block
	 */
	Program program() throws SyntaxException {
		Token first=t;
		Token progName=match(IDENTIFIER);
		Block e1=block();
		return new Program(first,progName,e1);
	}
	
	/*
	 * Block ::=  { (  (Declaration | Statement) ; )* }
	 */
	//Type
	Kind[] firstDec = { KW_int, KW_boolean, KW_image, KW_float, KW_filename };
	//todo
	Kind[] firstStatement = {KW_sleep,KW_while,IDENTIFIER,KW_if,KW_show,KW_write,KW_input,
			/*color*/KW_red,KW_blue,KW_green,KW_alpha};
	
	Kind[] functionName={KW_polar_r,KW_cart_x/* cart_x*/, KW_cart_y/* cart_y */, 
			KW_polar_a/* polar_a*/, KW_polar_r/* polar_r*/, KW_abs/* abs */, KW_sin/* sin*/, KW_cos/* cos */, 
			KW_atan/* atan */, KW_log/* log */,KW_int/* int */, KW_float /* float */,KW_width,KW_height,
			KW_red /* red */, KW_blue/* blue */, 
			
			
			KW_green /* green */, KW_alpha /* alpha*/};
	Kind[] PredefinedName={KW_Z,KW_default_width,KW_default_height};
	
	
	Block block() throws SyntaxException {
		Token first=t;
		match(LBRACE);
		ArrayList<ASTNode> Decsandstat = new ArrayList<ASTNode>();
		
		while (isKind(firstDec)|isKind(firstStatement)) {
	     if (isKind(firstDec)) {
	    	 Declaration e = declaration();
	    	 Decsandstat.add(e);	
		} else if(isKind(firstStatement)) {
			Statement s=statement();
			Decsandstat.add(s);
		}
		match(SEMI);
		}
		match(RBRACE);
		return new Block(first, Decsandstat) ;
	}
	public PixelSelector pixelselector() throws SyntaxException{
		if (t.kind==LSQUARE){
		Token first=match(LSQUARE);
		Expression e1=expression();
		match(COMMA);
		Expression e2=expression();
		match(RSQUARE);
		return new PixelSelector(first,e1,e2);
		}
		else error(t,"Wrong happen in pixelselector");
		return null;
	}
	
	
	Declaration declaration() throws SyntaxException{
		Token first = t;
		Token type = consume();
		Token name = match(IDENTIFIER);
		Expression e1 = null;
		Expression e2= null;
		if (type.kind==KW_image){
			if(isKind(LSQUARE)){
			consume();
			 e1 =expression();
			match(COMMA);
			 e2 =expression();
			match(RSQUARE);
			}
		}
		 return new Declaration(first,type,name,e1,e2);
		//	throw new UnsupportedOperationException();
		
	}
	
	
	Statement statement() throws SyntaxException {
		Token first = t;
		switch(t.kind){
		case KW_input:{
			consume();//consume input
			Token destName = match(IDENTIFIER);
			match(KW_from);
			match(OP_AT);
			Expression e1=expression();
			return new StatementInput(first, destName, e1);
		}
		case KW_write:{
			consume();//consume output
			Token sourceName = match(IDENTIFIER);
			match(KW_to);
			Token destName =match(IDENTIFIER);
			return new StatementWrite(first, sourceName, destName);
		}
		////LHS
		case KW_red:{
			Token color=consume();
			match(LPAREN);
			Token  name=match(IDENTIFIER);
			PixelSelector selector=pixelselector();
			match(RPAREN);
			match(OP_ASSIGN);
			LHS lhs=new LHSSample(first, name, selector, color);
			Expression e1=expression();
			return new StatementAssign(first, lhs, e1);
			}
		case KW_green:{
			Token color=consume();
			match(LPAREN);
			Token  name=match(IDENTIFIER);
			PixelSelector selector=pixelselector();
			match(RPAREN);
			match(OP_ASSIGN);
			LHS lhs=new LHSSample(first, name, selector, color);
			Expression e1=expression();
			return new StatementAssign(first, lhs, e1);
			}
			
		case KW_alpha:{
			Token color=consume();
			match(LPAREN);
			Token  name=match(IDENTIFIER);
			PixelSelector selector=pixelselector();
			match(RPAREN);
			match(OP_ASSIGN);
			LHS lhs=new LHSSample(first, name, selector, color);
			Expression e1=expression();
			return new StatementAssign(first, lhs, e1);
			}
		case KW_blue:{
			Token color=consume();
			match(LPAREN);
			Token  name=match(IDENTIFIER);
			PixelSelector selector=pixelselector();
			match(RPAREN);
			match(OP_ASSIGN);
			LHS lhs=new LHSSample(first, name, selector, color);
			Expression e1=expression();
			return new StatementAssign(first, lhs, e1);
			}
		///// Color check finished	
		case IDENTIFIER:{
			Token name=consume();
			if (isKind(LSQUARE))
			{	
				PixelSelector pixel=pixelselector();
				LHS e1=new LHSPixel(first, name, pixel);
				match(OP_ASSIGN);
				Expression e2=expression();
				return new StatementAssign(first,e1,e2);
			}
			
			else{
				LHS e1=new LHSIdent(first,name);
				match(OP_ASSIGN);
				Expression e2=expression();
				return new StatementAssign(first,e1,e2);
			}
			
			
		}
		
		/////////////////////////////////////////////////////////////////////////
		case KW_while:{
			consume();//consume while
			match(LPAREN);
			Expression e1=expression();
			match(RPAREN);
			Block b=block();
			return new StatementWhile(first, e1, b);
		}
			
		case KW_if:{
			consume();
			match(LPAREN);
			Expression e1=expression();
			match(RPAREN);
			Block b=block();
			return new StatementIf(first, e1, b);
		}
/////////////////////////////////////////////////////////////////////////
		case KW_show:{
			consume();
			Expression e1=expression();
			return new StatementShow(first, e1);
			}
		case KW_sleep:{
		consume();
		Expression e1=expression();	
		return new StatementSleep(first, e1);
		}
		default:{
			throw new UnsupportedOperationException();
		}
		}
	}
	
	
	
	Expression expression() throws SyntaxException{
		Token first=t;
		Expression e1=OrExpression();
		if (t.kind==OP_QUESTION){
		consume();
		Expression e2=expression();
		match(OP_COLON);
		Expression e3=expression();
		e1= new ExpressionConditional(first,e1,e2,e3);
		}
		return e1;
	}
	Expression OrExpression() throws SyntaxException{
		Token first=t;
		Expression e1=AndExpression();
		while(t.kind==OP_OR){
			Token op=consume();
			Expression e2=AndExpression();
			e1=new ExpressionBinary(first,e1,op,e2);
		}
		return e1;
	}
	Expression AndExpression() throws SyntaxException{
		Token first=t;
		Expression e1=EqExpression();
		while(t.kind==OP_AND){
			Token op=consume();
			Expression e2=EqExpression();
			e1=new ExpressionBinary(first,e1,op,e2);
		}
		return e1;
	}
	Expression EqExpression() throws SyntaxException{
		Token first=t;
		Expression e1=RelExpression();
		while(t.kind==OP_NEQ|t.kind==OP_EQ){
			Token op=consume();
			Expression e2=RelExpression();
			e1=new ExpressionBinary(first,e1,op,e2);
		}
		return e1;
	}
	Expression RelExpression() throws SyntaxException{
		Token first=t;
		Expression e1=AddExpression();
		while(t.kind==OP_LE|t.kind==OP_GE|t.kind==OP_GT|t.kind==OP_LT){
			Token op=consume();
			Expression e2=AddExpression();
			e1=new ExpressionBinary(first,e1,op,e2);
		}
		return e1;
		
	}
	Expression AddExpression() throws SyntaxException{
		Token first=t;
		Expression e1=MultExpression();
		while(t.kind==OP_PLUS|t.kind==OP_MINUS){
			Token op=consume();
			Expression e2=MultExpression();
			e1=new ExpressionBinary(first,e1,op,e2);
		}
		return e1;
	}
	//MultExpression
	Expression MultExpression() throws SyntaxException{
		Token first=t;
		Expression e1= Powerexpression();
		while (t.kind==OP_MOD|t.kind==OP_DIV|t.kind==OP_TIMES){
			Token op=consume();
			Expression e2 = Powerexpression();
			e1=new ExpressionBinary(first,e1,op,e2);
		}
		return e1;
	}
	// Powerexpression
	Expression Powerexpression()throws SyntaxException{
		Token first=t;
		Expression e1=Unaryexpression();
		if (isKind(OP_POWER)){
			Token op=consume();
			Expression e2=Powerexpression();
			return new ExpressionBinary(first,e1,op,e2); 
		}
		return e1;
		
	}
	//UnaryExpression
	Expression Unaryexpression() throws SyntaxException{
		Token first=t;
		if (t.kind==OP_PLUS|t.kind==OP_MINUS){
			Token op=consume();
			Expression e1=Unaryexpression();
			return new ExpressionUnary(first,op,e1); 
		}
		else {
			return  Unaryexpressionotplusminus();
		}
	}
	//UnaryExpressionNotPlusMinus
	
	Expression Unaryexpressionotplusminus() throws SyntaxException{
		Token first=t;
			if (t.kind==OP_EXCLAMATION){
				Token op=consume();
				Expression e = Unaryexpression();
				return new ExpressionUnary(first,op,e);		
			}
			else{
				return primary();
			}
	}
	
///////////////////////////////////////////////////////////////////	
	/*Primary ::= INTEGER_LITERAL | BOOLEAN_LITERAL | FLOAT_LITERAL |
	( Expression ) | FunctionApplication | IDENTIFIER | PixelExpression |
	PredefinedName | PixelConstructor*/
	Expression  functionapplication() throws SyntaxException{
		Token first= t;
		Token name = consume();
		if (t.kind==(LPAREN)){
			consume();//Consume LPAREN
			Expression e=expression();
			match(RPAREN);
			return new ExpressionFunctionAppWithExpressionArg(first, name, e);
		}
		else if(t.kind==(LSQUARE)){
			consume();
			Expression e1=expression();
			match(COMMA);
			Expression e2=expression();
			match(RSQUARE);
			return new ExpressionFunctionAppWithPixel(first,name, e1, e2);
		}
		else{
		error(t, "Error in functionapplication");
		return null;}
	}
	
	protected Expression primary() throws SyntaxException{
		Kind kind=t.kind;
		Token first=t;
		switch(kind){
		case INTEGER_LITERAL:{
			Token intLit = consume();
			return new ExpressionIntegerLiteral(first, intLit);
		}
		
		case BOOLEAN_LITERAL:{
			Token booleanlit = consume();
			return new ExpressionBooleanLiteral(first, booleanlit);
		}
		
		case FLOAT_LITERAL:{
			Token float_leanit=consume();
			return new ExpressionFloatLiteral(first,float_leanit);
		}
	
		case LPAREN: {
			consume();
			Expression e=expression();
			match(RPAREN);
			return e;
		}
		
		case LPIXEL:{
			match(LPIXEL);
			Expression a= expression();
			match(COMMA);
			Expression b= expression();
			match(COMMA);
			Expression c= expression();
			match(COMMA);
			Expression d= expression();
			match(RPIXEL);
			return new ExpressionPixelConstructor(first, a,b, c, d);
		}
		
		/*PixelSelector ::= [ Expression , Expression ]*/
		case IDENTIFIER:{
			Token name=consume();
			//PixelExpression
			if (t.kind==LSQUARE){
				PixelSelector pixelSelector=pixelselector();
				return new ExpressionPixel(first, name, pixelSelector);
				}
			return new ExpressionIdent(first, name);
		}
		
		case KW_polar_r:
		case KW_cart_x:
		case KW_cart_y:
		case KW_polar_a:
		case KW_abs:
		case KW_sin:
		case KW_cos:
		case KW_atan:
		case KW_log:
		case KW_int:
		case KW_float:
		case KW_width:
		case KW_height:
		case KW_red:
		case KW_blue:
		case KW_green:
		case KW_alpha:{
			Expression e=functionapplication();
			return e;
		}
		
		case KW_Z:
		case KW_default_width:
		case KW_default_height:{
			Token t = consume();
			return new ExpressionPredefinedName(first,t);
		}
		
		default:
			error(t, "Wrong is in expression");
			return null; 
		}
		//End Switch
		
			
	}
	
	
	
	
	
	
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind){
				return true;
			}
		}
		return false;
	}
	/**
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	 Token match(Kind kind) throws SyntaxException {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		throw new SyntaxException(t,"Syntax error happen at position "+t.pos+"and the kind is"+t.kind.toString()+"."); //TODO  give a better error message!
		 // unreachable
	}


	private Token consume() throws SyntaxException {
		Token tmp = t;
		if (isKind(EOF)) {
			throw new SyntaxException(t,"EoF  should not be here.");  
			//Note that EOF should be matched by the matchEOF method which is called only in parse().  
			//Anywhere else is an error. */
		}
		t = scanner.nextToken();
		return tmp;
	}


	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (isKind(EOF)) {
			return t;
		}
		throw new SyntaxException(t,"Syntax Error, there is no EOF"); //TODO  give a better error message!
	}
	


}

