# COMP3011J Mobile Computing 2021-2022 - Word Reciting App - "King of the Words (K.O.Word)"

This is the project of COMP3011J_2021 **LiuZhe 19206218** 
which is titled as "**King of the Words (K.O.Word)**".

## About the references
1. Some basic aesthetic ideas of the layout appearance
is learned from the book - "The First Line of Code in Android 2nd Edition".  
For example, the Floating button, the side menu, the advanced title bar in the "book detail activity".

2. All the resources of words and word books (including cover pictures and word audio) are got from github https://github.com/kajweb/dict.git  
Pronouncing audio of words are got from "https://dict.youdao.com/dictvoice?audio="  

## About How to run this App
Due to this is an Word reciting app, there should be some resource of word books and words in our database in advance. So when running on a device the first time, you should use the script I have written to insert the word book data from json files into the database. **(This will be definitely improved in the beta version, I will change it to the database on my cloud server, so that in the beta version, we do not need to do this.)**

To insert the data, you should set the activity called **DataInsertActivity** in the **util** package as the **Launching activity** when first running. When running, if there is a prompt on the screen, the data insertion is finished. Then you can set the launching activity back to the **LoginActivity**.

To set the **Launching activity**, you should cut the following code in the
**AndroidManifest.xml** file from the tag of the original launching activity to the tag of the new launching activity.

```xml
<intent-filter>
    <action android:name="android.intent.action.MAIN"/>
    <category android:name="android.intent.category.LAUNCHER"/>
</intent-filter>