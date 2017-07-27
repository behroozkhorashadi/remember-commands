package com.khorashadi.ui;

import org.junit.Assert;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class UiUtilsTest {
    @Test
    public void formatForSave_whenBasicURL_shouldReplaceWithHtml() {
        String url = "This is a url: https://google.com in a string";
        String result = UiUtils.findReplaceUrl(url);
        String excpected = "This is a url: <a href='https://google.com'>https://google.com</a> " +
                "in a string";
        Assert.assertEquals(excpected, result);
    }

    @Test
    public void formatForSave_whenUrlAlreadyConverted_shouldNotReplaceWithHtml() {
        String url = "This is a url: <a href='https://google.com'>https://google.com</a> " +
                "in a string";
        String result = UiUtils.findReplaceUrl(url);
        Assert.assertEquals(url, result);
    }

    @Test
    public void formatForSave_whenWwwBasicURL_shouldReplaceWithHtml() {
        String url = "This is a url: www.google.com in a string";
        String result = UiUtils.formatForSave(url);
        String excpected = "This is a url: <a href='www.google.com'>www.google.com</a> " +
                "in a string";
        Assert.assertEquals(excpected, result);
    }

    @Test
    public void formatForSave_whenMultipleBasicURL_shouldReplaceWithHtml() {
        String url = "This is a url: https://google.com in a string https://myurl.com";
        String result = UiUtils.formatForSave(url);
        String excpected = "This is a url: <a href='https://google.com'>https://google.com</a> " +
                "in a string <a href='https://myurl.com'>https://myurl.com</a>";
        Assert.assertEquals(excpected, result);
    }

    @Test
    public void findReplaceRegexUrl_whenBasicURLAtStart_shouldReplaceWithHtml() {
        String url = "https://google.com in a string \\nhttps://myurl.com";
        String result = UiUtils.formatForSave(url);
        String excpected = "<a href='https://google.com'>https://google.com</a> " +
                "in a string \\n<a href='https://myurl.com'>https://myurl.com</a>";
        Assert.assertEquals(excpected, result);
    }

    @Test
    public void findReplaceRegexUrl_whenInHTML_shouldReplaceWithRefTags() {
        String html = "<html dir=\\\"ltr\\\"><head></head><body contenteditable=\\\"true\\\"><ol>"
                + "<li>Add key to os ssh key agent\\nhttps://help.github.com/articles/" +
                "generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/" +
                "#adding-your-ssh-key-to-the-ssh-agent&nbsp;\\n</li><li>Add key to github" +
                "\\nhttps://help.github.com/articles/adding-a-new-ssh-key-to-your-github-account/ "
                + "&nbsp;</li><li>&nbsp;Switch to using ssh repo instead of https\\n" +
                "https://help.github.com/articles/changing-a-remote-s-url/</li></ol></body></html>";
        String expected = "<html dir=\\\"ltr\\\"><head></head><body contenteditable=\\\"true\\\">" +
                "<ol><li>Add key to os ssh key agent\\n<a href='https://help.github.com/articles/" +
                "generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/" +
                "#adding-your-ssh-key-to-the-ssh-agent'>https://help.github.com/articles/" +
                "generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/" +
                "#adding-your-ssh-key-to-the-ssh-agent</a>&nbsp;\\n</li><li>Add key to github\\n" +
                "<a href='https://help.github.com/articles/" +
                "adding-a-new-ssh-key-to-your-github-account'>" +
                "https://help.github.com/articles/adding-a-new-ssh-key-to-your-github-account" +
                "</a>/ &nbsp;</li><li>&nbsp;Switch to using ssh repo instead of https\\n" +
                "<a href='https://help.github.com/articles/changing-a-remote-s-url'>" +
                "https://help.github.com/articles/changing-a-remote-s-url</a>/</li></ol>" +
                "</body></html>";
        String result = UiUtils.formatForSave(html);
        assertThat(result).isEqualTo(expected);
    }
}