package me.binarybench.chattranslator.translator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.binarybench.chattranslator.api.Callback;
import me.binarybench.chattranslator.api.Lang;
import me.binarybench.chattranslator.api.Translator;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Bench on 5/13/2016.
 */
public class TranslatorManager {

    private LoadingCache<TranslateInfo, String> cache;
    private ExecutorService executor;

    private Translator translator;

    public TranslatorManager(ExecutorService executor, Translator translator) {
        this.executor = executor;
        this.translator = translator;

        cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<TranslateInfo, String>() {
                            @Override
                            public String load(TranslateInfo key) {
                                if (getTranslator() == null)
                                    return null;
                                return getTranslator().translate(key.getSourceLang(), key.getTargetLang(), key.getSourceText());
                            }
                        }
                );
    }

    public void translate(String sourceLang, String targetLang, String sourceText, Callback<String> callback)
    {
        translate(new TranslateInfo(sourceLang, targetLang, sourceText));
    }

    public Future<String> translate(String sourceLang, String targetLang, String sourceText)
    {
        return translate(new TranslateInfo(sourceLang, targetLang, sourceText));
    }

    public String translateSync(String sourceLang, String targetLang, String sourceText)
    {
        return translateSync(new TranslateInfo(sourceLang, targetLang, sourceText));
    }

    public void translate(final TranslateInfo translateInfo, final Callback<String> callback)
    {
        if (shouldCancel(translateInfo))
            callback.call(translateInfo.getSourceText());

        getExecutor().execute(new Runnable() {
            public void run() {
                callback.call(translateSync(translateInfo));
            }
        });
    }

    public Future<String> translate(final TranslateInfo translateInfo)
    {
        if (shouldCancel(translateInfo))
            return ConcurrentUtils.constantFuture(translateInfo.getSourceText());

        return getExecutor().submit(new Callable<String>() {
            public String call() throws Exception {
                return translateSync(translateInfo);
            }
        });
    }

    public String translateSync(TranslateInfo translateInfo)
    {
        if (shouldCancel(translateInfo))
            return translateInfo.getSourceText();

        return getCache().getUnchecked(translateInfo);
    }


    public boolean shouldCancel(TranslateInfo translateInfo)
    {
        Lang source = Lang.fromFromId(translateInfo.getSourceLang());
        Lang target = Lang.fromFromId(translateInfo.getTargetLang());

        return
                source == null ||
                target == null ||
                source.equals(target) ||
                target.equals(Lang.DETECT_LANG) ||
                target.equals(Lang.DISABLED);
    }


    public LoadingCache<TranslateInfo, String> getCache() {
        return cache;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public Translator getTranslator() {
        return translator;
    }
}
