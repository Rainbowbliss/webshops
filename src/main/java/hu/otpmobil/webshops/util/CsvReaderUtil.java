package hu.otpmobil.webshops.util;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import hu.otpmobil.webshops.error.CsvProcessingException;
import hu.otpmobil.webshops.model.Customer;
import hu.otpmobil.webshops.model.Payment;
import hu.otpmobil.webshops.model.PaymentType;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CsvReaderUtil {

  private CsvReaderUtil() {
  }

  public static List<Customer> readCustomers(String csvFile) {
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(new ClassPathResource(csvFile).getInputStream()))) {

      CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
      CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();

      var customers = new ArrayList<Customer>();

      String[] line;

      while ((line = csvReader.readNext()) != null) {
        var nextCustomer = new Customer(line[0], line[1], line[2], line[3]);

        var webShopContainsCustomer = customers.stream().anyMatch(
            customer -> customer.getCustomerId().equals(nextCustomer.getCustomerId()) &&
                customer.getWebShopId().equals(nextCustomer.getWebShopId()));

        if (webShopContainsCustomer) {
          log.error("The customer {} already exists in web shop {}!", Arrays.toString(line),
              nextCustomer.getWebShopId());
        } else {
          customers.add(nextCustomer);
        }
      }

      csvReader.close();

      return customers;
    } catch (IOException | CsvValidationException e) {
      log.error("Error while processing CSV file!", e);
      throw new CsvProcessingException("Couldn't process CSV file!");
    }
  }

  public static List<Payment> readPayments(String csvFile) {
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(new ClassPathResource(csvFile).getInputStream()))) {

      CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
      CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();

      var payments = new ArrayList<Payment>();

      String[] line;

      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

      while ((line = csvReader.readNext()) != null) {
        LocalDate paymentTime = getLocalDate(line, dateTimeFormatter);
        if (paymentTime == null) continue;

        var nextPayment = new Payment(new Payment.WebShopCustomerId(line[0], line[1]),
            PaymentType.valueOf(line[2]),
            new BigDecimal(line[3]), line[4], line[5], paymentTime
        );

        var isCardPaymentAndHasCardNumber = PaymentType.card.equals(nextPayment.getPaymentType()) &&
            !StringUtils.isBlank(nextPayment.getCardNumber());
        var isTransferAndHasAccountNumber =
            PaymentType.transfer.equals(nextPayment.getPaymentType()) &&
                !StringUtils.isBlank(nextPayment.getBankAccountNumber());

        if (!isCardPaymentAndHasCardNumber && !isTransferAndHasAccountNumber) {
          log.error("The row {} does not contain necessary information!", Arrays.toString(line));
        } else {
          payments.add(nextPayment);
        }
      }

      csvReader.close();

      return payments;
    } catch (IOException | CsvValidationException e) {
      log.error("Error while processing CSV file!", e);
      throw new CsvProcessingException("Couldn't process CSV file!");
    }
  }

  private static LocalDate getLocalDate(String[] line, DateTimeFormatter dateTimeFormatter) {
    LocalDate paymentTime;
    try {
      paymentTime = LocalDate.parse(line[6], dateTimeFormatter);
    } catch (DateTimeException e) {
      log.error("The row {} has invalid date format!", Arrays.toString(line));
      return null;
    }
    return paymentTime;
  }
}
