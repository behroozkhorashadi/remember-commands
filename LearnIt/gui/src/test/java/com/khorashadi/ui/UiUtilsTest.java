package com.khorashadi.ui;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class UiUtilsTest {
    @Test
    public void testURLFinder_whenBasicURL_shouldReplaceWithHtml() {
        String url = "This is a url: https://google.com in a string";
        String result = UiUtils.findReplaceUrl(url);
        String excpected = "This is a url: <a href='https://google.com'>https://google.com</a> " +
                "in a string";
        Assert.assertEquals(excpected, result);
    }

    @Test
    public void testURLFinder_whenUrlAlreadyConverted_shouldNotReplaceWithHtml() {
        String url = "This is a url: <a href='https://google.com'>https://google.com</a> " +
                "in a string";
        String result = UiUtils.findReplaceUrl(url);
        Assert.assertEquals(url, result);
    }

    @Test
    public void findReplaceRegexUrl_whenWwwBasicURL_shouldReplaceWithHtml() {
        String url = "This is a url: www.google.com in a string";
        String result = UiUtils.findReplaceRegexUrl(url);
        String excpected = "This is a url: <a href='www.google.com'>www.google.com</a> " +
                "in a string";
        Assert.assertEquals(excpected, result);
    }

    @Test
    public void findReplaceRegexUrl_whenBasicURL_shouldReplaceWithHtml() {
        String url = "This is a url: https://google.com in a string";
        String result = UiUtils.findReplaceRegexUrl(url);
        String excpected = "This is a url: <a href='https://google.com'>https://google.com</a> " +
                "in a string";
        Assert.assertEquals(excpected, result);
    }

    @Test
    public void findReplaceRegexUrl_whenMultipleBasicURL_shouldReplaceWithHtml() {
        String url = "This is a url: https://google.com in a string https://myurl.com";
        String result = UiUtils.findReplaceRegexUrl(url);
        String excpected = "This is a url: <a href='https://google.com'>https://google.com</a> " +
                "in a string <a href='https://myurl.com'>https://myurl.com</a>";
        Assert.assertEquals(excpected, result);
    }

    @Test
    public void findReplaceRegexUrl_whenBasicURLAtStart_shouldReplaceWithHtml() {
        String url = "https://google.com in a string https://myurl.com";
        String result = UiUtils.findReplaceRegexUrl(url);
        String excpected = "<a href='https://google.com'>https://google.com</a> " +
                "in a string <a href='https://myurl.com'>https://myurl.com</a>";
        Assert.assertEquals(excpected, result);
    }
}