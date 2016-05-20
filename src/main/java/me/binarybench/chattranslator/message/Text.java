package me.binarybench.chattranslator.message;

import me.binarybench.chattranslator.translator.TranslatorManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Bench on 5/14/2016.
 */
public class Text {
    private String originalText;

    private HashMap<String, Future<String>> translations = null;

    private String sourceLang;

    public Text(String sourceLang, String originalText, boolean translate) {
        this.originalText = originalText;
        this.sourceLang = sourceLang;
        if (translate)
            this.translations = new HashMap<String, Future<String>>();
    }

    public void translate(String targetLang, TranslatorManager translatorManager)
    {
        if (translations == null || translations.containsKey(targetLang))
            return;

        translations.put(targetLang, translatorManager.translate(sourceLang, targetLang, originalText));

    }

    public void translate(Collection<String> langs, TranslatorManager translatorManager)
    {
        if (translations == null)
            return;
        for (String lang : langs)
        {
            translate(lang, translatorManager);
        }
    }

    public String getText(String lang) throws ExecutionException, InterruptedException {
        if (translations == null || !translations.containsKey(lang))
            return originalText;
        return translations.get(lang).get();
    }

    public String getOriginalText()
    {
        return originalText;
    }

    public boolean isTranslate()
    {
        return translations != null;
    }
}
