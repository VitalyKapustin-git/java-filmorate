# java-filmorate
Template repository for Filmorate project.

### ERD:

![ERD](https://raw.githubusercontent.com/VitalyKapustin-git/java-filmorate/erd/filmorate-ERD.png)

UNF:
* [x] Строки не имеют привязки к нумерации

1NF:
* [x] В таблице не должно быть дублирующих строк.
* [x] В каждой ячейке таблицы хранится атомарное значение (одно не составное значение)
* [x] В столбце хранятся данные одного типа
* [x] Отсутствуют массивы и списки в любом виде

2NF:
* [x] Таблица должна находиться в первой нормальной форме
* [x] Таблица должна иметь ключ

3NF:
* [x] Таблица должна находиться во второй нормальной форме
* [x] Транзитивные зависимости отсутствуют


### Запросы:
Вывести список всех пользователей:
```
SELECT * FROM Users;
```
Вывести список всех фильмов:
```
SELECT * FROM Films;
```
Узнать рейтинг фильмов:
```
SELECT 
  film_id,
  count(usr_id) rating
FROM FilmsRating
GROUP BY film_id;
```
Узнать ТОП-10 фильмов по рейтингу:
```
SELECT 
  film_id,
  count(usr_id) rating
FROM FilmsRating
GROUP BY film_id
ORDER BY count(usr_id) DESC -- сортируем по убыванию (от большего к меньшему)
LIMIT 10;
```
Узнать какие пользователи голосовали за фильм с ID 1:
```
declare @film_id int;
set @film_id = 1;

-- Выведем логин и email для наглядности
SELECT u.login, u.email FROM Users u
join FilmsRating fr on fr.usr_id = u.usr_id
where film_id = @film_id;
```
Узнать список друзей пользователя с ID 1:
```
-- Здесь объявляю переменную @usr_id для наглядности и присваиваю ей значение (1)
declare @usr_id int;
set @usr_id = 1;

select * from Friends
where usr_id = @usr_id;
```
Узнать жанры фильма с ID 1:
```
-- Здесь объявляю переменную @film_id для наглядности и присваиваю ей значение (1)
declare @film_id int;
set @film_id = 1;

select * from FilmsGenre fg
join Genres g on g.genre_id = fg.genre_id
where film_id = @film_id;
```
Список возрастных ограничений:
```
SELECT * FROM MPA;
```
Список возрастных ограничений в связке с фильмами:
```
SELECT 
  f.*,  -- вывожу все поля по фильму
  m.mpa -- вывожу только человеко-понятный текст ограничения по возрасту
FROM Films f
join MPA m on m.mpa_id = f.mpa_id ;
```
Список общих друзей:
```
-- Логика следующая. Берем список моих друзей и список друзей моего друга и сравниваем пересечения.
declare @me int;
declare @my_friend int;

set @me = 1;
set @my_friend = 2;

-- Список моих друзей (это массив данных)
SELECT friend_id FROM Friends 
WHERE usr_id = @me;

->> Вывод: 1 2 3 4 7

-- Список друзей моего друга
SELECT friend_id FROM Friends 
WHERE usr_id = @my_friend; 

->> Вывод: 1 3 2 9 6 18

-- Теперь объединяем все что выше и находим пересечения:
SELECT friend_id FROM Friends 
WHERE usr_id = @me
and friend_id in (
  SELECT friend_id FROM Friends 
  WHERE usr_id = @my_friend; 
);

->> Вывод: 1 2 3
```
