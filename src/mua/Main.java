package mua;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Parser.setScannerIn(in);
        Parser.ParserFromTerminal();
        in.close();
    }
}