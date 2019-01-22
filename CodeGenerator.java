/**
 * Starter code for CodeGenerator.java used n the class project in COP5556 Programming Language Principles 
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
import org.objectweb.asm.ClassWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import cop5556sp18.Types.Type;
import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.ASTVisitor;
import cop5556sp18.AST.Expression;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
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
import cop5556sp18.CodeGenUtils;
import cop5556sp18.Scanner.Kind;
public class CodeGenerator implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */

	static final int Z = 255;

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	Label cStart;
	Label cEnd;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	private int slot=1;
	final int defaultWidth;
	final int defaultHeight;
	// final boolean itf = false;
	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 * @param defaultWidth
	 *            default width of images
	 * @param defaultHeight
	 *            default height of images
	 */
	public CodeGenerator(boolean DEVEL, boolean GRADE, String sourceFileName,
			int defaultWidth, int defaultHeight) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
		this.defaultWidth = defaultWidth;
		this.defaultHeight = defaultHeight;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		Label l0 = new Label();
		
		for (ASTNode node : block.decsOrStatements) {
			if(node instanceof Declaration)
			{
				((Declaration) node).set_current_slot(slot);
				slot++;
			}
		}
		cStart = l0;
		mv.visitLabel(l0);
		for (ASTNode node : block.decsOrStatements) {
			if(node instanceof Declaration)
			{
				image( node, arg);
				};
			}
		for (ASTNode node : block.decsOrStatements) {
			if(node instanceof Statement)
			{
				((Statement) node).visit(this, arg);
			}
		}
		Label l1 = new Label();
		cEnd = l1;
		mv.visitLabel(l1);
		
		for (ASTNode node : block.decsOrStatements) {
			if(node instanceof Declaration)
			{
				((Declaration) node).visit(this, arg);
			}
		}
	
		return null;
	}
	
	public Object image(ASTNode node, Object arg)
			throws Exception {
		if(((Declaration) node).get_Typename()==Type.IMAGE){
			if(((Declaration) node).height != null)
			{
				((Declaration) node).width.visit(this, arg);
				((Declaration) node).height.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "makeImage", RuntimeImageSupport.makeImageSig, false);
			}
			else
			{
				mv.visitLdcInsn(defaultWidth);
				mv.visitLdcInsn(defaultHeight);
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "makeImage", RuntimeImageSupport.makeImageSig, false);
			}
			mv.visitVarInsn(ASTORE, ((Declaration) node).slot_number);
		}
        
        return null;
	}
	@Override
	public Object visitDeclaration(Declaration declaration, Object arg)
			throws Exception {
		if(declaration.get_Typename()==Type.INTEGER)
        {
            mv.visitLocalVariable(declaration.name, "I", null, cStart, cEnd, declaration.slot_number);
        }
        else if(declaration.get_Typename()==Type.FLOAT)
        {	
            mv.visitLocalVariable(declaration.name, "F", null, cStart, cEnd, declaration.slot_number);
        }
        else if(declaration.get_Typename()==Type.BOOLEAN)
        {	
            mv.visitLocalVariable(declaration.name, "Z", null, cStart, cEnd, declaration.slot_number);
        }
        else if(declaration.get_Typename()==Type.IMAGE)
        {	
        	mv.visitLocalVariable(declaration.name, "Ljava/awt/image/BufferedImage;", null, cStart, cEnd, declaration.slot_number);
            
        }
        else if(declaration.get_Typename()==Type.FILE)
        {	
            mv.visitLocalVariable(declaration.name, "Ljava/lang/String;", null, cStart, cEnd, declaration.slot_number);
        }
        
        return null;
	}
	@Override
	public Object visitBooleanLiteral(
			ExpressionBooleanLiteral expressionBooleanLiteral, Object arg)
			throws Exception {
		mv.visitLdcInsn(expressionBooleanLiteral.value);
		return null;
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary,
			Object arg) throws Exception {
		Label l1 = new Label(), l2 = new Label();
    	Kind kind = expressionBinary.op;
    	Expression e0=expressionBinary.leftExpression;
    	Expression e1=expressionBinary.rightExpression;
    	e0.visit(this, arg);
        e1.visit(this, arg);
        switch(kind){
        case OP_PLUS:{
        	if(e0.type == Type.INTEGER && e1.type == Type.INTEGER){
        		mv.visitInsn(IADD);
        	}
        	else if(e0.type == Type.FLOAT && e1.type == Type.FLOAT){
        		mv.visitInsn(FADD);
        	}
        	else if(e0.type == Type.FLOAT && e1.type == Type.INTEGER){
        		mv.visitInsn(I2F);
        		mv.visitInsn(FADD);
        	}
        	else if(e0.type == Type.INTEGER && e1.type == Type.FLOAT){
        		mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(FADD);
        	}
        	
        }break;
        case OP_MINUS:{
        	 if(e0.type == Type.INTEGER && e1.type == Type.INTEGER){
        		mv.visitInsn(ISUB);
        	}
        	else if(e0.type == Type.FLOAT && e1.type == Type.FLOAT){
        		mv.visitInsn(FSUB);
        	}
        	else if(e0.type == Type.FLOAT && e1.type == Type.INTEGER){
        		mv.visitInsn(I2F);
				mv.visitInsn(FSUB);
        	}
        	else if(e0.type == Type.INTEGER && e1.type == Type.FLOAT){
        		mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(SWAP);
				mv.visitInsn(FSUB);
        	}
        }break;
        case OP_TIMES:{
        	 if(e0.type == Type.INTEGER && e1.type == Type.INTEGER){
        		mv.visitInsn(IMUL);
        		
        	}
        	else if(e0.type == Type.FLOAT && e1.type == Type.FLOAT){
        		mv.visitInsn(FMUL);
        	}
        	else if(e0.type == Type.INTEGER && e1.type == Type.FLOAT){
        		mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(FMUL);
        	}
        	else if(e0.type == Type.FLOAT && e1.type == Type.INTEGER){
        		mv.visitInsn(I2F);
				mv.visitInsn(FMUL);
        	}
        }break;
        case OP_DIV:{
        	if(e0.type == Type.INTEGER && e1.type == Type.INTEGER){
        		mv.visitInsn(IDIV);
        		
        	}
        	else if(e0.type == Type.FLOAT && e1.type == Type.FLOAT){
        		mv.visitInsn(FDIV);
        	}
        	else if(e0.type == Type.INTEGER && e1.type == Type.FLOAT){
        		mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(SWAP);
				mv.visitInsn(FDIV);
        	}
        	else if(e0.type == Type.FLOAT && e1.type == Type.INTEGER){
        		mv.visitInsn(I2F);
				mv.visitInsn(FDIV);
        	}
        }break;
        case OP_POWER:{
        	if(e0.type == Type.FLOAT && e1.type == Type.FLOAT)
			{
				mv.visitInsn(F2D);
				mv.visitVarInsn(DSTORE, 0);
				mv.visitInsn(F2D);
				mv.visitVarInsn(DLOAD, 0);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
			}
			if(e0.type == Type.INTEGER && e1.type == Type.INTEGER)
			{
				mv.visitInsn(I2D);
				mv.visitVarInsn(DSTORE, 0);
				mv.visitInsn(I2D);
				mv.visitVarInsn(DLOAD, 0);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2I);
			}
			if(e0.type == Type.INTEGER && e1.type == Type.FLOAT)
			{
				mv.visitInsn(F2D);
				mv.visitVarInsn(DSTORE, 0);
				mv.visitInsn(I2D);
				mv.visitVarInsn(DLOAD, 0);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
			}
			if(e0.type == Type.FLOAT && e1.type == Type.INTEGER)
			{
				mv.visitInsn(I2D);
				mv.visitVarInsn(DSTORE, 0);
				mv.visitInsn(F2D);
				mv.visitVarInsn(DLOAD, 0);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
			}
        }break;
        case OP_AND:{
            mv.visitInsn(IAND);
        }break;
        case OP_OR:{
            mv.visitInsn(IOR);
        }break;
        case OP_MOD:{
        	 if(e0.type == Type.INTEGER && e1.type == Type.INTEGER){
        		mv.visitInsn(IREM);
        	}
        }break;
        case OP_LT:{
        	if(e0.type == Type.INTEGER || e1.type == Type.BOOLEAN){
                mv.visitJumpInsn(IF_ICMPLT, l1);
        	}else{
        		mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFLT, l1);
        	}
        	mv.visitLdcInsn(false);
        }break;
        case OP_LE:{
        	if(e0.type == Type.INTEGER || e1.type == Type.BOOLEAN){
                mv.visitJumpInsn(IF_ICMPLE, l1);
        	}else{
        		mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFLE, l1);
        	}
        	mv.visitLdcInsn(false);
        }break;
        case OP_GT:{
        	if(e0.type == Type.INTEGER || e1.type == Type.BOOLEAN){
                mv.visitJumpInsn(IF_ICMPGT, l1);
        	}else{
        		mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFGT, l1);
        	}
        	mv.visitLdcInsn(false);
        }break;
        case OP_GE:{
        	if(e0.type == Type.INTEGER || e1.type == Type.BOOLEAN){
                mv.visitJumpInsn(IF_ICMPGE, l1);
        	}else{
        		mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFGE, l1);
        	}
        	mv.visitLdcInsn(false);
        }break;
        case OP_EQ:{
        	if(e0.type == Type.INTEGER || e1.type == Type.BOOLEAN){
                mv.visitJumpInsn(IF_ICMPEQ, l1);
        	}else{
        		mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFEQ, l1);
        	}
        	mv.visitLdcInsn(false);
        }break;
        case OP_NEQ:{
        	if(e0.type == Type.INTEGER || e1.type == Type.BOOLEAN){
        		mv.visitJumpInsn(IF_ICMPNE, l1);
        	}else{
        		mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFNE, l1);
        	}
        	mv.visitLdcInsn(false);
        }break;
        
        default:{
        }break;
    }
        mv.visitJumpInsn(GOTO, l2);
		mv.visitLabel(l1);
		mv.visitLdcInsn(true);
		mv.visitLabel(l2);	
        return null;
	}

	@Override
	public Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg)throws Exception {
		expressionConditional.guard.visit(this, arg);
		Label f1 = new Label();
		Label f2 = new Label();
		mv.visitJumpInsn(IFNE, f1);
		expressionConditional.falseExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, f2);
		mv.visitLabel(f1);
		expressionConditional.trueExpression.visit(this, arg);
		mv.visitLabel(f2);
		return null;
	}
	

	@Override
	public Object visitExpressionFloatLiteral(
			ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		mv.visitLdcInsn(expressionFloatLiteral.value);
		return null;
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg,
			Object arg) throws Exception {
		expressionFunctionAppWithExpressionArg.e.visit(this, arg);
		Type t = expressionFunctionAppWithExpressionArg.e.type;
		Kind function=expressionFunctionAppWithExpressionArg.function; 
		switch(function){
		case KW_sin:{
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
			mv.visitInsn(D2F);
		}break;
		case KW_cos:{
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
			mv.visitInsn(D2F);
		}break;
		case KW_atan:{
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "atan", "(D)D", false);
			mv.visitInsn(D2F);
		}break;
		case KW_log:{
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "log", "(D)D", false);
			mv.visitInsn(D2F);
		}break;
		case KW_abs:{
			if(t==Type.FLOAT)
			{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F", false);
			}
			else if(t==Type.INTEGER)
			{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(I)I", false);
			}
			
		}break;
		case KW_int:{
			if(t==Type.FLOAT)
				mv.visitInsn(F2I);
		}break;
		case KW_float:{
			if(t==Type.INTEGER)
			
				mv.visitInsn(I2F);
			
		}break;
		case KW_alpha:{
			
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "getAlpha", RuntimePixelOps.getAlphaSig, false);
			
		}break;
		case KW_red:{
			
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "getRed", RuntimePixelOps.getRedSig, false);
			
		}break;
		case KW_blue:{
			
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "getBlue", RuntimePixelOps.getBlueSig, false);
		}break;
		case KW_green:{
			
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "getGreen", RuntimePixelOps.getGreenSig, false);
		}break;
		case KW_width:{
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "getWidth", RuntimeImageSupport.getWidthSig, false);
			
		}break;
		case KW_height:{
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "getHeight", RuntimeImageSupport.getHeightSig, false);
		}break;
		
		default:{
        }break;
		}
		return null;
	}

	@Override
	public Object visitExpressionFunctionAppWithPixel(
			ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
		expressionFunctionAppWithPixel.e0.visit(this, arg);
		expressionFunctionAppWithPixel.e1.visit(this, arg);
		if(expressionFunctionAppWithPixel.name==Kind.KW_cart_x || expressionFunctionAppWithPixel.name==Kind.KW_cart_y)
		{
			if(expressionFunctionAppWithPixel.name==Kind.KW_cart_x)
			{
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
				mv.visitInsn(D2F);
				mv.visitInsn(FMUL);
				mv.visitInsn(F2I);
			}
			else
			{
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
				mv.visitInsn(D2F);
				mv.visitInsn(FMUL);
				mv.visitInsn(F2I);
			}
		}
		else
		{
			if(expressionFunctionAppWithPixel.name==Kind.KW_polar_a)
			{
				mv.visitInsn(SWAP);
				mv.visitInsn(I2D);
				mv.visitVarInsn(DSTORE, 0);
				mv.visitInsn(I2D);
				mv.visitVarInsn(DLOAD, 0);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "atan2", "(DD)D", false);
				mv.visitInsn(D2F);
			}
			else
			{
				mv.visitInsn(I2D);
				mv.visitVarInsn(DSTORE, 0);
				mv.visitInsn(I2D);
				mv.visitVarInsn(DLOAD, 0);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "hypot", "(DD)D", false);
				mv.visitInsn(D2F);
			}
		}
		return null;
		
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent,
			Object arg) throws Exception {
		if(expressionIdent.type==Type.INTEGER | expressionIdent.type==Type.BOOLEAN)
		{
			mv.visitVarInsn(ILOAD, expressionIdent.dec.slot_number);
		}
		else if(expressionIdent.type==Type.FLOAT)
		{
			mv.visitVarInsn(FLOAD, expressionIdent.dec.slot_number);
		}
		else
		{
			mv.visitVarInsn(ALOAD, expressionIdent.dec.slot_number);
		}
		return null;
	}

	@Override
	public Object visitExpressionIntegerLiteral(
			ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		mv.visitLdcInsn(expressionIntegerLiteral.value);
		return null;
	}

	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel,
			Object arg) throws Exception {
		mv.visitVarInsn(ALOAD, expressionPixel.dec.slot_number);
		expressionPixel.pixelSelector.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "getPixel", RuntimeImageSupport.getPixelSig, false);
		return null;
	}

	@Override
	public Object visitExpressionPixelConstructor(
			ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		expressionPixelConstructor.alpha.visit(this, arg);
		expressionPixelConstructor.red.visit(this, arg);
		expressionPixelConstructor.green.visit(this, arg);
		expressionPixelConstructor.blue.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "makePixel", RuntimePixelOps.makePixelSig, false);
		return null;
	}

	@Override
	public Object visitExpressionPredefinedName(
			ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
		if (expressionPredefinedName.name==Kind.KW_Z)
		{
			mv.visitLdcInsn(Z);
		}
		else if (expressionPredefinedName.name==Kind.KW_default_width)
		{
			mv.visitLdcInsn(defaultWidth);
		}
		else if (expressionPredefinedName.name==Kind.KW_default_height)
		{
			mv.visitLdcInsn(defaultHeight);
		}
		return null;
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary,
			Object arg) throws Exception {
		Kind op=expressionUnary.op;
		expressionUnary.expression.visit(this, arg);
		Label l1 = new Label();
		Label len = new Label();
		if(op==Kind.OP_MINUS)
		{
			if(expressionUnary.expression.type==Type.INTEGER)
			{
				mv.visitInsn(DUP);
				mv.visitVarInsn(ISTORE, 0);
				mv.visitJumpInsn(IFGT, l1);
			}
			else if(expressionUnary.expression.type==Type.FLOAT)
			{
				mv.visitInsn(DUP);
				mv.visitVarInsn(FSTORE, 0);
				mv.visitLdcInsn((float) 0);
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFGT, l1);
			}
		}
		else if(op==Kind.OP_EXCLAMATION)
		{
			if(expressionUnary.expression.type==Type.INTEGER)
			{
				mv.visitLdcInsn(-1);
				mv.visitInsn(IXOR);
			}
			else if(expressionUnary.expression.type==Type.BOOLEAN)
			{
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(IF_ICMPEQ, l1);
				mv.visitLdcInsn(true);
					
			}
		}
		if(expressionUnary.expression.type==Type.INTEGER &&op==Kind.OP_MINUS)
		{
			mv.visitVarInsn(ILOAD, 0);
		}
		if(expressionUnary.expression.type==Type.FLOAT && op==Kind.OP_MINUS)
		{
			mv.visitVarInsn(FLOAD, 0);
		}
		mv.visitJumpInsn(GOTO, len);
		mv.visitLabel(l1);
		if(expressionUnary.expression.type==Type.INTEGER && op==Kind.OP_MINUS)
		{
			mv.visitVarInsn(ILOAD, 0);
			mv.visitInsn(INEG);
		}
		if(expressionUnary.expression.type==Type.FLOAT && op==Kind.OP_MINUS)
		{ 
			mv.visitVarInsn(FLOAD, 0);
			mv.visitInsn(FNEG);
		}
		if(expressionUnary.expression.type==Type.BOOLEAN &&op==Kind.OP_EXCLAMATION)
		{
			mv.visitLdcInsn(false);
			
		}
		mv.visitLabel(len);
		return null;
	}

	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg)
			throws Exception {
		if(lhsIdent.type==Type.IMAGE)
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "deepCopy", RuntimeImageSupport.deepCopySig, false);
			mv.visitVarInsn(ASTORE, lhsIdent.dec.slot_number);
		}
		else if(lhsIdent.type==Type.INTEGER || lhsIdent.type==Type.BOOLEAN)
		{
			mv.visitVarInsn(ISTORE, lhsIdent.dec.slot_number);
		}
		else if(lhsIdent.type==Type.FLOAT)
		{
			mv.visitVarInsn(FSTORE, lhsIdent.dec.slot_number);
		}
		else if(lhsIdent.type==Type.FILE)
		{
			mv.visitVarInsn(ASTORE, lhsIdent.dec.slot_number);
		}
		return null;
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD, lhsPixel.dec.slot_number);
		lhsPixel.pixelSelector.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "setPixel", RuntimeImageSupport.setPixelSig, false);
		return null;
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD, lhsSample.dec.slot_number);
		lhsSample.pixelSelector.visit(this, arg);
		Kind color=lhsSample.color;
		switch(color){
			case KW_green:{
				mv.visitLdcInsn(RuntimePixelOps.GREEN);
			}break;
			case KW_red:{
				mv.visitLdcInsn(RuntimePixelOps.RED);
			}break;
			case KW_blue:{
				mv.visitLdcInsn(RuntimePixelOps.BLUE);
			}break;
			case KW_alpha:{
				mv.visitLdcInsn(RuntimePixelOps.ALPHA);
			}break;
			default:{
				
			}break;
		}
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "updatePixelColor", RuntimeImageSupport.updatePixelColorSig, false);
		return null;
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg)
			throws Exception {
		pixelSelector.ex.visit(this, arg);
		pixelSelector.ey.visit(this, arg);
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		// cw = new ClassWriter(0); //If the call to mv.visitMaxs(1, 1) crashes,
		// it is
		// sometime helpful to
		// temporarily run it without COMPUTE_FRAMES. You probably
		// won't get a completely correct classfile, but
		// you will be able to see the code that was
		// generated.
		className = program.progName;
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null,
				"java/lang/Object", null);
		cw.visitSource(sourceFileName, null);

		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
				"([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();

		// add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);

		CodeGenUtils.genLog(DEVEL, mv, "entering main");

		program.block.visit(this, arg);

		// generates code to add string to log
		CodeGenUtils.genLog(DEVEL, mv, "leaving main");

		// adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		// adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart,
				mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		// Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the
		// constructor,
		// asm will calculate this itself and the parameters are ignored.
		// If you have trouble with failures in this routine, it may be useful
		// to temporarily change the parameter in the ClassWriter constructor
		// from COMPUTE_FRAMES to 0.
		// The generated classfile will not be correct, but you will at least be
		// able to see what is in it.
		mv.visitMaxs(0, 0);

		// terminate construction of main method
		mv.visitEnd();

		// terminate class construction
		cw.visitEnd();

		// generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign,
			Object arg) throws Exception {
		statementAssign.e.visit(this, arg);
        statementAssign.lhs.visit(this, arg);
        return null;
	}

	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg)
			throws Exception {
		statementIf.guard.visit(this, arg);
        Label passBlock = new Label();
        mv.visitJumpInsn(IFEQ, passBlock);
        statementIf.b.visit(this, arg);
        mv.visitLabel(passBlock);
        return null;
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg)
			throws Exception {
		Type t1 = statementInput.dec.get_Typename();
		if(t1==Type.INTEGER | t1==Type.BOOLEAN)
		{
			mv.visitVarInsn(ALOAD, 0);
			statementInput.e.visit(this, arg);
			mv.visitInsn(AALOAD);
			if(t1==Type.INTEGER)
			{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			}
			else
			{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			}
			mv.visitVarInsn(ISTORE, statementInput.dec.slot_number);
		}
		else if(t1==Type.FLOAT)
		{
			mv.visitVarInsn(ALOAD, 0);
			statementInput.e.visit(this, arg);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "parseFloat", "(Ljava/lang/String;)F", false);
			mv.visitVarInsn(FSTORE,  statementInput.dec.slot_number);
		}
		else if(t1==Type.FILE)
		{
			mv.visitVarInsn(ALOAD, 0);
			statementInput.e.visit(this, arg);
			mv.visitInsn(AALOAD);
			mv.visitVarInsn(ASTORE, statementInput.dec.slot_number);
		}
		else if(t1==Type.IMAGE)
		{
			mv.visitVarInsn(ALOAD, 0);
			statementInput.e.visit(this, arg);
			mv.visitInsn(AALOAD);
			if(statementInput.dec.height != null)
			{
				mv.visitTypeInsn(NEW, "java/lang/Integer");
				mv.visitInsn(DUP);
				statementInput.dec.width.visit(this, arg);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Integer", "<init>", "(I)V", false);
				mv.visitTypeInsn(NEW, "java/lang/Integer");
				mv.visitInsn(DUP);
				statementInput.dec.height.visit(this, arg);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Integer", "<init>", "(I)V", false);
			}
			else
			{
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "readImage", RuntimeImageSupport.readImageSig, false);
			mv.visitVarInsn(ASTORE, statementInput.dec.slot_number);
		}
		return null;
	}

	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg)
			throws Exception {
		/**
		 * For integers, booleans, and floats, generate code to print to
		 * console. For images, generate code to display in a frame.
		 * In all cases, invoke CodeGenUtils.genLogTOS(GRADE, mv, type); before
		 * consuming top of stack.
		 */
		statementShow.e.visit(this, arg);
		Type type = statementShow.e.type;
		switch (type) {
			case INTEGER : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out","Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(I)V", false);
			}break;
			case BOOLEAN : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out","Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream","println", "(Z)V", false);
			}break; 
			case FLOAT : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream","println", "(F)V", false);
			}break;
			case IMAGE : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "makeFrame", RuntimeImageSupport.makeFrameSig, false);
			}break;
			case FILE: {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream","println", "(Ljava/lang/String)V", false);
			}break;
			case NONE : {
				
			}break;

		}
		return null;
	}

	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg)
			throws Exception {
		statementSleep.duration.visit(this, arg);//Leaving the value on top of the stack.
    	mv.visitInsn(I2L);
    	mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
        return null;
	}


	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg)
			throws Exception {
		Label l1 = new Label();
		mv.visitJumpInsn(GOTO, l1);
		Label l2 = new Label();
		mv.visitLabel(l2);
		statementWhile.b.visit(this, arg);
		mv.visitLabel(l1);
		statementWhile.guard.visit(this, arg);
		mv.visitJumpInsn(IFNE, l2);
		return null;
	}

	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg)
			throws Exception {
		
		mv.visitVarInsn(ALOAD, statementWrite.sourceDec.slot_number);
		mv.visitVarInsn(ALOAD, statementWrite.destDec.slot_number);
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "write", RuntimeImageSupport.writeSig, false);
		return null;
	}

}