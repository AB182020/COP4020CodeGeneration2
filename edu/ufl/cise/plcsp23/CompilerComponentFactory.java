/*Copyright 2023 by Beverly A Sanders
 * 
 * This code is provided for solely for use of students in COP4020 Programming Language Concepts at the 
 * University of Florida during the spring semester 2023 as part of the course project.  
 * 
 * No other use is authorized. 
 * 
 * This code may not be posted on a public web site either during or after the course.  
 */

package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.ASTVisitor;
import edu.ufl.cise.plcsp23.ast.CodeGenerator;
import edu.ufl.cise.plcsp23.ast.TypeChecker;

public class CompilerComponentFactory
{
	public static IScanner makeScanner(String input)
	{
		//char[] inp_Char = input.toCharArray();
		//Add statement to return an instance of your scanner
		return new Scanner(input) ;

	}
	public static IParser makeParser(String input)
			throws LexicalException
	{
	return new Parser(input);
	}

	public static ASTVisitor makeTypeChecker()
	{
		return new TypeChecker();
	}

	public static ASTVisitor makeCodeGenerator(String Package)
	{
	//	code to instantiate a return an ASTVisitor for code generation
		return  new CodeGenerator();
	}

}
