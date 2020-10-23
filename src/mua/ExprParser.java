package mua;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 基本思路：1.做一个纯中缀表达式的解析器；2.先把总表达式扫一遍，把前缀表达式做完(其中的中缀用前面的解析器求解)并转化为字符串中的运算数；再把剩余的中缀表达式求解
public class ExprParser {
    public static String ParserFromExpression(String expression) {
        String infix_expr = parsePrefix(expression);
        String result = parseInfix(infix_expr);

        return result;
    }

    public static String parsePrefix(String expression) {
        // String tmp = expression.substring(1, expression.length() - 1);
        // tmp = tmp.replace("(", " ( ");
        // tmp = tmp.replace(")", " ) ");
        // ArrayList<String> list = new ArrayList<>(Arrays.asList(tmp.split("\\s+")));
        ArrayList<String> list = ExprParser.preprocess(expression);
        // ArrayList<String> result = new ArrayList<>();
        Iterator<String> it = list.iterator();
        String result = "";

        String word;
        // 这里仅执行前缀表达式，并把处理完的结果放到result容器中
        while (it.hasNext()) {
            word = it.next().trim();
            // 遇到前缀就执行
            if (Character.isLowerCase(word.charAt(0))) {
                result += " " + ExprParser.exec(word, it) + " ";
            } else if (!word.equals("")) {
                result += " " + word + " ";
            }
        }
        return result;
    }

    public static String exec(String symbol, Iterator<String> it) {
        Operator op = Operator.getOperator(symbol);

        // 如果symbol是非OP，则直接返回symbol本身的值
        if (op.name().equals("OTHER")) {
            return symbol;
        }

        int argNum = op.getArgNum();
        ArrayList<String> args = new ArrayList<>();
        String word, arg;

        for (int i = 0; i < argNum; i++) {
            word = ExprParser.readNext(it);
            arg = ExprParser.parse(word, it);
            args.add(arg);
        }

        return op.execute(args);
    }

    public static String readNext(Iterator<String> it) {
        // 只有list需要进行多次读取
        String word = it.next();
        String wordBuffer = "";
        int num = 0;

        // 注意，读入的第一个word本身可能含有多个”[“和”]“，因此首先初始化num为两者的个数差，再做表平衡
        num = Parser.countSymbolNum(word, "(") - Parser.countSymbolNum(word, ")");

        while (num != 0) {
            wordBuffer = it.next();
            if (wordBuffer.contains("(")) {
                num += Parser.countSymbolNum(wordBuffer, "(");
            }
            // 不能用else，可能wordBuffer里同时有"["和"]"
            if (wordBuffer.contains(")")) {
                num -= Parser.countSymbolNum(wordBuffer, ")");
            }
            word += " " + wordBuffer;
        }

        return word;
    }

    public static String parse(String word, Iterator<String> it) {
        // use Parser.in to read
        String arg = "";
        // 如果是:标记的字面量，则取出该字面量的值作为参数传入arg
        if (word.charAt(0) == ':') {
            arg = Variable.getValue(word.substring(1));
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
            arg = ExprParser.exec(word, it);
        }
        // 如果是list，连带着[]一起传入arg中
        else if (word.charAt(0) == '(') {
            // arg = ListParser.ParserFromList(word);
            // 匹配是否存在前缀运算(不能匹配进:a)
            Pattern p = Pattern.compile("(\\(|\\)|\\+|\\-|\\*|\\/|\\%|\\s)[a-zA-Z]+");
            Matcher m = p.matcher(word);
            if (m.find()) {
                arg = ExprParser.ParserFromExpression(word);
            }
            // 如果没有前缀运算了，直接调用纯中缀运算，并计算出结果返回
            else {
                arg = ExprParser.parseInfix(word);
            }
        } else {
            throw new IllegalArgumentException();
        }

        return arg;
    }

    // 纯中缀解析器
    public static String parseInfix(String expression) {
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
                    numStack.push(Double.valueOf(Variable.getValue(str.substring(1))));
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
    public static ArrayList<String> preprocess(String expression) {
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