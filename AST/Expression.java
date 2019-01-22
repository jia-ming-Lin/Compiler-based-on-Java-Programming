package cop5556sp18.AST;
import cop5556sp18.Types.*;
import cop5556sp18.Scanner.Token;
import cop5556sp18.Types.Type;
import cop5556sp18.Types;
import cop5556sp18.Parser.SyntaxException;
/**

 * This code is for the class project in COP5556 Programming Language Principles 
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



public abstract class Expression extends ASTNode {

	public Expression(Token firstToken) {
		super(firstToken);
	}
	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;
	public Integer val;
	public Type type;
	
	public void set_Typeame(Token t)throws SyntaxException {
		type = Types.getType(t.kind);
	}
	
	public void setTypeName(Type typeName){
		type = typeName;
	}
	

	public Type get_Typename(){
		return type;
	}
	
	public Type getType(){
		return type;
	}
}
