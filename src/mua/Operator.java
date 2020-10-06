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
