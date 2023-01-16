package fr.openai_test_unitaire_code;

import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

public class Utils {

    public static String processSelectedSyntax(final String text) {
        if (text.equals(".java")) {
            return SyntaxConstants.SYNTAX_STYLE_JAVA;
        } else if (text.equals(".js")) {
            return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
        } else if (text.equals(".jsp")) {
            return SyntaxConstants.SYNTAX_STYLE_JSP;
        } else if (text.equals(".sql")) {
            return SyntaxConstants.SYNTAX_STYLE_SQL;
        } else {
            return StringUtils.EMPTY;
        }
    }

    public static String removeCommentary(final String javaCode) {
        String newJavaCode = "";
        boolean isCommentary = false;
        for (int i = 0; i < javaCode.length(); i++) {
            if (i + 1 < javaCode.length() && javaCode.charAt(i) == '/' && javaCode.charAt(i + 1) == '*') {
                isCommentary = true;
            } else if (i >= 2 && javaCode.charAt(i - 2) == '*' && javaCode.charAt(i - 1) == '/') {
                isCommentary = false;
            }
            if (!isCommentary) {
                newJavaCode += javaCode.charAt(i);
            }
        }
        return newJavaCode;
    }
}
