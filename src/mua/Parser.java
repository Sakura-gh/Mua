package mua;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

public class Parser {
    private Scanner in;
    private Variable globalVariable;
    // private Variable localVariable;
    private ListParser listParser;
    private ExprParser exprParser;

    Parser(ListParser listParser, ExprParser exprParser, Scanner in, Variable globalVariable) {
        // 设置输入，必须先做这一步才能执行后续函数！
        this.listParser = listParser;
        this.exprParser = exprParser;
        this.in = in;
        this.globalVariable = globalVariable;
    }

    // 从terminal读入命令并进行语法解析与执行
    public void ParserFromTerminal() {
        String word;
        word = in.next();
        while (!word.equals("exit")) {
            exec(word);
            if (in.hasNext())
                word = in.next();
            else
                break;
        }
    }

    // 根据符号来解析操作符，并根据操作符读入所需的操作数，调用Operator进行运算并返回结果
    public String exec(String symbol) {
        ArrayList<String> args = new ArrayList<>();
        Operator op = Operator.getOperator(symbol);
        // // 如果symbol是非OP，则直接返回symbol本身的值
        // if (op.name().equals("OTHER")) {
        // return symbol;
        // }

        // 如果symbol是系统函数(OP)，则查看是否是自定义函数(是否存在于名字空间里)
        if (op.name().equals("OTHER")) {
            // 如果symbol也不存在于名字空间中，则直接返回symbol本身的值
            if (!Boolean.valueOf(globalVariable.exsitKey(symbol))) {
                return symbol;
            }
            // 解析成hashmap传进去，第一个参数是函数体，第二个参数是hashmap
            // 如果symbol存在于名字空间，暂且认为它就是自定义函数，直接把函数名作为参数传给operator执行

            String func = globalVariable.getValue(symbol);
            String funcParam = "";
            String funcBody = "";
            // 提取出表中的函数参数和函数体
            for (int i = 0; i < func.length(); i++) {
                if (func.charAt(i) == ']') {
                    funcParam = func.substring(1, i + 1).trim();
                    funcBody = func.substring(i + 1, func.length() - 1).trim();
                    args.add(funcBody);
                    break;
                }
            }

            // 创建表的局部变量
            Variable localVariable = new Variable();
            String[] params = funcParam.substring(1, funcParam.length() - 1).trim().split("\\s+");
            String paramName, paramValue;
            for (int i = 0; i < params.length; i++) {
                // 如果函数表为空，则params会有且只有一个空字符串元素，故要排除这种情况
                if (!params[i].equals("")) {
                    paramName = params[i];
                    // 从全局变量池中解析出赋给局部变量的值！！！
                    paramValue = parse(readNext(), globalVariable);
                    localVariable.addMap(paramName, paramValue);
                }
            }

            // 执行
            return Operator.FUNC.execute(args, localVariable);
        }

        int argNum = op.getArgNum();
        String word, arg;

        for (int i = 0; i < argNum; i++) {
            word = readNext();
            // 默认使用全局变量池解析
            arg = parse(word, globalVariable);
            args.add(arg);
        }

        return op.execute(args, globalVariable);
    }

    // 用于读取一个基本数据单元，可能是word、number、bool或list、expression
    // 这里主要是为了读取list写的函数，根据"["和"]"个数是否相等来判断是否读取完毕，即表平衡
    // 添加读取中缀表达式(+-*/)的功能，这里把整个()包含的中缀表达式当做整体读进来
    public String readNext() {
        // 只有list需要进行多次读取
        String word = in.next();

        // 读取list []
        if (word.charAt(0) == '[') {
            word = symbolBalance(word, "[", "]", " ");
        }
        // 读取expression ()
        else if (word.charAt(0) == '(') {
            word = symbolBalance(word, "(", ")", " ");
        }

        return word;
    }

    // 表平衡
    public String symbolBalance(String word, String symbol1, String symbol2, String space) {
        String wordBuffer = "";
        int num = 0;
        // 注意，读入的第一个word本身可能含有多个symbol1和symbol2，因此首先初始化num为两者的个数差，再做表平衡
        num = countSymbolNum(word, symbol1) - countSymbolNum(word, symbol2);

        // 如果num不等于0，说明word中的"["和"]"个数不平衡，需要继续读取直至表中的"["和"]"个数平衡
        while (num != 0) {
            wordBuffer = in.next();
            if (wordBuffer.contains(symbol1)) {
                num += countSymbolNum(wordBuffer, symbol1);
            }
            // 不能用else，可能wordBuffer里同时有"["和"]"
            if (wordBuffer.contains(symbol2)) {
                num -= countSymbolNum(wordBuffer, symbol2);
            }
            word += space + wordBuffer;
        }
        return word;
    }

    // 返回字符串S中包含符号c的个数
    public int countSymbolNum(String s, String c) {
        return s.length() - s.replace(c, "").length();
    }

    // 可能参数是一个操作的返回结果，因此需要解析处理
    // 尤其注意":"，解析时需要根据不同变量池来决定
    public String parse(String word, Variable variable) {
        // use Parser.in to read
        String arg = "";
        // 如果是"标记的字面量，则从第2个字符开始的子串作为参数传入arg
        if (word.charAt(0) == '"') {
            arg = word.substring(1);
        }
        // 如果是:标记的字面量，则取出该字面量的值作为参数传入arg
        else if (word.charAt(0) == ':') {
            // 在函数中访问（读取）变量的值的时候，首先访问本地，如果本地不存在，则访问全局
            arg = variable.getValue(word.substring(1));
            if (arg == null) {
                arg = globalVariable.getValue(word.substring(1));
            }
        }
        // 如果是数字(包含负数)，则直接作为参数传入arg
        else if (Character.isDigit(word.charAt(0)) || word.charAt(0) == '-') {
            arg = word;
        }
        // 如果是bool，则直接作为参数传入arg
        else if (word.equals("true") || word.equals("false")) {
            arg = word;
        }
        // 如果是操作，则执行操作并把执行结果传入arg中
        else if (Character.isLowerCase(word.charAt(0)) && !word.equals("true") && !word.equals("false")) {
            arg = exec(word);
        }
        // 如果是expression，则返回执行后的值
        else if (word.charAt(0) == '(') {
            // arg = ExprParser.parseInfix(word); // 纯中缀表达式
            arg = exprParser.ParserFromExpression(word, variable);
        }
        // 如果是list，连带着[]一起传入arg中
        else if (word.charAt(0) == '[') {
            arg = word;
        } else {
            throw new IllegalArgumentException();
        }

        return arg;
    }

}
