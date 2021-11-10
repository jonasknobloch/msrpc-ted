package me.jonasknobloch.msrpcted;

import at.unisalzburg.dbresearch.apted.costmodel.*;
import at.unisalzburg.dbresearch.apted.distance.APTED;
import at.unisalzburg.dbresearch.apted.node.*;

import edu.stanford.nlp.simple.*;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TreeEditDistanceAnalysis {
    public static void main(String[] args) {
        String path = "msr_paraphrase_train.txt";

        boolean enableLimit = false;
        int limit = 0;

        if (args.length > 0) {
            path = args[0];
        }

        if (args.length > 1) {
            try {
                enableLimit = true;
                limit = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                return;
            }
        }

        RedwoodConfiguration.current().clear().apply();

        PennInputParser penn = new PennInputParser();

        CostModel<StringNodeData> cm = new PerEditOperationStringNodeDataCostModel(1, 1, 1);
        APTED<CostModel<StringNodeData>, StringNodeData> apted = new APTED<>(cm);

        File file = new File(path);

        Scanner sc;

        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException exception) {
            return;
        }

        int iteration = 0;

        sc.nextLine();

        System.out.println("Quality" +
                "\t#1 ID" + "\t#2 ID" +
                "\t#1 Sentence Length\t#2 Sentence Length" +
                "\t#1 Tree Size\t#2 Tree Size" +
                "\t#1 Tree Depth\t#2 Tree Depth" +
                "\tAPTED");

        while (sc.hasNextLine() && (!enableLimit || iteration < limit)) {
            String[] sample = sc.nextLine().split("\t");

            Sentence sent = new Sentence(sample[3]);
            Sentence para = new Sentence(sample[4]);

            Tree sentTr = sent.parse();
            Tree paraTr = para.parse();

            Node<StringNodeData> aNodeTr = penn.fromString(sentTr.toString());
            Node<StringNodeData> bNodeTr = penn.fromString(paraTr.toString());

            float ed = apted.computeEditDistance(aNodeTr, bNodeTr);

            String sb = sample[0] + '\t' + sample[1] + '\t' + sample[2]
                    + '\t' + sent.length() + '\t' + para.length()
                    + '\t' + sentTr.size() + '\t' + paraTr.size()
                    + '\t' + sentTr.depth() + '\t' + paraTr.depth()
                    + '\t' + ed;

            System.out.println(sb);

            iteration++;
        }
    }
}
