# Процедура запуска автотестов
### Необходимое ПО:
- Операционная система: Windows 10
- IDE: IntelliJ IDEA 2023.3.4 (Community Edition)
- Java: OpenJDK11
- Браузер: Google Chrome Версия 123.0.6312.107 (Официальная сборка), (64 бит) 
- Docker v1.0.35+desktop.10
### Запуск c MySQL
1.	Клонировать репозиторий [DiplomaQA](https://github.com/ShLiliya/DiplomaQA/)
2.	Открыть проект в IntelliJ IDEA
3.	Запустить контейнеры БД командой: `docker-compose up -d`
4.	Запустить MySQL контейнер: `java -jar./artifacts/aqa-shop.jar`
5.	Удостовериться в успешном запуске, открыв в браузере адрес `localhost:8080`
6.	Запустить тесты: `.\gradlew clean test`
7.	Сформировать отчет в Allure: `./gradlew allureServe`
8.	Остановить формирование отчета сочетанием клавиш: `CTRL+C` затем `Y`
9.	Остановить контейнеры командой: `docker-compose down`

### Запуск c PostgreSQL
*Для воспроизведения тестов через данный контейнер в предыдущем алгоритме заменить пункты 4 и 6 на:*

*4.  Запустить PostgreSQL контейнер: `java -jar./artifacts\aqa-shop.jar --spring.datasource.url=jdbc:postgresql://localhost:5432/app`.

*6.  Запустить тесты: `.\gradlew clean test -D dbUrl=jdbc:postgresql://localhost:5432/app -D dbUser=app -D dbPass=pass`.

