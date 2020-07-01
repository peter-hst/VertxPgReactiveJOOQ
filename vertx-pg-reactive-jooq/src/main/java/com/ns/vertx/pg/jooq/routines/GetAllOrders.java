/*
 * This file is generated by jOOQ.
 */
package com.ns.vertx.pg.jooq.routines;


import com.ns.vertx.pg.jooq.Public;

import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GetAllOrders extends AbstractRoutine<String> {

    private static final long serialVersionUID = 1813538537;

    /**
     * The parameter <code>public.get_all_orders.RETURN_VALUE</code>.
     */
    public static final Parameter<String> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.JSON, false, false, org.jooq.Converter.ofNullable(org.jooq.JSON.class, String.class, Object::toString, org.jooq.JSON::valueOf));

    /**
     * Create a new routine call instance
     */
    public GetAllOrders() {
        super("get_all_orders", Public.PUBLIC, org.jooq.impl.SQLDataType.JSON, org.jooq.Converter.ofNullable(org.jooq.JSON.class, String.class, Object::toString, org.jooq.JSON::valueOf));

        setReturnParameter(RETURN_VALUE);
    }
}
