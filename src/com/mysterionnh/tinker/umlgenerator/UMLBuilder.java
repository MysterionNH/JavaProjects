package com.mysterionnh.tinker.umlgenerator;

import com.mysterionnh.Constants;
import com.mysterionnh.util.Logger;
import com.mysterionnh.util.R;

import java.io.*;
import java.util.ArrayList;

class UMLBuilder {
    //private Logger log;

    //private final int LETTER_LENGTH = 12;
    //private final int LETTER_HEIGHT = 20;

    //private final String GRAPH_TEMPLATE = R.getResource(Constants.STRING_RESOURCE_PATH, "umlgenerator.graphTemplate");
    private final String NODE_TEMPLATE = R.getResource(Constants.STRING_RESOURCE_PATH, "umlgenerator.nodeTemplate");
    //private final String ARROW_TEMPLATE = R.getResource(Constants.STRING_RESOURCE_PATH, "umlgenerator.arrowTemplate");

    public UMLBuilder(Logger logger) {
        //log = logger;
    }

    public void createNodes(FileParser parser, int num) throws IOException {
        ArrayList<ArrayList<String>> strings = parser.getNode(num);
        File tempNode = new File("..\\temp\node" + num + ".temp");

        String temp = "";
        String temp2;

        int attributes = strings.get(0).size();
        int methods = strings.get(3).size();

        String className = strings.get(7).get(0);
        String node = NODE_TEMPLATE;

        for (int j = 0; j < attributes; j++) {
            temp += strings.get(2).get(j) + " " + strings.get(0).get(j) + " : ";
            temp2 = strings.get(1).get(j).replaceAll("<", "&lt;");
            temp += temp2.replaceAll(">", "&gt;") + "\r\n";
        }
        node = node.replace("FIELD_BLOCK", temp);

        temp = "+ " + className + "()\r\n";
        for (int k = 0; k < methods; k++) {
            if (!strings.get(3).get(k).equals(className)) {
                temp += strings.get(6).get(k) + " " + strings.get(3).get(k) + "(";
                temp2 = strings.get(4).get(k).replaceAll("<", "&lt;") + ") : ";
                temp += temp2.replaceAll(">", "&gt;");
                temp2 = strings.get(5).get(k).replaceAll("<", "&lt;");
                temp += temp2.replaceAll(">", "&gt;");
            }
        }
        node = node.replace("METHOD_BLOCK", temp);

        PrintWriter writer = new PrintWriter(tempNode);
        writer.print("");
        writer.close();

        FileOutputStream fos = new FileOutputStream(tempNode);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (int i = 0; i < 2; i++) {
            bw.write(node);
            bw.newLine();
        }
        bw.close();
    }
}