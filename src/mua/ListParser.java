package mua;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ListParser {
    // /////////////FOR LIST//////////////////////////////////////////////////
    private Variable globalVaribale;
    private ExprParser exprParser;

    public ListParser(ExprParser exprParser, Variable globalVariable) {
        this.globalVaribale = globalVariable;
        this.exprParser = exprParser;
    }

    public String ParserFromList(String list, Variable variable) {
        ArrayList<String> words = new ArrayList<>(
                Arrays.asList(list.substring(1, list.length() - 1).trim().split("\\s+")));
        Iterator<String> it = words.iterator();
        String word = "";
        String result = "";

        // 如果列表不为空，则读取第一个word；如果列表为空，words也还会有第一个元素为空串“”
        // 如果列表为空，则返回"[]"
        if (!it.hasNext())
            result = "";
        else if (words.size() == 1 && words.get(0).equals(""))
            result = "[]";
        else {
            word = it.next();
            while (!word.equals("exit")) {
                result = exec(word, it, variable);
                if (it.hasNext())
                    word = it.next();
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
    public String exec(String symbol, Iterator<String> it, Variable variable) {
        Operator op = Operator.getOperator(symbol);

        // 如果symbol是非OP，则直接返回symbol本身的值
        if (op.name().equals("OTHER")) {
            return symbol;
        }

        int argNum = op.getArgNum();
        ArrayList<String> args = new ArrayList<>();
        String word, arg;

        for (int i = 0; i < argNum; i++) {
            word = readNext(it);
            // 选择变量池解析
            arg = parse(word, it, variable);
            args.add(arg);
        }

        return op.execute(args, variable);
    }

    public String readNext(Iterator<String> it) {
        // 只有list需要进行多次读取
        String word = it.next();
        String wordBuffer = "";
        int num = 0;

        // 注意，读入的第一个word本身可能含有多个”[“和”]“，因此首先初始化num为两者的个数差，再做表平衡
        num = countSymbolNum(word, "[") - countSymbolNum(word, "]");

        while (num != 0) {
            wordBuffer = it.next();
            if (wordBuffer.contains("[")) {
                num += countSymbolNum(wordBuffer, "[");
            }
            // 不能用else，可能wordBuffer里同时有"["和"]"
            if (wordBuffer.contains("]")) {
                num -= countSymbolNum(wordBuffer, "]");
            }
            word += " " + wordBuffer;
        }

        return word;
    }

    // 返回字符串S中包含符号c的个数
    public int countSymbolNum(String s, String c) {
        return s.length() - s.replace(c, "").length();
    }

    // 可能参数是一个操作的返回结果，因此需要解析处理
    // 需要考虑在不同变量池下的解析
    public String parse(String word, Iterator<String> it, Variable variable) {
        // use Parser.in to read
        String arg = "";
        // 如果是"标记的字面量，则从第2个字符开始的子串作为参数传入arg
        if (word.charAt(0) == '"') {
            arg = word.substring(1);
        }
        // 如果是:标记的字面量，则取出该字面量的值作为参数传入arg
        else if (word.charAt(0) == ':') {
            arg = variable.getValue(word.substring(1));
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
            arg = exec(word, it, variable);
        }
        // 如果是list，连带着[]一起传入arg中
        else if (word.charAt(0) == '[') {
            arg = ParserFromList(word, variable);
        }
        // 如果是expression，则返回执行后的值
        else if (word.charAt(0) == '(') {
            arg = exprParser.ParserFromExpression(word, variable);
        } else {
            throw new IllegalArgumentException();
        }

        return arg;
    }
    // /////////////FOR LIST//////////////////////////////////////////////////
}
