package hu.otpmobil.webshops.service;

import hu.otpmobil.webshops.model.Customer;
import hu.otpmobil.webshops.model.CustomerPaymentSummary;
import hu.otpmobil.webshops.model.Payment;
import hu.otpmobil.webshops.model.PaymentType;
import hu.otpmobil.webshops.model.WebShopRevenueSummary;
import hu.otpmobil.webshops.util.CsvWriterUtil;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class StatisticsService {

  private final List<Payment> payments;
  private final List<Customer> customers;

  public StatisticsService(PaymentService paymentService, CustomerService customerService) {
    this.payments = paymentService.getPayments();
    this.customers = customerService.getCustomers();
  }

  public void createStatistics() {
    List<CustomerPaymentSummary> customerPaymentSummary = getCustomerPaymentSummary();
    List<CustomerPaymentSummary> topCustomers = getTopCustomers(customerPaymentSummary);
    List<WebShopRevenueSummary> webShopRevenueSummary = getWebShopRevenue();

    CsvWriterUtil.writeCustomerStatisticsToCsv("report01.csv", customerPaymentSummary);
    CsvWriterUtil.writeCustomerStatisticsToCsv("top.csv", topCustomers);
    CsvWriterUtil.writeRevenueSummariesToCsv(webShopRevenueSummary);
  }

  private List<CustomerPaymentSummary> getTopCustomers(
      List<CustomerPaymentSummary> customerPaymentSummary) {
    var topCustomers = new ArrayList<CustomerPaymentSummary>();
    var sortedList = customerPaymentSummary.stream()
        .sorted(Comparator.comparing(CustomerPaymentSummary::getSpending).reversed())
        .toList();

    if (!sortedList.isEmpty()) {
      topCustomers.add(sortedList.get(0));
      if (sortedList.size() > 1) {
        topCustomers.add(sortedList.get(1));
      }
    }
    return topCustomers;
  }

  private List<CustomerPaymentSummary> getCustomerPaymentSummary() {
    var result = new ArrayList<CustomerPaymentSummary>();

    Map<Payment.WebShopCustomerId, BigDecimal> paymentSummaryByUser = payments.stream()
        .collect(Collectors.groupingBy(Payment::getWebShopCustomerId,
            Collectors.reducing(BigDecimal.ZERO,
                Payment::getAmount,
                BigDecimal::add)));

    paymentSummaryByUser.forEach((webShopCustomerId, spending) -> {
      var customerOptional = customers.stream()
          .filter(customer -> customer.getCustomerId().equals(webShopCustomerId.getCustomerId()) &&
              customer.getWebShopId().equals(webShopCustomerId.getWebShopId()))
          .findFirst();
      if (customerOptional.isPresent()) {
        var customer = customerOptional.get();
        result.add(new CustomerPaymentSummary(customer.getCustomerName(),
            customer.getCustomerAddress(), spending));
      } else {
        log.error("No customer found in web shop {} with the following id: {}",
            webShopCustomerId.getWebShopId(), webShopCustomerId.getCustomerId());
      }
    });
    return result;
  }

  private List<WebShopRevenueSummary> getWebShopRevenue() {
    return payments.stream()
        .collect(Collectors.groupingBy(payment -> payment.getWebShopCustomerId().getWebShopId(),
            Collectors.partitioningBy(p -> p.getPaymentType().equals(PaymentType.card),
                Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add))))
        .entrySet()
        .stream()
        .map(e -> new WebShopRevenueSummary(
            e.getKey(),
            e.getValue().get(true),
            e.getValue().get(false)))
        .toList();
  }
}
