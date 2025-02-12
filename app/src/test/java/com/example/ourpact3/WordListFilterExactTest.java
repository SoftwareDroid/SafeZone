package com.example.ourpact3;

/*
public class WordListFilterExactTest {

    private WordListFilterExact filter;
    private PipelineResultBase result;

    @Before
    public void setup() {
//        result = new PipelineResultBase(); // assume this is a mock or a test implementation
    }


    @Test
    public void testFeedWord_IgnoreCase() throws CloneNotSupportedException
    {
        ArrayList<String> listOfWords = new ArrayList<>();
        listOfWords.add("word1");
        filter = new WordListFilterExact("test", listOfWords, true, result);
        String text = "Word1";
        filter.feedWord(text);
        assertTrue(filter.isFinished());
    }

    @Test
    public void testFeedWord_NoMatch() throws CloneNotSupportedException
    {
        ArrayList<String> listOfWords = new ArrayList<>();
        listOfWords.add("word1");
        filter = new WordListFilterExact("test", listOfWords, true, result);
        String text = "word2";
        assertNull(filter.feedWord(text, false));
    }

    @Test
    public void testFeedWord_Match() throws CloneNotSupportedException
    {
        ArrayList<String> listOfWords = new ArrayList<>();
        listOfWords.add("word1");
        filter = new WordListFilterExact("test", listOfWords, true, result);
        String text = "word1";
        PipelineResultBase result = filter.feedWord(text, false);
        assertNotNull(result);
    }

    @Test
    public void testFeedWord_MultipleMatches() {
        ArrayList<String> listOfWords = new ArrayList<>();
        listOfWords.add("word1");
        listOfWords.add("word2");
        filter = new WordListFilterExact("test", listOfWords, true, result);
        String text = "word1";
        filter.feedWord(text, false);
        text = "word2";
        PipelineResultBase result = filter.feedWord(text, false);
        assertNotNull(result);
    }

    @Test
    public void testFeedWord_EditMode() {
        ArrayList<String> listOfWords = new ArrayList<>();
        listOfWords.add("word1");
        filter = new WordListFilterExact("test", listOfWords, true, result);
        String text = "word1";
        assertNull(filter.feedWord(text, true));
    }

    @Test
    public void testIsFinished() {
        ArrayList<String> listOfWords = new ArrayList<>();
        listOfWords.add("word1");
        listOfWords.add("word2");
        filter = new WordListFilterExact("test", listOfWords, true, result);
        assertFalse(filter.isFinished());
        filter.feedWord("word1", false);
        assertFalse(filter.isFinished());
        filter.feedWord("word2", false);
        assertTrue(filter.isFinished());
    }

    @Test
    public void testReset() {
        ArrayList<String> listOfWords = new ArrayList<>();
        listOfWords.add("word1");
        filter = new WordListFilterExact("test", listOfWords, true, result);
        filter.feedWord("word1", false);
        assertTrue(filter.isFinished());
        filter.reset();
        assertFalse(filter.isFinished());
        filter.reset();
        assertFalse(filter.isFinished());
    }
}*/