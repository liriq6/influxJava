package HttpRequest.api;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ApiApplication.class, args);

        String databaseURL = "http://localhost:8086";
        InfluxDB influxDB = InfluxDBFactory.connect(databaseURL);

        influxDB.createDatabase("Test");
        influxDB.createRetentionPolicy("defaultPolicy", "baeldung", "30d", 1, true);

        BatchPoints batchPoints = BatchPoints
                .database("Test")
                .retentionPolicy("autogen")
                .build();

        Thread threadOne = new Thread() {
            public void run() {
                while (false != true) {
                    int min = 50;
                    int max = 100;
                    int diff = max - min;
                    Random random = new Random();
                    int i = random.nextInt(diff + 1);
                    i += min;

                    Point point = Point.measurement("memory")
                            .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                            .addField("name", "SYSTEM")
                            .addField("value", i)
                            .build();
                    batchPoints.point(point);
                    try {
                        Thread.sleep((random.nextInt(2)+1)*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread threadTwo = new Thread() {
            public void run() {
                while (false != true) {
                   try {
                       Thread.sleep(15000);
                   } catch (InterruptedException e) {
                        e.printStackTrace();
                   }
                    influxDB.write(batchPoints);
                }
            }
        };
        threadOne.start();
        threadTwo.start();
    }
}