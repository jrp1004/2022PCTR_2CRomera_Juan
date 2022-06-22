# 2022PCTR_2CRomera_Juan

Práctica de Programación Concurrente en Java segunda convocatoria 2021/2022
Juan Romera Pérez

En el siguiente programa se simula un videojuego en el que se crean y se eliminan enemigos.

Tendremos hilos enemigos y aliados e hilos encargados de generarlos en función de su tipo.
El hilo generador creará una cantidad determinada de hilos nuevos en intervalos de tiempo aleatorios.
El hilo enemigo simulará su comportamiento durmiendo un tiempo aleatorio.
El hilo aliado tratará de eliminar al enemigo generando un booleano aleatorio. Cuando sea verdadero interrumpirá la ejecución del hilo enemigo y terminará.
La clase juego se encarga de gestionar la correcta aplicación de las normas y el sistema lanzador creará el juego y los generadores de enemigos.
