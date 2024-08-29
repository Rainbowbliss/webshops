package hu.otpmobil.webshops.util;

import com.opencsv.CSVWriter;
import hu.otpmobil.webshops.model.CustomerPaymentSummary;
import hu.otpmobil.webshops.model.WebShopRevenueSummary;
import lombok.extern.slf4j.Slf4j;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
public class CsvWriterUtil {

  private CsvWriterUtil() {
  }

  public static void writeCustomerStatisticsToCsv(String filename,
      List<CustomerPaymentSummary> summaries) {
    try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
      String[] header = {"name", "address", "spending"};
      writer.writeNext(header);

      for (CustomerPaymentSummary summary : summaries) {
        String[] line = {
            summary.getName(),
            summary.getAddress(),
            summary.getSpending().toString()
        };
        writer.writeNext(line);
      }
    } catch (IOException e) {
      log.error("Error while writing to {} file!", filename, e);
    }
  }

  public static void writeRevenueSummariesToCsv(List<WebShopRevenueSummary> summaries) {
    try (CSVWriter writer = new CSVWriter(new FileWriter("report02.csv"))) {
      String[] header = {"webShopId", "cardRevenue", "transferRevenue"};
      writer.writeNext(header);

      for (WebShopRevenueSummary summary : summaries) {
        String[] line = {
            summary.getWebShopId(),
            summary.getCardRevenue().toString(),
            summary.getTransferRevenue().toString()
        };
        writer.writeNext(line);
      }
    } catch (IOException e) {
      log.error("Error while writing to report02.csv file!", e);
    }
  }
}
