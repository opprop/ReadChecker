package read.typeHierarchy;

import read.qual.SafeByte;
import read.qual.SafeChar;
import read.qual.SafetyBottom;
import read.qual.UnknownSafety;
import read.qual.UnsafeByte;
import read.qual.UnsafeChar;

// javac-dev -processorpath ./bin:./build-deps/framework.jar -processor read.ReadChecker -cp ./bin tests/read-typeHierarchy/TypeHierarchy.java

public class TypeHierarchy {

    @UnsafeByte int produceReadByte() {
        return -1;
    }

    void consumeReadByte(@UnsafeByte int inbuff) { }

    void testMethod(int i, @UnsafeByte int inbuff) {

        //:: error: (argument.type.incompatible)
        consumeReadByte(i); // ERROR

        consumeReadByte(produceReadByte()); // OK

        long j = 1L; // 1L is is @UnkownSafetyLiterals

        consumeReadByte( (int) j); // OK

        int k = produceReadByte();

        consumeReadByte(k); // OK

        k = - 1; // -1 is @UnkownSafetyLiterals

        consumeReadByte(k);

        @UnknownSafety int p = 9;

        k = p; // This is also Ok, why? Because Declaritive type is always top
    }

}
