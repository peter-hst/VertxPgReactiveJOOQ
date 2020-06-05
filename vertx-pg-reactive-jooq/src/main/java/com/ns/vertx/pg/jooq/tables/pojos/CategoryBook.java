/*
 * This file is generated by jOOQ.
 */
package com.ns.vertx.pg.jooq.tables.pojos;


import com.ns.vertx.pg.jooq.tables.interfaces.ICategoryBook;

import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CategoryBook implements VertxPojo, ICategoryBook {

    private static final long serialVersionUID = -1256746671;

    private Long categoryId;
    private Long bookId;

    public CategoryBook() {}

    public CategoryBook(ICategoryBook value) {
        this.categoryId = value.getCategoryId();
        this.bookId = value.getBookId();
    }

    public CategoryBook(
        Long categoryId,
        Long bookId
    ) {
        this.categoryId = categoryId;
        this.bookId = bookId;
    }

    @Override
    public Long getCategoryId() {
        return this.categoryId;
    }

    @Override
    public CategoryBook setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    @Override
    public Long getBookId() {
        return this.bookId;
    }

    @Override
    public CategoryBook setBookId(Long bookId) {
        this.bookId = bookId;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CategoryBook (");

        sb.append(categoryId);
        sb.append(", ").append(bookId);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(ICategoryBook from) {
        setCategoryId(from.getCategoryId());
        setBookId(from.getBookId());
    }

    @Override
    public <E extends ICategoryBook> E into(E into) {
        into.from(this);
        return into;
    }

    public CategoryBook(io.vertx.core.json.JsonObject json) {
        this();
        fromJson(json);
    }
}
