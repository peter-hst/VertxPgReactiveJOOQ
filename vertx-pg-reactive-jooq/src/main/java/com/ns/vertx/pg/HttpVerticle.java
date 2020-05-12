package com.ns.vertx.pg;

import static com.ns.vertx.pg.ActionHelper.*;
import static com.ns.vertx.pg.DBQueries.CREATE_CATEGORY_TABLE_SQL;

import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ns.vertx.pg.jooq.tables.daos.AuthorBookDao;
import com.ns.vertx.pg.jooq.tables.daos.BookDao;
import com.ns.vertx.pg.jooq.tables.daos.CategoryBookDao;
import com.ns.vertx.pg.jooq.tables.mappers.RowMappers;
import com.ns.vertx.pg.jooq.tables.pojos.Category;
import com.ns.vertx.pg.jooq.tables.records.AuthorBookRecord;
import com.ns.vertx.pg.jooq.tables.records.AuthorRecord;
import com.ns.vertx.pg.jooq.tables.records.BookRecord;
import com.ns.vertx.pg.service.BookServiceImpl;
import com.ns.vertx.pg.service.CategoryServiceImpl;

import io.github.jklingsporn.vertx.jooq.classic.reactivepg.ReactiveClassicGenericQueryExecutor;
import io.github.jklingsporn.vertx.jooq.classic.reactivepg.ReactiveClassicQueryExecutor;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.PreparedStatement;
import io.vertx.sqlclient.SqlConnection;


// MainVerticle is ALSO HttpVerticle in this scenario
public class HttpVerticle extends AbstractVerticle {

	private final static Logger LOGGER = LoggerFactory.getLogger(HttpVerticle.class);
	private static int LISTEN_PORT = 8080;

	private PgPool pgClient;
	private ReactiveClassicGenericQueryExecutor queryExecutor;
	private Configuration configuration;
	
	
	// FIXME: remove ALL DAOs
	private BookDao bookDAO;
	private AuthorBookDao authorBookDAO;
	private CategoryBookDao categoryBookDAO;
//	private OrdersDao ordersDAO;
//	private OrderItemDao orderItemDAO;

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		Router routerREST = Router.router(vertx);
		routerREST.post().handler(BodyHandler.create());
		routerREST.put().handler(BodyHandler.create());
		
		// Categories REST API		
		routerREST.get("/categories").handler(this::getAllCategoriesHandler);
		routerREST.get("/categories/:id").handler(this::getCategoryByIdHandler);				
		routerREST.post("/categories").handler(this::createCategoryHandler);
		routerREST.put("/categories/:id").handler(this::updateCategoryHandler);
		routerREST.delete("/categories/:id").handler(this::deleteCategoryHandler);

		// Books REST API		
		routerREST.get("/books").handler(this::getAllBooksHandlerJooq);
		routerREST.get("/books/:id").handler(this::getBookByIdHandler);		
		routerREST.post("/books").handler(this::createBookHandler);
		routerREST.put("/books/:id").handler(this::updateBookHandler);
		routerREST.delete("/books/:id").handler(this::deleteBookHandler);
		
		// Orders REST API
		// TODO: create HANDLER methods 
//		routerREST.get("/orders").handler(this::getAllOrdersHandler);
//		routerREST.post("/orders").handler(this::createOrderHandler);
		
		Router routerAPI = Router.router(vertx);
		routerAPI.mountSubRouter("/api", routerREST);
		routerAPI.errorHandler(500, error -> {
			Throwable failure = error.failure();
			if (failure != null) {
				failure.printStackTrace();
			}
		});

		PgConnectOptions connectOptions = new PgConnectOptions()
			.setPort(5432)
			.setHost("localhost")
			.setDatabase("vertx-jooq-cr")
			.setUser("postgres").setPassword("postgres"); // DB User credentials

		PoolOptions poolOptions = new PoolOptions().setMaxSize(30);
		pgClient = PgPool.pool(vertx, connectOptions, poolOptions);

		// setting up JOOQ configuration
		configuration = new DefaultConfiguration();
		configuration.set(SQLDialect.POSTGRES);
		
		// instantiating DAOs
		bookDAO = new BookDao(configuration, pgClient);
		authorBookDAO = new AuthorBookDao(configuration, pgClient);
		categoryBookDAO = new CategoryBookDao(configuration, pgClient);
//		ordersDAO = new OrdersDao(configuration, pgClient);
//		orderItemDAO = new OrderItemDao(configuration, pgClient);

		/* TODO: Check is DB conn CLOSED? Because of this 'DSLContext doesn't close the connection.' @:
		 * https://www.jooq.org/doc/3.11/manual/getting-started/tutorials/jooq-in-7-steps/jooq-in-7-steps-step5/ */
		queryExecutor = new ReactiveClassicGenericQueryExecutor(configuration, pgClient);
		// ================================================================================================
		// ========================== Testing classic-reactive-jOOQ:: START ===============================
		// no other DB-Configuration necessary because jOOQ is only used to render our
		// statements - not for execution		
		
		Future<Void> futureConnection = connect().compose(connection -> {
			Promise<Void> retFuture = Promise.promise();
			createTableIfNeeded().future().onComplete(x -> {
				connection.close();
				retFuture.handle(x.mapEmpty());
			});
			connection.exceptionHandler(handler -> {
				LOGGER.error("Error occured in connection! Cause: " + handler.getCause());
			});
			return retFuture.future();
		});
		
		futureConnection
			.compose(v -> createHttpServer(pgClient, routerAPI))
			.onComplete(startPromise);
		
//		createHttpServer(pgClient, routerAPI).onComplete(startPromise);
		
	}// start::END
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public Future<SqlConnection> connect() {
		Promise<SqlConnection> promise = Promise.promise();
		pgClient.getConnection(ar -> {
			if (ar.succeeded()) {
				promise.handle(ar.map(connection -> connection));
			} else {
				promise.fail(ar.cause());
			}
		});
		return promise.future();
	}

	public Future<Void> createHttpServer(PgPool pgClient, Router router) {
		Promise<Void> promise = Promise.promise();
		
		vertx.createHttpServer()
			 .requestHandler(router)
			 .listen(LISTEN_PORT, res -> promise.handle(res.mapEmpty()));
		return promise.future();
	}

	private Promise<SqlConnection> createTableIfNeeded() {
		Promise<SqlConnection> promise = Promise.promise();
		pgClient.getConnection(ar1 -> {
			if (ar1.succeeded()) {
				LOGGER.info("Connected!");
				SqlConnection conn = ar1.result();
				conn.prepare(CREATE_CATEGORY_TABLE_SQL, fetch -> {
					if (fetch.succeeded()) {
						PreparedStatement ps = fetch.result();
						ps.query().execute(rs -> {
							if (rs.succeeded()) {
								promise.handle(rs.map(conn));
							} else {
								LOGGER.error("Error, executing 'create_category_table_sql' " 
										+ "query failed!", rs.cause());
							}
						});
					} else {
						conn.close();
					}
				});
			} else {
				LOGGER.error("Error, acquiring DB connection! Cause: ", ar1.cause());
			}
		});
		return promise;
	}

	private void getAllCategoriesHandler(RoutingContext rc) {
		CategoryServiceImpl.getAllCategoriesJooq(queryExecutor).onComplete((ok(rc)));				
	}
	
	// FIXME: fix handling of NON-EXISTING 'category_id' which has been sent in HTTP GET request!!!
	private void getCategoryByIdHandler(RoutingContext rc) {
		long id = Long.valueOf(rc.request().getParam("id"));
		CategoryServiceImpl.getCategoryByIdJooq(queryExecutor, id).onComplete(ok(rc));
	}
	
	
	private void getAllBooksHandlerJooq(RoutingContext rc) {
//		BookJooqQueries.getAllBooksJooq(queryExecutor).onComplete(ok(rc));		
		BookServiceImpl.getAllBooksJooq(queryExecutor).onComplete(ok(rc));
	}
	

	private void getBookByIdHandler(RoutingContext rc) {
		long id = Long.valueOf(rc.request().getParam("id"));
//		BookJooqQueries.getBookByIdJooq(queryExecutor, id).onComplete((ok(rc)));
		BookServiceImpl.getBookByIdJooq(queryExecutor, id).onComplete((ok(rc)));
	}
	
	
	private void createCategoryHandler(RoutingContext rc) {
		JsonObject json = rc.getBodyAsJson();
		CategoryServiceImpl
			.createCategoryJooq(queryExecutor, json.getString("name"), json.getBoolean("is_deleted"))
			.onComplete(created(rc));
	}
	
	
	private void createBookHandler(RoutingContext rc) {
		JsonObject bookJO = rc.getBodyAsJson();		
		LOGGER.info("In 'createBookHandler(..)' bookJO =\n" + bookJO.encodePrettily());		
		/* BookJooqQueries.createBookJooq(queryExecutor, bookDAO, authorBookDAO, categoryBookDAO, bookJO)
			.onComplete(created(rc)); */		
		BookServiceImpl.createBookJooq(queryExecutor, bookJO, configuration, pgClient).onComplete(created(rc));
	}

	
	private void updateCategoryHandler(RoutingContext rc) {
		long id = (long) Integer.valueOf(rc.request().getParam("id"));
		Category categoryPojo = new Category(rc.getBodyAsJson());
		categoryPojo.setCategoryId(id);
		LOGGER.info("(in updateCategoryHandler) categoryPojo.toString(): " + categoryPojo.toString());
		CategoryServiceImpl.updateCategoryJooq(queryExecutor, categoryPojo, id).onComplete(noContent(rc));
	}	
	
	private void updateBookHandler(RoutingContext rc) {
		long id = (long) Integer.valueOf(rc.request().getParam("id"));
		JsonObject bookJO = rc.getBodyAsJson();
		bookJO.put("book_id", id);
		BookJooqQueries.updateBookJooq(queryExecutor, bookJO, bookDAO, authorBookDAO, categoryBookDAO, id)
					   .onComplete( (ok(rc)) );
		BookServiceImpl.updateBookJooq(queryExecutor, bookJO, bookDAO, authorBookDAO, categoryBookDAO, id)
		   .onComplete( (ok(rc)) );
	}
	
	private void deleteCategoryHandler(RoutingContext rc) {
		long id = Long.valueOf(rc.request().getParam("id"));
		CategoryServiceImpl.deleteCategoryJooq(queryExecutor, id).onComplete(noContent(rc));		
	}
	
	private void deleteBookHandler(RoutingContext rc) {
		long id = Long.valueOf(rc.request().getParam("id"));
//		BookJooqQueries.deleteBookJooq(queryExecutor, id).onComplete(noContent(rc));
		BookServiceImpl.deleteBookJooq(queryExecutor, id).onComplete(noContent(rc));
	}
	
//	private void getAllOrdersHandler(RoutingContext rc) {
//		OrdersJooqQueries.getAllOrdersJooq(queryExecutor).onComplete(ok(rc));
//	}

//	private void createOrderHandler(RoutingContext rc) {
//		JsonObject orderJO = rc.getBodyAsJson();
//		OrdersJooqQueries.createOrdersJooq()
//	}

}
