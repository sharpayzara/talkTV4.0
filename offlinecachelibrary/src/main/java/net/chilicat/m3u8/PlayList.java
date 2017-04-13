package net.chilicat.m3u8;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.List;

/**
 * @author dkuffner
 */
public final class PlayList implements Iterable<Element> {

    public static PlayList parse(Readable readable) throws ParseException {
        return PlayListParser.create(PlayListType.M3U8).parse(readable);
    }

    public static PlayList parse(String playlist) throws ParseException {
        return parse(new StringReader(playlist));
    }

    public static PlayList parse(InputStream playlist) throws ParseException {
        return parse(new InputStreamReader(playlist));
    }

    public static PlayList parse(ReadableByteChannel playlist) throws ParseException {
        return parse(makeReadable(playlist));
    }

    private static Readable makeReadable(ReadableByteChannel source) {
        if (source == null)
            throw new NullPointerException("source");
        String defaultCharsetName = PlayListType.M3U8.getEncoding();
        return Channels.newReader(source,
                java.nio.charset.Charset.defaultCharset().name());
    }

    private final List<Element> elements;
    private final boolean endSet;
    private final int targetDuration;
    private int mediaSequenceNumber;

    PlayList(List<Element> elements, boolean endSet, int targetDuration, int mediaSequenceNumber) {
        if (elements == null) {
            throw new NullPointerException("elements");
        }
        this.targetDuration = targetDuration;
        this.elements = elements;
        this.endSet = endSet;
        this.mediaSequenceNumber = mediaSequenceNumber;
    }

    public int getTargetDuration() {
        return targetDuration;
    }

    public Iterator<Element> iterator() {
        return elements.iterator();
    }

    public List<Element> getElements() {
        return elements;
    }

    public boolean isEndSet() {
        return endSet;
    }

    public int getMediaSequenceNumber() {
        return mediaSequenceNumber;
    }

    @Override
    public String toString() {
        return "PlayListImpl{" +
                "elements=" + elements +
                ", endSet=" + endSet +
                ", targetDuration=" + targetDuration +
                ", mediaSequenceNumber=" + mediaSequenceNumber +
                '}';
    }
}