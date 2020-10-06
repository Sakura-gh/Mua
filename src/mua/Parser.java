package mua;

import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    private static Scanner in;

    // 设置输入，必须先做这一步才能执行后续函数！
    public static void setScannerIn(Scanner in) {
        Parser.in = in;
    }

    // 根据符号来解析操作符，并根据操作符读入所需的操作数，调用Operator进行运算并返回结果
    public static String exec(String symbol) {
        Operator op = Operator.getOperator(symbol);
        int argNum = op.getArgNum();
        ArrayList<String> args = new ArrayList<>();
        String word, arg;

        for (int i = 0; i < argNum; i++) {
            word = Parser.in.next();
            arg = parse(word);
            args.add(arg);
        }

        return op.execute(args);
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
        // 如果是操作，则执行操作并把执行结果传入arg中
        else if (Character.isLowerCase(word.charAt(0))) {
            arg = Parser.exec(word);
        } else {
            throw new IllegalArgumentException();
        }

        return arg;
    }

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
}
