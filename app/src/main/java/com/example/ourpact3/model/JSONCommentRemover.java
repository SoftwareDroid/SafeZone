package com.example.ourpact3.model;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class JSONCommentRemover
{
    public static String removeComments(String data)
    {
        // Regulärer Ausdruck, um Kommentare zu finden, die nicht Teil eines Strings sind
        Pattern pattern = Pattern.compile("\\s*//.*?$|\\s*/\\*.*?\\*/", Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(data);

        // Kommentare entfernen
        return matcher.replaceAll("");
    }
}
