package com.github.mikesafonov.starter.smpp.utils;

import com.cloudhopper.commons.charset.CharsetUtil;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

/**
 * @author Mike Safonov
 */
@UtilityClass
public class MessageUtil {

    private static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9 \\r\\n@£$¥èéùìòÇØøÅå\u0394_\u03A6\u0393\u039B\u03A9\u03A0\u03A8\u03A3\u0398\u039EÆæßÉ!\"#$%&'()*+,\\-./:;<=>?¡ÄÖÑÜ§¿äöñüà^{}\\\\\\[~\\]|\u20AC]*$");

    public static final int ucs2RegularMessageLength = 70;
    public static final int ucs2MultipartMessageLength = 67;
    public static final int gsm7RegularMessageLength = 160;
    public static final int gsm7MultipartMessageLength = 153;

    /**
     * Calculate count of sms message parts need to delivery with message text {@code message}. If {@code ucs2Only = true}
     * then message calculated in UCS2 encoding, otherwise encoding detecting according to message text
     *
     * @param message  message text
     * @param ucs2Only using UCS2 encoding only
     * @return count sms parts
     */
    public static CountWithEncoding calculateCountSMS(@Nullable String message, boolean ucs2Only) {
        if (isNullOrBlank(message)) {
            return CountWithEncoding.empty(CharsetUtil.CHARSET_UCS_2);
        }
        if (ucs2Only) {
            return new CountWithEncoding(countUcs2(message.length()), CharsetUtil.CHARSET_UCS_2);
        } else {
            return (isUcs2(message)) ? new CountWithEncoding(countGsm(message.length()), CharsetUtil.CHARSET_GSM) :
                    new CountWithEncoding(countUcs2(message.length()), CharsetUtil.CHARSET_UCS_2);
        }
    }

    /**
     * Calculate count of sms message parts need to delivery with message text {@code message}.
     * Encoding of sms detecting according to message text
     *
     * @param message message text
     * @return count sms parts
     */
    public static CountWithEncoding calculateCountSMS(@Nullable String message) {
        return calculateCountSMS(message, false);
    }

    private static boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean isUcs2(@NotNull String message) {
        return PATTERN.matcher(message).matches();
    }

    private static int countUcs2(int length) {
        return countFragments(length, ucs2RegularMessageLength, ucs2MultipartMessageLength);
    }

    private static int countGsm(int length) {
        return countFragments(length, gsm7RegularMessageLength, gsm7MultipartMessageLength);
    }

    /**
     * Method calculate message parts count of incoming length.
     *
     * @param length    length of incoming
     * @param regular   size of regular message
     * @param multipart size of multipart message
     * @return message parts count
     */
    private static int countFragments(int length, int regular, int multipart) {
        if (length > regular) {
            int fragmentsCount = length / multipart;
            if (length % multipart > 0) {
                fragmentsCount += 1;
            }
            return fragmentsCount;

        } else {
            return 1;
        }
    }
}
