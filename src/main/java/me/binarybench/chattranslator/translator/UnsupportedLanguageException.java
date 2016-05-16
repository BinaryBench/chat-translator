package me.binarybench.chattranslator.translator;

/**
 * Created by Bench on 5/16/2016.
 */
public class UnsupportedLanguageException extends RuntimeException {
    private String lang;

    public UnsupportedLanguageException(String lang) {
        super(lang);
        this.lang = lang;
    }
}
