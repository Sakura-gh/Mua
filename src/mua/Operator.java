package mua;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public enum Operator {
    FUNC("", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // args[0]里存放的是函数体，variable里存放的是函数局部变量池
            return listParser.ParserFromList(args.get(0), variable);
        }
    },
    RETURN("return", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return args.get(0);
        }
    },
    EXPORT("export", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            globalVariable.addMap(args.get(0), variable.getValue(args.get(0)));
            return variable.getValue(args.get(0));
        }
    },
    MAKE("make", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            variable.addMap(args.get(0), args.get(1));
            return args.get(1);
        }
    },
    THING("thing", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return variable.getValue(args.get(0));
        }
    },
    COLON(":", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return variable.getValue(args.get(0));
        }
    },
    PRINT("print", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // 如果是表，则不打印外层的[]；如果是其他类型，直接打印值即可
            if (args.get(0).charAt(0) == '[' && args.get(0).charAt(args.get(0).length() - 1) == ']') {
                System.out.println(args.get(0).substring(1, args.get(0).length() - 1).trim());
            } else {
                System.out.println(args.get(0));
            }
            return args.get(0);
        }
    },
    READ("read", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return args.get(0);
        }
    },
    ADD("add", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return String.valueOf(Double.parseDouble(args.get(0)) + Double.parseDouble(args.get(1)));
        }
    },
    SUB("sub", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return String.valueOf(Double.parseDouble(args.get(0)) - Double.parseDouble(args.get(1)));
        }
    },
    MUL("mul", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return String.valueOf(Double.parseDouble(args.get(0)) * Double.parseDouble(args.get(1)));
        }
    },
    DIV("div", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return String.valueOf(Double.parseDouble(args.get(0)) / Double.parseDouble(args.get(1)));
        }
    },
    MOD("mod", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return String.valueOf(Double.parseDouble(args.get(0)) % Double.parseDouble(args.get(1)));
        }
    },
    ERASE("erase", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return variable.removeMap(args.get(0));
        }
    },
    ISEMPTY("isempty", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            if (args.get(0).equals("") || args.get(0).equals("[]")) {
                return String.valueOf(true);
            } else {
                return String.valueOf(false);
            }
        }
    },
    ISNAME("isname", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return variable.exsitKey(args.get(0));
        }
    },
    ISNUMBER("isnumber", 1) {
        // 需要用抛出异常来判断是否为int、double、float类型数字，而不能单纯扫描string里的每个字符(误判负号和小数点)
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            String str = args.get(0);
            try {
                Integer.parseInt(str);
                return String.valueOf(true);
            } catch (NumberFormatException e1) {
                try {
                    Double.parseDouble(str);
                    return String.valueOf(true);
                } catch (NumberFormatException e2) {
                    try {
                        Float.parseFloat(str);
                        return String.valueOf(true);
                    } catch (NumberFormatException e3) {
                        return String.valueOf(false);
                    }
                }
            }
        }
    },
    ISWORD("isword", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            if (Boolean.valueOf(variable.exsitKey(args.get(0)))) {
                return String.valueOf(true);
            } else {
                return String.valueOf(false);
            }
        }
    },
    ISBOOL("isbool", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            if (args.get(0).equals("true") || args.get(0).equals("false")) {
                return String.valueOf(true);
            } else {
                return String.valueOf(false);
            }
        }
    },
    ISLIST("islist", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // 取出表参数
            String arg = args.get(0);
            // 首先保证表平衡
            int num = parser.countSymbolNum(arg, "[") - parser.countSymbolNum(arg, "]");
            // 其次保证首尾是list的标志位"["和"]"
            if (arg.charAt(0) == '[' && arg.charAt(arg.length() - 1) == ']' && num == 0) {
                return String.valueOf(true);
            } else {
                return String.valueOf(false);
            }
        }
    },
    // 读取表
    READLIST("readlist", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return args.get(0);
        }
    },
    RUN("run", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            String result = "";
            result = listParser.ParserFromList(args.get(0), variable);
            return result;
        }
    },
    // REPEAT("repeat", 2) {
    // @Override
    // public String execute(ArrayList<String> args, Variable variable) {
    // String result = "";
    // for (int i = 0; i < Integer.parseInt(args.get(0)); i++) {
    // result = listParser.ParserFromList(args.get(1), variable);
    // }
    // return result;
    // }
    // },
    IF("if", 3) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            String result = "";
            // 如果bool为true，返回第一个列表的返回值
            if (args.get(0).equals("true")) {
                result = listParser.ParserFromList(args.get(1), variable);
            }
            // 如果bool为false，返回第二个列表的返回值
            else {
                result = listParser.ParserFromList(args.get(2), variable);
            }
            return result;
        }
    },
    EQUAL("eq", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // return String.valueOf(args.get(0).equals(args.get(1)));

            // if (Character.isDigit(args.get(0).charAt(0))) {
            // // 避免实际上相等的int和double比较返回false，这里先将两个操作数转化为double再做比较
            // return
            // String.valueOf(Double.valueOf(args.get(0).trim()).equals(Double.valueOf(args.get(1).trim())));
            // } else {
            // // 非数字情况的判断
            // return String.valueOf(args.get(0).equals(args.get(1)));
            // }

            // 注意两个比较对象可能不一定是数字，因此用try-catch来判别两种情况
            try {
                // 避免实际上相等的int和double比较返回false，这里先将两个操作数转化为double再做比较
                return String.valueOf(Double.valueOf(args.get(0)).equals(Double.valueOf(args.get(1))));
            } catch (NumberFormatException e) {
                // 非数字情况的判断
                return String.valueOf(args.get(0).equals(args.get(1)));
            }
        }
    },
    GREATERTHAN("gt", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            int result;
            try {
                // 如果比较的两个对象都是数字，则将它们都转化成double再进行比较(避免int和double比较造成错误)
                result = Double.valueOf(args.get(0)).compareTo(Double.valueOf(args.get(1)));
            } catch (NumberFormatException e) {
                // 如果比较的对象有一个不是数字，则会抛出异常，此时直接对字符串进行比较即可
                result = args.get(0).compareTo(args.get(1));
            }
            if (result > 0)
                return String.valueOf(true);
            else
                return String.valueOf(false);
        }
    },
    LESSTHAN("lt", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            int result;
            try {
                // 如果比较的两个对象都是数字，则将它们都转化成double再进行比较(避免int和double比较造成错误)
                result = Double.valueOf(args.get(0)).compareTo(Double.valueOf(args.get(1)));
            } catch (NumberFormatException e) {
                // 如果比较的对象有一个不是数字，则会抛出异常，此时直接对字符串进行比较即可
                result = args.get(0).compareTo(args.get(1));
            }
            if (result < 0)
                return String.valueOf(true);
            else
                return String.valueOf(false);
        }
    },
    AND("and", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return String.valueOf(Boolean.parseBoolean(args.get(0)) && Boolean.parseBoolean(args.get(1)));
        }
    },
    OR("or", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return String.valueOf(Boolean.parseBoolean(args.get(0)) || Boolean.parseBoolean(args.get(1)));
        }
    },
    NOT("not", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return String.valueOf(!Boolean.parseBoolean(args.get(0)));
        }
    },
    WORD("word", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // 第一个参数必须是word，第二个参数可以是word/number/bool，并实现两个参数的拼接
            return args.get(0) + args.get(1);
        }
    },
    SENTENCE("sentence", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // 把不同操作数里的内容连接合并成一个新的表，操作数可能是表，也可能是其他类型
            String arg0 = args.get(0);
            String arg1 = args.get(1);
            // 如果操作数是表，则先提取表里的所有元素(去除外层的[]); 如果不是表，直接取值即可
            if (arg0.charAt(0) == '[' && arg0.charAt(arg0.length() - 1) == ']') {
                arg0 = arg0.substring(1, arg0.length() - 1).trim();
            }
            if (arg1.charAt(0) == '[' && arg1.charAt(arg1.length() - 1) == ']') {
                arg1 = arg1.substring(1, arg1.length() - 1).trim();
            }
            return "[" + arg0 + " " + arg1 + "]";
        }
    },
    LIST("list", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // 与sentence不同的是，list是把整个操作数作为单个元素合并到新表中，即不对表操作数做拆分
            return "[" + args.get(0) + " " + args.get(1) + "]";
        }
    },
    JOIN("join", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // 第一个参数必须是表，把第二个参数(可以是任何类型)添加为该表的最后一个元素
            return args.get(0).substring(0, args.get(0).length() - 1).trim() + " " + args.get(1) + "]";
        }
    },
    FIRST("first", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // 返回word的第一个字符，或list的第一个元素
            // 注意，对list来说，不能简单用空格的split划分，要考虑[[a b] ...]中[a b]整体作为一个元素
            String arg = args.get(0);
            if (arg.charAt(0) == '[' && arg.charAt(arg.length() - 1) == ']') {
                ArrayList<String> words = new ArrayList<>(
                        Arrays.asList(arg.substring(1, arg.length() - 1).trim().split("\\s+")));
                return listParser.readNext(words.iterator());
            } else {
                return String.valueOf(arg.charAt(0));
            }
        }
    },
    LAST("last", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // 返回word的最后一个字符，或list的最后一个元素
            String arg = args.get(0);
            if (arg.charAt(0) == '[' && arg.charAt(arg.length() - 1) == ']') {
                ArrayList<String> words = new ArrayList<>(
                        Arrays.asList(arg.substring(1, arg.length() - 1).trim().split("\\s+")));
                Iterator<String> it = words.iterator();
                String result = "";
                while (it.hasNext()) {
                    result = listParser.readNext(it); // 一个readnext(it)里可能包含多个it.next()
                }
                return result;
            } else {
                return String.valueOf(arg.charAt(arg.length() - 1));
            }
        }
    },
    BUTFIRST("butfirst", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // 返回除第一个元素外剩下的表，或除第一个字符外剩下的字
            String arg = args.get(0);
            if (arg.charAt(0) == '[' && arg.charAt(arg.length() - 1) == ']') {
                ArrayList<String> words = new ArrayList<>(
                        Arrays.asList(arg.substring(1, arg.length() - 1).trim().split("\\s+")));
                Iterator<String> it = words.iterator();
                String result = "[";
                listParser.readNext(it); // 先消耗掉表中的第一个元素，可能是普通变量，也可能是表
                while (it.hasNext()) {
                    result += listParser.readNext(it) + " "; // 读完表中剩余的元素
                }
                result = result.trim() + "]";
                return result;
            } else {
                return arg.substring(1);
            }
        }
    },
    BUTLAST("butlast", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // 返回除最后一个元素外剩下的表，或除最后一个字符外剩下的字
            String arg = args.get(0);
            if (arg.charAt(0) == '[' && arg.charAt(arg.length() - 1) == ']') {
                ArrayList<String> words = new ArrayList<>(
                        Arrays.asList(arg.substring(1, arg.length() - 1).trim().split("\\s+")));
                Iterator<String> it = words.iterator();
                String result = "[";
                String last = "";
                String next = "";
                // 保证最后一个元素不被合进result里，使用readNext()是为了把内部的整个表作为单个元素读取
                if (it.hasNext())
                    last = listParser.readNext(it);
                while (it.hasNext()) {
                    next = listParser.readNext(it);
                    result += last + " ";
                    last = next;
                }
                result = result.trim() + "]";
                return result;
            } else {
                return arg.substring(0, arg.length() - 1);
            }
        }
    },
    RANDOM("random", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return String.valueOf(Math.random() * Double.valueOf(args.get(0)));
        }
    },
    INT("int", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            // 注意，不能使用Integer.valueOf(Double)，但是可以直接调用Double类的intValue()函数获取int部分的值
            return String.valueOf(Double.valueOf(args.get(0)).intValue());
        }
    },
    SQRT("sqrt", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return String.valueOf(Math.sqrt(Double.valueOf(args.get(0))));
        }
    },
    SAVE("save", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            globalVariable.saveVariable(args.get(0));
            return args.get(0);
        }
    },
    LOAD("load", 1) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            try {
                Scanner in = new Scanner(new FileInputStream(args.get(0)));
                Parser loadParser = new Parser(listParser, exprParser, in, globalVariable);
                loadParser.ParserFromStream();
            } catch (IOException e) {
                System.out.println("load error!");
            }

            return String.valueOf(true);
        }
    },
    ERALL("erall", 0) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            globalVariable.clearVariable();
            return String.valueOf(true);
        }
    },
    POALL("poall", 0) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return globalVariable.getVarible();
        }
    },
    OTHER("", 0) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            throw new UnsupportedOperationException();
        }
    };

    // 整个Operator通用的属性
    private static Parser parser;
    private static ListParser listParser;
    private static ExprParser exprParser;
    private static Variable globalVariable;

    public static void setParser(Parser parser) {
        Operator.parser = parser;
    }

    public static void setListParser(ListParser listParser) {
        Operator.listParser = listParser;
    }

    public static void setExprParser(ExprParser exprParser) {
        Operator.exprParser = exprParser;
    }

    public static void setGlobalVariable(Variable globalVariable) {
        Operator.globalVariable = globalVariable;
    }

    public static Operator getOperator(String symbol) {
        for (Operator op : Operator.values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        return Operator.OTHER;
    }

    // 每个OP各自的属性
    private String symbol;
    private int argNum;

    private Operator(String symbol, int argNum) {
        this.symbol = symbol;
        this.argNum = argNum;
    }

    public int getArgNum() {
        return this.argNum;
    }

    // op本身是与变量空间无关的，但op的执行却是与变量空间相关的
    // 这里不再使用Variable作为静态全局变量，而是通过变量池传参的方式决定执行函数的变量空间
    public abstract String execute(ArrayList<String> args, Variable variable);

}
