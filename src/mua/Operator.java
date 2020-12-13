package mua;

import java.util.ArrayList;

public enum Operator {
    FUNC("", 0) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            return null;
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
            System.out.println(args.get(0));
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
    REPEAT("repeat", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            String result = "";
            for (int i = 0; i < Integer.parseInt(args.get(0)); i++) {
                result = listParser.ParserFromList(args.get(1), variable);
            }
            return result;
        }
    },
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
            return String.valueOf(args.get(0).equals(args.get(1)));
        }
    },
    GREATERTHAN("gt", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            int result = args.get(0).compareTo(args.get(1));
            if (result > 0)
                return String.valueOf(true);
            else
                return String.valueOf(false);
        }
    },
    LESSTHAN("lt", 2) {
        @Override
        public String execute(ArrayList<String> args, Variable variable) {
            int result = args.get(0).compareTo(args.get(1));
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

    public static void setParser(Parser parser) {
        Operator.parser = parser;
    }

    public static void setListParser(ListParser listParser) {
        Operator.listParser = listParser;
    }

    public static void setExprParser(ExprParser exprParser) {
        Operator.exprParser = exprParser;
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
