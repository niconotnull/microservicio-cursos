###En este microservicio se implemento Resilencia y corto-circuito

Resilencia y corto-circuito
La resilencia es cuando hay una tolerancia a fallos, resistencia problemas
errores.

Es muy común cuando se trabaja en un ecosistema de microservicios distribuidos
con cientos de clientes que llaman a clientes interconectados entre sí,
la comunicación podría fallar eventualemente los recursos se podrían ver
agotados con un timeOut, por ejemplo tiempos de respuestas demasiado prolongados
o algún problema de red o simplemente que el servicio no este disponible

¿Qué se puede hacer al respecto, como se pueden manejar esta fallas?

A través de una buena práctica utilizando el patrón corto circuito o
circuir breaker, que tiene la capasidad de recuperarse a tales fallas,
permanecer funcionando y hacer que el sistema sea más resistente, resilente
y también evita cualquier falla en cascada en los servicios posteriores.

Resilience4j es la librería que permite trabajar con la resilensia tolerancia a fallos
en nuestros servicios es mucho más optimizada y utiliza el patrón corto circuito, en versiones anteriores
a spring cloud 2.4.1 se implementaba la resilensia con Histrix de Netflix, Histrix  a quedado en
modo de mantenimiento para dar pauta a Resilience4j.


Estados del Circuit Breaker

Estado Cerrado: Es el estado inicial cuando está todo normal vamos a tener un cierto umbral
que es el porcentaje de fallas que es configurable, mientras este porcentaje de umbral de fallas
permanece bajo está todo correctamente, es decir se encuentra en un estado NORMAL, pero cuando aumenta
o suben los errores por sobre la tasa de configuración se abre un CORTO-CIRCUITO

Estado Abierto: Cuando aumentan o suben los errores por encima del umbral de la tasa de fallas se
abre el CORTO-CIRCUITO, todas las peticiones, al microservicio que presento fallas no se realizarán más
solicitudes queda en modo no disponible y cada vez que se desee acceder al servicio cuando
se encuentre en estado abierto lanzara un mensaje de error(el corto-circuito está abierto),
esta es una opción pero como segunda opción es posible tener un camino alternativo una función
callback que llame a otro microservicio y lo remplace. Se tienen esas dos alternativas cuando se
está en estado abierto.
Este estado es finito, tiene un tiempo de espera de forma predeterminada son 60sg cuando pase ese
tiempo pasa al estado SEMI-ABIERTO.

Estado Semi-abierto: Cuando un servicio pasa de un estado abierto a semi-abierto es como un periodo
de prueba, realiza nuevamente peticiones al microservicio que tenía algún error o fallas y si la
tasa permanece por debajo del umbral, está todo bien y vuelve a estar en estado CERRADO, pero si vuelve a
fallar y la tasa de fallas esta por encima del umbral vuelve a estado Abierto

PARÁMETROS DEL CIRCUIT-BREAKER

slidingWindowSize(100) tamaño de la ventana deslizante, esto es un muestreo estadístico
por ejemplo vamos a tener 100 peticiones a un microservicio particular y dentro de esas 100 solicitudes
se va a registrar un porcentaje de fallas, y si ese porcentaje es igual o mayor que el umbral que
tenemos configurado en los criterios, entonces se abre el CORTO-CIRCUITO,

failureRateThreshold(50) es el porcentaje de fallas en el umbral por ejemplo si se configuró el
slidingWindowSize en 100 y si de esos 100 falla el 50% del conteo entonces se va a abrir el cortocircuito y comienza
el ciclo de los estados, después de 60sg por defecto para a semiabierto y después de semiabierto
realizará peticiones de prueba y validar si falla o no, y así validar si continúa el ciclo

waitDurationInOpenState(60000ms) tiempo de duración en que permanece en estado abierto por defecto
son 60sg, no recibirá más peticiones

permittedNumberOfCallsInHalfOpenState(10) número permitido de llamadas en estado SEMI-ABIERTO.
Esto quiere decir que cuando pasa del estado abierto al estado semi-abierto, después de 60 segundos pasa
a estado semi-abierto y va a realizar peticiones de prueba y serán 10 por defecto, es decir
si el porcentaje del 50% que se tiene configurado por defecto si es igual o mayor regresa a estado
abierto si esta por debajo del umbral pasa a estado cerrado

### NOTAS
## Manejando los errores en las peticiones
Para probar la resilensia en el microservicio-cursos  se ha establecido en el endPoint /api/cursos/10 presenta una falla
que al realizar una petición a través de FeignClient al servicio de /alumnos-por-curso presentara un error que 
ejecutara el callBall(metodAlternativo) del servicio de /alumnos-por-curso, para poner en marcha el CORTO-CIRCUITO es necesario mandar 55 peticiones
al endpoint /api/cursos/10 para que se abra el corto circuito, ya que por default está configurado a 100 peticiones
y la tasa de umbral con un porcentaje de 50% es decir que al mandar 55 peticiones incorrectas  se generará un CORTO-CIRCUITO y quedara
en estado SEMI-ABIERTO  en un estado de prueba y si mandamos 45 peticiones correctamente, entonces la tasa 
de peticiones incorrectas es mayor a la tasa de peticiones correctas es decir fallo en un 55% de las peticiones pero como
estamos por encima del umbral definido en 50& se abre el CORTO-CIRCUITO por 60 SEGUNDOS después de estar en CORTO-CIRCUITO
pasara nuevamente a estado SEMI-ABIERTO y comenzara nuevamente a evaluar las peticiones si el porcentaje de las 
peticiones correctas es mayor al porcentaje de las peticiones erróneas se CIERRA-CORTO-CIRCUITO en caso contrario se repite
el ciclo

##Manejando los errores en en timeOut
Manejando las llamadas lentas se deberá de configurar otro umbral de duración, por ejemplo si se pasa  de 2 segundos será
registrada como una petición o request con falla, si el tamaño de la ventana deslizante es mayor de 50% es decir sean lentas 
entrará en un corto-circuito

## Peticiones con timeOut
El timeOut es 1sg /api/cursos/9 con este endPoint se valida el timeOut
Determinar el tiempo de espera de un TimeOut en Default que es igual aun segundo
timeLimiterConfig(TimeLimiterConfig.ofDefaults())

Determinar el tiempo de espera de un TimeOut de forma configurable
timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2L)).build())


## Peticiones lentas
Para poder evaluar las llamadas lentas, el umbral del timeOut deberá de ser mayor al umbral de las 
peticiones de las llamadas lentas.
Cuando el porcentaje de llamadas en general es igual o mayor que el umbral que se esta configurando
el CORTO-CIRCUITO entra en estado abierto y es considerada una LLAMADA LENTA

Se determina el porcentaje del umbral de las llamadas lentas
slowCallRateThreshold(50) 

Se configura el tiempo de duración es decir el tiempo maximo que debiera demorar una llamada en particular
slowCallDurationThreshold(Duration.ofSeconds(2L))



##NOTA IMPORTANTE
Cuando se utiliza la anotación solo se puede realizar la configuración a través de los archivos application.properties
o application.yml, no funciona con la clase(AppConfiguration) 

@CircuitBreaker














