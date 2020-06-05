/*
 * This file is generated by jOOQ.
 */
package com.ns.vertx.pg.jooq.tables.pojos;


import com.ns.vertx.pg.jooq.tables.interfaces.ICategory;

import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Category implements VertxPojo, ICategory {

    private static final long serialVersionUID = -72752475;

    private Long    categoryId;
    private String  name;
    private Boolean isDeleted;

    public Category() {}

    public Category(ICategory value) {
        this.categoryId = value.getCategoryId();
        this.name = value.getName();
        this.isDeleted = value.getIsDeleted();
    }

    public Category(
        Long    categoryId,
        String  name,
        Boolean isDeleted
    ) {
        this.categoryId = categoryId;
        this.name = name;
        this.isDeleted = isDeleted;
    }

    @Override
    public Long getCategoryId() {
        return this.categoryId;
    }

    @Override
    public Category setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Category setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    @Override
    public Category setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Category (");

        sb.append(categoryId);
        sb.append(", ").append(name);
        sb.append(", ").append(isDeleted);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(ICategory from) {
        setCategoryId(from.getCategoryId());
        setName(from.getName());
        setIsDeleted(from.getIsDeleted());
    }

    @Override
    public <E extends ICategory> E into(E into) {
        into.from(this);
        return into;
    }

    public Category(io.vertx.core.json.JsonObject json) {
        this();
        fromJson(json);
    }
}
