package org.galatea.starter.database;

import java.util.List;
import lombok.NonNull;
import org.galatea.starter.object.Stock;
import org.galatea.starter.object.StockSymbol;
import org.hibernate.Session;

/**
 * I'm not sure what to call this. It interfaces with the database, so interface?
 */
public class DatabaseConnectionHandler {

  private DatabaseConnectionHandler() {}

  /**
   * Updates this object in the database to match changed values.
   */
  public static void updateStockRecord(@NonNull final StockSymbol stock, final int stockId,
      final Session session) {
    stock.setStockId(stockId);
    session.beginTransaction();
    session.saveOrUpdate(stock);
    session.getTransaction().commit();
  }

  /**
   * Updates these objects in the database to match changed values.
   */
  public static void updateStockRecords(@NonNull final List<StockSymbol> stockList,
      final int stockId, @NonNull final Session session) {
    session.beginTransaction();
    for (final StockSymbol stock : stockList) {
      stock.setStockId(stockId);
      session.saveOrUpdate(stock);
    }
    session.getTransaction().commit();

  }

  /**
   * insert this symbol if it doesn't exist, and then get the id of this symbol from the database.
   */
  public static int selectOrInsertStock(@NonNull final String symbol,
      @NonNull final Session session) {
    final int id;
    @SuppressWarnings("unchecked") final List<Stock> stockList =
        session.createQuery("from Stock where symbol = :symbol")
            .setParameter("symbol", symbol).list();
    if (stockList.isEmpty()) {
      session.beginTransaction();
      session.getTransaction().commit();
      final Stock stock = new Stock();
      stock.setSymbol(symbol);
      session.beginTransaction();
      id = (Integer) session.save(stock);
      session.getTransaction().commit();
    } else {
      id = stockList.get(0).getId();
    }
    return id;
  }
}
