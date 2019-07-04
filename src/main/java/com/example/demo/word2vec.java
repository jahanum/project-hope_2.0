package com.example.demo;
import java.io.*;

import com.google.common.primitives.Doubles;
import org.apache.commons.io.FileUtils;

import org.deeplearning4j.models.embeddings.WeightLookupTable;

//import org.deeplearning4j.spark.models.embeddings.learning.impl.elements.CBOW;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;

//import org.deeplearning4j.spark.models.embeddings.word2vec.*;

import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;

import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.File;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import opennlp.tools.tokenize.SimpleTokenizer;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import com.opencsv.*;






public class word2vec {
    public static void main(String[] args) {


        File gModel = new File("/Users/Dikshitasalecha/downloads/GoogleNews-vectors-negative300.bin");

        Word2Vec vec = WordVectorSerializer.readWord2VecModel(gModel);
        System.out.println("model loaded");

       /* System.out.println(vec.wordsNearest("good", 5));
        System.out.println(vec.getWordVectorMatrix("good"));
        System.out.println(vec.similarity("good", "bad"));
       */

        List<String> Groundtruth =new ArrayList<String>() ;
        List<String> Predictions =new ArrayList<String>();



        InputStream posModelIn = null;
        InputStream tokenModelIn = null;

        String csvFile = "/Users/dikshitasalecha/Documents/rssk.csv";
        CSVReader reader = null;

        try {
            SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;




            reader = new CSVReader(new FileReader(csvFile));
            String[] line;


            while ((line = reader.readNext()) != null)
            {
                //	("\nTitle:  " + line[0] + ",\n type:" + line[1] + " ,\n Description:" + line[2] + "]");
                String sentence = line[0];
                Groundtruth.add(line[1]);
                // String sentence = "This is a terrible idea to execute ";
                // Tokenizing the given sentence
                String tokens[] = simpleTokenizer.tokenize(sentence);
                //Printing the tokens
        /*for (String token : tokens)
        {
            System.out.println(token);
        }

        */


                // Parts-Of-Speech Tagging
                // reading parts-of-speech model to a stream
                posModelIn = new FileInputStream("/Users/Dikshitasalecha/Downloads/en-pos-maxent.bin");
                // loading the parts-of-speech model from stream
                POSModel posModel = new POSModel(posModelIn);
                // initializing the parts-of-speech tagger with model
                POSTaggerME posTagger = new POSTaggerME(posModel);
                // Tagger tagging the tokens
                String tags[] = posTagger.tag(tokens);
                // Getting the probabilities of the tags given to the tokens

                double good = 0;
                double bad = 0;

                //System.out.println("Token\t:\tTag\t:\n----------");
                //for (int i = 0; i < tokens.length; i++)
               // System.out.println(good +"\t");
                //System.out.print(bad);


                for (int i = 0; i < tokens.length; i++)
                {
                    if (tags[i] .equals("JJ") || tags[i] .equals( "JJR" )||
                            tags[i] .equals( "JJS" )|| tags[i].equals("NNS") ||
                            tags[i].equals("VB") || tags[i].equals("VBZ")||
                            tags[i].equals("VBG") || tags[i].equals("VBD" ))
                    {
                       // System.out.println(tokens[i] + "\t:\t" + tags[i] + "\t\t");
                        good = good +vec.similarity(tokens[i], "good");
                        bad = bad + vec.similarity(tokens[i], "bad");
                    }

                    else
                       // System.out.println(tokens[i] + "\t:\t" + tags[i] + "\t\t");
                        continue;



                    //System.out.println(tokens[i] + "\t:\t" + tags[i] + "\t\t");
                }

                if(good>bad)
                    Predictions.add("Good");
                else
                    Predictions.add("Bad");


            }

            System.out.println(Arrays.toString(Predictions.toArray()));
            System.out.println(Arrays.toString(Groundtruth.toArray()));


            int tp=0,tn=0,fp=0,fn=0;


            for (int i=0; i<Groundtruth.size(); i++)
            {
                if (Groundtruth.get(i).equals("Good") && (Predictions.get(i).equals("Good")))
                {
                    tp=tp+1;
                }

                else if (Groundtruth.get(i).equals("Bad")&& Predictions.get(i).equals("Bad"))
                {
                    tn=tn+1;
                }

                else if (Groundtruth.get(i).equals("Bad")&& Predictions.get(i).equals("Good"))
                {
                    fp=fp+1;
                }

                //if (Groundtruth.get(i).equals("Good")&& Predictions.get(i).equals("Bad"))
                else
                {
                    fn=fn+1;
                }
            }


            double precision=tp*100/(tp+fp);
            double recall = tp*100/ (tp + fn) ;

            System.out.println( "Precision is  "+ precision);
            System.out.println("Recall is " + recall);





        }
        catch (IOException e)
        {
            // Model loading failed, handle the error
            e.printStackTrace();
        }

        finally
        {
            if (tokenModelIn != null)
            {
                try
                {
                    tokenModelIn.close();
                } catch (IOException e) {
                }
            }
            if (posModelIn != null)
            {
                try
                {
                    posModelIn.close();
                }
                catch (IOException e)
                {
                }
            }
        }



    }

}
