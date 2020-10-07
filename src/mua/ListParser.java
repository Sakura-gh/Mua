package mua;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ListParser {
    // /////////////FOR LIST//////////////////////////////////////////////////
    public static String ParserFromList(String list) {
        ArrayList<String> words = new ArrayList<>(
                Arrays.asList(list.substring(1, list.length() - 1).trim().split("\\s+")));
        Iterator it = words.iterator();
        String word = "";
        String result = "";

        // 如果列表不为空，则读取第一个word；否则直接返回空串
        if (!it.hasNext())
            return word;
        else {
            word = (String) it.next();
            while (!word.equals("exit")) {
                result = ListParser.exec(word, it);
                if (it.hasNext())
                    word = (String) it.next();
                else
                    break;
            }
        }
        return result;
    }

    // repeat 1 [repeat 3 [print [add 1 2]]]会有问题，
    // 前面的repeat是通过Parser解析的，因此list作为参数整个传入，
    // 但后面的repeat是通过ListParser解析的，list表会被直接解出值，然后变成 repeat 3 3，造成error
    // 想要解决这个问题，list中就不能放repeat，或者把Parser的方式统一
    public static String exec(String symbol, Iterator it) {
        Operator op = Operator.getOperator(symbol);
        int argNum = op.getArgNum();
        ArrayList<String> args = new ArrayList<>();
        String word, arg;

        for (int i = 0; i < argNum; i++) {
            word = ListParser.readNext(it);
            arg = parse(word, it);
            args.add(arg);
        }

        return op.execute(args);
    }

    public static String readNext(Iterator it) {
        // 只有list需要进行多次读取
        String word = (String) it.next();
        String wordBuffer = "";
        int num = 0;
        if (word.charAt(0) == '[') {
            num++;
            while (num != 0) {
                wordBuffer = (String) it.next();
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

    // 可能参数是一个操作的返回结果，因此需要解析处理
    public static String parse(String word, Iterator it) {
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
            arg = ListParser.exec(word, it);
        }
        // 如果是list，连带着[]一起传入arg中
        else if (word.charAt(0) == '[') {
            arg = ListParser.ParserFromList(word);
        } else {
            throw new IllegalArgumentException();
        }

        return arg;
    }
    // /////////////FOR LIST//////////////////////////////////////////////////
}
