package io.moia.service.shortener

import io.moia.service.TestServiceT

class StringManipulatorSpec extends TestServiceT {
  object test extends StringManipulatorT
  "The String manipulator " must {
    "return 8 unique character " in {
      val s1 = test.getUniqueString
      val s2 = test.getUniqueString

      assert(s1 !=  s2 && (s1.length == 8) && (s2.length == 8))


    }
  }

}
