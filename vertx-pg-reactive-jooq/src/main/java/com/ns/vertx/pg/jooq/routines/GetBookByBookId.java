/*
 * This file is generated by jOOQ.
 */
package com.ns.vertx.pg.jooq.routines;


import com.ns.vertx.pg.jooq.Public;

import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GetBookByBookId extends AbstractRoutine<JSON> {

    private static final long serialVersionUID = -950986613;

    /**
     * The parameter <code>public.get_book_by_book_id.RETURN_VALUE</code>.
     */
    public static final Parameter<JSON> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.JSON, false, false);

    /**
     * The parameter <code>public.get_book_by_book_id.b_id</code>.
     */
    public static final Parameter<Long> B_ID = Internal.createParameter("b_id", org.jooq.impl.SQLDataType.BIGINT, false, false);

    /**
     * Create a new routine call instance
     */
    public GetBookByBookId() {
        super("get_book_by_book_id", Public.PUBLIC, org.jooq.impl.SQLDataType.JSON);

        setReturnParameter(RETURN_VALUE);
        addInParameter(B_ID);
    }

    /**
     * Set the <code>b_id</code> parameter IN value to the routine
     */
    public void setBId(Long value) {
        setValue(B_ID, value);
    }

    /**
     * Set the <code>b_id</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public GetBookByBookId setBId(Field<Long> field) {
        setField(B_ID, field);
        return this;
    }
}
