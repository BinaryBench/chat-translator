package me.binarybench.chattranslator.api;

import java.util.UUID;

/**
 * Created by Bench on 5/13/2016.
 */
public interface LangStorage {

    void setLang(UUID playersUUID, String lang);

    String getLang(UUID playersUUID);

}
