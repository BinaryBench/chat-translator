package me.binarybench.chattranslator.translator;

/**
 * Created by Bench on 5/13/2016.
 */
public class TranslateInfo {
    private String sourceLang;
    private String targetLang;
    private String sourceText;

    public TranslateInfo(String sourceLang, String targetLang, String sourceText) {
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.sourceText = sourceText;
    }

    public String getSourceLang() {
        return sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public String getSourceText() {
        return sourceText;
    }
}
