package OLLIEEXTRACTOR;

/**
 * Created by chanakya on 9/11/14.
 */

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Set;

//import com.aliasi.cluster.HierarchicalClusterer;
//import com.aliasi.cluster.SingleLinkClusterer;
//import com.aliasi.spell.EditDistance;
//import com.aliasi.util.Distance;
import edu.knowitall.ollie.Ollie;
import edu.knowitall.ollie.OllieExtraction;
import edu.knowitall.ollie.OllieExtractionInstance;
import edu.knowitall.tool.parse.MaltParser;
import edu.knowitall.tool.parse.graph.DependencyGraph;

//import scala.util.parsing.combinator.testing.Str;
//import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

public class OllieExtractor {
    private Ollie ollie;
    private MaltParser maltParser;
    private static final String MALT_PARSER_FILENAME = "/home/chanakya/NetBeansProjects/Concepto/src/java/generate/engmalt.linear-1.7.mco";

    public OllieExtractor() throws MalformedURLException {
        scala.Option<File> nullOption = scala.Option.apply(null);
        maltParser = new MaltParser(new File(MALT_PARSER_FILENAME));
        ollie = new Ollie();
    }

    public Iterable<OllieExtractionInstance> extract(String sentence) {
        DependencyGraph graph = maltParser.dependencyGraph(sentence);
        Iterable<OllieExtractionInstance> extrs = scala.collection.JavaConversions.asJavaIterable(ollie.extract(graph));
        return extrs;
    }

    public static void main(String args[]) throws MalformedURLException, FileNotFoundException, UnsupportedEncodingException {
        System.out.println(OllieExtractor.class.getResource("/logback.xml"));

        String dir = "";
        String filename = "";
        if (args.length > 0) {
            if (args.length > 2) {
                System.err.println("Argument count exceeds two");
                System.exit(1);
            } else {
                    dir = args[0];
                    filename = args[1];
            }
        } else {
            dir = "/home/chanakya/NCERT";
        }

        OllieExtractor ollieWrapper = new OllieExtractor();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(dir + "/" + filename + "_ParseOutput.txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String line = null;
            ArrayList<String> text = new ArrayList<String>();
            try {
                while ((line = reader.readLine()) != null) {
                    text.add(line.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter writer = new PrintWriter(dir + "/" + filename + "_OllieOutput.txt", "UTF-8");
            String tuple;
            ArrayList<String> list_tuple = new ArrayList<String>();
            for (String sentence : text) {
                Iterable<OllieExtractionInstance> extracts = ollieWrapper.extract(sentence);
                for (OllieExtractionInstance instance : extracts) {
                    OllieExtraction extract = instance.extr();
                    String arg1;
                    if (extract.arg1().text().length() > 5) // len of 'these'
                        arg1 = extract.arg1().text().replaceFirst("^(([Tt][Hh][Ee][Ss][Ee])|([Tt][Hh][Ee])|([Aa][Nn])|([Aa])) ", "");
                    else
                        arg1 = extract.arg1().text();
                    String arg2;
                    if (extract.arg2().text().length() > 5)
                        arg2 = extract.arg2().text().replaceFirst("^(([Tt][Hh][Ee])|([Aa][Nn])|([Aa])) ", "");
                    else
                        arg2 = extract.arg2().text();

                    tuple = (arg1 + "\t" + extract.rel().text() + "\t" + arg2);
                    list_tuple.add(tuple);
                }
            }

                for (String tup : list_tuple)
                    writer.println(tup);
            writer.close();
        }
    }

