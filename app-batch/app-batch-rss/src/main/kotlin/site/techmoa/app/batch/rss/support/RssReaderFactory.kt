package site.techmoa.app.batch.rss.support

import com.apptasticsoftware.rssreader.RssReader
import site.techmoa.app.batch.rss.support.CustomInvalidXmlCharacterFilter

object RssReaderFactory {
    fun getInstance(): RssReader {
        return RssReader().addFeedFilter(CustomInvalidXmlCharacterFilter()) as RssReader
    }
}
