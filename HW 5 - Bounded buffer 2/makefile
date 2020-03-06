JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

CLASSES = \
		BoundedBuffer.java \
		Item.java \
		Consumer.java \
		Producer.java \
		ProducerConsumerDemo.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class