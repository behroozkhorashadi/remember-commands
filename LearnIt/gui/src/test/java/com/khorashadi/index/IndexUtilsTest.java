package com.khorashadi.index;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class IndexUtilsTest {
    @Test
    public void extractText_whenHtmlText_shouldRemoveHtmlTags() {
        String html = "<html dir=\"ltr\"><head></head><body contenteditable=\"true\"><ol>"
                + "<li>Add key to os ssh key agent\\nhttps://help.github.com/articles/" +
                "generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/" +
                "#adding-your-ssh-key-to-the-ssh-agent&nbsp;\\n</li><li>Add key to github" +
                "\\nhttps://help.github.com/articles/adding-a-new-ssh-key-to-your-github-account/ "
                + "&nbsp;</li><li>&nbsp;Switch to using ssh repo instead of https\\n" +
                "https://help.github.com/articles/changing-a-remote-s-url/</li></ol></body></html>";
        String result = IndexUtils.extractText(html);
        String expected = "Add key to os ssh key agent\\n" +
                "https://help.github.com/articles/generating-a-new-ssh-key-and-adding-it-to-" +
                "the-ssh-agent/#adding-your-ssh-key-to-the-ssh-agent \\n Add key to github\\n" +
                "https://help.github.com/articles/adding-a-new-ssh-key-to-your-github-account/    " +
                "Switch to using ssh repo instead of https\\n" +
                "https://help.github.com/articles/changing-a-remote-s-url/";
        assertThat(result).isEqualTo(expected);
    }
}