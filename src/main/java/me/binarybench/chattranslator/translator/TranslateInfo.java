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


    @Override
    public boolean equals(Object obj) {

        if(obj == this) {
            return true;
        } else if(!(obj instanceof TranslateInfo)) {
            return false;
        } else {

            TranslateInfo other = (TranslateInfo)obj;

            return
                    this.getSourceLang().equals(other.getSourceLang()) &&
                    this.getTargetLang().equals(other.getTargetLang()) &&
                    this.getSourceText().equals(other.getSourceText());
        }
    }

    public int hashCode() {
        return (this.getSourceLang() == null?0:this.getSourceLang().hashCode()) ^ (this.getTargetLang() == null?0:this.getTargetLang().hashCode()) ^ (this.getSourceText() == null?0:this.getSourceText().hashCode());
    }

}
