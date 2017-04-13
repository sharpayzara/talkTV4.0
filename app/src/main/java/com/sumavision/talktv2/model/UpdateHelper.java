package com.sumavision.talktv2.model;

/**
 * Created by sharpay on 16-7-6.
 */

import com.sumavision.talktv2.model.entity.UpdateInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class UpdateHelper {
    public UpdateInfo processUpdateInfo(InputStream is) {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        UpdateInfoHandler handler = new UpdateInfoHandler();
        try {
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(is));

            if (handler.downloadUrl != null) {
                UpdateInfo updateInfo = new UpdateInfo(handler.packageName, handler.versionCode, handler.versionName,
                        handler.downloadUrl, handler.apkSize, handler.isForceUpdate,handler.releaseList);
                return updateInfo;

            } else {
                return null;
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    class UpdateInfoHandler extends DefaultHandler {
        StringBuilder currentDataBuilder = new StringBuilder();

        String packageName = null;
        String versionCode = null;
        String versionName = null;
        String downloadUrl = null;
        String apkSize = null;
        List<String> releaseList = new ArrayList<>();
        boolean isForceUpdate = false;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            currentDataBuilder.delete(0, currentDataBuilder.length());
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            currentDataBuilder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            String tagName = localName.length() != 0 ? localName : qName;
            tagName = tagName.toLowerCase().trim();

            if (tagName.equals("packagename")) {
                packageName = currentDataBuilder.toString();
            } else if (tagName.equals("versioncode")) {
                versionCode = currentDataBuilder.toString();
            } else if (tagName.equals("versionname")) {
                versionName = currentDataBuilder.toString();
            } else if (tagName.equals("downloadurl")) {
                downloadUrl = currentDataBuilder.toString();
            } else if (tagName.equals("note")) {
               // releaseNote = currentDataBuilder.toString();
                releaseList.add(currentDataBuilder.toString());
            } else if (tagName.equals("apksize")) {
                apkSize = currentDataBuilder.toString();
            } else if (tagName.equals("isforceupdate")) {
                isForceUpdate = Boolean.parseBoolean(currentDataBuilder.toString());
            }

        }
    }

}
