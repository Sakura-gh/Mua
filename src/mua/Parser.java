package mua;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
            word = Parser.readNext();
            arg = parse(word);
            args.add(arg);
        }

        return op.execute(args);
    }

    // 用于读取一个基本数据单元，可能是word、number、bool或list
    // 这里主要是为了读取list写的函数，根据"["和"]"个数是否相等来判断是否读取完毕，即表平衡
    public static String readNext() {
        // 只有list需要进行多次读取
        String word = Parser.in.next();
        String wordBuffer = "";
        int num = 0;
        if (word.charAt(0) == '[') {
            num++;
            while (num != 0) {
                wordBuffer = Parser.in.next();
                if (wordBuffer.contains("[")) {
                    num += Parser.countSymbolNum(wordBuffer, "[");
                } else if (wordBuffer.contains("]")) {
                    num -= Parser.countSymbolNum(wordBuffer, "]");
                }
                word += " " + wordBuffer;
            }
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
        // 如果是list，连带着[]一起传入arg中
        else if (word.charAt(0) == '[') {
            arg = word;
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

    // /////////////FOR LIST//////////////////////////////////////////////////
    public static String ParserFromList(String list) {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(list.substring(1, list.length() - 1).split("\\s")));
        Iterator it = words.iterator();
        String word = (String) it.next();

        String result = "";

        while (!word.equals("exit")) {
            result = Parser.execList(word, it);
            if (it.hasNext())
                word = (String) it.next();
            else
                break;
        }

        return result;
    }

    public static String execList(String symbol, Iterator it) {
        Operator op = Operator.getOperator(symbol);
        int argNum = op.getArgNum();
        ArrayList<String> args = new ArrayList<>();
        String word, arg;

        for (int i = 0; i < argNum; i++) {
            word = (String) it.next();
            arg = parseList(word, it);
            args.add(arg);
        }

        return op.execute(args);
    }

    // 可能参数是一个操作的返回结果，因此需要解析处理
    public static String parseList(String word, Iterator it) {
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
            arg = Parser.execList(word, it);
        } else {
            throw new IllegalArgumentException();
        }

        return arg;
    }
    // /////////////FOR LIST//////////////////////////////////////////////////
}
