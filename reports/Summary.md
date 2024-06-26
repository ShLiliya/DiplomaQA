# Отчет по итогам автоматизации
### Запланированный и реализованный план
В ходе тестирования приложения для покупки туристической путевки при помощи Docker Desktop настроена тестовая среда 
с возможностью запуска приложения и тестирования SUT при помощи MySQL и Postgers. 

Автоматизация реализована через логические конструкции, которые отражены в классах: 
[DataHelper](https://github.com/ShLiliya/DiplomaQA/blob/main/src/test/java/ru/netology/data/DataHelper.java), 
[SQLHelper](https://github.com/ShLiliya/DiplomaQA/blob/main/src/test/java/ru/netology/data/SQLHelper.java), 
[CardType](https://github.com/ShLiliya/DiplomaQA/blob/main/src/test/java/ru/netology/page/CardType.java), 
[DashboardPage](https://github.com/ShLiliya/DiplomaQA/blob/main/src/test/java/ru/netology/page/DashboardPage.java), 
[ShopPage](https://github.com/ShLiliya/DiplomaQA/blob/main/src/test/java/ru/netology/page/ShopPage.java).

Согласно [плану](https://github.com/ShLiliya/DiplomaQA/blob/main/Plan.md), реализованы позитивные и негативные автоматизируемые сценарии, 
при этом количество сценариев было увеличено для достижения максимального покрытия автотестами.

Также возникли многочисленные трудности, которые не были учтены в плане. Так, классы, выполняющие необходимые логическе операции, многократно 
подверхались корректировке и дополнению из-за ошибок, выявленных при написании автотестов в классах [CreditCartTest](https://github.com/ShLiliya/DiplomaQA/blob/main/src/test/java/ru/netology/test/CreditCartTest.java), 
[DebitCardTest](https://github.com/ShLiliya/DiplomaQA/blob/main/src/test/java/ru/netology/test/DebitCardTest.java)

### Cработавшие риски
Высокое влияние на сроки выполения работы оказали следующие риски.
- Риск ошибок настройки программ в ходе подготовки окружения к тестированию. Для успешного подключения контейнеров потребовалась использовать различные вариации написания
  файла [docker-compose](https://github.com/ShLiliya/DiplomaQA/blob/main/docker-compose.yml).
- Риск неверного обнаружения необходимых селекторов и их привязки к автотестам. Для реалиации поставленных задач был произведен подбор селекторов,
  при этом приходилось сталкиваться с ошибками и переберать селекторы до выяления подходящих.
- Риск ошибок при написании автотестов, которые приведут к "падению" тестов и увеличению сроков сдачи работы. Для успешной работы тестов потребовалась
их корректировка совместно с методами, к которым обращались автотесты.

### Общий итог по времени: сколько запланировано и сколько выполнено с обоснованием расхождения
Соответсвенно [плану](https://github.com/ShLiliya/DiplomaQA/blob/main/Plan.md), наиболее затратная по времени выполнения являлась автоматизациия тестирования. 
На нее было запланированно потратить 32 часа, однако фактическое время составило более 50 часов, 
кроме того дополнительно в ходе написания автотестов на коррекцию автоматизации затрачено около 5 часов. 
Причинами являются сложности с подбором селекторов, ошибки в логике, ошибки тест-дизайна, которые связаны с нехваткой опыта реализации подобных проектов.

При этом на подготовку отчетных документов затрачено меньше времени, чем было заявлено в плане, что позволило выполнить необходимые работы в срок.
