НИУ ИТМО, Машинное обучение (осень 2012)
================================================

Лабораторные работы по курсу [машинного обучения][ml home] студентов
четвертого курса НИУ ИТМО.

Рекомендуемые языки программирования: Java, Python, Matlab(Octave). 
По согласованию с преподавателем возможны другие варианты.

Структура каталогов
-------------------

lib - общие библиотеки

data - архивы с данными для лабораторных

<имя.фамилия> - папка с личными наработками студента

Внутренняя организация оставляется на личное усмотрение.

[ml home]: http://neerc.ifmo.ru/~ml

Распознавание цифр
------------------

Блок лабораторные работы по рапознаванию рукописных цифр архива MNIST.

####1. Линейный перцептрон
Реализуйте алгоритм обучения линейного перцептрона. Ограничьте
число шагов алгоритма. Используйте метод one-vs-all.

Ключевые пункты:
- загрузка данных,
- обучение перцептрона,
- вычисление ошибки обучения на тестовых данных.

####2. Логистическая регрессия
Используйте логистическую регрессию для классификации цифр по
схеме one-vs-all.

Примените регуляризацию. Резуляризационную константу C выберите при
помощи кросс-валидации.

####3. Support Vector Machine
То же самое при помощи SVM.

####4. Выбор модели
Используйте полиномиальное расширение входных данных.
Постройте кривые обучения. Применяя кросс-валидацию
выберите оптимальный алгоритм классификации и преобразование
входных данных.

####5. Нейронная сеть
Постройте трехслойную нейронную сеть для распознавания рукописного текста.
Входной слой 28*28, выходной 10. Число внутренних вершин
выберите при помощи кросс-валидации.

Добавьте еще один внутренний слой. Сравните результат.

(Опционально) Попробуйте несколько разных пороговых функций.
