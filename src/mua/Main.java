package mua;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 输入及全局变量池
        Scanner in = new Scanner(System.in);
        Variable globalVariable = new Variable();

        // 表达式解析器、列表解析器及总的解析器
        ExprParser exprParser = new ExprParser(globalVariable);
        ListParser listParser = new ListParser(exprParser, globalVariable);
        Parser parser = new Parser(listParser, exprParser, in, globalVariable);

        // 设置Operator相关属性
        Operator.setParser(parser);
        Operator.setListParser(listParser);
        Operator.setExprParser(exprParser);
        Operator.setGlobalVariable(globalVariable);

        parser.ParserFromTerminal();
        in.close();
    }
}