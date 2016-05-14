package me.binarybench.chattranslator.translator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.binarybench.chattranslator.api.Translator;
import org.apache.http.client.utils.URIBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

/**
 * Created by Bench on 5/14/2016.
 */
public class GoogleAppsTranslator implements Translator {


    public String translate(String sourceLang, String targetLang, String sourceText)
    {
        URIBuilder builder = new URIBuilder()
                .setScheme("http")
                .setHost("translate.googleapis.com/translate_a/single")
                .setParameter("client", "gtx")
                .setParameter("sl", sourceLang)
                .setParameter("tl", targetLang)
                .setParameter("dt", "t")
                .setParameter("text", sourceText)
                ;

        //String urlString = "https://translate.googleapis.com/translate_a/single?client=gtx&sl="
        //        + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + URLUtil.encodeURIComponent(sourceText);

        //System.out.println(builder.toString());

        try
        {
            URL url = new URL(builder.toString());
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder returnFile = new StringBuilder();

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                returnFile.append(inputLine);
            in.close();

            //Example [[["Kuh!","Cow!",,,2]],,"en"]
            JsonArray json = new JsonParser().parse(returnFile.toString()).getAsJsonArray();


            StringBuilder outputBuilder = new StringBuilder();

            for (Iterator<JsonElement> jsonIter = json.getAsJsonArray().get(0).getAsJsonArray().iterator(); jsonIter.hasNext();)
            {
                outputBuilder.append(jsonIter.next().getAsJsonArray().get(0).getAsString());
            }
            //System.out.println(outputBuilder.toString());
            return outputBuilder.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
