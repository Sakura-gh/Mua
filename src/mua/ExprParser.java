package mua;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 基本思路：1.做一个纯中缀表达式的解析器；2.先把总表达式扫一遍，把前缀表达式做完(其中的中缀用前面的解析器求解)并转化为字符串中的运算数；再把剩余的中缀表达式求解
public class ExprParser {
    private Variable globalVariable;

    public ExprParser(Variable globalVariable) {
        this.globalVariable = globalVariable;
    }

    public String ParserFromExpression(String expression, Variable variable) {
        String infix_expr = parsePrefix(expression, variable);
        String result = parseInfix(infix_expr, variable);

        return result;
    }

    public String parsePrefix(String expression, Variable variable) {
        ArrayList<String> list = preprocess(expression);
        Iterator<String> it = list.iterator();
        String result = "";

        String word;
        // 这里仅执行前缀表达式，并把处理完的结果放到result容器中
        while (it.hasNext()) {
            word = it.next().trim();
            // 遇到前缀就执行
            if (Character.isLowerCase(word.charAt(0))) {
                result += " " + exec(word, it, variable) + " ";
            } else if (!word.equals("")) {
                result += " " + word + " ";
            }
        }
        return result;
    }

    public String exec(String symbol, Iterator<String> it, Variable variable) {
        ArrayList<String> args = new ArrayList<>();
        Operator op = Operator.getOperator(symbol);

        // 如果symbol不是系统函数(OP)，则查看是否是自定义函数(是否存在于名字空间里)
        if (op.name().equals("OTHER")) {
            // 如果symbol既不是全局变量池中的自定义函数，也不是局部变量池中作为参数的的函数名，则直接返回symbol本身的值
            if (!Boolean.valueOf(globalVariable.exsitKey(symbol)) && !Boolean.valueOf(variable.exsitKey(symbol))) {
                return symbol;
            }
            // 解析成hashmap传进去，第一个参数是函数体，第二个参数是hashmap
            // 如果symbol存在于名字空间，暂且认为它就是自定义函数，直接把函数名作为参数传给operator执行

            // 首先查看是否是在全局变量池中用户自定义的函数
            String func = globalVariable.getValue(symbol);
            // 其次查看是否是作为参数传入局部变量池的函数
            if (func == null) {
                func = variable.getValue(symbol);
            }
            // 如果该函数名既不是系统函数也不是自定义函数或函数参数，则报error
            if (func == null) {
                System.out.println("the function not exists!");
            }

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
            arg = parse(word, it, variable);
            args.add(arg);
        }

        return op.execute(args, variable);
    }

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

    // 需要考虑在不同变量池下的解析
    public String parse(String word, Iterator<String> it, Variable variable) {
        // use Parser.in to read
        String arg = "";
        // 如果是:标记的字面量，则取出该字面量的值作为参数传入arg
        if (word.charAt(0) == ':') {
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
            arg = word.equals("true") ? "1" : "0";
        }
        // 如果是操作，则执行操作并把执行结果传入arg中
        else if (Character.isLowerCase(word.charAt(0)) && !word.equals("true") && !word.equals("false")) {
            arg = exec(word, it, variable);
        }
        // 如果是list，连带着[]一起传入arg中
        else if (word.charAt(0) == '(') {
            // arg = ListParser.ParserFromList(word);
            // 匹配是否存在前缀运算(不能匹配进:a)
            Pattern p = Pattern.compile("(\\(|\\)|\\+|\\-|\\*|\\/|\\%|\\s)[a-zA-Z]+");
            Matcher m = p.matcher(word);
            if (m.find()) {
                arg = ParserFromExpression(word, variable);
            }
            // 如果没有前缀运算了，直接调用纯中缀运算，并计算出结果返回
            else {
                arg = parseInfix(word, variable);
            }
        } else {
            throw new IllegalArgumentException();
        }

        return arg;
    }

    // 纯中缀解析器
    // 需要考虑在不同变量池下的解析
    public String parseInfix(String expression, Variable variable) {
        // 保存split后的表达式
        ArrayList<String> expr = preprocess(expression);

        // result
        double operand1, operand2, result, final_result = 0;
        // operand
        Stack<Double> numStack = new Stack<>();
        // operator
        Stack<ExprOp> opStack = new Stack<>();
        // op
        ExprOp op;

        Iterator<String> it = expr.iterator();
        String str;
        while (it.hasNext()) {
            str = it.next();
            op = ExprOp.getExprOp(str);
            // 如果扫描到字面量或数字，则直接压入数字栈
            if (op == ExprOp.OTHER) {
                if (str.charAt(0) == ':') {
                    // 如果在局部变量池中找不到对应变量，则去全局变量池中寻找，暂不考虑全局变量池中也不存在的情况
                    String value = variable.getValue(str.substring(1));
                    if (value == null) {
                        value = globalVariable.getValue(str.substring(1));
                    }
                    numStack.push(Double.valueOf(value));
                } else {
                    numStack.push(Double.valueOf(str));
                }
                continue;
            }
            // 如果运算符栈为空，或者扫描到左括号，则直接压入符号栈
            if (opStack.isEmpty() || op == ExprOp.LEFT_BRACKET) {
                opStack.push(op);
                continue;
            }
            // 如果扫描到右括号，则直接计算
            if (op == ExprOp.RIGHT_BRACKET) {
                while (opStack.peek() != ExprOp.LEFT_BRACKET && numStack.size() >= 2) {
                    operand2 = numStack.pop();
                    operand1 = numStack.pop();
                    result = opStack.pop().compute(operand1, operand2);
                    numStack.push(result);
                }
                if (opStack.pop() != ExprOp.LEFT_BRACKET) {
                    throw new IllegalArgumentException();
                }
                continue;
            }
            if (op.getPriority() <= opStack.peek().getPriority()) {
                if (numStack.size() < 2) {
                    throw new IllegalArgumentException();
                }
                while (opStack.size() > 0 && numStack.size() >= 2 && op.getPriority() <= opStack.peek().getPriority()) {
                    operand2 = numStack.pop();
                    operand1 = numStack.pop();
                    result = opStack.pop().compute(operand1, operand2);
                    numStack.push(result);
                }
                opStack.push(op);
            } else {
                opStack.push(op);
            }
        }
        while (opStack.size() > 0 && numStack.size() >= 2) {
            operand2 = numStack.pop();
            operand1 = numStack.pop();
            result = opStack.pop().compute(operand1, operand2);
            numStack.push(result);
        }
        if (numStack.size() == 1 && opStack.isEmpty()) {
            final_result = numStack.pop();
        }
        return String.valueOf(final_result);
    }

    // 返回将运算符和运算数分隔处理完成后的字符串数组，关于负数部分一定要单独处理
    public ArrayList<String> preprocess(String expression) {
        // 将符号两侧加空格
        String[] op = new String[] { "(", ")", "+", "-", "*", "/", "%" };
        String tmp = expression.substring(1, expression.length() - 1);
        for (String i : op) {
            tmp = tmp.replace(i, " " + i + " ");
        }
        // split
        String[] tmp_array = tmp.trim().split("\\s+");

        ArrayList<String> result = new ArrayList<>();
        // 检查如果出现负数，则与后面的数字合并
        // 注意，考虑add 3 -2的情况，此时同样需要把负号与数字2合并，判断条件是：符号前存在操作名
        for (int i = 0; i < tmp_array.length; i++) {
            if (tmp_array[i].equals("-") && (i == 0 || tmp_array[i - 1].equals("(") || tmp_array[i - 1].equals("+")
                    || tmp_array[i - 1].equals("-") || tmp_array[i - 1].equals("*") || tmp_array.equals("/")
                    || tmp_array[i - 1].equals("%") || (i >= 2 && Character.isLowerCase(tmp_array[i - 2].charAt(0))))) {
                result.add(tmp_array[i] + tmp_array[i + 1]);
                i += 1;
            } else {
                result.add(tmp_array[i]);
            }
        }
        return result;
    }

}

enum ExprOp {
    LEFT_BRACKET("(", 0) {
        @Override
        public double compute(double operand1, double operand2) {
            throw new UnsupportedOperationException();
        }
    },
    RIGHT_BRACKET(")", 0) {
        @Override
        public double compute(double operand1, double operand2) {
            throw new UnsupportedOperationException();
        }
    },
    PLUS("+", 1) {
        @Override
        public double compute(double operand1, double operand2) {
            return operand1 + operand2;
        }
    },
    MINUS("-", 1) {
        @Override
        public double compute(double operand1, double operand2) {
            return operand1 - operand2;
        }
    },
    MULTIPLY("*", 2) {
        @Override
        public double compute(double operand1, double operand2) {
            return operand1 * operand2;
        }
    },
    DIVIDE("/", 2) {
        @Override
        public double compute(double operand1, double operand2) {
            return operand1 / operand2;
        }
    },
    REMAINDER("%", 2) {
        @Override
        public double compute(double operand1, double operand2) {
            return operand1 % operand2;
        }
    },
    OTHER("", 0) {
        @Override
        public double compute(double operand1, double operand2) {
            return 0;
        }
    };

    private String symbol;
    private int priority;

    private ExprOp(String symbol, int priority) {
        this.symbol = symbol;
        this.priority = priority;
    }

    public static ExprOp getExprOp(String symbol) {
        for (ExprOp op : ExprOp.values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        // 数字
        return OTHER;
    }

    public int getPriority() {
        return priority;
    }

    public abstract double compute(double operand1, double operand2);
}