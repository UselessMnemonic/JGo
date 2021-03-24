# JGo
Welcome to JGo, my pet project to bring Go to the JVM! This repo is a running history of my
research and progress. I'm inspired by Kotlin, which shares a lot of similarities to Go and serves
as a proof that many of their similar constructs can work with Java. In the future, an adaptation
of this project will also have its basis in Kotlin.

## Mapping Go to Java
### Calling Conventions
In Java, primitives (like `int`, `double`, and `ref`) are passed into functions by their
values alone; this makes them pass-by-value. Objects themselves (meaning the data comprising an
object) are always passed by their references, so objects in Java are _always_ pass-by-reference.

In Go (much like in C), all data is by-default pass-by-value. The data comprising a `struct`, for
instance, is copied entirely when passed into a function. There are some exceptions with constructs
like maps and slices; still, any value can be pass-by-reference by using a pointer to that value.

#### Java Conventions
Java objects can emulate pass-by-value via cloning, so that a new object is constructed when a
distinct copy is needed. Such clones must be deep copies, but this can only be practically
guaranteed when a Go type is translated to Java.

It is not so trivial, however, to emulate pass-by-reference in Java. Pointers in Go can be made from
most types, wherever data may be legally addressed. Java requires some mechanism by which fields,
indexed memory, and local variables alike can be made accessible by a reference. In some cases,
first-order references to an object can be satisfied by its reference value allowing us to use Java
objects normally.

#### The Reference Class
We introduce a new class called `Reference` that will work for all orders of references. It is
declared as follows:
```java
public class Reference<T> {
    T get();
    set(T referent);
}
```
It contains constructors for referencing the fields of `structs`, indexed values of `slices` and
`arrays`, or independent values.

### Defined Types
Defined types in Go become their own types in Java:
```
+---------------+--------------+
|      GO       |    JAVA      |
+---------------+--------------+
|               |              |
| (u)int8       | byte         |
|               |              |
| (u)int16      | short        |
|               |              |
| (u)int32      | int          |
|               |              |
| (u)int64      | long         |
|               |              |
| float32       | float        |
|               |              |
| float64       | double       |
|               |              |
| (*)complex64  | Complex64    |
|               |              |
| (*)complex128 | Complex128   |
|               |              |
| *(u)int8      | (U)Int8      |
|               |              |
| *(u)int16     | (U)Int16     |
|               |              |
| *(u)int32     | (U)Int32     |
|               |              |
| *(u)int64     | (U)Int64     |
|               |              |
| *float32      | Float32      |
|               |              |
| *float64      | Float64      |
|               |              |
| **T           | Reference<T> |
+---------------+--------------+
```
When a type definition is created from any Go type, its corresponding Java class gains a
deep-copy-constructor, a constructor that accepts the base type, and a method that generates a new
instance of the base type. In some cases, a local primitive will be promoted to its corresponding
object type.

### Structs
Structs in Go become independent classes in Java. So, a Go struct declared like this:
```golang
type Item struct {
    Name  String
    Count int
}
```
becomes the following Java declaration:
```java
public class Item {
   public String Name;
   public int    Count;
}
```
Additionally, all Go structs automatically acquire an autogenerated `equals()`, `hashCode()`, and
deep-copy-constructor

#### Anonymous Fields
Go allows the user to furnish structs with anonymous fields, fields who take the names of their
types as identifiers. This works easily in Java, except for identifiers that match Java keywords.
For these and other incompatible identifiers, a dollar sign `$` is prepended, so the following Go
declaration:
```golang
type Container struct {
    X double
    int
}
```
becomes this Java declaration:
```java
public class Container {
    public double X;
    public int    $int;
}
```
An embedded type must be specified as a type name `T`, or as a pointer to a non-interface type name
`*T` provided `T` is not a pointer type. In either case, the unqualified type name `T` acts as the
field name.

#### Promoted Fields and Methods
When a struct `A` is anonymously embedded into another struct `B`, the fields and methods of `A`
become _promoted_—visible from `B` as if they were its own. Here is a small example:
```golang
type Base struct {
    b int
}
type Container struct {
    Base         // Base is embedded
    c string
}
c := Container{}
c.b = 0          // b is now visible from Container
c.Base.b = 1     // a less succint form of the above line
```
Say `Container` already has its own field `b`. Then the field `Base.b` becomes _shadowed_ by
`Container.b`, accessible only by first accessing the anonymous `Base`:
```golang
c.b = 0          // Container.b is modified
c.Base.b = 1     // Base.b is modified
```
This amounts to syntactic sugar. Thus, the same rules for access apply in Java as in anonymous
fields:
```java
public class Base {
    int b;
}
public class Container {
    Base   Base;
    String c;
}
```

### Functions
Functions have similar mappings to Java as fields and variables. A function declared in Go:
```golang
func doSomething(arg1, arg2 int) string
```
takes its prefix notation form in Java
```java
String doSomething(int arg1, int arg2)
```

#### Multiple Return
Go allows functions to return multiple values at once, but Java does not. To remedy this, functions
that return multiple values instead return a `Tuple`. The `Tuple` is an abstract class, whose
children are any of `Couple`, `Triple`, and so on up to `Septuple` and the general-purpose
`NTuple`. They are immutable POJOs. Here is an example:
```golang
func returnSeveral() (a long, b int)
```
Now here is the Java equivalent:
```java
Couple<Long, Integer> returnSeveral()
```

#### Anonymous Functions
Go also allows anonymous for functions, which don't have their own names and typically have limited
scope. The Java equivalent is the lambda, which acts like an anonymous function in Go. Java provides
some functional interfaces for passing these lambdas, allowing Java users to apply them.

### Method Sets
A Go type may have a method set associated with it. The method set of an interface type is its
interface. The method set of any other type `T` consists of all methods declared with receiver type
`T`. The method set of type `*T` also contains the method set of type `T`.

A Java class representing `T` can have instance methods that reflect those in `T`. Instance methods
can be defined on `T` (or `*T` provided `T` itself is not a pointer type) if those methods are
defined in the same package as `T`. Therefore, `Reference` instances cannot have methods, nor can
base types like `int` or `complex64`.

### Interfaces
In Go, an interface type specifies a method set, called its _interface_. Any type that implements
all the methods of an interface is said to implement that interface.
```golang
type I interface {
    M()
}
```
Any type that has a method `M()` automatically implements interface `I`. Thus, with the following
declaration, `T` implements `I`:
```golang
func (i T) M() {...}
```
This allows a value of type `T` to be passed into a variable of type `I`, like so:
```golang
var v I = i
```
The corresponding Java interface will be declared as follows:
```java
public interface I {
    void M();
}
```
and any Go types that implement `I` will automatically have an `implements I` clause in their
corresponding Java classes. Also, all types implicitly implement the empty interface `interface{}`,
whose method set is empty, and whose functionality is present in our class `EmptyInterface`.

### Visibility
#### Packages
Go packages become Java packages, so a Go package `package mypkg` on the path `xyz/` becomes
`xyz.mypkg`. Individual Go files become Java class files, so a file like `example.go` becomes
`ExampleGo.java` and its class is fully qualified as `xyz.mypkg.ExampleGo`.

Functions defined in a Go file become static methods in its corresponding Java class. If the
following is in `example.go`:
```golang
func Square(x int) int
func sqrt(x float32) float32
```
then the following becomes part of `ExampleGo.java`
```java
public class ExampleGo {
    public static int Square(int x);
    static int sqrt(float x);
}
```

#### Identifiers
In Go, identifiers are always visible inside the packages in which they are declared. However, they
are only visible to other packages if those identifiers begin with capital letters. Take the
following example:
```golang
type Address struct {  
    City  string
    State string
    zip   int
}
func (a Address) String() String
func (a Address) transform()
```
The corresponding Java type appears as such:
```java
public class Address implements Stringer {
    public  String City;
    public  String State;
    int     zip;
    
    public  String String();
    void    transform();
}
```
Therefore, `transform()` and `zip` are visible for the package in which `Address` is defined, but
are not visible anywhere else.

#### Aliases
Aliases are, well, as their name suggests, aliases. They do not appear in the final Java source.

## Defer, Panic, and Recover
A defer statement pushes a function call onto a list. The list of saved calls is executed after the
surrounding function returns. Defer is commonly used to simplify functions that perform various
clean-up actions. The behavior of defer statements is straightforward and predictable. There are
three simple rules:

1. A deferred function's arguments are evaluated when the defer statement is evaluated.
2. Deferred function calls are executed in Last In First Out order after the surrounding function
   returns.
3. Deferred functions may read and assign to the returning function's named return values.

Defer statements are not available to Java users, since the JVM does not offer any such facility.
They are automatically generated and hence hidden from normal operation.

#### Panic
It is customary in Java to handle errors by throwing exceptions, but Go prefers to make errors part
of function returns. However, some functions that do encounter errors invoke the `panic()` function,
a built-in function that stops the ordinary flow of control and begins panicking.

When a function `F` calls `panic()`, execution of `F` stops, any deferred statements in `F` are
executed normally, and then `F` returns to its caller. To the caller, `F` now behaves like a call to
`panic()`. The process continues up the stack until all functions in the current goroutine have
returned, at which point the program crashes.

Panics translate into runtime exceptions in Java, avoiding the need to declare them in method
declarations. This allows errors to propagate through threads and eventually the JVM, much like
panics.

#### Recover
`recover()` is a built-in function that regains control of a panicking goroutine. `recover()` is
only useful inside deferred functions. During normal execution, a call to `recover()` will return
`nil` and have no other effect. If the current goroutine is panicking, a call to `recover()` will
capture the value given to `panic()` and resume normal execution.

Recovery is not a facility exposed to Java users, like `defer` and `panic()` before it. Instead,
exceptions caused by panics must be caught by the user using Java, allowing for traditional error
handling. Internally, `recover()` translates into a `try-catch` block that captures the exception.

### Errors
TODO

## Concurrency
### Channels
Channels are powerful tools in Go, allowing for the sharing of memory by communicating. A channel's
type comprises an element type, and a direction of data flow. A channel can be input-only
(`chan<- T`), output-only (`<-chan T`), or bi-directional (`chan T`).

In Java, the channel is represented by an abstract `Channel<T>` class. It can be input-only
(`Channel<? super T>`), output-only (`Channel<? extends T>`), or bi-directional (`Channel<T>`). The
bounds in this case help differentiate between channel types.

#### Buffered Channels
A buffered channel has a limited capacity for elements. If empty, an attempt to receive from the
channel will block. If full, an attempt to send to the channel will block.

#### Range, Close, and Select
TODO

### Goroutines
TODO

### Mutexes
TODO