package mua;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ListParser {
    // /////////////FOR LIST//////////////////////////////////////////////////
    private Variable globalVariable;
    private ExprParser exprParser;

    public ListParser(ExprParser exprParser, Variable globalVariable) {
        this.globalVariable = globalVariable;
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
        ArrayList<String> args = new ArrayList<>();
        Operator op = Operator.getOperator(symbol);

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
                    // 使用上一级函数的变量池解析！！！
                    paramValue = parse(readNext(it), it, variable);
                    localVariable.addMap(paramName, paramValue);
                }
            }

            // 执行
            return Operator.FUNC.execute(args, localVariable);
        }

        int argNum = op.getArgNum();
        String word, arg;

        for (int i = 0; i < argNum; i++) {
            word = readNext(it);
            // 选择变量池解析
            arg = parse(word, it, variable);
            args.add(arg);
        }

        return op.execute(args, variable);
    }

    // public String readNext(Iterator<String> it) {
    // // 只有list需要进行多次读取
    // String word = it.next();
    // String wordBuffer = "";
    // int num = 0;

    // // 注意，读入的第一个word本身可能含有多个”[“和”]“，因此首先初始化num为两者的个数差，再做表平衡
    // num = countSymbolNum(word, "[") - countSymbolNum(word, "]");

    // while (num != 0) {
    // wordBuffer = it.next();
    // if (wordBuffer.contains("[")) {
    // num += countSymbolNum(wordBuffer, "[");
    // }
    // // 不能用else，可能wordBuffer里同时有"["和"]"
    // if (wordBuffer.contains("]")) {
    // num -= countSymbolNum(wordBuffer, "]");
    // }
    // word += " " + wordBuffer;
    // }

    // return word;
    // }

    // 用于读取一个基本数据单元，可能是word、number、bool或list、expression
    // 这里主要是为了读取list写的函数，根据"["和"]"个数是否相等来判断是否读取完毕，即表平衡
    // 添加读取中缀表达式(+-*/)的功能，这里把整个()包含的中缀表达式当做整体读进来
    public String readNext(Iterator<String> it) {
        // 只有list需要进行多次读取
        String word = it.next();

        // 读取list []
        if (word.charAt(0) == '[') {
            word = symbolBalance(it, word, "[", "]", " ");
        }
        // 读取expression ()
        else if (word.charAt(0) == '(') {
            word = symbolBalance(it, word, "(", ")", " ");
        }

        return word;
    }

    // 表平衡
    public String symbolBalance(Iterator<String> it, String word, String symbol1, String symbol2, String space) {
        String wordBuffer = "";
        int num = 0;
        // 注意，读入的第一个word本身可能含有多个symbol1和symbol2，因此首先初始化num为两者的个数差，再做表平衡
        num = countSymbolNum(word, symbol1) - countSymbolNum(word, symbol2);

        // 如果num不等于0，说明word中的"["和"]"个数不平衡，需要继续读取直至表中的"["和"]"个数平衡
        while (num != 0) {
            wordBuffer = it.next();
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
            arg = exec(word, it, variable);
        }
        // 如果是list，连带着[]一起传入arg中
        // 对分支来说，没必要将两个选项的list值都计算出来(如果存在函数调用，堆栈会一直算下去而不返回)，
        // 只需要将两个选项对应的list都传入参数中，由bool值判断执行哪个选项(这样才能到判断条件时成功返回)
        else if (word.charAt(0) == '[') {
            // arg = ParserFromList(word, variable);
            arg = word;
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
