# Chartographer-Kontur
Решение тестового задания Chartographer для стажировки в СКБ Контур

# Задача
Необходимо написать сервис **Chartographer** — сервис для восстановления изображений древних свитков и папирусов.
Изображения растровые и создаются поэтапно (отдельными фрагментами).
Восстановленное изображение можно получать фрагментами (даже если оно лишь частично восстановленное).

Предполагается, что этим сервисом будет одновременно пользоваться множество учёных.
## HTTP API

Необходимо реализовать 4 HTTP-метода:

```
POST /chartas/?width={width}&height={height}
```
Создать новое изображение папируса заданного размера (в пикселях),
где `{width}` и `{height}` — положительные целые числа, не превосходящие `20 000` и `50 000`, соответственно.  
Тело запроса пустое.  
В теле ответа возвращается `{id}` — уникальный идентификатор изображения в строковом представлении.  
Код ответа: `201 Created`.

```
POST /chartas/{id}/?x={x}&y={y}&width={width}&height={height}
```
Сохранить восстановленный фрагмент изображения размера `{width} x {height}` с координатами `({x};{y})`.
Под координатами подразумевается положение левого верхнего угла фрагмента относительно левого верхнего угла всего изображения.
Другими словами, левый верхний угол изображения является началом координат, т.е. эта точка имеет координаты `(0;0)`.  
Тело запроса: изображение в формате `BMP` (цвет в RGB, 24 бита на 1 пиксель).  
Тело ответа пустое.  
Код ответа: `200 OK`.

```
GET /chartas/{id}/?x={x}&y={y}&width={width}&height={height}
```
Получить восстановленную часть изображения размера `{width} x {height}` с координатами `({x};{y})`,
где `{width}` и `{height}` — положительные целые числа, не превосходящие 5 000.
Под координатами подразумевается положение левого верхнего угла фрагмента относительно левого верхнего угла всего изображения.
Другими словами, левый верхний угол изображения является началом координат, т.е. эта точка имеет координаты `(0;0)`.  
Тело ответа: изображение в формате `BMP` (цвет в RGB, 24 бита на 1 пиксель).  
Код ответа: `200 OK`.

```
DELETE /chartas/{id}/
```
Удалить изображение с идентификатором `{id}`.  
Тело запроса и ответа пустое.  
Код ответа: `200 OK`.

# Реализация решения
```
POST /chartas/?width={width}&height={height}
```
Создаётся директория, в которой будут храниться фрагменты папируса. В ней создаётся файл image.properties, 
в котором хранится вся информация об изображении: размер, расположение фрагментов и их размеры.

Сервер отвечает кодом 201(Created), тело содержит id по которому можно взаимодействовать с изображением.
Сервер возвращает код 400(Bad Request) при неверных параметрах. Width - положительное число, не превышающее 20000,
height - положительное число, не превышающее 50000.

```
POST /chartas/{id}/?x={x}&y={y}&width={width}&height={height}
```
Сервер принимает файл из тела запроса и загружает его в директорию с изображением. В image.properties 
добавляется информация о размерах и положении фрагмента.

Сервер отвечает кодом 200(OK), при успешном запросе.
Сервер возвращает код 400(Bad Request) при неверных параметрах. Проверяется существование изображения, 
пересечение фрагмента с изображением, размер фрагмента, который не должен превышать 20000 x 50000, и
соответствие width и height размерам изображения.

Так как сервер хранит все восстановленные изображения, можно реализовать функцию удаления восстановленного фрагмента.

```
GET /chartas/{id}/?x={x}&y={y}&width={width}&height={height}
```

Сервер определяет, какие восстановленные фрагменты попадают в запрашиваемую область, читает все видимые области изображений, 
создаёт на их основе картинку и отправляет её в теле ответа.

Сервер отвечает кодом 200(OK) при успешном запросе, тело содержит bmp фотографию.
Сервер возвращает код 400(Bad Request) при неверных параметрах. Проверяется существование изображения, 
пересечение фрагмента с изображением, размер фрагмента, который не должен превышать 5000 x 5000.

```
DELETE /chartas/{id}/
```

Удаляет все файлы, связанные с данным изображением и директорию {id}.

Сервер отвечает кодом 200(OK), при успешном запросе.
Сервер возвращает код 400(Bad Request), если изображения не существует.

