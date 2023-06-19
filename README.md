# kafka-cdc
Apache Kafka ile Basit CDC Uygulaması Geliştirme Projesi

Bu projede, MongoDB'den veri çeken ve bu veriyi Apache Kafka'ya gönderen bir Spring Boot uygulaması (A Uygulaması) ve Kafka'dan bu veriyi çeken bir diğer Spring Boot uygulaması (B Uygulaması) bulunmaktadır.

## Kurulum
Projeyi çalıştırmak için aşağıdaki adımları takip edin:

1. Projeyi klonlayın: 
   ```sh
   git clone https://github.com/bernabaris/kafka-producer-consumer.git
   ```
2. Ana dizinde maven build alın:
   ```sh
   mvn clean install
   ```
   "consumer/target" ve "producer/target" dizinlerinde jar dosyasının oluştuğunu göreceksiniz.
3. Ayrı ayrı consumer ve producer klasörlerinde docker imajlarını oluşturun
   ```sh
   docker build -t consumer:1.0-SNAPSHOT .
   docker build -t producer:1.0-SNAPSHOT .
   ```
4. "compose" klasöründeki mongodb ve kafka dizinlerinde ayrı ayrı up komutunu kullanarak uygulamaları kaldırın
   ```sh
   docker-compose up -d
   ```
   Ana dizinde de yukarıdaki komutu çalıştırırsanız consumer ve producer uygulamaları ayağa kalkacaktır.
5. Yine ana dizindeki "mongodb_doc_gen.py" python scriptini kullanarak mongodb'de "cdc" isimli 
collection'a rastgele bir döküman ekleyecektir.
   ```sh
   pip3 install -r requirements.txt
   python3 mongodb_doc_gen.py
   ```

Sonuç olarak aşağıdakine benzer logları göreceksiniz:

### producer
   ```sh
   2023-06-19 19:32:12.548  INFO 1 --- [ad | producer-1] c.g.b.kafka.KafkaProducerService         : sent message= SendResult 
   [producerRecord=ProducerRecord(topic=cdc, partition=null, headers=RecordHeaders(headers = [], isReadOnly = true), key=null, 
   value={"_id": {"$oid": "6490ad36255e1fe49fb3e8bc"}, "firstName": "Rebecca", "lastName": "Castaneda", "job": "Therapist, sports", 
   "address": "8849 Rose Lakes Suite 888\nSouth Robert, NC 38324", "city": "Port Joseph", "email": "timothyburns@example.net"}, timestamp=null), 
   recordMetadata=cdc-0@0] with offset= 0
   ```
### consumer
   ```sh
   2023-06-19 19:32:12.580  INFO 1 --- [ntainer#0-0-C-1] c.g.bernabaris.KafkaConsumerService      : {"_id": {"$oid": "6490ad36255e1fe49fb3e8bc"}, 
   "firstName": "Rebecca", "lastName": "Castaneda", "job": "Therapist, sports", "address": "8849 Rose Lakes Suite 888\nSouth Robert, NC 38324", 
   "city": "Port Joseph", "email": "timothyburns@example.net"}
   ```

## Lisans
Bu proje Apache License lisansı altında lisanslanmıştır.
