package com.example.kingofthewords.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.kingofthewords.dbModels.Book;
import com.example.kingofthewords.dbModels.Sentence;
import com.example.kingofthewords.dbModels.Translation;
import com.example.kingofthewords.dbModels.Word;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an util class used to inserting data into our database.
 * These data are deserialized from json files.
 */
public class ReadJsonIntoDB {

    /**
     *  This method is used to separated a json file into lines, those line are stored into an list.
     *  (each line is an json object of a single word)
     * @param fileName The file name of the json file that should be separated into lines
     * @return An array list of strings that are lines in json file. (each line is an json object of a single word)
     */
    private static List<String> readJSONLines(String fileName){
        //prepare a list for storing the lines in json file
        List<String> wordLines = new ArrayList<>();

        //use the bufferedReader to read eah line in json file into the list
        try (BufferedReader br = new BufferedReader(new InputStreamReader(ReadJsonIntoDB.class.getResourceAsStream(fileName)))){
            String line;
            //if there is still a line can be read
            while((line = br.readLine()) != null){
                //add the line into our list
                wordLines.add(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        //return the List
        return wordLines;
    }

    /**
     * This method can be called in MainActivity to initialize our database.
     * This is an integration of WriteBooksToDB() and WriteWordsToDB().
     */
    public static void WriteDB(){
        //create the database and tables
        LitePal.getDatabase();
        //insert data into book table
        WriteBooksToDB();
        //insert data into word table
        WriteWordsToDB();
    }

    /**
     * This method is used to write the book objects into our database
     * !!! This method should be called before calling WriteWordsToDB() !!!
     */
    private static void WriteBooksToDB(){

        /*
            the elements in the following arrays must be in the same order
        */

        //all the book ids of our word books
        String[] bookIds = new String[]{
                "CET4_1",
                "CET4_2",
                "CET4_3",
                "CET6_1",
                "CET6_2",
                "CET6_3",
                "IELTS_2",
                "IELTS_3"
        };

        //all the book names of our word books
        String[] bookNames = new String[]{
                "CET4 core word",
                "CET4 words from YouDao (ascending)",
                "CET4 words from newOriental",
                "CET6 core words (ascending)",
                "CET6 words from YouDao",
                "CET6 words from newOriental",
                "IELTS words from YouDao (ascending)",
                "IELTS words from newOriental"
        };

        //all the http requests for cover pictures of our word books
        String[] coverPictures = new String[]{
                "https://nos.netease.com/ydschool-online/1491037568440CET4_1.jpg",
                "https://nos.netease.com/ydschool-online/youdao_CET4_2.jpg",
                "https://nos.netease.com/ydschool-online/newOriental_CET4_3.jpg",
                "https://nos.netease.com/ydschool-online/1491037677590CET6_1.jpg",
                "https://nos.netease.com/ydschool-online/youdao_CET6_2.jpg",
                "https://nos.netease.com/ydschool-online/newOriental_CET6_3.jpg",
                "https://nos.netease.com/ydschool-online/youdao_IELTS_2.jpg",
                "https://nos.netease.com/ydschool-online/newOriental_IELTS_3.jpg"
        };

        //all the word number count of our word books
//        int[] wordNumbers = new int[]{
//                1162,
//                3739,
//                2607,
//                1228,
//                2345,
//                2078,
//                3427,
//                3575,
//        };
        int[] wordNumbers = new int[]{
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
        };

        /*
            loop through all the book info to create the objects and add these books into our database
        */
        for (int i = 0; i < bookIds.length; i++){
            //create a new book object
            Book book = new Book();
            //set the value for each attribute
            book.setBookId(bookIds[i]);
            book.setBookName(bookNames[i]);
            book.setCoverPicture(coverPictures[i]);
            book.setWordNumber(wordNumbers[i]);
            book.setWords(new ArrayList<>());
            //commit it to database
            book.save();
        }
    }

    /**
     * This method is used to write the word objects in json files into our database
     * !!! This method should be called after calling WriteBooksToDB() !!!
     */
    private static void WriteWordsToDB(){

        //all the json file names of our word books
        String[] filenames = new String[]{
                "/CET4_1.json",
                "/CET4_2.json",
                "/CET4_3.json",
                "/CET6_1.json",
                "/CET6_2.json",
                "/CET6_3.json",
                "/IELTS_2.json",
                "/IELTS_3.json"
        };

        //loop over all the word book json files
        for (String filename : filenames){

            // read lines from json file
            List<String> lines = readJSONLines(filename);

            for (String l : lines){

                //get the word object in the json file
                JSONObject jsonObject = JSON.parseObject(l);

                String bookId = jsonObject.getString("bookId"); //the book id of this word

                //go deeper into the word object
                jsonObject = jsonObject.getJSONObject("content").getJSONObject("word");

                String word = jsonObject.getString("wordHead");//the word
                String wordId = jsonObject.getString("wordId");//the word id

                //go deeper into the word object
                jsonObject = jsonObject.getJSONObject("content");

                String usPhone = jsonObject.getString("usphone");//phonetic symbol of us pronunciation
                String ukPhone = jsonObject.getString("ukphone");//phonetic symbol of uk pronunciation
                String audioUS = jsonObject.getString("usspeech");//the http request for us pronunciation audio
                String audioUK = jsonObject.getString("ukspeech");//the http request for uk pronunciation audio

                //if the audio http request is not in the json file, we will add it manually
                if(audioUS == null){
                    audioUS = word + "&type=1";
                }
                if(audioUK == null){
                    audioUK = word + "&type=2";
                }


                /*
                    Establish the relationship between this word and its book
                */
                //query the book of this word by using its bookId
                Book book = LitePal.where("bookId = ?", bookId).find(Book.class).get(0);

                //create a new word object
                Word wordObj = new Word();
                //set values for each attribute of this word object
                wordObj.setWordId(wordId);
                wordObj.setWord(word);
                wordObj.setUsPhone(usPhone);
                wordObj.setUkPhone(ukPhone);
                wordObj.setAudioUS(audioUS);
                wordObj.setAudioUK(audioUK);
                wordObj.setBookId(bookId);
                wordObj.setBook(book);
                //commit it to the database
                wordObj.save();

                //put the word into the word list of this book
                book.getWords().add(wordObj);
                book.save();

                /*
                    Establish the relationship between this word and its example sentences
                */
                //get the example sentence array of this word
                if(jsonObject.getJSONObject("sentence") != null){
                    JSONArray sentenceArray = jsonObject.getJSONObject("sentence").getJSONArray("sentences");
                    for (int i  = 0; i < sentenceArray.size(); i++){
                        JSONObject sentence = sentenceArray.getJSONObject(i);
                        String EnglishSentence = sentence.getString("sContent");
                        String ChineseSentence = sentence.getString("sCn");

                        //create a sentence object and add it to the database
                        Sentence sentenceObj = new Sentence();
                        sentenceObj.setSentenceCN(ChineseSentence);
                        sentenceObj.setSentenceEN(EnglishSentence);
                        sentenceObj.setWord(wordObj);
                        sentenceObj.save();

                        //save the sentence object into this word
                        wordObj.getSentences().add(sentenceObj);
                        wordObj.save();

                    }
                }

                /*
                    Establish the relationship between this word and its translations
                */
                //get the translation array of this word
                JSONArray translationArray = jsonObject.getJSONArray("trans");
                for (int i  = 0; i < translationArray.size(); i++){
                    JSONObject translation = translationArray.getJSONObject(i);
                    String transCN = translation.getString("tranCn");
                    String pos = translation.getString("pos");

                    //create a translation object and add it to the database
                    Translation translationObj = new Translation();
                    translationObj.setPos(pos);
                    translationObj.setTransCN(transCN);
                    translationObj.setWord(wordObj);
                    translationObj.save();

                    //save the translation object into this word
                    wordObj.getTranslations().add(translationObj);
                    wordObj.save();
                }


                /*
                //for test
                //the Book object of this word
                System.out.println("bookId: " + bookId);
                System.out.println("word: " + word);
                System.out.println("wordId: " + wordId);
                System.out.println("usPhone: " + usPhone);
                System.out.println("ukPhone: " + ukPhone);
                System.out.println("audioUS: " + audioUS);
                System.out.println("audioUK: " + audioUK);
                for (Word.Sentence s : sentences){
                    System.out.println(s.getSentenceEN());
                    System.out.println(s.getSentenceCN());
                }
                for (Word.Translation t : translations){
                    System.out.println(t.getTransCN());
                    System.out.println(t.getPos());
                }
                System.out.println();
                System.out.println("--------------------------------------------------------------------------------------------");
                System.out.println("--------------------------------------------------------------------------------------------");
                System.out.println("--------------------------------------------------------------------------------------------");
                System.out.println();
                */
            }

        }


    }

}
