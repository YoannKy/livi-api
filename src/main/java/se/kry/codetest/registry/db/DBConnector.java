package se.kry.codetest.registry.db;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;

/**
 * Handles the connection to the DB.
 */
public class DBConnector {
  private final String DB_PATH = "poller.db";
  private final SQLClient client;

  /**
   * Constructor
   * @param vertx The vertx used for the db queries.
   */
  public DBConnector(Vertx vertx){
    final JsonObject config = new JsonObject()
        .put("url", "jdbc:sqlite:" + DB_PATH)
        .put("driver_class", "org.sqlite.JDBC")
        .put("max_pool_size", 30);

    client = JDBCClient.createShared(vertx, config);
  }

  /**
   * Runs a query.
   * @param query The sql query.
   * @return The future to retrieve the result.
   */
  public Future<ResultSet> query(String query) {
    return query(query, new JsonArray());
  }

  /**
   * Runs a query with parameters.
   * @param query The sql query.
   * @param params The parameters of the query.
   * @return The future to retrieve the result.
   */
  public Future<ResultSet> query(String query, JsonArray params) {
    if(query == null || query.isEmpty()) {
      return Future.failedFuture("Query is null or empty");
    }
    if(!query.endsWith(";")) {
      query = query + ";";
    }

    final Future<ResultSet> queryResultFuture = Future.future();

    client.queryWithParams(query, params, result -> {
      if(result.failed()){
        queryResultFuture.fail(result.cause());
      } else {
        queryResultFuture.complete(result.result());
      }
    });
    return queryResultFuture;
  }
}
