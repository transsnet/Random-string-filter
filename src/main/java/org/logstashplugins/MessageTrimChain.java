package org.logstashplugins;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MessageTrimChain {

    private String message;

    MessageTrimChain(String message) {
        this.message = message;
    }

    String trim() {
        if (StringUtils.isNotBlank(message)) {
            this.trimEntity().trimLongWord().trimIP().trimUUID().trimLongNumber().trimID();
        }
        return this.message;
    }

    private MessageTrimChain trimEntity() {
        String prefix = "[[";
        String suffix = "]]";
        if (message.startsWith(prefix) && message.endsWith(suffix)) {
            message = message.substring(prefix.length(), message.length() - suffix.length());
        }
        message = message.replaceAll("\\[\\[.*?]]", "?");
        return this;
    }

    private MessageTrimChain trimID() {
        Pattern r = Pattern.compile("[0-9A-Za-z]{8,}");
        Matcher m = r.matcher(message);
        while (m.find()) {
            String suspectedID = m.group();
            String idRgx = "(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,}";
            if (suspectedID.matches(idRgx)) {
                message = message.replaceAll(suspectedID, "?");
            }
        }
        return this;
    }

    private MessageTrimChain trimIP() {
        message = message.replaceAll("(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\." +
                "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}(:" +
                "(\\d){1,5}){0,1}", "?");
        return this;
    }

    private MessageTrimChain trimLongNumber() {
        message = message.replaceAll("[0-9]{8,}", "?");
        return this;
    }

    private MessageTrimChain trimLongWord() {
        Pattern r = Pattern.compile("[0-9A-Za-z-]{20,}");
        Matcher m = r.matcher(message);
        while (m.find()) {
            String suspectedID = m.group();
            String wordRgx = "^[a-zA-Z]{20,}$";
            if (!suspectedID.matches(wordRgx)) {
                message = message.replaceAll(suspectedID, "?");
            }
        }
        return this;
    }

    private MessageTrimChain trimUUID() {
        message = message.replaceAll("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}", "?");
        return this;
    }
}
