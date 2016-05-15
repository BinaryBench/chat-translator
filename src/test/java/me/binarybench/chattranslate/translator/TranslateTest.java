package me.binarybench.chattranslate.translator;

import me.binarybench.chattranslator.api.Translator;
import me.binarybench.chattranslator.translator.GoogleAppsTranslator;
import me.binarybench.chattranslator.translator.TranslatorManager;
import me.binarybench.chattranslator.translator.TranslateInfo;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * Created by Bench on 5/14/2016.
 */
public class TranslateTest {
    private Translator translator;
    private TranslatorManager translatorManager;

    private String sourceLang = "en";
    private String targetLang = "de";
    private String sourceText = "Hello World";

    @Before
    public void setUp()
    {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        this.translator = new GoogleAppsTranslator();
        this.translatorManager = new TranslatorManager(threadPool, translator);
    }

    @Test
    public void testTranslate()
    {
        assertEquals("Hallo Welt",      translator.translate("en", "de", "Hello World"));
        assertEquals("Sveika Pasaule",  translator.translate("en", "lv", "Hello World"));
        assertEquals("こんにちは世界",   translator.translate("en", "ja", "Hello World"));
    }

    @Test
    public void testTranslateInfo()
    {
        assertTrue(new TranslateInfo(sourceLang, targetLang, sourceText).equals(new TranslateInfo(sourceLang, targetLang, sourceText)));
    }

    @Test
    public void testCache()
    {
        translatorManager.translateSync(sourceLang, targetLang, sourceText);
        assertNotNull(translatorManager.getCache().getIfPresent(new TranslateInfo(sourceLang, targetLang, sourceText)));
    }

}
