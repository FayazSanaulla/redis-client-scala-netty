package com.fotolog.redis

/**
  * Created by faiaz on 12.07.16.
  */
import org.scalatest.{FlatSpec, Matchers}

class HashCommandsSpec extends FlatSpec with Matchers with TestClient {
  val c = createClient
  c.flushall

  "A hset" should "allow to put and get sets" in {
    c.hset("key0", "f0", "value0") shouldBe true
    c.hset("key0", "f0", "value1") shouldBe true
    c.hget[String]("key0", "f0") shouldEqual Some("value1")
    c.hget[String]("key0", "f1") shouldEqual None
  }

  "A hdel" should "correctly delete results" in {
    c.hset("key0", "f0", "value0") shouldBe true
    c.hdel("key0", "f0") shouldBe true
    c.hdel("key0", "f0") shouldBe false
  }

  "A hmset" should "allow to set and retrieve multiple values" in {
    c.hmset("key1", ("f0", "Hello"), ("f1", "World")) shouldBe true
    c.hmget[String]("key1", "f0", "f1") shouldEqual Map[String, String]("f0" -> "Hello", "f1" -> "World")
  }

  "A hincr" should "correctly increment ints" in {
    c.hset("key1", "f2", 25)
    c.hincr("key1", "f2", 5) shouldEqual 30
    c.hincr("key1", "f2", -6) shouldEqual 24
    c.hincr("key1", "f2") shouldEqual 25
  }

  "A hexists" should "return Boolean result" in {
    c.hset("key", "f0", "xx") shouldBe true
    c.hexists("key", "f0") shouldBe true
    c.hexists("key", "f4") shouldBe false
  }

  "A hlen" should "return length of a hash set" in {
    c.hmset("key1", ("f0", "Hello"), ("f1", "World")) shouldBe true
    c.hlen("key1") shouldEqual 2
  }

  "A keys" should "return keys sequence" in {
    c.hmset("key1", ("f1", "Hello"), ("f2", "World"), ("f3", "World")) shouldBe true
    c.hkeys("key1") shouldEqual Seq[String]("f1", "f2", "f3")
  }

  "A hvals" should "return values sequence" in {
    c.hmset("key2", ("f0", 13), ("f1", 15)) shouldBe true
    c.hmset("key1", ("f1", "Hello"), ("f2", "World"), ("f3", "!")) shouldBe true

    c.hvals[String]("key1") shouldEqual Seq[String]("Hello", "World", "!")
    c.hvals[Int]("key2") shouldEqual Seq[Int](13, 15)
  }

  "A hgetAll" should "return all stored values" in {
    c.hmset("key1", ("f1", "Hello"), ("f2", "World"), ("f3", "!")) shouldBe true
    c.hmset("key2", ("f0", 13), ("f1", 15)) shouldBe true

    c.hgetall[String]("key1") shouldEqual Map[String, String](("f1", "Hello"), ("f2", "World"), ("f3", "!"))
    c.hgetall[Int]("key2") shouldEqual Map[String, Int](("f0", 13), ("f1", 15))
  }

  // TODO: uncomment when will be able to run tests against Redis 3.2
  "A hstrlen" should "return length of a value of hash field" ignore {
    c.hmset("key1", ("f1", "Hi"), ("f2", "World")) shouldBe true
    c.hstrlen("key1", "f1") shouldEqual 2
    c.hstrlen("key1", "f2") shouldEqual 5
  }

  "A hsetnx" should "return Boolean result" in {
    c.hset("key3", "f0", "Hello") shouldBe true
    c.hsetnx("key3", "f0", "Hello Vesteros") shouldBe false
    c.hsetnx("key3", "f3", "field3") shouldBe true
    c.hget[String]("key3", "f3") shouldEqual Some("field3")
  }

  "A hincrbyfloat" should "correctly increment and decrement hash value" in {
    c.hmset("key1", ("f2", 25)) shouldBe true
    c.hincrbyfloat[Double]("key1", "f2", 25.0) shouldEqual 50.0
    c.hincrbyfloat[Double]("key1", "f2", -24.0) shouldEqual 26.0
    c.hincrbyfloat[Double]("key1", "f2") shouldEqual 27.0

    c.hincrbyfloat[Double]("key1", "f1", 30.0) shouldEqual 30.0
  }
}
