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

        InputStream posModelIn = null;
        InputStream tokenModelIn = null;

        String csvFile = "/Users/dikshitasalecha/Documents/wor2vec.csv";
        CSVReader reader = null;

        try {
            SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;

            reader = new CSVReader(new FileReader(csvFile));
            String[] line;
            while ((line = reader.readNext()) != null) {
                //	("\nTitle:  " + line[0] + ",\n type:" + line[1] + " ,\n Description:" + line[2] + "]");
                String sentence = line[0];
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

                double good=0;
                double bad=0;
                int count=0;

                System.out.println("Token\t:\tTag\t:\n----------");
                //for (int i = 0; i < tokens.length; i++)
                 for(int i=0;i< tokens.length;i++)
                   {
                     if(tags[i]=="JJ" || tags[i]=="JJR" || tags[i]=="JJS" || tags[i]=="NNS")
                     {
                         good += vec.similarity(tokens[i], "good");
                         bad += vec.similarity(tokens[i], "bad");
                         count = count + 1;
                     }
                     else
                         continue;;




                    //System.out.println(tokens[i] + "\t:\t" + tags[i] + "\t\t");
                }


        }
        catch (IOException e) {
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
                catch (IOException e) {
                }
            }


        }
    }

}
