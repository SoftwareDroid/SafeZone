package com.example.ourpact3.topics;

import com.example.ourpact3.db.AppsDatabase;
import com.example.ourpact3.db.ContentFilterEntity;
import com.example.ourpact3.db.LanguageEntity;
import com.example.ourpact3.db.WordEntity;
import com.example.ourpact3.db.WordListEntity;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import java.util.Map;
import java.util.stream.Collectors;

public class TopicManager
{
    public static class WordEntityWrapper
    {
        private final WordEntity wordEntity;
        private Pattern compiledRegex;

        public WordEntityWrapper(WordEntity wordEntity, String regex)
        {
            this.wordEntity = wordEntity;
            if (wordEntity.isRegex())
            {
                // Replace double backslashes with a single backslash
                regex = regex.replace("\\\\", "\\");
                this.compiledRegex = Pattern.compile(regex);
            }
        }

        public WordEntity getWordEntity()
        {
            return wordEntity;
        }


    }

    public AppsDatabase db;
    private final Map<Long, List<WordEntityWrapper>> cachedWordsFromDB = new HashMap<>();
    private final Map<Long, Boolean> langCodeToEnabled = new HashMap<>();


    /**
     * only small letters a-z, numbers and underscore is allowed
     *
     * @return if valid
     */
    public static boolean isValidTopicID(String topicID)
    {
        if (topicID != null)
        {
            return topicID.matches("^[a-z0-9_]+$");
        }
        return false;
    }

    public static class SearchResult2
    {
        public String inputText;
        public int accumulatedScore = 0;
        public List<WordEntity> matches;
    }

    public void clearCache()
    {
        this.langCodeToEnabled.clear();
        this.cachedWordsFromDB.clear();
    }

    private void loadWordlist(long wordListID)
    {

        if (!cachedWordsFromDB.containsKey(wordListID))
        {
            WordListEntity wordListEntity = db.wordListDao().getWordListById(wordListID);

            if (wordListEntity == null)
            {
                throw new NoSuchElementException("Word list missing: " + wordListID);
            }

            List<WordEntity> wordsInList = db.wordDao().getAllWordsInList(wordListEntity.getId());

            List<WordEntityWrapper> wrappedWords = wordsInList.stream().filter(word -> {
                        // load only languages which are enabled and cache this for faster access
                        Boolean languageEnabled = langCodeToEnabled.get(word.getLanguageId());
                        // language is not found load it from database
                        if (languageEnabled == null)
                        {
                            LanguageEntity languageEntity = db.languageDao().getLanguageById(word.getLanguageId());
                            langCodeToEnabled.put(languageEntity.getId(), languageEntity.isEnabled());
                            languageEnabled = languageEntity.isEnabled();
                        }
                        return languageEnabled;
                    })
                    .map(word -> new WordEntityWrapper(word, word.getText()))
                    .collect(Collectors.toList());
            cachedWordsFromDB.put(wordListEntity.getId(), wrappedWords);
        }
    }

    /**
     * searches in parallel against a word list
     * only enabled languages are used
     *
     */
    public SearchResult2 isStringInWordList(String text, boolean isTextEditable, ContentFilterEntity contentFilter)
    {
        SearchResult2 result = new SearchResult2();
        // check only against lower if wanted
        String finalText = contentFilter.getIgnoreCase() ? text.toLowerCase() : text;
        // cache word list from db
        loadWordlist(contentFilter.getWordListID());
        List<WordEntityWrapper> wordsInList = cachedWordsFromDB.get(contentFilter.getWordListID());
        // search in parallel

        assert wordsInList != null;
        List<WordEntity> matchedEntities = wordsInList.parallelStream()
                .filter(entity -> {
                    if (entity.wordEntity.isRegex())
                    {
                        // count only the first match
                        return entity.compiledRegex.matcher(finalText).find();
                    } else
                    {
                        // simple suffix match
                        return finalText.contains(entity.wordEntity.getText());
                    }
                }).map(WordEntityWrapper::getWordEntity)//Map back to only WordEntity
                .collect(Collectors.toList());
        // calculate result on matches only

        result.inputText = finalText;
        result.matches = matchedEntities;
        for (WordEntity matchedWord : matchedEntities)
        {
            result.accumulatedScore += isTextEditable ? matchedWord.getWriteScore() : matchedWord.getReadScore();
        }
        return result;
    }
}
