package org.galatea.starter.database;

import java.time.OffsetDateTime;
import java.util.List;
import javax.persistence.Query;
import org.galatea.starter.object.Stock;
import org.galatea.starter.object.StockSymbol;
import org.hibernate.Session;

/**
 * I'm not sure what to call this. It interfaces with the database, so interface?
 */
public class DatabaseConnectionHandler {

  private DatabaseConnectionHandler() {}

  public static List<StockSymbol> getStockData(int stockId, OffsetDateTime date, Session session) {
    Query select = session.createQuery(
        "from StockSymbol where stockId = :stockId and tradeDate > :date order by tradeDate desc");
    select.setParameter("stockId", stockId);
    select.setParameter("date", date);

    @SuppressWarnings("unchecked")
    List<StockSymbol> results =  select.getResultList();
    return results;
  }

  public static void updateStockRecord(StockSymbol stock, int stockId, Session session) {
    stock.setStockId(stockId);
    session.beginTransaction();
    session.saveOrUpdate(stock);
    session.getTransaction().commit();
  }

  public static void updateStockRecords(List<StockSymbol> stockList, int stockId, Session session) {
    session.beginTransaction();
    for (StockSymbol stock : stockList) {
      stock.setStockId(stockId);
      session.saveOrUpdate(stock);
    }
    session.getTransaction().commit();

  }

  public static int selectOrInsertStock(String symbol, Session session) {
    int id;
    @SuppressWarnings("unchecked")
    List<Stock> stockList = session.createQuery("from Stock where symbol = :symbol")
        .setParameter("symbol", symbol).list();
    if (stockList.isEmpty()) {
      session.beginTransaction();
      session.getTransaction().commit();
      Stock stock = new Stock();
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
