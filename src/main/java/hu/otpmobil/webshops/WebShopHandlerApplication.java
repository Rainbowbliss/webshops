package hu.otpmobil.webshops;

import hu.otpmobil.webshops.service.CustomerService;
import hu.otpmobil.webshops.service.PaymentService;
import hu.otpmobil.webshops.service.StatisticsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebShopHandlerApplication {

  private static final StatisticsService statisticsService = new StatisticsService(
      new PaymentService(),
      new CustomerService());

  public static void main(String[] args) {
    SpringApplication.run(WebShopHandlerApplication.class, args);
    statisticsService.createStatistics();
    System.exit(0);
  }
}
