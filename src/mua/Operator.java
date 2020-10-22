package mua;

import java.util.ArrayList;

public enum Operator {
    MAKE("make", 2) {
        @Override
        public String execute(ArrayList<String> args) {
            Variable.addMap(args.get(0), args.get(1));
            return args.get(1);
        }
    },
    THING("thing", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            return Variable.getValue(args.get(0));
        }
    },
    COLON(":", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            return Variable.getValue(args.get(0));
        }
    },
    PRINT("print", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            System.out.println(args.get(0));
            return args.get(0);
        }
    },
    READ("read", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            return args.get(0);
        }
    },
    ADD("add", 2) {
        @Override
        public String execute(ArrayList<String> args) {
            return String.valueOf(Double.parseDouble(args.get(0)) + Double.parseDouble(args.get(1)));
        }
    },
    SUB("sub", 2) {
        @Override
        public String execute(ArrayList<String> args) {
            return String.valueOf(Double.parseDouble(args.get(0)) - Double.parseDouble(args.get(1)));
        }
    },
    MUL("mul", 2) {
        @Override
        public String execute(ArrayList<String> args) {
            return String.valueOf(Double.parseDouble(args.get(0)) * Double.parseDouble(args.get(1)));
        }
    },
    DIV("div", 2) {
        @Override
        public String execute(ArrayList<String> args) {
            return String.valueOf(Double.parseDouble(args.get(0)) / Double.parseDouble(args.get(1)));
        }
    },
    MOD("mod", 2) {
        @Override
        public String execute(ArrayList<String> args) {
            return String.valueOf(Double.parseDouble(args.get(0)) % Double.parseDouble(args.get(1)));
        }
    },
    ERASE("erase", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            return Variable.removeMap(args.get(0));
        }
    },
    ISEMPTY("isempty", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            if (args.get(0).equals("") || args.get(0).equals("[]")) {
                return String.valueOf(true);
            } else {
                return String.valueOf(false);
            }
        }
    },
    ISNAME("isname", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            return Variable.exsitKey(args.get(0));
        }
    },
    ISNUMBER("isnumber", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            for (int i = 0; i < args.get(0).length(); i++) {
                if (!Character.isDigit(args.get(0).charAt(i))) {
                    return String.valueOf(false);
                }
            }
            return String.valueOf(true);
        }
    },
    ISWORD("isword", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            if (Boolean.valueOf(Variable.exsitKey(args.get(0)))) {
                return String.valueOf(true);
            } else {
                return String.valueOf(false);
            }
        }
    },
    ISBOOL("isbool", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            if (args.get(0).equals("true") || args.get(0).equals("false")) {
                return String.valueOf(true);
            } else {
                return String.valueOf(false);
            }
        }
    },
    ISLIST("islist", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            // 取出表参数
            String arg = args.get(0);
            // 首先保证表平衡
            int num = Parser.countSymbolNum(arg, "[") - Parser.countSymbolNum(arg, "]");
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
        public String execute(ArrayList<String> args) {
            return args.get(0);
        }
    },
    RUN("run", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            String result = "";
            result = ListParser.ParserFromList(args.get(0));
            return result;
        }
    },
    REPEAT("repeat", 2) {
        @Override
        public String execute(ArrayList<String> args) {
            String result = "";
            for (int i = 0; i < Integer.parseInt(args.get(0)); i++) {
                result = ListParser.ParserFromList(args.get(1));
            }
            return result;
        }
    },
    IF("if", 3) {
        @Override
        public String execute(ArrayList<String> args) {
            String result = "";
            // 如果bool为true，返回第一个列表的返回值
            if (args.get(0).equals("true")) {
                result = ListParser.ParserFromList(args.get(1));
            }
            // 如果bool为false，返回第二个列表的返回值
            else {
                result = ListParser.ParserFromList(args.get(2));
            }
            return result;
        }
    },
    EQUAL("eq", 2) {
        @Override
        public String execute(ArrayList<String> args) {
            return String.valueOf(args.get(0).equals(args.get(1)));
        }
    },
    GREATERTHAN("gt", 2) {
        @Override
        public String execute(ArrayList<String> args) {
            int result = args.get(0).compareTo(args.get(1));
            if (result > 0)
                return String.valueOf(true);
            else
                return String.valueOf(false);
        }
    },
    LESSTHAN("lt", 2) {
        @Override
        public String execute(ArrayList<String> args) {
            int result = args.get(0).compareTo(args.get(1));
            if (result < 0)
                return String.valueOf(true);
            else
                return String.valueOf(false);
        }
    },
    AND("and", 2) {
        @Override
        public String execute(ArrayList<String> args) {
            return String.valueOf(Boolean.parseBoolean(args.get(0)) && Boolean.parseBoolean(args.get(1)));
        }
    },
    OR("or", 2) {
        @Override
        public String execute(ArrayList<String> args) {
            return String.valueOf(Boolean.parseBoolean(args.get(0)) || Boolean.parseBoolean(args.get(1)));
        }
    },
    NOT("not", 1) {
        @Override
        public String execute(ArrayList<String> args) {
            return String.valueOf(!Boolean.parseBoolean(args.get(0)));
        }
    },
    OTHER("", 0) {
        @Override
        public String execute(ArrayList<String> args) {
            throw new UnsupportedOperationException();
        }
    };

    private String symbol;
    private int argNum;

    private Operator(String symbol, int argNum) {
        this.symbol = symbol;
        this.argNum = argNum;
    }

    public int getArgNum() {
        return this.argNum;
    }

    public static Operator getOperator(String symbol) {
        for (Operator op : Operator.values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        return Operator.OTHER;
    }

    public abstract String execute(ArrayList<String> args);

}
