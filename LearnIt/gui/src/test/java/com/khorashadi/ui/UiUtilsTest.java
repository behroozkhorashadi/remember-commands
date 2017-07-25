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
}