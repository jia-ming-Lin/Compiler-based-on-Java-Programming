package cop5556sp18;


import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.Token;

public class Types {
	public static void  main(String[] args){
		Type a=Type.FLOAT;
		System.out.println(a.getJVMTypeDesc());
		
	}
	public static enum Type {
		INTEGER("I"), 
		BOOLEAN("Z"),
		IMAGE("Ljava/awt/image/BufferedImage;"), 
		FLOAT("F"),
	    FILE("Ljava/lang/String;"), 
	    NONE(null);
	public boolean isType(Type... types){
		for (Type type: types){
			if (type.equals(this)) return true;
			}
			return false;
		}
	String jvmType;
	Type(String jvmType){
		this.jvmType = jvmType;
	}
	
	public String getJVMTypeDesc() {
		return jvmType;
	}
	public String getJVMClass(){
		return jvmType.substring(1,jvmType.length()-1);  //removes L and ;
	}
}
	
//*finished*//
	public static Type getType(Kind kind) {
		switch (kind) {
		case KW_int: {return Type.INTEGER;}
		case KW_boolean: {return Type.BOOLEAN;}
		case KW_image: {return Type.IMAGE;}
		case KW_filename: {return Type.FILE;}
		case KW_float: {return Type.FLOAT;}
		default:
			break;
		}
		// should not reach here
		assert false: "invoked getType with Kind that is not a type"; 
		return null;
		
	}
	
}
