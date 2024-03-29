# AN INTRODUCTION TO FUNCTIONAL PROGRAMMING - WITH WEB PROGRAMMING EXAMPLES

WHAT IS FUNCTIONAL PROGRAMMING?
===============================

## FUNCTIONS  

**FP is programming with functions.** A functional program *is* a function. A function is a computing device that transforms values into values, and nothing more. This works for any domain. Scala examples: mathematical (fibonacci), parsing (parseInt), image processing, server programming, ...

(**FP is not ...**. Programming with higher orfer functions (map, filter, reduce, ...), i.e. having functions as first class values, inmutable data, recursion (with tail calls), ... These are common ingredients of FP, but they are not sufficient conditions for a functional programming experience - and some of them are not necessary either. Examples? defining memoize functions with uses vars internally, writing a higher order function with side effects, ... These examples will appear latter.)

**Values.** Everything is a value. Including functions!

**Composition.** Composition of functions: andThen, compose. Scala in-depths: implicit classes to enable infix operators. Examples: parsing and then fibonacci (with and without combinators). But we need more: what if it can't be parsed?, what if I want to log events? 

**Side effects.** Example: impure program. Functional programming is programming with functions, but seriously, not cheating. And that means: writing _pure_ functions, i.e without _side effects_. You look their signatures, and you know almost all about them. Functions as in mathematics (def.), as blackboxes that transform inputs into outputs. The best documentation ever. And sometimes, the signature gives you the only thing the function can do (with polymorphic functions, especially - cf. theorems for free). Names of functions don't matter much. But, then, how do we represent effects? How can pure values allow to do something useful?

**Effects.** We represent effects as values, that need to be later interpreted. Pure and impure parts of a functional framework. 

**Algebraic data types.** Cases (sums) and tuples (products). Pattern matching. Examples: Try, Option, Logging, ... Previous parsing + fib combination with Try, Option and Logging. We can no longer apply *function* composition!

**Data type combinators.** First, we can try to pattern match. But this is ugly: we will do it a million of times. Combinators! So, function composition does not work, but in FP programming functions are not the only things that compose: Kleisly functions, pipes, iterateees, ... This should be rather called categorical programming! Examples.

**More on pure functions: equational reasoning**. Pure functions allow _equational reasoning_: reasoning about the behaviour of our programs in terms of substitutions. This is what _referentially transparent expressions_ are all about. "RT is the essence of FP". FP is not a sequence of instructions, it's a RT expression. It's computing with _values_. Functions have to be transparent, in the sense that they do nothing more than what they say. Moment to introduce laws and scalacheck. The substitution model of evaluation. Equational reasoning only works if functions are pure, i.e. don't generate side-effects. This is because, we need to be sure that the function always return the same output when given the same inputs. Otherwise, we can't progress in the substitutions. We'd get stuck. "from this point, we could proceed this way ... or not."

**More on composition.** Compositional/categorical programming. Functor-based programming (haskell blog). The slogan: Pure functions compose.

**More on pure functions: descriptive programming**. FP is programming without side effects: writing reading from files, databases, sockets, mutating variable, exceptions, etc. But how can we do something useful without doing side effects? We can't indeed. What we do is to _DESCRIBE_ effects, and then having a different part of the programm _INTERPRETING_ these descriptions. Prgramming without side effects really means programming leaving side effects _aside_, keeping the bulk of your program _pure_ - i.e., programming using RT expressions. At the end of the universe, the side effects described by your program will be effected or executed. Descriptive programming is something that you can do with any language: not only scala, or haskell, but also Java, Python, Ruby, Javascript, ... What we do is decoupling side-effects from our pure functions. Why? unit testing, separation of concerns, understandability (difficulty of reasoning), composability (side-effects don't compose). Things that can go wrong when effecting side effects: the disk may be full, permissions, ... 

**Examples: this is FP, this is not FP**. To be used throughout this section. Tiny programs which are and are not RT expressions. Those that not will throw exceptions, mutate variables in place, read from files, launch computations (e.g. Scala Futures), etc. The way to introduce side effects. Our icon for purity: ![Mr. proper](http://www.yofuiaegb.com/wp-content/uploads/2012/10/Mr-Propper.jpg) (properly tuned with lambdas). Mutable variables are an anti-pattern when it comes to reasoning about our code (Ghosh), it's against equational reasoning (why?: because you can't reason in isolation using your functions - since there are many potential clients of the same mutex, besides you). More failing cases (besides Futures): Try, Either, ... 

**Example: Option.**. Do not use .get!

**Example: Try.** http://stackoverflow.com/questions/12886285/throwing-exceptions-in-scala-what-is-the-official-rule?lq=1. Failing Try. https://twitter.com/dibblego/status/239498333641666561. http://stackoverflow.com/questions/12886285/throwing-exceptions-in-scala-what-is-the-official-rule?lq=1. Arguably, Try is the official FP way to handle exceptions in Scala.

**Example: Either.** Try is better than throwing exceptions, but why throwing them anyway?. Either comes. http://www.scalautils.org/user_guide/OrAndEvery.

**Example: Validation and \/. 

HOW DO WE PROGRAM WITH FUNCTIONS?
=================================

**Algebraically.** Choosing _data types_, _functions_ (a.k.a operators, combinators) that operate on those data types, and _laws_ which establish the proper behaviour of those functions. _Algebra_ (def.): "... algebra to mean a collection of functions operating over some data type(s), along with a set of laws specifying relationships between these functions."

**Algebraic design.** An API/Library can be described algebrically, by an algebra that obeys specific laws. This is the shape of a purely functional library. Questions: what data types should we use for our library/API? What primitive functions should we define and what might they mean? What laws/properties should they satisfy? The API thus designed forms an algebra. Algebraic design: representations for data types come next; first, interfaces and laws, and then picking up concrete representatios. 

**FP design as an iterative process**. Pure algebraic design: design the ideal API without concerning ourselves with the representation of data types, i.e. working with pure abstract types. But: The design of the API can also be informed by the choice of representation. Functional design is thus an iterative process. 

**FP design and object-orientation.** OO couples state and behaviour in one entity. FP decouples them: behaviours are pure functions, and state is handled by immutable data types, algebraic data types (ADTs) in particular. 

### FUNCTIONS

**Composing functions: combinators.** A program is a RT expression made from functions, which are combined thanks to higher-order functions: these functions can be properly called _combinators_. The only way to compose functions is through functions. Higher order functions, which receive functions and return functions. These special functions are called combinators. The most simple combinator: _composition_ of functions. Other combinators for functions: compose, andthen, ... Look to the code! Example: the map function as the way to avoid imperative loops. It enables higher-level reasoning; or more aggregate reasoning; or reasoning with bigger blocks. Example: 1 to 10: Vector[Int]. Compositionality: what is composed is of the same type. Operations are closed. Otherwise, adaptors, decorators, etc., will be needed: accidental complexity?!. [haskell blog]. 

**FP design: expressiveness.** Primitive vs. derive functions. Expressiveness: what can be expressed with our primitive operations: the universe of meaning that our primitives engender. What operations can be expressed by our API. Sometimes one operation can be expressed in terms of other, more powerful, primitive. It's not primitive then. Something primitive is not really expressible in terms of other primitive. In some cases, we may want to implement the operation primitively to take advantage of the underlying representation, because of efficiency reasons. Reduce an API to a minimal set of primitive functions is important: to reuse the tricky logic, to remove coupling. Goal: find a minimal, yet expressive, set of primitive functions. API components are generic, reusable components that express or factor out common functionaly. 

**FP design: genericity**. Important to identify generic combinators: refining combinators to their more general form. This leads to generic APIs: functors, monoids, monads, etc. These are generic, but at the same time very expressive. More general in the sense that they are able to cover more use cases. The most general also happens to be the most primitive. Same functions in different domains: same signature and same laws. These generic functions are patterns that cut across domains. Fundamental structures that appear over and over again across multiple domains. They represent patterns, and we give them a name in order to: communicate better, quickly find solutions or reduce the design space, imrove conceptual consitency, reuse code!

**More on pure functions: RT expressions and observability**. RT is a relative notion. Although the representation is not pure, the API can be, if this impure stuff is properly encapsulated. It is important that side effects are relegated until the user calls run ... at the end of the day. Difference between effects and side effects. Local side effects may not be observable thanks to scoping constructs (private[...]), or otherwise. All this is to guarantee that the API remains pure - that laws hold. Because side effects can't be accessed externally.

**FP design: expressiveness and purity.** Any hidden or out of band assumption (which is not published in the signature), in particular side effects, in which this primitive may incur would not allow us to treat the function/component as a black box and hence will hamper composability, and hence expressiveness. 

### DATA TYPES

Data types in pure functional programming are *Algebraic Data Types (ADTs)*.

**FP design: data types.** They are more like the *description* of a computation that can be *interpreted* later (or a *program* than we can *run*) than the container of some value. When side effects are removed from the construction of our data structure, we get a pure data structure. Data types have an API. Data types have a representation. In a pure algebraic approach, data type representations don't matter much. It is the relationship between then that matters, as represented by the functions that relate them according to the laws declared for them. 

**Inmutability.** Data are not mutable at all. They are values, and we use Persistent data structures for efficiently copying with mutation. Examples in the REPL: res0 is still there when we add some element to the list. Mutation is thus represented through streams of values. But we lose identity here. FRP to the rescue here, but better support by the language would be desirable.

**Combinator libraries.** _compose_ is a very general combinator. For specific purposes and data types will end up with special-purpose combinator libraries. Examples: Option, Either, Futures, IO, Predicates, ... with their respective combinators: map, flatmap, fold, ... 

**Fold: a general combinator for ADTs.** The case of fold: special interpreter for lists or foldable structures. Defining functions through other functions applied to the different cases of a data type. Generalising fold as an interpreter of the instructions given by an ADT, and the ADT as defining an instruction set.

**A side note: Church encodings.** Data types are functions in disguise! Don't know if this is very useful for this talk though ... 

### LAWS

**FP design: laws.** Meaningful laws aid reasoning, make the API more usable and prove, reinforces, enable he blackboxed-ness of the API. Laws are the guardian of purity. Choosing laws for API: look to the conceptual model, invent them, look to the implementation.

DSLs 
====

**FP design as DSL design.** When we design a library/API we are designing a little language to express a certain kind of computations.

**Awkwardness in functional programming.**: This might be due to some functional abastraction which has not being identified properly. But also the lack of some syntactic sugar. Example for the former: bimap.

**Our icon for DSLs.** 

![Celia Cruz: ¡¡Azúcar!!"](http://www.cubaeuropa.com/homenaje/discografia/fotos/foto2.jpg)

**On usability.** DSLs does not necessarily improve the expressiveness of the API, but its usability, making its use more pleasant. How? Through syntax and helper functions. Scala features: implicits, default args, named args, apply, extractors, companion objects, def macros, annotation macros, ... 

**On patterns.** Builders, wrappers, decorators (implicit class extensions), ...

FP IN PRACTICE: WEB PROGRAMMING EXAMPLES
========================================

_The purpose of this section is illustrating the FP design process in a familiar domain: Web programming, and using a familiar framework: Play. Ideally, the different concerns exposed above should be somehow exemplified: FP design, ADTs, data types, combinator libraries, side effects/purity, genericity, expressiveness, laws, DSLs, patterns, etc. If the Play framework does not comply with some of these principles and concerns, that would also be good for the purpose of this talk._

### GUIÓN PARA PLAY

##### Antes (ya debería haberse contado...)

* Explicación de combinadores básicos

* Explicación de Futures

* Explicación de Iteratees

* Tipos contenedores A[_]

##### Durante

* Hello World: contar las acciones (Your Server as a Function)
  Descripción de la computación (handler).

* Distintas formas de crear acciones: `Action.apply`s,
  `Action.async`s... Aparece el BodyParser, pero se describe muy
  superficialmente.

* ¿Cómo reutilizamos cierta funcionalidad asociada a las acciones sin
  tener que escribirla de nuevo? Action Composition, Decorator,
  Logging.

* Servicio PUT: añadir una palabra

* El decorator tiene ciertas limitaciones. ¿Qué debemos hacer si
  queremos ampliar la información que viene en la cabecera?
  => ActionTransformer & WordRequest (WrappedRequest)

* Los transformadores se pueden componer fácilmente mediante el
  combinador `andThen`. WordRequest andThen ToLower.

* También podemos filtrar las peticiones utilizando un filter:
  WordRequest andThen ToLower andThen ExistingFilter andThen
  ¿NaughtyFilter? (no queda muy limpio, podríamos quitarlo también).

* Servicio search: reutilizamos ToLower andThen NonExistingFilter.

* Servicio POST: añadir una palabra JSON. ¡No podemos reutilizar, no
  podemos acceder de forma segura al tipo del cuerpo! (al menos sin
  hacer guarradas)

* Utilizar el servicio anterior para realizar el parser de Json a
  palabra (String, String). Nos sirve para enseñar un nuevo tipo de
  combinación, el método map para convertir un JsValue en (String,
  String)

* Ampliar servicio de búsqueda: Si la palabra no existe en el
  diccionario, probar con un servicio externo (local) de palabras, en
  el que podremos mostrar el Reverse Routing. Pero principalmente,
  introducimos este servicio para jugar con los Futures, sus
  combinadores y mostrar cómo es realmente una acción. Podríamos
  solicitar un token de autenticación antes de llamar a dicho
  servicio.

* WebSocket: servicio de adición de palabras en formato Json. Jugar
  con los Enumeratees e Iteratees para mostrar código modular y
  reusable.

* Realizar alguna prueba en ScalaTest, para mostrar la importancia del
  DSL y un entorno muy propicio para aprender a programar en Scala.


##### Conclusiones (negativas, las positivas son más sencillas :)

* WrappedRequest rompe la reusability, al acoplar el dominio en los
  servicios.

* `ActionFunction` no permite acceder al body (tipado) de forma
  sencilla (sin lambda types).

* Play no nos ofrece un lenguaje "descriptivo" para modificar el
  estado interno (Cache), por lo que o bien creamos nuestro lenguaje,
  o siendo pragmáticos, añadimos un efecto de lado.

* Los Futures de Play (Scala) tienen efectos de lado. Inmediatamente
  pasan a calcular el valor. Contar con un intérprete que se encargase
  de dicha tarea en una capa más externa sería una práctica más
  funcional (Futures de Scalaz).


### PLAY INTRODUCTION

* Java & **Scala** versions
* MVC (though we'll focus on controllers exclusively)
* Simple
* HTTP nature awareness
* Threads issue
* Asynchronous (Futures) *Asynchronous Programming is the price you pay, know what you're paying for*
* Reactive (Inversion of control - Iteratees)
* Action: your server as a function `Request => Result`


### DESCRIPTION/INTERPRETATION

*How is Play adapted to the Description/Interpretation model?* It's
 adapted to that model by providing a description for the actions
 which will be executed once the server receives the HTTP
 request.

*How is Play NOT adapted to the Description/Interpretation model?*
 Firstly, scala `Future`s run a side effect in the background, because
 they are evaluated as soon as possible, instead of having an external
 interpreter. Secondly, it does not provide a descriptive language to
 modify the internal state (I mean, the Cache). Thereby, if you don't
 want to create your own language/interpreter, you'll be forced to
 have side effects.

### ADTs: DATA TYPES AND COMBINATORS 

Here, we'll be talking about the data types and the combinator libraries.

#### Action

```scala
trait Action[A] extends EssentialAction
object Action extends ActionBuilder[Request]

// builders (object Action)
def apply(block: ⇒ Result): Action[AnyContent]
def apply(block: (Request[AnyContent]) ⇒ Result): Action[AnyContent]  
def apply[A](bodyParser: BodyParser[A])(block: (Request[A]) ⇒ Result): Action[A]
def async[A](bodyParser: BodyParser[A])(block: (Request[A]) ⇒ Future[Result]): Action[A] 
def async(block: (Request[AnyContent]) ⇒ Future[Result]): Action[AnyContent] 
def async(block: ⇒ Future[Result]): Action[AnyContent] 

// combinators (trait Action):
def andThen[A](g: (Iteratee[Array[Byte], Result]) ⇒ A): (RequestHeader) ⇒ A 
def compose[A](g: (A) ⇒ RequestHeader): (A) ⇒ Iteratee[Array[Byte], Result]

// wrappers
Logging { Action { ... } }
```

#### ActionBuilder

```scala
trait ActionBuilder[+R[_]] extends ActionFunction[Request, R] {
  def invokeBlock[A](request: Request[A], block: (R[A]) ⇒ Future[Result]): Future[Result] 
}

// builders
???

// combinators
def andThen[Q[_]](other: ActionFunction[R, Q]): ActionBuilder[Q]
def compose(other: ActionBuilder[Request]): ActionBuilder[R]
def compose[Q[_]](other: ActionFunction[Q, Request]): ActionFunction[Q, R] 

// wrappers
???
```

#### ActionFilter

```scala
trait ActionFilter[R[_]] extends ActionRefiner[R, R] {
  def filter[A](request: R[A]): Future[Option[Result]]
}

// builders
???

// combinators
def andThen[Q[_]](other: ActionFunction[R, Q]): ActionFunction[R, Q]
def compose(other: ActionBuilder[R]): ActionBuilder[R]
def compose[Q[_]](other: ActionFunction[Q, R]): ActionFunction[Q, R] 

// wrappers
???
```

#### ActionTransformer

```scala
trait ActionTransformer[-R[_], +P[_]] extends ActionRefiner[R, P] {
  def transform[A](request: R[A]): Future[P[A]] 
}

// builders
???

// combinators
def andThen[Q[_]](other: ActionFunction[P, Q]): ActionFunction[R, Q]
def compose(other: ActionBuilder[R]): ActionBuilder[P]
def compose[Q[_]](other: ActionFunction[Q, R]): ActionFunction[Q, P] 

// wrappers
???
```

#### BodyParser

```scala
trait BodyParser[+A] extends (RequestHeader) ⇒ Iteratee[Array[Byte], Either[Result, A]] 
object BodyParser
object BodyParsers {
  object parse
}

// builders
def apply[T](debugName: String)(f: (RequestHeader) ⇒ Iteratee[Array[Byte], Either[Result, T]]): BodyParser[T]
def apply[T](f: (RequestHeader) ⇒ Iteratee[Array[Byte], Either[Result, T]]): BodyParser[T]
parse.json
parse.text
parse.xml
parse.error.
parse.file

// combinators
def andThen[A](g: (Iteratee[Array[Byte], Either[Result, A]]) ⇒ A): (RequestHeader) ⇒ A
def compose[A](g: (A) ⇒ RequestHeader): (A) ⇒ Iteratee[Array[Byte], Either[Result, A]]
def flatMap[B](f: (A) ⇒ BodyParser[B])(implicit ec: ExecutionContext): BodyParser[B]
def flatMapM[B](f: (A) ⇒ Future[BodyParser[B]])(implicit ec: ExecutionContext): BodyParser[B]
def map[B](f: (A) ⇒ B)(implicit ec: ExecutionContext): BodyParser[B]
def mapM[B](f: (A) ⇒ Future[B])(implicit ec: ExecutionContext): BodyParser[B]
parse.using
parse.when

// wrappers
???
```

#### Future

```scala
trait Future[+T] extends Awaitable[T]
object Future

// builders
def apply[T](body: ⇒ T)(implicit executor: ExecutionContext): Future[T]
def failed[T](exception: Throwable): Future[T]
def successful[T](result: T): Future[T] 
def fold[T, R](futures: TraversableOnce[Future[T]])(zero: R)(op: (R, T) ⇒ R)(implicit executor: ExecutionContext): Future[R]
def reduce[T, R >: T](futures: TraversableOnce[Future[T]])(op: (R, T) ⇒ R)(implicit executor: ExecutionContext): Future[R]
def sequence[A, M[X] <: TraversableOnce[X]](in: M[Future[A]])(implicit cbf: CanBuildFrom[M[Future[A]], A, M[A]], executor: ExecutionContext): Future[M[A]]
def traverse[A, B, M[X] <: TraversableOnce[X]](in: M[A])(fn: (A) ⇒ Future[B])(implicit cbf: CanBuildFrom[M[A], B, M[B]], executor: ExecutionContext): 

// combinators
def andThen[U](pf: PartialFunction[Try[T], U])(implicit executor: ExecutionContext): Future[T]
def filter(p: (T) ⇒ Boolean)(implicit executor: ExecutionContext): Future[T]
def flatMap[S](f: (T) ⇒ Future[S])(implicit executor: ExecutionContext): Future[S]
def foreach[U](f: (T) ⇒ U)(implicit executor: ExecutionContext): Unit
def map[S](f: (T) ⇒ S)(implicit executor: ExecutionContext): Future[S]
def transform[S](s: (T) ⇒ S, f: (Throwable) ⇒ Throwable)(implicit executor: ExecutionContext): Future[S]
def zip[U](that: Future[U]): Future[(T, U)] 

// wrappers
???
```

WHY SHOULD WE PROGRAM FUNCTIONALY?
==================================

Arguably, because we will write better programs: understandable, reusable, modifiable, testable, paralelizable, ... Programs that will evolve.

**Modularity.** "the degree into which a system's components may be separated and recombined"

**Composability.** Side effects or unit signatures hamper compositionality: you can't manipulate runnable objets generically since you always need to know something about their internal behaviour. For example: having map2 start executing its args inmediately requires access to the executor service. It's better if this is abstracted away.

NEXT STEPS IN FP
================

Further topics in FP: type classes, monoids, functors, monads, scala check, streams, ...

REFERENCES
==========

**Scalaz.**

**Shapeless.**

....

**Finagle (Twitter).** Handles execution of service specs declared by the programmer using abstractions like futures, services, filtrrs, ... It implements the runtime. This has been enormously beneficial, freeing the programmer from the tedium of managing threads, queues, resource pools, and resource reclamation, allowing him instead to focus on application semantics.
This achieves a kind of modularity as we separate concerns of program semantics from their execution.

**Books.** FP in scala (runar, chiusano), Functional and Reactive Domain Modeling (Ghosh), DSLs (Ghosh), ... 