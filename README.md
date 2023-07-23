# IntelliJ Idea Inspired text utils for 1C: EDT.
Плагин вдохновлен возможностями рефакторинга кода в Android Studio и ставит перед собой цель
перенести схожие по удобству функции в EDT, для тех мест где они явно напрашиваются.

## Возможности плагина
### Добавлены новые быстрые исправления
#### "Переместить метод в другую область..."
Функция используется в комбинации с проверкой "Стандартов разработки" и позволяет быстро перемещать тело процедуры\функции
в соответствующую стандарту область. Механизм анализирует результаты проверок плагином и в зависимости от найденных проблем предлагает:
* Пользователю выбрать область в которую следует переместить тело процедуры\функции:
  ![изображение](https://github.com/OIegZolotarev/i3textutils/assets/5837087/ad91611c-35e8-4342-bb95-91e15a37a5b9)
* Переместить в тело процедуры\функции в конкретную область, если она единственная из допустимых:
  ![изображение](https://github.com/OIegZolotarev/i3textutils/assets/5837087/63453318-2413-416f-83dd-67dec52285ea)

#### Добавить директиву "&НаСервере"
Для соотвествия "стандартам разработки" функция позволяет расставить всем процедурам и функциям в модуле без директивы компиляции
директиву "&НаСервере". Также реализовано быстрое исправление для добавления этой директивы конкретной процедуре\функции:

![изображение](https://github.com/OIegZolotarev/i3textutils/assets/5837087/8448997d-8120-4d8d-8b91-94ed5901e506)


#### Преобразовать в процедуру\функцию
Функция позволяет быстро поменять ключевые слова "Процедура\Функция" и "КонецПроцедуры\КонецФункции" на противоположные.

### Команда "Добавить структуру модуля"
Используется совместно с плагином "Стандарты разработки" - позволяет добавить в модуль структуру областей из заданных в плагине настроек.
Удобно при приведении старого кода к стандартам.

### Функция "Выровнять вертикально" 
Реализовано выравнивание в блоке текста по знаку равно, такое выравнивание позволяет повыямть удобочитаемость блока:

| До выравнивания | После выравнивания|
| ----------- | ----------- |
| ![изображение](https://github.com/OIegZolotarev/i3textutils/assets/5837087/5b0e5cd7-8c0b-4eb2-b54b-0f79ac9068fd) | ![изображение](https://github.com/OIegZolotarev/i3textutils/assets/5837087/d0f5b0f9-858e-407a-a493-da961c7a67fb) |

### Исправлена вырвиглазная цветовая гамма подсказки для темной темы
Панель подсказок не учитывает выбранную настройку цветовой гаммы и при использовании темной темы
удобство пользования подсказкой снижалось. Плагин исправляет этот недостаток, заставляя подсказку использовать
цвет "BSLKeywordColor" для оформления текста

| До | После |
| ----------- | ----------- |
| ![изображение](https://github.com/OIegZolotarev/i3textutils/assets/5837087/767ecdf8-ece7-42c9-b1ac-c224175eeb19) | ![изображение](https://github.com/OIegZolotarev/i3textutils/assets/5837087/863c8b49-2c4e-4526-8450-df9f01e3e6f3)


