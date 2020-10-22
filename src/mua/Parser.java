package mua;

import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    private static Scanner in;

    // 设置输入，必须先做这一步才能执行后续函数！
    public static void setScannerIn(Scanner in) {
        Parser.in = in;
    }

    // 从terminal读入命令并进行语法解析与执行
    public static void ParserFromTerminal() {
        String word;
        word = Parser.in.next();
        while (!word.equals("exit")) {
            Parser.exec(word);
            if (Parser.in.hasNext())
                word = Parser.in.next();
            else
                break;
        }
    }

    // 根据符号来解析操作符，并根据操作符读入所需的操作数，调用Operator进行运算并返回结果
    public static String exec(String symbol) {
        Operator op = Operator.getOperator(symbol);

        // 如果symbol是非OP，则直接返回symbol本身的值
        if (op.name().equals("OTHER")) {
            return symbol;
        }

        int argNum = op.getArgNum();
        ArrayList<String> args = new ArrayList<>();
        String word, arg;

        for (int i = 0; i < argNum; i++) {
            word = Parser.readNext();
            arg = parse(word);
            args.add(arg);
        }

        return op.execute(args);
    }

    // 用于读取一个基本数据单元，可能是word、number、bool或list、expression
    // 这里主要是为了读取list写的函数，根据"["和"]"个数是否相等来判断是否读取完毕，即表平衡
    // 添加读取中缀表达式(+-*/)的功能，这里把整个()包含的中缀表达式当做整体读进来
    public static String readNext() {
        // 只有list需要进行多次读取
        String word = Parser.in.next();

        // 读取list []
        if (word.charAt(0) == '[') {
            word = Parser.symbolBalance(word, "[", "]", " ");
        }
        // 读取expression ()
        else if (word.charAt(0) == '(') {
            word = Parser.symbolBalance(word, "(", ")", "");
        }

        return word;
    }

    // 表平衡
    public static String symbolBalance(String word, String symbol1, String symbol2, String space) {
        String wordBuffer = "";
        int num = 0;
        // 注意，读入的第一个word本身可能含有多个symbol1和symbol2，因此首先初始化num为两者的个数差，再做表平衡
        num = Parser.countSymbolNum(word, symbol1) - Parser.countSymbolNum(word, symbol2);

        // 如果num不等于0，说明word中的"["和"]"个数不平衡，需要继续读取直至表中的"["和"]"个数平衡
        while (num != 0) {
            wordBuffer = Parser.in.next();
            if (wordBuffer.contains(symbol1)) {
                num += Parser.countSymbolNum(wordBuffer, symbol1);
            }
            // 不能用else，可能wordBuffer里同时有"["和"]"
            if (wordBuffer.contains(symbol2)) {
                num -= Parser.countSymbolNum(wordBuffer, symbol2);
            }
            word += space + wordBuffer;
        }
        return word;
    }

    // 返回字符串S中包含符号c的个数
    public static int countSymbolNum(String s, String c) {
        return s.length() - s.replace(c, "").length();
    }

    // 可能参数是一个操作的返回结果，因此需要解析处理
    public static String parse(String word) {
        // use Parser.in to read
        String arg = "";
        // 如果是"标记的字面量，则从第2个字符开始的子串作为参数传入arg
        if (word.charAt(0) == '"') {
            arg = word.substring(1);
        }
        // 如果是:标记的字面量，则取出该字面量的值作为参数传入arg
        else if (word.charAt(0) == ':') {
            arg = Variable.getValue(word.substring(1));
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
            arg = Parser.exec(word);
        }
        // 如果是expression，则返回执行后的值
        else if (word.charAt(0) == '(') {
            arg = ExprParser.parseInfix(word);
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
