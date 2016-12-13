Атака Франклина-Рейтера на RSA

Имеются два сообщения M1 , M2 ∈ Zn причём M1 = f ( M2 ) mod n , где f ∈ Zn[ x ] - некоторый открытый многочлен.

Сторона A с открытым ключом ( n , e ) получает эти сообщения от стороны B, которая просто зашифровывает сообщения M1, и передаёт полученные шифротексты C1 , C2.

Задача: Противник, зная ( n, e, C1, C2, f ) хочет восстановить M1 , M2.