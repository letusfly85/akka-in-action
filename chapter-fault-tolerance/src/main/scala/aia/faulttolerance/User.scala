package aia.faulttolerance

case class User(name: String, age:Int, myNumber: Int) {

  // ３つサバを読む
  def sayMyAge(): Unit = {
    println(s"name: ${name}, age: ${(age-3).toString}")
  }

}
