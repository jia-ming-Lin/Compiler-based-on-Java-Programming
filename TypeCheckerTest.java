package cop5556sp18;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Parser;
import cop5556sp18.Scanner;
import cop5556sp18.AST.ASTVisitor;
import cop5556sp18.AST.Program;
import cop5556sp18.TypeChecker.SemanticException;

public class TypeCheckerTest {

	/*
	 * set Junit to be able to catch exceptions
	 */
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * Prints objects in a way that is easy to turn on and off
	 */
	static final boolean doPrint = true;

	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 * Scans, parses, and type checks the input string
	 * 
	 * @param input
	 * @throws Exception
	 */
	void typeCheck(String input) throws Exception {
		show(input);
		// instantiate a Scanner and scan input
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		// instantiate a Parser and parse input to obtain and AST
		Program ast = new Parser(scanner).parse();
		show(ast);
		// instantiate a TypeChecker and visit the ast to perform type checking and
		// decorate the AST.
		ASTVisitor v = new TypeChecker();
		ast.visit(v, null);
	}



	/**
	 * Simple test case with an almost empty program.
	 * 
	 * @throws Exception
	 */
	@Test
	public void emptyProg() throws Exception {
		String input = "emptyProg{}";
		typeCheck(input);
	}
	@Test
	public void test1() throws Exception {
		String input = "emptyProg{if(true){ int x; }; int x; x := 5; show x;}";
		typeCheck(input);
	}
	@Test
	public void testdec() throws Exception {
		String input = "emptyProg{int a;boolean c; float d;image j;filename filetest1;}";
		typeCheck(input);
	}
	
	@Test
	public void testduplicated1() throws Exception {
		String input = "emptyProg{int a;boolean c; float a;image j;filename filetest1;}";
		thrown.expect(SemanticException.class);
		try {
		typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testimagedeclaration() throws Exception {
		String input = "emptyProg{image test1 [10,20];}";
		typeCheck(input);
	}
	@Test
	public void testduplicateddec() throws Exception {
		String input = "emptyProg{int a;int a;}";
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void expression112() throws Exception {
		String input = "prog {show 3+4;}";
		typeCheck(input);
	}

	@Test
	public void expression1202_fail() throws Exception {
		String input = "prog {boolean true1; show true1+4; }"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	@Test
	public void TestIF() throws Exception {
		String input = "prog{boolean a;boolean b;int d;int c; if(d>c){};}";
		typeCheck(input);
	}
	@Test
	public void TestInput() throws Exception {
		String input = "prog{boolean test;input test from @ 10+5046060;}";
		typeCheck(input);
	}
	@Test
	public void TestInputfail() throws Exception {
		String input = "prog{boolean test;int a10;input testa from @ 1000+500;}";
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	@Test
    public void testImageandFile() throws Exception {
        String input = "prog {image a; image src[12,34]; filename tar; write src to tar;}";
        typeCheck(input);
    }
	@Test
    public void testShow() throws Exception {
        String input = "prog {filename a;show a;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw(e);
        }
    }
	@Test
    public void image1() throws Exception {
        String input = "prog {image a;green(a[2, 4]):=a[3,5];}";
        
        typeCheck(input);
    }

    @Test
    public void simpleImage() throws Exception {
        String input = "X{ image im[1,2]; }";
        typeCheck(input);
    }
    
    @Test
    public void simpleImageFail() throws Exception {
        String input = "X{ image im[1.0, 2]; }";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch (SemanticException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void testedDec3() throws Exception {
        String input = "X{ int x; int y; while (x == y) { show x;}; }";
        typeCheck(input);
}
    @Test
    public void testedDec120() throws Exception {
        String input = "X{ int x; float y; while (x == log(y)) {}; }";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch (SemanticException e) {
            show(e);
            throw e;
        }
}


    @Test
    public void testTypeimage() throws Exception {
        String input = "prog {image a [cart_x[1.1,3.2], cart_y[4.2, 3.1]];}";
        typeCheck(input);
    }


    @Test
    public void testvalidStatementWrite150() throws Exception {
        String input = "prog{image image1; filename f1; write image1 to f1;}";
        typeCheck(input);        
    } 
    
    @Test
    public void testvalidStatementAssign180() throws Exception {
        String input = "prog{int var1; var1 := 1; "
        		+ "float var2; var2 := 1.0;"
        		+ "boolean var3; var3 := true;"
        		+ "filename f1; filename f2; f1 := f2;image var4; image var5[500,500]; var4 := var5;}";
        typeCheck(input);        
    } 

    @Test
    public void testedwhile() throws Exception {
        String input = "X{ int x; int y; image a;while ( "
        		+ "int(3.4) == 2**4  != log(float(blue(3)))) {int x;}; }";
        
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void invalidStatementWhile1() throws Exception {
        String input = "prog{int a; int b; while(a & b){};}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void invalidStatementWrite2() throws Exception {
        String input = "prog{filename f1; write image1 to f1;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void invalidStatementAssign2() throws Exception {
        String input = "prog{image var; var[0,0] := 1.0;alpha(var[0,0]) := 1.0;" + 
    " red(var[0,0]) := 1.0; green(var[0,0]) := 1.0; blue(var[0,0]) := 1.0;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void invalidExpressionConditional1() throws Exception {
        String input = "prog{int cond;int var1; var1 := cond ? var1+ 1 : var1;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void invalidExpressionConditional2() throws Exception {
        String input = "prog{boolean cond;int var1; var1 := cond ? var1+ 1.0 : var1;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void invalidFunctionAppWithArg1() throws Exception {
        String input = "prog {int var1;var1 := abs(1.0);}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void invalidFunctionAppWithArg2() throws Exception {
        String input = "prog {float var1;var1 := abs(1);}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void invalidFunctionAppWithArg6() throws Exception {
        String input = "prog {int var1;var1 := cart_x(1.0);}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidFunctionAppWithArg7() throws Exception {
        String input = "prog {float var1;var1 := polar_a(1);}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void testinvalidScope2 () throws Exception {
        String input = "p{int var; if(true) {float var; var := 5.0;}; var := 5.0;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testinvalidScope3 () throws Exception {
        String input = "p{int var; if(true) {float var; var := 5;}; var := 5;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void testinvalidScope30 () throws Exception {
        String input = "p{int var; if(true) {float var; var := 5;}; var := 5;}";
     
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
        
    }
    @Test
    public void testinvalidScope4 () throws Exception {
        String input = "p{int var; if(true) {float var; var := 5;}; var := 5.0;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testinvalidStatementIf1() throws Exception {
        String input = "prog{int a; int b; if(a & b){};}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    

    @Test
    public void testinvalidPixelConstructor1() throws Exception {
        String input = "prog{ int var; var := <<1.0,2,3,4>>;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    } 
    
    @Test
    public void testvalidStatementIf1() throws Exception {
        String input = "prog{boolean a; boolean b; if(a & b){};}";
        typeCheck(input);        
    } 
    
    @Test
    public void testvalidDeclarations1() throws Exception {
        String input = "prog{int var1; float var2; boolean var3; image var4; filename var5; image var6[500,500];}";
        typeCheck(input);        
    } 
    
    @Test
    public void testvalidDeclarations2() throws Exception {
        String input = "prog{int var1; float var2; boolean var3; image var4; filename var5; image var6[500,500];if(true){int var1; float var2; boolean var3; image var4; filename var5; image var6[500,500];};}";
        typeCheck(input);        
    } 
    
    @Test
    public void testvalidDeclarations3() throws Exception {
        String input = "prog{if(false){int var1; float var2; boolean var3; image var4; filename var5; image var6[500,500];};if(true){int var1; float var2; boolean var3; image var4; filename var5; image var6[500,500];};}";
        typeCheck(input);        
    } 
    
    @Test
    public void testvalidStatementShow() throws Exception {
        String input = "prog{boolean a; int b; float c; image d;show a; show b; show c; show d;}";
        typeCheck(input);        
    } 


    @Test
    public void testType1() throws Exception {
        String input = "prog {if (10 == 1.3) {};}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw(e);
        }
    }

    @Test
    public void testType2() throws Exception {
        String input = "prog {int a;a := <<1.0, 3 , Z, 4>>;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw(e);
        }
    }

    @Test
    public void testType3() throws Exception {
        String input = "prog {float a; a := 3.0 <= 2.0? 3.0 : abs(3);}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw(e);
        }
    }

    @Test
    public void testType4() throws Exception {
        String input = "prog {float a;a := float(3 < 3);}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw(e);
        }
    }

    @Test
    public void testType5() throws Exception {
        String input = "prog {float a;a := polar_a[a, a];}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw(e);
        }
    }
    
    @Test
    public void invalidDeclarations1() throws Exception {
        String input = "prog{int var1; float var2; boolean var3; image var4; filename var5;"
                +"image var6[500,500];if(true){int var1; float var2; boolean var3;" +
                "image var4; filename var5; image var6[500,500];};float var1;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidDeclarations2() throws Exception {
        String input = "prog{image var1[1.0, 500];}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidDeclarations3() throws Exception {
        String input = "prog{image var1[500, 1.0];}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidExpressionPixel1() throws Exception {
        String input = "prog{ int var2; var2 := var1[0,0];}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void invalidExpressionPixel10() throws Exception {
        String input = "prog{ int var2; var2 := var1[0,0];}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidExpressionPixel2() throws Exception {
        String input = "prog{ int var1; int var2; var2 := var1[0,0];}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidLhsSample1() throws Exception {
        String input = "prog{red (var1[0,0]) := 5;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidLhsSample2() throws Exception {
        String input = "prog{int var1; red( var1[0,0]) := 5;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidLhsPixel1() throws Exception {
        String input = "prog{var1[0,0] := 5;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidLhsPixel2() throws Exception {
        String input = "prog{int var1; var1[0,0] := 5;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidStatementInput1() throws Exception {
        String input = "prog{input var from @1; int var;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidStatementInput2() throws Exception {
        String input = "prog{if(true){int var;}; if(true){input var from @1;};}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidStatementInput3() throws Exception {
        String input = "prog{if(true){int var;}; input var from @1;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void invalidStatementInput4() throws Exception {
        String input = "prog{float var; input var from @var;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void validStatementShow1() throws Exception {
        String input = "prog{boolean a; int b; float c; image d;show a; show b; show c; show d;}";
        typeCheck(input);
    }

    @Test
    public void invalidStatementSleep1() throws Exception {
        String input = "prog{sleep 1.0;}";
        thrown.expect(SemanticException.class);
        try {
        typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    

    

    
    @Test
    public void invalidStatementWrite4() throws Exception {
        String input = "prog{image image1; write image1 to image1;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void invalidPixelSelector1() throws Exception {
        String input = "prog{image var1; red( var1[0,0.0]) := 5;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void invalidPixelSelector87() throws Exception {
        String input = "prog{image var1; red(var1[0,0]) := 5;}";
       
            typeCheck(input);
       
    }

    @Test
    public void testinvalidPixelSelector2() throws Exception {
        String input = "prog{image var1; red( var1[0.0,0]) := 5;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testinvalidPixelSelector3() throws Exception {
        String input = "prog{image var1; red( var1[true,false]) := 5;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testExpressionFunctionAppWithPixel3() throws Exception {
        String input = "prog { int var2; var2 := cart_x[1,6.5];}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testExpressionFunctionAppWithPixel4() throws Exception {
        String input = "prog { int var2; var2 := cart_x[1.0,6];}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testExpressionFunctionAppWithPixel5() throws Exception {
        String input = "prog { float var2; var2 := polar_a[1.0,6];}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testExpressionFunctionAppWithPixel6() throws Exception {
        String input = "prog { float var2; var2 := polar_a[1,6.5];}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testinvalidStatementAssign1() throws Exception {
        String input = "prog{int var1; var1 := 1.0; float var2; var2 := 1;boolean var3; var3 := 1;filename f1;" + 
                " f1 := 1;image var4; var4 := 1;image var5[500,500]; var5 := 1;}";
       thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
     
    @Test
    public void testinvalidPixelSelector1() throws Exception {
        String input = "prog{image var1; red( var1[0,0.0]) := 5;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    } 
    @Test
    public void invalidFunctionAppWithArg4()throws Exception {
        String input = "prog {float var1;var1 := sin(1);}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void invalidFunctionAppWithArg5() throws Exception {
        String input = "prog {int var1;var1 := width(1);}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    
    
    @Test
    public void testvalidStatementAssign1() throws Exception {
        String input = "prog{int var1; var1 := 1; float var2; var2 := 1.0;boolean var3; var3 := true;filename f1; filename f2; f1 := f2;image var4; image var5[500,500]; var4 := var5;}";
        typeCheck(input);        
    } 
    
    @Test
    public void testvalidStatementInput1() throws Exception {
        String input = "prog{int var; int var2;input var from @1; input var from @var2; input var from @<<1,2,3,4>>;}";
        typeCheck(input);        
    } 
       
    @Test
    public void testvalidStatementSleep1() throws Exception {
        String input = "prog{sleep 1;}";
        typeCheck(input);        
    } 
    
    @Test
    public void testvalidStatementWrite1() throws Exception {
        String input = "prog{image image1; filename f1; write image1 to f1;}";
        typeCheck(input);        
    } 
    @Test
    public void invalidFunctionAppWithArg8() throws Exception {
        String input = "prog {int var1;var1 := cart_y(1);}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    
    @Test
    public void invalidFunctionAppWithArg9() throws Exception {
        String input = "prog {float var1;var1 := polar_r(1.0);}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void invalidFunctionAppWithArg3() throws Exception {
        String input = "prog {int var1;var1 := red(1.0);}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void invalidStatementWrite3() throws Exception {
        String input = "prog{filename f1; write f1 to f1;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void invalidStatementWrite1() throws Exception {
        String input = "prog{image image1; write image1 to f1;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void testTypeImage() throws Exception {
        String input = "prog {image a [cart_x[1.1,3.2], cart_y[4.2, 3.1]];}";
        typeCheck(input);
    }
@Test
public void testedDec4() throws Exception {
    String input = "X{ int x; int y; while (x == y) { int z;}; show z;}";
    thrown.expect(SemanticException.class);
    try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
}

    @Test
    public void testedDec1() throws Exception {
        String input = "X{ int x; int y; while (x == y) {int x;}; }";
        typeCheck(input);
    }
    @Test
    public void testedDec1fail() throws Exception {
        String input = "X{ float x; int y; while (log(x) == y) {int x;}; }";
        thrown.expect(SemanticException.class);
        try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
    }
    
	@Test
	public void testassign1() throws Exception {
		String input = " prog{float var1; var1:=5.0;}";
		typeCheck(input);
	}
	
	@Test
	public void expression10_fail() throws Exception {
		String input ="samples{image bird;"
				+ "filename abc;write bird to abc;"
				+ "show bird;"
				+ "sleep(4000);"
				+ "image bird2[width(bird),height(bird)];"
				+ "int x;x:=0;"
				+ "while(x<width(bird2)) "
				+ "{int y;y:=0;while(y<height(bird2)) "
				+ "{blue(bird2[x,y]):=red(bird[x,y]);green(bird2[x,y]):=blue(bird[x,y]);"
				+ "red(bird2[x,y]):=green(bird[x,y]);alpha(bird2[x,y]):=Z;y:=y+1;};x:=x+1;};show bird2;sleep(4000);}";
		
		typeCheck(input);
//		thrown.expect(SemanticException.class);
//		try {
//			typeCheck(input);
//		} catch (SemanticException e) {
//			show(e);
//			throw e;
//		}
	}
	
	@Test
	public void emptyProg6() throws Exception {
		String input = " prog{float var; var := 5.0;}";
		typeCheck(input);
	}
	
	@Test
	public void WritePrograme() throws Exception {
		String input = "prog{image f1;filename f2; write f1 to f2;}";
		typeCheck(input);
	}
	@Test
	public void emptyProg2() throws Exception {
		String input = 

				"prog{int var1; float var2; boolean var3; image var4; filename var5; image var6[500,500];if(true){int var1; float var2; boolean var3; image var4; filename var5; image var6[500,500];};}";
		typeCheck(input);
	}
	
	@Test
	public void emptyProg10() throws Exception {
		String input = "emptyProg{}";
		typeCheck(input);
	}

	@Test
	public void expressionshowInt() throws Exception {
	    //StatementShow ::= show??Expression
		String input = "prog {show 3+4;}";
		typeCheck(input);
	}
	
	@Test
    public void expressionshowfloat() throws Exception {
        //StatementShow ::=?‹show??Expression
        String input = "prog {show 3.4 + 4.6;}";
        typeCheck(input);
    }
	@Test
    public void expressionshowboolean() throws Exception {
        //StatementShow ::=?‹show??Expression
        String input = "prog {show true;}";
        typeCheck(input);
    }

	@Test
	public void expression2_fail10() throws Exception {
		String input = "prog { show true+4; }"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
    public void testDuplicateVar() throws Exception {
        String input = "prog {" +
                "int a;" +
                "int a;" +
                "}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testScope0() throws Exception {
        String input = "prog{int i;i := 0;if(i < 0){int i; int j;i := 1;};sleep i;int j;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void testScope1() throws Exception {
        String input = "prog { float j; if(true) { float i; i := j + 1; }; show i;}";
        thrown.expect(SemanticException.class);
        try {
            typeCheck(input);
        } catch(SemanticException e) {
            show(e);
            throw(e);
        }
    }
    @Test
    public void assign() throws Exception {
        String input = "assign{int a;a:=3;}";
            typeCheck(input);
    
    }

	@Test
	public void expression1() throws Exception {
		String input = "prog {show 3+4;}";
		typeCheck(input);
	}
	
	@Test
	public void expression2() throws Exception {
		String input = "prog {show 3.5+4;}";
		typeCheck(input);
	}
	
	@Test
	public void expression3() throws Exception {
		String input = "prog {show 3**4;}";
		typeCheck(input);
	}
	
	@Test
	public void expression4() throws Exception {
		String input = "prog {show 3&4;}";
		typeCheck(input);
	}
	
	@Test
	public void expression5() throws Exception {
		String input = "prog {show 3.5/4.5;}";
		typeCheck(input);
	}
	
	@Test
	public void expression6() throws Exception {
		String input = "prog {show 3==4;}";
		typeCheck(input);
	}
	
	@Test
	public void expression7() throws Exception {
		String input = "prog {show 3>=4;}";
		typeCheck(input);
	}
	
	@Test
	public void expression8() throws Exception {
		String input = "prog {show true&true;}";
		typeCheck(input);
	}
	
	@Test
	public void expression9() throws Exception {
		String input = "prog {show true==false;}";
		typeCheck(input);
	}
	
	@Test
	public void expression10() throws Exception {
		String input = "prog {sleep 10;}";
		typeCheck(input);
	}
	
	@Test
	public void expression11() throws Exception {
		String input = "prog {int i; i := 0; while(i < 2){show i; i := i + 1;};}";
		typeCheck(input);
	}
	
	@Test
	public void expression12() throws Exception {
		String input = "prog {while(false){show 2+2;};}";
		typeCheck(input);
	}
	
	@Test
	public void expression13() throws Exception {
		String input = "prog {while(false){show 2+2;};}";
		typeCheck(input);
	}
	
	@Test
	public void expression14() throws Exception {
		String input = "prog {show abs(5);}";
		typeCheck(input);
	}
	
	@Test
	public void expression15() throws Exception {
		String input = "prog {show red(4);}";
		typeCheck(input);
	}
	
	@Test
	public void expression16() throws Exception {
		String input = "prog {show atan(90.8);}";
		typeCheck(input);
	}
	
	@Test
	public void expression17() throws Exception {
		String input = "prog {show cart_x[4.4, 3.4];}";
		typeCheck(input);
	}
	
	@Test
	public void expression18() throws Exception {
		String input = "prog {show cart_y[4.4, 3.4];}";
		typeCheck(input);
	}
	
	@Test
	public void expression19() throws Exception {
		String input = "prog {show polar_a[4, 3];}";
		typeCheck(input);
	}
	
	@Test
	public void expression20() throws Exception {
		String input = "prog {show polar_r[4, 3];}";
		typeCheck(input);
	}
	
	@Test
	public void expression21() throws Exception {
		String input = "prog {show <<2, 3, 4, 5>>;}";
		typeCheck(input);
	}
	
	@Test
	public void expression22() throws Exception {
		String input = "prog {image a; filename b; write a to b;}";
		typeCheck(input);
	}
	
	@Test
	public void expression23() throws Exception {
		String input = "prog {image a[2, 4]; filename b; write a to b;}";
		typeCheck(input);
	}
	
	@Test
	public void expression24() throws Exception {
		String input = "prog {image a; input a from @ 2;}";
		typeCheck(input);
	}
	
	@Test
	public void expression25() throws Exception {
		String input = "prog {int a; a := 2;}";
		typeCheck(input);
	}
	
	@Test
	public void expression26() throws Exception {
		String input = "prog {image b; b[2, 3] := 5;}";
		typeCheck(input);
	}
	
	@Test
	public void expression27() throws Exception {
		String input = "prog {image b; b[2.5, 3.5] := 5;}";
		typeCheck(input);
	}
	
	@Test
	public void expression28() throws Exception {
		String input = "prog {image b; green(b[2.5, 3.5]) := 5;}";
		typeCheck(input);
	}
	
	@Test
	public void expression29() throws Exception {
		String input = "prog {float a; a := 2.5;}";
		typeCheck(input);
	}
	
	@Test
	public void expression30() throws Exception {
		String input = "prog {boolean b; boolean c; b := c;}";
		typeCheck(input);
	}
	
	@Test
	public void expression31() throws Exception {
		String input = "prog {filename b; filename c; b := c;}";
		typeCheck(input);
	}
	
	@Test
	public void expression32() throws Exception {
		String input = "prog {image b; image c; b := c;}";
		typeCheck(input);
	}
	
	@Test
	public void expression33() throws Exception {
		String input = "prog {int a; a := 2==3 ? 5 : 4;}";
		typeCheck(input);
	}
	
	@Test
	public void expression34() throws Exception {
		String input = "prog {filename a; filename b; filename c; a := 2==3 ? b : c;}";
		typeCheck(input);
	}
	
	@Test
	public void expression35() throws Exception {
		String input = "prog {int a; a := 2==3 ? +2 : -2;}";
		typeCheck(input);
	}
	
	@Test
	public void expression36() throws Exception {
		String input = "prog {int a; image b; a := b[2,3];}";
		typeCheck(input);
	}
	
	@Test
	public void expression37() throws Exception {
		String input = "prog {show 3**4.5;}";
		typeCheck(input);
	}
	
	@Test
	public void expression2_fail() throws Exception {
		String input = "prog { show true+4; }"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression3_fail() throws Exception {
		String input = "prog { show true+false; }"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression4_fail() throws Exception {
		String input = "prog { show 3.5%4.5; }"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression5_fail() throws Exception {
		String input = "prog {sleep 3.5;}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression6_fail() throws Exception {
		String input = "prog {if(2){show 4;};}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression7_fail() throws Exception {
		String input = "prog {while(2){show 4;};}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression8_fail() throws Exception {
		String input = "prog {show red(5.5);}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression9_fail() throws Exception {
		String input = "prog {show log(10);}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression10_fail1343214() throws Exception {
		String input = "prog {show polar_r[4.4, 3.4];}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression11_fail() throws Exception {
		String input = "prog {show cart_x[4, 3];}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression12_fail() throws Exception {
		String input = "prog {show <<2.4, 3.4, 4.4, 5.4>>;}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression13_fail() throws Exception {
		String input = "prog {image a[2.5, 4.2]; filename b; write a to b;}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression14_fail() throws Exception {
		String input = "prog {int a; a := 2.5;}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression18_fail() throws Exception {
		String input = "prog {image a; a := 2.5;}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression19_fail() throws Exception {
		String input = "prog {float a; a := 2;}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression15_fail() throws Exception {
		String input = "prog {image b; b[2, 3.5] := 3;}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression16_fail() throws Exception {
		String input = "prog {image b; b[2, 3] := 3.5;}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression17_fail() throws Exception {
		String input = "prog {image b; blue(b[2, 3]) := 3.5;}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression20_fail() throws Exception {
		String input = "prog {float b; b := 2==3 ? 4 : 5;}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression21_fail() throws Exception {
		String input = "prog {float b; b := 2 ? 4 : 5;}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression22_fail() throws Exception {
		String input = "prog {int b; image c; b := c[2, 3.5];}"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}

	
	}


