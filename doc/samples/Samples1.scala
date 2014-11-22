package code

trait NoComposition{

  def parseInt(s: String): Int = 
    Integer.parseInt(s)

  def factorial(n: Int): Int = 
    if (n == 0) 1
    else n * factorial(n-1)

  def main(s: String): Int = 
    factorial(parseInt(s))

  def main: String => Int = 
    (s: String) => factorial(parseInt(s))

}

trait Composition{ self: NoComposition => 

  def composeF[A,B,C](g: B => C, f: A => B): A => C = 
    (a: A) => g(f(a))

  def main1: String => Int = 
    composeF(factorial, parseInt)

  def main2: String => Int = 
    (factorial _) compose parseInt

  def main3: String => Int = 
    (parseInt _) andThen factorial

}



trait SideEffects{

  def factorial(n: Int): Int = 
    if (n < 0) 
      throw new IllegalArgumentException
    else {
      val result = if (n==0) 1 else n * factorial(n-1)
      println(s"factorial($n)=$result")
      result
    }  

}

trait DataTypes{

  /* PURE */ 

  sealed trait Option[+T]
  case object None extends Option[Nothing]
  case class Some[+T](value: T) extends Option[T]

  sealed trait Logging[A]{
    
    def returned: A = this match {
      case Debug(_, next) => next.returned
      case Error(_, next) => next.returned
      case Return(a) => a
    }

    def changeValue[B](f: A => B): Logging[B] = this match{
      case Debug(msg, next) => Debug(msg, next changeValue f)
      case Error(msg, next) => Error(msg, next changeValue f)
      case Return(a) => Return(f(a))
    }

    def concat[B](f: A => Logging[B]): Logging[B] = this match{
      case Debug(msg, next) => Debug(msg, next concat f)
      case Error(msg, next) => Error(msg, next concat f)
      case Return(a) => f(a)
    }

  }

  case class Debug[A](msg: String, next: Logging[A]) extends Logging[A]
  case class Error[A](msg: String, next: Logging[A]) extends Logging[A]
  case class Return[A](value: A) extends Logging[A]

}


trait EffectsFunctions extends DataTypes{

  def parseInt(s: String): Logging[Option[Int]] = 
    try{
      Debug(s"Parsing $s", Return(Option(Integer.parseInt(s))))
    } catch {
      case _: NumberFormatException => 
        Error(s"'$s' is not an integer", Return(None))
    }

  def factorial(n: Int): Logging[Option[Int]] = 
    if (n < 0) 
      Error(s"factorial($n) = error: negative number", Return(None))
    else if (n == 0) 
      Debug(s"factorial($n)=1", Return(Some(1)))
    else {
      factorial(n-1) concat {
        case None => 
          Return(None)
        case Some(rec_result) => 
          val result = n * rec_result
          Debug(s"factorial($n)=$result", Return(Option(result)))
      }
    }

  def main: String => Logging[Option[Int]] = 
    (s: String) => {
      parseInt(s) concat {
        case None => Return(None)
        case Some(number: Int) => factorial(number)
      }
    }

}

trait Interpreters{ self: DataTypes => 

    /* IMPURE */ 

  def optionInterpreter[T](result: => Option[T]): Unit = 
    result match {
      case None => println("Computation: FAILED")
      case Some(result) => println(s"Computation: $result")
    }

  def loggerInterpreter[T](logging: => Logging[T]): Unit = 
    logging match {
      case Debug(msg, next) => 
        println(s"Log: $msg")
        loggerInterpreter(next)
      case Error(msg, next) => 
        println(s"Log: $msg")
        loggerInterpreter(next)
      case _ => ()
    }

  def interpreter[T](result: => Logging[Option[T]]): Unit = {
    loggerInterpreter(result)
    optionInterpreter(result.returned)
  }

}

object EffectsProgram extends EffectsFunctions with Interpreters

trait DataTypeComposition extends DataTypes{

  /* PURE */ 

  // sealed trait Option[+T]
  // case object None extends Option[Nothing]
  // case class Some[+T](value: T) extends Option[T]

  implicit class ExtendedLogging[A](logging: Logging[A]){

    def concatOption[B,C](f: B => Logging[Option[C]])(implicit ev: A <:< Option[B]): Logging[Option[C]] = 
      logging concat {
        case _: None.type => Return(None)
        case Some(b: B@unchecked) => f(b)
      }

  }

}

trait EffectsFunctionsWithComposition extends DataTypeComposition{

  def parseInt(s: String): Logging[Option[Int]] = 
    try{
      Debug(s"Parsing $s", Return(Option(Integer.parseInt(s))))
    } catch {
      case _: NumberFormatException => 
        Error(s"'$s' is not an integer", Return(None))
    }

  def factorial(n: Int): Logging[Option[Int]] = 
    if (n < 0) 
      Error(s"factorial($n) = error: negative number", Return(None))
    else if (n == 0) 
      Debug(s"factorial($n)=1", Return(Some(1)))
    else {
      factorial(n-1) concatOption {
        (rec_result: Int) => 
          val result = n * rec_result
          Debug(s"factorial($n)=$result", Return(Option(result)))
      }
    }

  def main: String => Logging[Option[Int]] = 
    (s: String) => 
      parseInt(s) concatOption {
        (number: Int) => factorial(number)
      }
}


object EffectCompositionProgram extends EffectsFunctionsWithComposition with Interpreters

trait KleisliComposition extends DataTypeComposition{
  import scala.language.implicitConversions

  def composeK[A,B,C](g: B => Logging[Option[C]], f: A => Logging[Option[B]]): A => Logging[Option[C]] = 
    (a: A) => f(a) concatOption { (b: B) => g(b) }

  /* kleisli transformation */ 

  implicit def transformLog[A,B](f: A => Logging[B]): A => Logging[Option[B]] = 
    (a: A) => f(a) changeValue { (b: B) => Option(b) }

  implicit def transformOpt[A,B](f: A => Option[B]): A => Logging[Option[B]] = 
    (a: A) => Return(f(a))

  implicit def transform[A,B](f: A => B): A => Logging[Option[B]] = 
    transformOpt(f andThen {(b: B) => Option(b)})

  /* kleisli combinators */

  def if_K[A,B,C](
    cond: A => Logging[Option[Boolean]])(
    then_K: B => Logging[Option[C]],
    else_K: B => Logging[Option[C]]): A => B => Logging[Option[C]] = 
    (a: A) => (b: B) => 
      cond(a) concatOption { (bool: Boolean) => 
        if (bool) then_K(b) else else_K(b)
      }

  def if2_K[A,B](
    cond: A => Logging[Option[Boolean]])(
    then_K: A => Logging[Option[B]],
    else_K: A => Logging[Option[B]]): A => Logging[Option[B]] = 
    (a: A) => (if_K(cond)(then_K, else_K))(a)(a)

}

trait KleisliCompositionFunctions extends EffectsFunctionsWithComposition with KleisliComposition{

  /* Example */

  def authenticated: String => Boolean = 
    (s: String) => s.length < 5

  def main2: String => Logging[Option[Int]] = 
    if2_K(authenticated)(
      then_K = composeK(factorial, parseInt),
      else_K = { (s: String) => Error(s"Possible number too large: $s", Return(None)) })

}

object KleisliCompositionProgram extends KleisliCompositionFunctions with Interpreters





