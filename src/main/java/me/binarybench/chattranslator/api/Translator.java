package me.binarybench.chattranslator.api;

/**
 * Created by Bench on 5/13/2016.
 */
public interface Translator {

    String translate(String sourceLang, String targetLang, String sourceText);

}
