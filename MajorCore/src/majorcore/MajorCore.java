package majorcore;

/**
 * Created by chanakya on 7/11/14.
 */


/*
    Improvement 1 : Use XML Input and Output instead of plain text.
 *  */

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;

import java.io.*;
import java.util.*;

public class MajorCore {
    public static void main(String[] args) throws IOException {
        String direct_text_given = "";
        ArrayList<String> files_to_process = new ArrayList<String>();
        String dir = "";
        String filename = "";
        if (args.length > 0) {
            if (args.length > 2) {
                System.err.println("Argument count exceeds two");
                System.err.println("Usage : MajorCore.jar <Path to Directory containing stories>");
                System.exit(1);
            } else {
                dir = args[0];
                filename = args[1];
            }
        } else {
            dir = "/home/chanakya/NCERT";
        }

        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
        props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz");
        props.setProperty("dcoref.postprocessing", "true");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(dir + "/" + filename+".txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        String text = "";
        while ((line = reader.readLine()) != null) {
            text += (line);
        }
        Reader read_ = new StringReader(text);
        DocumentPreprocessor doc_pro = new DocumentPreprocessor(read_);
        List<String> all_sentences = new ArrayList<String>();
        Iterator<List<HasWord>> it = doc_pro.iterator();
        while (it.hasNext()) {
            StringBuilder sentence_whole = new StringBuilder();
            List<HasWord> sentence = it.next();
            for (HasWord ele : sentence) {
                if (sentence_whole.length() >= 1) {
                    sentence_whole.append(" ");
                }
                sentence_whole.append(ele);
            }
            all_sentences.add(sentence_whole.toString());
        }

        StringBuilder text_build = new StringBuilder();
        String modified = "";
        List<String> temp_all_sentences = new ArrayList<String>();
        for (String s : all_sentences) {
            if (s.contains("[Fig")) {
                modified = s.replaceAll("(\\[Fig.*\\])", "");
                s = modified;
            } else if (s.contains("(Fig")) {
                modified = s.replaceAll("(\\(Fig.*\\))", "");
                s = modified;
            }

            modified = s.replaceAll("((([Ff][Ii][Gg][Ss])|([Ff][Ii][Gg])).*(([0-9][\\.])|([0-9] \\.)))", " ");
            if (modified.equals(s)) {
                temp_all_sentences.add(s);
                text_build.append(s);
            }
        }
        text = text_build.toString();
        all_sentences.clear();
        all_sentences.addAll(temp_all_sentences);
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        Map<Integer, CorefChain> coref = document.get(CorefChainAnnotation.class);

        for (Map.Entry<Integer, CorefChain> entry : coref.entrySet()) {
            CorefChain chain = entry.getValue();
            if (chain.getMentionsInTextualOrder().size() <= 1)
                continue;
            CorefChain.CorefMention mention = chain.getRepresentativeMention();

            String repr = "";
            List<CoreLabel> tks = document.get(CoreAnnotations.SentencesAnnotation.class).get(mention.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
            String head = "";
            for (int i = mention.startIndex - 1; i < mention.endIndex - 1; i++) {
                if (i == mention.headIndex - 1)
                    head = tks.get(i).get(CoreAnnotations.TextAnnotation.class).trim() + " ";
                repr += tks.get(i).get(CoreAnnotations.TextAnnotation.class).trim() + " ";
            }
            String[] rep = repr.split(",");
            repr = rep[0].toString();
            document.get(CoreAnnotations.SentencesAnnotation.class).get(mention.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
            Map<Integer, ArrayList<String>> mentions_to_sentence = new HashMap<Integer, ArrayList<String>>();
            for (CorefChain.CorefMention m : chain.getMentionsInTextualOrder()) {
                String mentioned = "";
                tks = document.get(CoreAnnotations.SentencesAnnotation.class).get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
                for (int i = m.startIndex - 1; i < m.endIndex - 1; i++)
                    mentioned += tks.get(i).get(CoreAnnotations.TextAnnotation.class) + " ";
                mentioned = mentioned.trim();
                if (repr.equals(mentioned))
                    continue;
                if (mentions_to_sentence.containsKey(m.sentNum - 1)) {
                    mentions_to_sentence.get(m.sentNum - 1).add(mentioned);
                } else {
                    mentions_to_sentence.put(m.sentNum - 1, new ArrayList<String>(Arrays.asList(mentioned)));
                }
            }

            for (Integer i : mentions_to_sentence.keySet()) {
                String temp = all_sentences.get(i);
                for (String s : mentions_to_sentence.get(i)) {
                    if (head.length() > 1)
                        temp = temp.replace(" " + s.trim() + " ", " " + head.trim() + " ");
                    else
                        temp = temp.replace(" " + s.trim() + " ", " " + repr.trim() + " ");
                }
                all_sentences.set(i, temp);
            }
        }

        PrintWriter writer = new PrintWriter(dir + "/" + filename + "_ParseOutput.txt", "UTF-8");
        for (String s : all_sentences) {
            writer.println(s);
        }
        writer.close();
    }
}
