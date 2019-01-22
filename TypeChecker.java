package cop5556sp18;

import cop5556sp18.Scanner.Token;

import java.util.*;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.ASTVisitor;
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
import cop5556sp18.AST.ExpressionPixel;
import cop5556sp18.AST.ExpressionPixelConstructor;
import cop5556sp18.AST.ExpressionPredefinedName;
import cop5556sp18.AST.ExpressionUnary;
import cop5556sp18.AST.LHSIdent;
import cop5556sp18.AST.LHSPixel;
import cop5556sp18.AST.LHSSample;
import cop5556sp18.AST.PixelSelector;
import cop5556sp18.AST.Program;
import cop5556sp18.AST.Statement;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementIf;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.StatementWhile;
import cop5556sp18.AST.StatementWrite;
import cop5556sp18.Types.*;
import cop5556sp18.Types.Type;

public class TypeChecker implements ASTVisitor {
	// symbol table part
	int  current_scope;
	int  next_scope;
	Stack<Integer> scope_stack=new Stack<Integer>();
	HashMap <String, ArrayList<decnode>> st;
	TypeChecker(){
		scope_stack.push(0);
		current_scope=0;
		next_scope = 1;
		st = new HashMap <String, ArrayList<decnode>>();
	}
	public class decnode 
	{
		  int dec_scope;
		  Declaration dec;
		  public decnode(int s, Declaration d)
		  {
			  dec_scope = s;
			  dec = d;
		  }
		  public int getScope()
		  {
			  return dec_scope;
		  }
		  public Declaration getDec()
		  {
			  return dec;
		  }
	}
	void enterScope()	
	{   current_scope = next_scope++; 
		scope_stack.push(current_scope);
	}
	void leaveScope(){
		 
		 scope_stack.pop();
		 current_scope = scope_stack.peek();
	 }
	 public Declaration lookup(String ident){
		 if(!st.containsKey(ident)){
				return null;}
			
			Declaration dec=null;
			ArrayList<decnode> ps = st.get(ident);
			for(int i=ps.size()-1;i>=0;i--)
			{
				int temp_scope = ps.get(i).getScope();
				if(scope_stack.contains(temp_scope))
				{
					dec = ps.get(i).getDec();
					break;
				}
			}
			return dec;
		}
	 
	 boolean insert(String ident, Declaration dec){
		 ArrayList<decnode> ps = new ArrayList<decnode>();
			decnode p = new decnode(current_scope, dec);
			if(st.containsKey(ident))
			{
				ps = st.get(ident);
				for(decnode it: ps)
				{
					if(it.getScope()==current_scope)
						return false;
				}
			}
			ps.add(p);
			st.put(ident, ps);		
			return true;	
		}
	 
	 public Type inferredType(Type e0,Type e1,Kind op)throws Exception{
		 switch(op){
		 case OP_PLUS:
		 case OP_MINUS:
		 case OP_TIMES:
		 case OP_DIV:
		 case OP_POWER:{
			 //System.out.println("HI");
			 if(!((e0==Type.FLOAT|e0==Type.INTEGER)&(e1==Type.FLOAT|e1==Type.INTEGER))){
				 throw new SemanticException("The problem occurs in inferredTypeFunctionApp that type is not INTEGER");
			 }
			 if(e0==Type.FLOAT&e1==Type.FLOAT){
				 return Type.FLOAT;
			 }
			 if(e0==Type.FLOAT&e1==Type.INTEGER){
				 return Type.FLOAT;
			 }
			 if(e0==Type.INTEGER&e1==Type.FLOAT){
				 return Type.FLOAT;
			 }
			 if(e0==Type.INTEGER&e1==Type.INTEGER){
				 return Type.INTEGER;
			 }
		 }
		 case OP_MOD:{
			 if(!((e0==Type.INTEGER)&(e1==Type.INTEGER))){
				 throw new SemanticException("The problem occurs in inferredTypeFunctionApp that type is not INTEGER(Mod)");
			 }
		 }
		 case OP_AND:
		 case OP_OR:{
			 if(!((e0==Type.BOOLEAN)&(e1==Type.BOOLEAN)|(e0==Type.INTEGER)&(e1==Type.INTEGER))){
				 throw new SemanticException("The problem occurs in inferredTypeFunctionApp that type is not wrong(AND/OR)");
			 }
			 return e0;
		 }
		 case OP_EQ:
		 case OP_NEQ:
		 case OP_GE:
		 case OP_LE:
		 case OP_GT:
		 case OP_LT:{
			 if(!(((e0==Type.BOOLEAN)&(e1==Type.BOOLEAN))|((e0==Type.INTEGER)&(e1==Type.INTEGER))|((e0==Type.FLOAT)&(e1==Type.FLOAT)))){
				 throw new SemanticException(
						 "The problem occurs in inferredTypeFunctionApp that type is wrong(Comparision operator)");
			 }
			 return Type.BOOLEAN;
		 }
			 
		 default:{
				throw new SemanticException(" It is not a legal ExpressionBinary.");
			}	 
		 }
	}
	 public Type inferredTypeFunctionApp(Kind functionname,Type type)throws Exception{
		 
		switch(functionname){ 
	 	case KW_red:
	 	case KW_green:
	 	case KW_blue:
	 	case KW_alpha:
	 	{	
	 		if(type!=Type.INTEGER){
	 			throw new SemanticException("The problem occurs in inferredTypeFunctionApp(red,green,blue,alpha) that type is not INTEGER");
	 		}
	 		return type.INTEGER;
	 	}
	 	case KW_abs:{
	 		if(!(type==Type.INTEGER|type==Type.FLOAT)){
	 			throw new SemanticException(
	 					"The problem occurs in inferredTypeFunctionApp(abs) that type is not INTEGER or Float");
	 		}
	 		return type;
	 	}
	 		
	 	case KW_sin:
	 	case KW_cos:
	 	case KW_atan:
	 	case KW_log:
	 	{
	 		if(!(type==Type.FLOAT)){
	 			throw new SemanticException(
	 					"The problem occurs in inferredTypeFunctionApp(sin cos atan log) that type is not Float");
	 		}
	 		return type.FLOAT;
	 	}
	 	case KW_width:
	 	case KW_height:
	 	{
	 		if(!(type==Type.IMAGE)){
	 			throw new SemanticException(
	 					"The problem occurs in inferredTypeFunctionApp('width height') that type is not imgage");
	 		}
	 		return type.INTEGER;
	 	}
	 	case KW_float:
	 	{
	 		if(!(type==Type.INTEGER|type==Type.FLOAT)){
	 			throw new SemanticException(
	 					"The problem occurs in inferredTypeFunctionApp('float function') that type is not INTEGER or Float");
	 		}
	 		return type.FLOAT;
	 	}	
	 	case KW_int:
	 	{
	 		if(!(type==Type.INTEGER|type==Type.FLOAT)){
	 			throw new SemanticException(
	 					"The problem occurs in inferredTypeFunctionApp('Integer function') that type is not INTEGER or Float");
	 		}
	 		return type.INTEGER;
	 	}
	 	default:{
			throw new SemanticException(" It is not a legal ExpressionFunctionAppWithExpressionArg.");
		}
		}
	 }
	 
	// 

	
	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super(message);
			this.t = t;
		}
		public SemanticException(String message){
			super(message);
		}
	}
	
	
	// Name is only used for naming the output file. 
	// Visit the child block to type check program.
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		
		program.block.visit(this, arg);
		
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		enterScope();
		for (ASTNode it : block.decsOrStatements){
			it.visit(this,null);
		}
		leaveScope();
		return null;
	}
//Declaration(Token firstToken, Kind type, String name, Expression width, Expression height)
	@Override
	public Object visitDeclaration(Declaration declaration, Object arg) throws Exception {
		//System.out.println(declaration.gettype());
		declaration.set_outputtype(declaration.gettype());

		if(!insert(declaration.name, declaration)){
			throw new SemanticException ("The identifier is duplicatedly defined in current scope.");
		}
		//System.out.println("Fuck");
		if ((declaration.height==null)){
			if(declaration.width!=null)
			throw new SemanticException ("Expression1 is not null");
		}
		//System.out.println("Fuck1");
		if ((declaration.width==null)){
			if(declaration.height!=null)
			throw new SemanticException ("Expression0 is not null");
		}
		//System.out.println("Fuck2");
		//|(declaration.height.get_Typename()==Type.INTEGER&declaration.get_Typename()==Type.IMAGE)
		if((declaration.height!=null)){
			if(!(declaration.height.visit(this, null)==Type.INTEGER&declaration.get_Typename()==Type.IMAGE)){
				throw new SemanticException ("Wrong is in height");
			}
		}
		//System.out.println("Fuck3");
		//|(declaration.width.get_Typename()==Type.INTEGER&declaration.get_Typename()==Type.IMAGE)
		if(!(declaration.width==null)){
			if(!(declaration.width.visit(this, null)==Type.INTEGER&declaration.get_Typename()==Type.IMAGE)){
				throw new SemanticException ("Wrong is in width");
			}
		}
		//System.out.println("Fuck4");
		
		return declaration.get_Typename();
	}

	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg) throws Exception {
		//System.out.println(st);
		statementWrite.set_sourceDec(lookup(statementWrite.sourceName));
		//System.out.println(statementWrite.sourceName);
		if (statementWrite.sourceDec==null){
			throw new SemanticException ("The sourceDec is null in visitStatementWrite");
		}
		//System.out.println(statementWrite.sourceName);
		statementWrite.set_destDec(lookup(statementWrite.destName));
		//System.out.println(statementWrite.destName);
		if (statementWrite.destDec==null){
				throw new SemanticException ("The destDec is null in visitStatementWrite");
		};
		//System.out.println(statementWrite.destName);
		//System.out.println(statementWrite.sourceDec.output_type);
		if(statementWrite.sourceDec.output_type!=Type.IMAGE){
			throw new SemanticException ("The sourceDec is not image type in visitStatementWrite");
		}
		//System.out.println(statementWrite.sourceDec.type);
		if(statementWrite.destDec.output_type!=Type.FILE){
			throw new SemanticException ("The sourceDec is not image type in visitStatementWrite");
		}
		
		return null;
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg) throws Exception {
		statementInput.set_declartion(lookup(statementInput.destName));
		if (statementInput.dec==null){
			throw new SemanticException ("Declaration is null in StatementInput.");
		}
		Type expr=(Type) statementInput.e.visit(this,null);
		if(expr!=Type.INTEGER){
			throw new SemanticException ("Expression is not INTEGER in StatementInput.");
		}
		return null;
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
		//System.out.println(pixelSelector.ex);
		//System.out.println(pixelSelector.ey);
		Type ex = (Type) pixelSelector.ex.visit(this, null);
		System.out.println(ex);
		Type ey = (Type) pixelSelector.ey.visit(this, null);
		
		
		if(!(ex==ey)){
				throw new SemanticException("Wrong expression type in  visitPixelSelector.");
		}
		if(!(ex==Type.INTEGER|ex==Type.FLOAT)){
			throw new SemanticException("Wrong expression type in  visitPixelSelector.");
	}
		return null;
	}

	@Override
	public Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg) throws Exception {
		Type expr = (Type) expressionConditional.guard.visit(this, null);
		Type trueexpr1=(Type) expressionConditional.trueExpression.visit(this, null);
		Type falseexpr2=(Type) expressionConditional.falseExpression.visit(this, null);
		if(expr != Type.BOOLEAN){
			throw new SemanticException("The type of expression is not Boolean but "+expr+" for visitExpressionConditional.");
		}
		if(trueexpr1 != falseexpr2){
			throw new SemanticException("The type of trueexpression is not same as falseexpr2.");
		}
		expressionConditional.setTypeName(trueexpr1);
		return expressionConditional.get_Typename();
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws Exception {
		//System.out.println(expressionBinary.leftExpression.visit(this, null));
		//System.out.println("-----");
		//System.out.println(expressionBinary.rightExpression.visit(this, null));
		expressionBinary.setTypeName(inferredType
				((Type)expressionBinary.leftExpression.visit(this, null),
					(Type)	expressionBinary.rightExpression.visit(this, null),expressionBinary.op));
		//System.out.println("wer");
		return expressionBinary.type;
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary, Object arg) throws Exception {
		expressionUnary.type=(Type)expressionUnary.expression.visit(this, null);
		return expressionUnary.type;
	}

	@Override
	public Object visitExpressionIntegerLiteral(ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		expressionIntegerLiteral.type=Type.INTEGER;
		return expressionIntegerLiteral.type;
	}

	@Override
	public Object visitBooleanLiteral(ExpressionBooleanLiteral expressionBooleanLiteral, Object arg) throws Exception {
		expressionBooleanLiteral.type=Type.BOOLEAN;
		return expressionBooleanLiteral.type;
	}

	@Override
	public Object visitExpressionPredefinedName(ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
		expressionPredefinedName.setTypeName(Type.INTEGER);
		return expressionPredefinedName.type;
	}

	@Override
	public Object visitExpressionFloatLiteral(ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		expressionFloatLiteral.type=Type.FLOAT;
		return expressionFloatLiteral.type;
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg, Object arg)
			throws Exception {
		//System.out.println(expressionFunctionAppWithExpressionArg.e.visit(this, null));
	//	System.out.println(expressionFunctionAppWithExpressionArg.function);
		//System.out.println(inferredTypeFunctionApp(expressionFunctionAppWithExpressionArg.function
			//	,(Type)expressionFunctionAppWithExpressionArg.e.visit(this, null)));
		expressionFunctionAppWithExpressionArg.setTypeName(
				inferredTypeFunctionApp(expressionFunctionAppWithExpressionArg.function
						,(Type)expressionFunctionAppWithExpressionArg.e.visit(this, null)));
		
		//System.out.println(expressionFunctionAppWithExpressionArg.type);
		return expressionFunctionAppWithExpressionArg.type;
	}

	@Override
	public Object visitExpressionFunctionAppWithPixel(ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
		
		if(expressionFunctionAppWithPixel.name==Kind.KW_cart_x||expressionFunctionAppWithPixel.name==Kind.KW_cart_y){
			Type expr1 = (Type) expressionFunctionAppWithPixel.e0.visit(this, null);
			Type expr2 = (Type) expressionFunctionAppWithPixel.e1.visit(this, null);
		if(expr1!=Type.FLOAT|expr2!=Type.FLOAT){
			throw new SemanticException("One of the expression is not zero");
		}
		expressionFunctionAppWithPixel.type=Type.INTEGER;
			return Type.INTEGER;
		}
		else if(expressionFunctionAppWithPixel.name==Kind.KW_polar_a||expressionFunctionAppWithPixel.name==Kind.KW_polar_r){
			Type expr1 = (Type) expressionFunctionAppWithPixel.e0.visit(this, null);
			Type expr2 = (Type) expressionFunctionAppWithPixel.e1.visit(this, null);
			if(expr1!=Type.INTEGER|expr2!=Type.INTEGER){
				throw new SemanticException("One of the expression is not zero");
			}
		expressionFunctionAppWithPixel.type=Type.FLOAT;
		return Type.FLOAT;
		}
		return null;
	}

	@Override
	public Object visitExpressionPixelConstructor(ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		Type alpha = (Type) expressionPixelConstructor.alpha.visit(this, null);
		Type blue=(Type) expressionPixelConstructor.blue.visit(this, null);
		Type green=(Type) expressionPixelConstructor.green.visit(this, null);
		Type red=(Type) expressionPixelConstructor.red.visit(this, null);
		if(alpha != Type.INTEGER|blue != Type.INTEGER|green!= Type.INTEGER|red!= Type.INTEGER){
			throw new SemanticException("Some of the color are not integer.");
		}
		expressionPixelConstructor.type=Type.INTEGER;
		return expressionPixelConstructor.type;
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws Exception {
		//System.out.println(st);
		Type LHS1 =(Type) statementAssign.lhs.visit(this,null);
		
		//System.out.println(LHS1);
		//System.out.println(statementAssign.e);
		Type temp2=(Type) statementAssign.e.visit(this,null);
		//System.out.println(temp2);
		//System.out.println(temp2);
		if(LHS1!=temp2){
			throw new SemanticException("The type of LHS and expression are different for statementAssign2.");
		}
		return null;
	}	

	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel, Object arg) throws Exception {
		//System.out.println(expressionPixel.name);
		//System.out.println(st);
		expressionPixel.dec=(lookup(expressionPixel.name));
		if(expressionPixel.dec == null){
			throw new SemanticException("Promblems happen in  visitExpressionPixel.");
		}
		expressionPixel.pixelSelector.visit(this, null);
		Type expr =(Type) expressionPixel.dec.get_Typename();
		
		if(expr !=Type.IMAGE){
			throw new SemanticException("Promblems happen in  visitExpressionPixel.");
		}
		expressionPixel.type=Type.INTEGER;
		return Type.INTEGER;
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws Exception {
		expressionIdent.setdec(lookup(expressionIdent.name));
		if (expressionIdent.dec==null){
			throw new SemanticException ("Declaration is null in  expressionIdent");
		}
		expressionIdent.setTypeName(expressionIdent.dec.output_type);
		return expressionIdent.type;
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg) throws Exception {
		lhsSample.setdec(lookup(lhsSample.name));
		lhsSample.pixelSelector.visit(this, null);
		//System.out.println(lhsSample.dec);
		if (lhsSample.dec==null){
			throw new SemanticException("Can't find any declaration  for visitLHSSample.");
		}
		Type expr = (Type) lhsSample.dec.output_type;
		if(expr != Type.IMAGE){
			throw new SemanticException("The type of expression is not IMAGE in visitLHSPixel.");
		}
		lhsSample.setType(Type.INTEGER);
		return  lhsSample.type;
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg) throws Exception {
		lhsPixel.setdec(lookup(lhsPixel.name));
		lhsPixel.pixelSelector.visit(this, null);
		
		if (lhsPixel.dec==null){
			throw new SemanticException("Can't find any declaration  for visitLHSIdent.");
		}
		Type expr = (Type) lhsPixel.dec.get_Typename();
		if(expr != Type.IMAGE){
			throw new SemanticException("The type of expression is not IMAGE in visitLHSPixel.");
		}
		lhsPixel.setType(Type.INTEGER);
		return lhsPixel.type;
	
	}

	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg) throws Exception {
		lhsIdent.setdec(lookup(lhsIdent.name));
		if (lhsIdent.dec==null){
			throw new SemanticException("Can't find any declaration  for visitLHSIdent.");
		}
		lhsIdent.setType(lhsIdent.dec.output_type);
		return lhsIdent.type;
	}
	

	
	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg) throws Exception,SemanticException {
		Type expr = (Type) statementIf.guard.visit(this,null);
		if(expr != Type.BOOLEAN){
			throw new SemanticException("The type of expression is not BOOLEAN but "+expr+" for StatementIf.");
		}
		statementIf.b.visit(this, null);
		return null;
	
	}

	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws Exception {
		//System.out.println(statementWhile);
		Type expr = (Type) statementWhile.guard.visit(this,null);
		if(expr != Type.BOOLEAN){
			throw new SemanticException("The type of expression is not BOOLEAN but "+expr+" for statementWhile.");
		}
		statementWhile.b.visit(this, arg);
		return null;
	
	}

	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg) throws Exception,SemanticException {	
		Type expr = (Type) statementSleep.duration.visit(this, null);
		if(expr != Type.INTEGER){
			throw new SemanticException("The type of expression is not INTEGER but "+expr+" for SleepStatement.");
		}
		return null;
	}
	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg) throws Exception,SemanticException {
		
		Type expr = (Type) statementShow.e.visit(this, null);
		if(expr == Type.FILE||expr ==Type.NONE){
			throw new SemanticException("The type of expression is not part of the specific types but "+expr+" for visitStatementShow.");
		}
		return null;
	}

}
