package io.github.zwieback.familyfinance.business.article.lifecycle.creator.exception;

import java.util.NoSuchElementException;

public class NoArticleFoundException extends NoSuchElementException {

    private static final long serialVersionUID = -4412352592461681914L;

    public NoArticleFoundException(String name) {
        super("No article was found with name '" + name + "'");
    }
}
