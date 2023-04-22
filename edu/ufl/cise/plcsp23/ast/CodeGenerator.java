package edu.ufl.cise.plcsp23.ast;

import java.util.HashMap;
import java.util.Map;

public class CodeGenerator implements ASTVisitor
{
    StringBuilder code;
    String javaType;
    boolean booleConversionVal = false;
    boolean whst = false;
    String opType ="";
    StringBuilder imPrt;
    String progName;
    @Override
    public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws PLCException
    {
        code.append(statementAssign.getLv().firstToken.getTokenString());
        code.append(" = ");
        if(statementAssign.getE() instanceof BinaryExpr)
        {
           // code.append("(");
//           if(((BinaryExpr) statementAssign.getE()).getLeft().firstToken instanceof IdentExpr )
//           {
//               code.append(((BinaryExpr) statementAssign.getE()).getLeft().firstToken.getTokenString());
//           }
//           else if(((BinaryExpr) statementAssign.getE()).getLeft().firstToken instanceof NumLitExpr )
//           {
//               code.append(((NumLitExpr) ((BinaryExpr) statementAssign.getE()).getLeft().firstToken).getValue());
//           }
//           code.append(((BinaryExpr) statementAssign.getE()).op.name());
            visitBinaryExpr((BinaryExpr) statementAssign.getE(),null);

        }
        else if(statementAssign.getE() instanceof NumLitExpr)
        {
            code.append(((NumLitExpr) statementAssign.getE()).getValue());
        }
        else if(statementAssign.getE() instanceof StringLitExpr)
        {
            code.append("\"");
            code.append(statementAssign.getE().firstToken.getTokenString());
            code.append("\"");
        }
        else if(statementAssign.getE() instanceof IdentExpr)
        {
            code.append(statementAssign.getE().firstToken.getTokenString());
        }
        code.append(";\n");
        return null;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCException
    {
       code.append("(");
        if(binaryExpr.left instanceof IdentExpr)
        {
            code.append(binaryExpr.left.firstToken.getTokenString());
        }
        else if(binaryExpr.left instanceof NumLitExpr)
        {
            code.append(((NumLitExpr) binaryExpr.left).getValue());
        }
       String op="";
        String binOp = binaryExpr.op.name();
        switch (binOp)
        {
            case "PLUS":
            {
                op = "+";
                break;
            }
            case "MINUS":
            {
                op ="-";
                break;
            }
            case "TIMES":
            {
                op ="*";
                break;
            }
            case "DIV":
            {
                op ="/";
                break;
            }
            case "GT":
            {
                op = ">";
                booleConversionVal = true;
                break;
            }
            case "LT":
            {
                op ="<";
                booleConversionVal = true;
                break;
            }
            case "LE":
            {
                op ="<=";
                booleConversionVal = true;
                break;
            }
            case "GE":
            {
                op =">=";
                booleConversionVal = true;
                break;
            }
        }
        code.append(op);
        if(binaryExpr.right instanceof IdentExpr)
        {
            code.append(binaryExpr.right.firstToken.getTokenString());
        }
        else if(binaryExpr.right instanceof NumLitExpr)
        {
            code.append(((NumLitExpr) binaryExpr.right).getValue());
        }

        code.append(")");
        if(javaType.equals("int") && booleConversionVal == true)
        {
            code.append("? 1 : 0");
        }
        return null;
    }


    @Override
    public Object visitBlock(Block block, Object param) throws PLCException
    {
        StringBuilder exprStr = new StringBuilder();
        Map<String,String> exprMap = new HashMap<String,String>();
        int statementSize=block.statementList.size();
        int decSize =block.decList.size();
        Statement st;
        String stm;
               if( decSize==0 && statementSize ==0 )
       {

           code.append("{\n");

       }
       else
       {
           code.append("{\n");
       for(int i=0; i<decSize; i++)
       {
           javaType = block.decList.get(i).nameDef.type.toString();
           if(javaType == "STRING")
           {
               javaType = "String";
           }
           else
           {
               javaType = javaType.toLowerCase();
           }
            code.append(javaType);
            code.append(" ");
            code.append(block.decList.get(i).nameDef.firstToken.getTokenString());
            code.append(" = ");
            if(block.decList.get(i).initializer instanceof NumLitExpr)
            {
                code.append(block.decList.get(i).initializer.firstToken.getTokenString());
                code.append(";\n");
            }
            else if(block.decList.get(i).initializer instanceof StringLitExpr)
           {
               code.append("\"");
               code.append(block.decList.get(i).initializer.firstToken.getTokenString());
               code.append("\"");
               code.append(";\n");
           }
            else if(block.decList.get(i).initializer instanceof IdentExpr)
            {
                code.append(block.decList.get(i).initializer.firstToken.getTokenString());
                code.append(";\n");
            }

          else if(block.decList.get(i).initializer instanceof ConditionalExpr)
           {
            code.append(" (");
            code.append("(");//add gaurd expresion
            code.append(((IdentExpr) ((BinaryExpr) ((ConditionalExpr) block.decList.get(i).initializer).getGuard()).left).getName());
            String relOperator="";
            String operator =( ((BinaryExpr) ((ConditionalExpr) block.decList.get(i).initializer).getGuard()).op.name());
          switch (operator)
          {
               case "GT":
               {
                   relOperator = ">";
                   break;
               }
              case "LT":
              {
                  relOperator ="<";
                  break;
              }
              case "LE":
              {
                  relOperator ="<=";
                  break;
              }
              case "GE":
              {
                  relOperator =">=";
                  break;
              }
          }

            code.append(relOperator);
          //Relational Operator
            if(((BinaryExpr) ((ConditionalExpr) block.decList.get(i).initializer).getGuard()).right instanceof NumLitExpr);
               {
                   code.append( ((NumLitExpr) ((BinaryExpr) ((ConditionalExpr) block.decList.get(i).initializer).getGuard()).right).getValue());
               }
            code.append(")");
            code.append("?");
            //TrueCase
            if( ((ConditionalExpr) block.decList.get(i).initializer).trueCase instanceof StringLitExpr)
            {
                code.append("\"");
                code.append( ((StringLitToken) ((ConditionalExpr) block.decList.get(i).initializer).getTrueCase().firstToken).getTokenString());
                code.append("\"");
            }

            else if(((ConditionalExpr) block.decList.get(i).initializer).trueCase instanceof NumLitExpr)
                code.append( ((NumLitToken) ((ConditionalExpr) block.decList.get(i).initializer).getTrueCase().firstToken).getTokenString());
            else
                code.append( ((IdentExpr) ((ConditionalExpr) block.decList.get(i).initializer).getTrueCase().firstToken).getName());
               code.append(":");
               //falseCase
               if( ((ConditionalExpr) block.decList.get(i).initializer).falseCase instanceof StringLitExpr)
               {
                   code.append("\"");
                   code.append( ((StringLitToken) ((ConditionalExpr) block.decList.get(i).initializer).getFalseCase().firstToken).getTokenString());
                   code.append("\"");
               }
               else if(((ConditionalExpr) block.decList.get(i).initializer).falseCase instanceof NumLitExpr)
               {
                   code.append( ((NumLitToken) ((ConditionalExpr) block.decList.get(i).initializer).getFalseCase().firstToken).getTokenString());
                   code.append(";\n");
               }

               else
                   code.append( ((IdentExpr) ((ConditionalExpr) block.decList.get(i).initializer).getFalseCase().firstToken).getName());

                code.append(");\n");


           }
          else if(block.decList.get(i).initializer instanceof BinaryExpr)
            {
               if(javaType.equals("int"))
                visitBinaryExpr((BinaryExpr)block.decList.get(i).initializer,null);
                code.append(";\n");
            }
       }
       //stmList
       for(int i=0; i<statementSize; i++)
       {
            javaType ="";
           IToken.Kind k= block.statementList.get(i).firstToken.getKind();
           if(block.statementList.get(i) instanceof AssignmentStatement)
           {
               if( k == IToken.Kind.COLON)
               {
                   code.append("return ");
                   if(((AssignmentStatement) block.statementList.get(i)).getE().firstToken.getKind() == IToken.Kind.NUM_LIT )
                   {
                       code.append(((NumLitToken) ((AssignmentStatement) block.statementList.get(i)).getE().firstToken).getValue());
                       //  code.append(";\n");
                   }
                   else if(((AssignmentStatement) block.statementList.get(i)).getE().firstToken.getKind() == IToken.Kind.IDENT)
                   {
                       //xprStr.append("(");
                       st = block.statementList.iterator().next();
                       if(st instanceof AssignmentStatement)
                       {
                           exprStr.append(((IdentExpr) ((AssignmentStatement) st).getE()).getName());
                       }
                       else
                       {
                           exprStr.append(block.decList.get(i).firstToken.getTokenString());
                       }

                   }

               }
               else if(k == IToken.Kind.PLUS ||k == IToken.Kind.MINUS || k == IToken.Kind.TIMES || k == IToken.Kind.DIV)
               {
//               visitBinaryExpr((BinaryExpr)(((AssignmentStatement) block.statementList.get(i)).getE()),exprStr);
                   if(k == IToken.Kind.PLUS)
                   {
                       exprStr.append("+");
                       st = block.statementList.iterator().next();
                       exprStr.append(((IdentExpr) ((AssignmentStatement) block.statementList.get(1)).getE()).getName());
                       //exprStr.append("bb");
                       exprStr.append(")");

                   }
                   else if(k == IToken.Kind.MINUS )
                   {
                       exprStr.append("-");
                       st = block.statementList.iterator().next();
                       exprStr.append(((IdentExpr) ((AssignmentStatement) block.statementList.get(i)).getE()).getName());
                   }
                   else if(k == IToken.Kind.TIMES)
                   {
                       exprStr.append("*");
                       st = block.statementList.iterator().next();
                       exprStr.append(((IdentExpr) ((AssignmentStatement) block.statementList.get(i)).getE()).getName());
                   }
                   else
                   {
                       exprStr.append("/");
                       st = block.statementList.iterator().next();
                       exprStr.append(((IdentExpr) ((AssignmentStatement) block.statementList.get(i)).getE()).getName());
                   }
               }
               else if(((AssignmentStatement) block.statementList.get(i)).firstToken.getKind() == IToken.Kind.IDENT)
               {
                   visitAssignmentStatement((AssignmentStatement) block.statementList.get(i),null);
               }
           }

           else if(block.statementList.get(i) instanceof WriteStatement)
           {
               imPrt.append("import edu.ufl.cise.plcsp23.runtime.ConsoleIO;\n");
                visitWriteStatement((WriteStatement) block.statementList.get(i),null);
           }
           else if(block.statementList.get(i) instanceof WhileStatement)
           {
               visitWhileStatement((WhileStatement) block.statementList.get(i),null);
           }
           else if(block.statementList.get(i) instanceof ReturnStatement)
           {
               visitReturnStatement((ReturnStatement) block.statementList.get(i),((ReturnStatement) block.statementList.get(i)).getE());
           }
       }
       if(exprStr.toString().contains("+") || exprStr.toString().contains("-") || exprStr.toString().contains("/") || exprStr.toString().contains("*"))
       {
           code.append("(");
       }
       code.append(exprStr);
       code.append(";\n");

       }
       if(whst != true)
       {
           code.append("}\n");
       }
       return null;
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitIdent(Ident ident, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitPixelFuncExpr(PixelFuncExpr pixelFuncExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitPredeclaredVarExpr(PredeclaredVarExpr predeclaredVarExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws PLCException
    {
        String par ="";
        StringBuilder parList = new StringBuilder();
       progName = program.ident.getName();
       code = new StringBuilder();
       imPrt = new StringBuilder();
       code.append("public class ");
       code.append(progName);
       code.append("{\n");
        code.append("public static ");
        if(program.type.toString() == "STRING")
        {
            code.append("String");
        }
        else
            code.append(program.type.toString().toLowerCase());
        if(program.paramList.size() ==0)
        {
            code.append(" apply()");
        }
        else
        {
            for(int i=0; i<program.paramList.size(); i++)
            {
                String javaType = program.paramList.get(i).getType().toString();
                if(javaType == "STRING")
                {
                    par = "String"+" "+program.paramList.get(i).getIdent().getName();
                }
                else
                 par = program.paramList.get(i).getType().toString().toLowerCase()+" "+program.paramList.get(i).getIdent().getName();
                parList.append(par);
                if(i!=program.paramList.size()-1)
                {
                    parList.append(",");
                }
            }
            code.append(" apply(");
            code.append(parList);
            code.append(")");
        }

       visitBlock(program.block,program);
       //end of program
        code.append("}");
        StringBuilder finalCode = new StringBuilder();
        if(imPrt == null)
        {
            finalCode.append(code.toString());
        }
        else
            finalCode.append(imPrt.toString()  + code.toString());
     return finalCode.toString();
    }

    @Override
    public Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCException
    {
       code.append("return (");
       if(arg instanceof NumLitExpr)
       {
           code.append(((NumLitExpr) arg).getValue());
       }
       else if(arg instanceof BinaryExpr)
       {
           visitBinaryExpr((BinaryExpr) arg,null);
       }
       else if(arg instanceof ConditionalExpr) {

           code.append("(");
           if (((ConditionalExpr) arg).getGuard() instanceof IdentExpr) {
               code.append(((ConditionalExpr) arg).getGuard().firstToken.getTokenString());
           } else if (((ConditionalExpr) arg).getGuard() instanceof NumLitExpr) {
               code.append(((NumLitExpr) ((ConditionalExpr) arg).getGuard()).getValue());
           }
           code.append(">0");
           code.append(")");

           code.append(" ?");
           //TrueCase
           if (((ConditionalExpr) arg).getTrueCase() instanceof IdentExpr) {
               code.append(((ConditionalExpr) arg).getTrueCase().firstToken.getTokenString());
           } else if (((ConditionalExpr) arg).getTrueCase() instanceof NumLitExpr) {
               code.append(((NumLitExpr) ((ConditionalExpr) arg).getTrueCase()).getValue());
           }
           code.append(" : ");
           //getFalseCase
           if (((ConditionalExpr) arg).getFalseCase() instanceof IdentExpr) {
               code.append(((ConditionalExpr) arg).getFalseCase().firstToken.getTokenString());
           } else if (((ConditionalExpr) arg).getFalseCase() instanceof NumLitExpr) {
               code.append(((NumLitExpr) ((ConditionalExpr) arg).getFalseCase()).getValue());
           }
           code.append(")");
       }
       return null;

    }

    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitUnaryExprPostFix(UnaryExprPostfix unaryExprPostfix, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws PLCException
    {

        int StmSize = whileStatement.block.statementList.size();
        int decSize = whileStatement.block.decList.size();
        code.append("while ");
        code.append("(");
        visitBinaryExpr((BinaryExpr) whileStatement.guard,null);
        code.append(") ");
      //  code.append("{\n");
        whst = true;
        visitBlock(whileStatement.block,null);
        code.append("}\n");
        whst = false;
        return null;
    }

    @Override
    public Object visitWriteStatement(WriteStatement statementWrite, Object arg) throws PLCException
    {
        code.append("ConsoleIO.write");
        code.append("(");
        if(statementWrite.getE() instanceof IdentExpr)
        {
            code.append(statementWrite.getE().firstToken.getTokenString());
        }
        else if(statementWrite.getE() instanceof NumLitExpr)
        {
            code.append(((NumLitExpr) statementWrite.getE()).getValue());
        }
        else if(statementWrite.getE() instanceof StringLitExpr)
        {
            code.append("\"");
            code.append(statementWrite.getE().firstToken.getTokenString());
            code.append("\"");
        }
        code.append(")");
        code.append(";\n");


        return null;
    }

    @Override
    public Object visitZExpr(ZExpr zExpr, Object arg) throws PLCException {
        return null;
    }
}
